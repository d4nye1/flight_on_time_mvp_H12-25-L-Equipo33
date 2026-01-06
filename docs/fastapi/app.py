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

# --- CONFIGURACIÓN AUTOMÁTICA DE JAVA ---
os.environ['JAVA_HOME'] = r'C:\Program Files\Java\jdk-17'
os.environ['PATH'] = os.path.join(os.environ['JAVA_HOME'], 'bin', 'server') + os.pathsep + os.environ['PATH']

# =======================
# APP
# =======================
app = FastAPI(title="FlightOnTime DS API")

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

# =======================
# CORS
# =======================
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

H2_JAR = r"C:\Users\Edson Castañeda\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar"
DB_PATH = r"C:\Users\Edson Castañeda\flight_on_time_mvp_H12-25-L-Equipo33\data\flightdb"
JDBC_URL = f"jdbc:h2:file:{DB_PATH};AUTO_SERVER=TRUE;AUTO_SERVER_PORT=9090"
USER = "sa"
PASSWORD = ""

# CARGA DEL MODELO
try:
    model = joblib.load("modelo_XGB.joblib")
    print("✅ Modelo cargado correctamente")
except Exception as e:
    model = None
    print(f"❌ Error al cargar el modelo: {e}")

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
# DB
# =======================
def obtener_distancia_db(origen, destino):
    conn = None
    try:
        conn = jaydebeapi.connect(
            "org.h2.Driver",
            JDBC_URL,
            [USER, PASSWORD],
            H2_JAR
        )
        curs = conn.cursor()
        curs.execute(
            "SELECT DISTANCIA FROM PREDICTIONS WHERE ORIGEN = ? AND DESTINO = ? LIMIT 1",
            (origen, destino)
        )
        row = curs.fetchone()
        curs.close()
        return float(row[0]) if row else None
    finally:
        if conn:
            conn.close()

# =======================
# ENDPOINT
# =======================
@app.post("/predict")
def predict(request: FlightRequest):

    if model is None:
        raise HTTPException(500, "Modelo de predicción no disponible")

    if request.origen == request.destino:
        raise HTTPException(400, "El origen y el destino no pueden ser iguales")

    # distancia_db = obtener_distancia_db(request.origen, request.destino)
    # if distancia_db is None:
    #     raise HTTPException(404, "Ruta no encontrada")

    # distancia_km = round(distancia_db * 1.60934, 2)

    df_input = pd.DataFrame([{
        "aerolinea": request.aerolinea,
        "aeropuerto_origen": request.origen,
        "aeropuerto_destino": request.destino,
        # "distancia": distancia_db,
        "fecha_vuelo": pd.to_datetime(request.fecha_partida)
    }])

    prob = model.predict_proba(df_input)[0][1]
    resultado = "Retrasado" if prob >= 0.5 else "Puntual"

    return {
        "prevision": resultado,
        "probabilidad": round(float(prob), 2),
        # "distancia": distancia_km
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)
