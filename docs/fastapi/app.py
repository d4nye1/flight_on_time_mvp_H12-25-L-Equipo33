import os
import jaydebeapi
import pandas as pd
import joblib
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

# --- CONFIGURACIÓN AUTOMÁTICA DE JAVA ---
os.environ['JAVA_HOME'] = r'C:\Program Files\Java\jdk-21'
os.environ['PATH'] = os.environ['JAVA_HOME'] + r'\bin\server;' + os.environ['PATH']

# 1. INICIALIZACIÓN DE FastAPI
app = FastAPI(title="FlightOnTime DS API")

# 2. CONFIGURACIÓN DE CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 3. RUTAS Y CONFIGURACIÓN H2
H2_JAR = r"C:\Users\danyb\.m2\repository\com\h2database\h2\2.2.224\h2-2.2.224.jar"
DB_PATH = r"C:\Users\danyb\Desktop\Hackatonoraclealura\data\flightdb"
JDBC_URL = f"jdbc:h2:file:{DB_PATH};AUTO_SERVER=TRUE"
USER = "sa"
PASSWORD = ""

# 4. CARGA DEL MODELO
try:
    model = joblib.load("modelo_XGB.joblib")
    print("✅ Modelo cargado correctamente")
except Exception as e:
    print(f"❌ Error al cargar el modelo: {e}")

class FlightRequest(BaseModel):
    aerolinea: str
    origen: str
    destino: str
    fecha_partida: str

def obtener_distancia_db(origen, destino):
    conn = None
    try:
        # Limpieza de parámetros
        origen_clean = origen.strip().upper()
        destino_clean = destino.strip().upper()
        
        print(f"🔎 Buscando distancia para: {origen_clean} -> {destino_clean}")

        conn = jaydebeapi.connect(
            "org.h2.Driver",
            JDBC_URL,
            [USER, PASSWORD],
            H2_JAR
        )
        curs = conn.cursor()
        
        query = 'SELECT DISTANCIA FROM PREDICTIONS WHERE ORIGEN = ? AND DESTINO = ? LIMIT 1'
        
        curs.execute(query, (origen_clean, destino_clean))
        resultado = curs.fetchone()
        
        if resultado:
            dist = float(resultado[0])
            print(f"✅ Distancia encontrada en DB: {dist} millas")
            return dist
            
        print("⚠️ No se encontró la ruta en la base de datos.")
        return None
    except Exception as e:
        print(f"❌ Error de conexión o consulta DB: {e}")
        return None
    finally:
        if conn:
            conn.close()

@app.post("/predict")
def predict(request: FlightRequest):
    try:
        # 1. Intentar obtener distancia de H2 (viene en Millas)
        distancia_db = obtener_distancia_db(request.origen, request.destino)
        
        if distancia_db is None:
            distancia_db = 500.0  # Valor por defecto en millas
        
        # 2. CONVERSIÓN A KM (Esto es lo que verá el HTML)
        distancia_km = round(distancia_db * 1.60934, 2)

        # 3. Preparar DataFrame para el modelo (usamos millas para no afectar la predicción)
        df_input = pd.DataFrame([{
            "aerolinea": request.aerolinea,
            "aeropuerto_origen": request.origen.upper(),
            "aeropuerto_destino": request.destino.upper(),
            "distancia": distancia_db,
            "fecha_vuelo": pd.to_datetime(request.fecha_partida)
        }])

        # 4. Realizar predicción
        prob = model.predict_proba(df_input)[0][1]
        resultado_texto = "Retrasado" if prob >= 0.5 else "Puntual"

        # 5. Retornar JSON con la distancia en KM
        return {
            "prevision": resultado_texto,
            "probabilidad": round(float(prob), 2),
            "distancia": distancia_km  # <--- Ahora sí envía Kilómetros siempre
        }
    except Exception as e:
        print(f"❌ Error en el proceso de predicción: {e}")
        raise HTTPException(status_code=500, detail=f"Error interno: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)