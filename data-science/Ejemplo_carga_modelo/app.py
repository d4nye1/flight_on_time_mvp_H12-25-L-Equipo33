import os
import pandas as pd
import joblib
import re
import numpy as np
import shap
from typing import Optional
from datetime import datetime
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, field_validator
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from fastapi import Request
import __main__

import custom_class_copy as cc
import feature_engineering_functions as func

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

# =================
# CARGA DEL MODELO
# =================
try:
    raw_pipeline = joblib.load("modelo_XGB_V2.1.joblib")
    model = cc.CustomPrediction(raw_pipeline)
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
    distancia: Optional[float] = None

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
# ENDPOINT METADATA
# =======================
@app.get("/metadata")
def get_metadata():
    """
    Retorna las aerolíneas y aeropuertos disponibles en el modelo entrenado
    """
    try:
        # Intentar diferentes formas de extraer las categorías según la estructura del pipeline

        # Método 1: Acceder directamente al modelo interno si existe
        if hasattr(model, 'model') and hasattr(model.model, 'feature_names_in_'):
            print("DEBUG: Usando método 1 - feature_names_in_")
            feature_names = model.model.feature_names_in_
            print(f"DEBUG: Features encontradas: {feature_names[:10]}")

        # Método 2: Extraer del diccionario de distancias si existe
        if hasattr(model, 'diccionario_distancias'):
            print("DEBUG: Extrayendo aeropuertos del diccionario de distancias")
            dict_dist = model.diccionario_distancias
            aeropuertos = sorted(list(set([k[0] for k in dict_dist.keys()] + [k[1] for k in dict_dist.keys()])))
            print(f"DEBUG: {len(aeropuertos)} aeropuertos encontrados")
        else:
            aeropuertos = []

        # Método 3: Extraer aerolíneas de los nombres de características one-hot
        if hasattr(model, 'model'):
            try:
                feature_names = model.model.feature_names_in_
                aerolineas = sorted(list(set([
                    f.replace('aerolinea_', '')
                    for f in feature_names
                    if f.startswith('aerolinea_')
                ])))
                print(f"DEBUG: {len(aerolineas)} aerolíneas encontradas")
            except:
                aerolineas = []
        else:
            aerolineas = []

        # Si no se encontraron datos, usar valores de ejemplo amplios
        if not aerolineas:
            print("⚠️ No se pudieron extraer aerolíneas del modelo, usando dataset de ejemplo")
            aerolineas = [
                "AA", "AS", "B6", "DL", "F9", "G4", "HA", "NK", "UA", "WN",
                "9E", "MQ", "OH", "OO", "YV", "YX", "EV", "QX", "5X", "CP"
            ]

        if not aeropuertos:
            print("⚠️ No se pudieron extraer aeropuertos del modelo, usando dataset de ejemplo")
            aeropuertos = [
                "ATL", "ORD", "DFW", "DEN", "LAX", "CLT", "LAS", "PHX", "IAH", "MCO",
                "MIA", "SEA", "EWR", "MSP", "DTW", "BOS", "JFK", "SLC", "SFO", "BWI",
                "LGA", "DCA", "SAN", "TPA", "PDX", "STL", "HNL", "AUS", "MDW", "BNA"
            ]

        return {
            "aerolineas": sorted(aerolineas),
            "aeropuertos": sorted(aeropuertos)
        }

    except Exception as e:
        print(f"❌ Error crítico en metadata: {e}")
        import traceback
        traceback.print_exc()

        # Fallback robusto con aerolíneas y aeropuertos comunes de USA
        return {
            "aerolineas": [
                "AA", "AS", "B6", "DL", "F9", "G4", "HA", "NK", "UA", "WN",
                "9E", "MQ", "OH", "OO", "YV", "YX", "EV", "QX", "5X", "CP"
            ],
            "aeropuertos": [
                "ATL", "ORD", "DFW", "DEN", "LAX", "CLT", "LAS", "PHX", "IAH", "MCO",
                "MIA", "SEA", "EWR", "MSP", "DTW", "BOS", "JFK", "SLC", "SFO", "BWI",
                "LGA", "DCA", "SAN", "TPA", "PDX", "STL", "HNL", "AUS", "MDW", "BNA",
                "DAL", "PHL", "FLL", "RDU", "SJC", "OAK", "SAT", "RSW", "SMF", "PIT"
            ]
        }

# =======================
# ENDPOINT PREDICT
# =======================
@app.post("/predict")
def predict(request: FlightRequest):
    if model is None:
        raise HTTPException(500, "Modelo no cargado")

    # Convertir la fecha string a datetime
    try:
        fecha_dt = pd.to_datetime(request.fecha_partida)
    except Exception as e:
        print(f"❌ Error parseando fecha: {e}")
        raise HTTPException(400, f"Formato de fecha inválido: {request.fecha_partida}")

    df_input = pd.DataFrame([{
        "aeropuerto_origen": request.origen,
        "aeropuerto_destino": request.destino,
        "aerolinea": request.aerolinea,
        "fecha_vuelo": fecha_dt
    }])

    print(f"🔍 DEBUG - Input recibido:")
    print(f"   Aerolínea: {request.aerolinea}")
    print(f"   Origen: {request.origen}")
    print(f"   Destino: {request.destino}")
    print(f"   Fecha: {fecha_dt}")

    try:
        resultado_lista = model.predict(df_input)
        datos_prediccion = resultado_lista[0]

        print(f"✅ Predicción exitosa: {datos_prediccion}")

        resultado = datos_prediccion["Predicción"]
        prob = datos_prediccion["Probabilidad de retraso"] / 100

        if request.distancia is not None:
            distancia_final = request.distancia
        else:
            distancia_final = round(float(datos_prediccion["Distancia"]), 2)

        return {
            "prevision": resultado,
            "probabilidad": round(float(prob), 2),
            "distancia": distancia_final
        }
    except Exception as e:
        print(f"❌ ERROR en predicción: {e}")
        import traceback
        traceback.print_exc()
        raise HTTPException(500, f"Error en la predicción: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)