import os
import jaydebeapi
import pandas as pd
import joblib
import re
from datetime import datetime
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, field_validator
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from fastapi import Request
import __main__

# --- CONFIGURACIÓN DE ENTORNO ---
os.environ['JAVA_HOME'] = r'C:\Program Files\Java\jdk-17'
os.environ['PATH'] = os.path.join(os.environ['JAVA_HOME'], 'bin', 'server') + os.pathsep + os.environ['PATH']

# ===========================
# 1. FUNCIONES DE INGENIERÍA 
# ===========================
def calcular_distancia(df, diccionario=None, default=0):
    df = df.copy()
    df['origen-destino'] = df['aeropuerto_origen'] + df['aeropuerto_destino']

    if 'distancia_millas' not in df.columns:
        df['distancia_millas'] = 0 
    return df

def extraer_features_fecha(df):
    df = df.copy()
    cyclical_columns = ['mes', 'dia_semana', 'hora_salida']
    df['hora_salida'] = df['fecha_vuelo'].dt.hour
    df['mes'] = df['fecha_vuelo'].dt.month
    df['dia_semana'] = df['fecha_vuelo'].dt.weekday + 1
    df['fin_de_semana'] = df['dia_semana'].isin([6, 7]).astype(bool)

    for column in cyclical_columns:
        max_val = df[column].max() if not df[column].empty else 1
        if max_val == 0: max_val = 1
        df[column + '_sin'] = np.sin(2 * np.pi * df[column] / max_val)
        df[column + '_cos'] = np.cos(2 * np.pi * df[column] / max_val)
        df = df.drop(columns=[column])
    df = df.drop(columns=['fecha_vuelo'])
    return df

__main__.calcular_distancia = calcular_distancia
__main__.extraer_features_fecha = extraer_features_fecha

# ==========================================
# 2. WRAPPER PARA EXTRACCIÓN DE DATOS
# ==========================================
class FlightModelWrapper:
    def __init__(self, pipeline):
        self.pipeline = pipeline
        self.gen_dist = pipeline.named_steps["gen-dist"]

    def predict_data(self, X):
        prob = self.pipeline.predict_proba(X)[0][1]
        
        X_transformed = self.gen_dist.transform(X)
        dist_millas = X_transformed['distancia_millas'].iloc[0]
        
        return prob, dist_millas

# =======================
# APP Y CONFIGURACIÓN
# =======================
app = FastAPI(title="FlightOnTime DS API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# =======================
# EXCEPTION HANDLER
# =======================
@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    first_error = exc.errors()[0]
    mensaje = first_error.get("msg", "Error de validación")
    mensaje = mensaje.replace("Value error, ", "")

    return JSONResponse(
        status_code=400,
        content={"error": mensaje}
    )


# CARGA DEL MODELO
try:
    raw_pipeline = joblib.load("modelo_XGB.joblib")
    model = FlightModelWrapper(raw_pipeline)
    print("✅ Modelo cargado y configurado con éxito")
except Exception as e:
    model = None
    print(f"❌ Error crítico al cargar el modelo: {e}")

# =======================
# MODELO DE ENTRADA
# =======================
class FlightRequest(BaseModel):
    aerolinea: str
    origen: str
    destino: str
    fecha_partida: str

    @field_validator("aerolinea")
    @classmethod
    def validar_aerolinea(cls, v):
        if not v or not v.strip():
            raise ValueError("La aerolínea es obligatoria")

        v = v.strip().upper()

        if not re.fullmatch(r"^[A-Z0-9]{2}$", v):
            raise ValueError(
                "La aerolínea debe ser un código IATA de 2 letras (ej: AA, AV, LA)"
            )

        return v

    @field_validator("origen", "destino")
    @classmethod
    def validar_aeropuertos(cls, v, info):
        if not v or not v.strip():
            raise ValueError(f"El {info.field_name.capitalize()} es obligatorio")
        return v.strip().upper()    

    @field_validator("fecha_partida")
    @classmethod
    def validar_fecha(cls, v):
        if not v or not v.strip():
            raise ValueError("Fecha partida es obligatoria")

        try:
            datetime.fromisoformat(v)
        except ValueError:
            raise ValueError("Fecha partida debe tener formato YYYY-MM-DD")

        return v

# =======================
# ENDPOINT
# =======================
@app.post("/predict")
def predict(request: FlightRequest):
    if model is None:
        raise HTTPException(500, "Modelo no cargado")

    df_input = pd.DataFrame([{
        "aeropuerto_origen": request.origen,
        "aeropuerto_destino": request.destino,
        "aerolinea": request.aerolinea,
        "fecha_vuelo": pd.to_datetime(request.fecha_partida)
    }])

    try:
        prob, dist_millas = model.predict_data(df_input)
        
        dist_km = round(float(dist_millas * 1.60934), 2)
        resultado = "Retrasado" if prob >= 0.5 else "Puntual"

        return {
            "prevision": resultado,
            "probabilidad": round(float(prob), 2),
            "distancia": dist_km
        }
    except Exception as e:
        raise HTTPException(500, f"Error en la predicción: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)
