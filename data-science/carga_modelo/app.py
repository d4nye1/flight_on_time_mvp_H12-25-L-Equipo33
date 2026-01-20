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
            resultado_lista = model.predict(df_input)

            datos_prediccion = resultado_lista[0]

            resultado = datos_prediccion["Predicción"]
            explicabilidad = datos_prediccion["Explicabilidad"]
            prob = datos_prediccion["Probabilidad de retraso"] / 100

            if request.distancia is not None:
                distancia_final = request.distancia
            else:
                distancia_final = round(float(datos_prediccion["Distancia"]), 2)

            return {
                "prevision": resultado,
                "probabilidad": round(float(prob), 2),
                "distancia": distancia_final,
                "explicabilidad": explicabilidad
            }
    except Exception as e:
            print(f"DEBUG ERROR: {e}")
            raise HTTPException(500, f"Error en la predicción: {str(e)}")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)
