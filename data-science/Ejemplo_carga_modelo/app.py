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
    Retorna las aerolíneas y aeropuertos disponibles en el modelo entrenado.
    Extrae directamente del OrdinalEncoder del pipeline.
    """
    try:
        # Acceder al paso 'preprocessing' del pipeline
        preprocessing_step = raw_pipeline.named_steps['preprocessing']

        # Obtener el transformer categórico (OrdinalEncoder)
        categorical_transformer = preprocessing_step.named_transformers_['categorical']

        # Extraer las categorías
        # categories_[0] = aerolíneas
        # categories_[1] = aeropuertos origen
        # categories_[2] = aeropuertos destino
        aerolineas = categorical_transformer.categories_[0].tolist()
        aeropuertos_origen = categorical_transformer.categories_[1].tolist()
        aeropuertos_destino = categorical_transformer.categories_[2].tolist()

        # Combinar aeropuertos y eliminar duplicados
        aeropuertos = sorted(list(set(aeropuertos_origen + aeropuertos_destino)))

        print(f"✅ Metadata extraída exitosamente:")
        print(f"   📋 {len(aerolineas)} aerolíneas: {aerolineas}")
        print(f"   📍 {len(aeropuertos)} aeropuertos únicos")

        return {
            "aerolineas": sorted(aerolineas),
            "aeropuertos": aeropuertos
        }

    except Exception as e:
        print(f"❌ Error extrayendo metadata del modelo: {e}")
        import traceback
        traceback.print_exc()

        # Fallback con datos reales conocidos del modelo
        print("⚠️ Usando fallback con datos conocidos del modelo")
        return {
            "aerolineas": ['9E', 'AA', 'AS', 'B6', 'DL', 'F9', 'G4', 'HA', 'MQ', 'NK', 'OH', 'OO', 'UA', 'WN', 'YX'],
            "aeropuertos": [
                'ABE', 'ABI', 'ABQ', 'ABR', 'ABY', 'ACK', 'ACT', 'ACV', 'ACY', 'ADK',
                'ADQ', 'AEX', 'AGS', 'AKN', 'ALB', 'ALO', 'AMA', 'ANC', 'APN', 'ASE',
                'ATL', 'ATW', 'AUS', 'AVL', 'AVP', 'AZO', 'BDL', 'BFL', 'BGM', 'BGR',
                'BHM', 'BIL', 'BIS', 'BJI', 'BLI', 'BMI', 'BNA', 'BOI', 'BOS', 'BPT',
                'BQK', 'BQN', 'BRO', 'BRW', 'BTM', 'BTR', 'BTV', 'BUF', 'BUR', 'BWI',
                'BZN', 'CAE', 'CAK', 'CDC', 'CDV', 'CEC', 'CHA', 'CHO', 'CHS', 'CIC',
                'CID', 'CLD', 'CLE', 'CLL', 'CLT', 'CMH', 'CMI', 'CMX', 'COD', 'COS',
                'COU', 'CPR', 'CRP', 'CRW', 'CSG', 'CVG', 'CWA', 'DAB', 'DAL', 'DAY',
                'DBQ', 'DCA', 'DEN', 'DFW', 'DHN', 'DLG', 'DLH', 'DRO', 'DSM', 'DTW',
                'EAU', 'ECP', 'EGE', 'EKO', 'ELM', 'ELP', 'ERI', 'ESC', 'EUG', 'EVV',
                'EWN', 'EWR', 'EYW', 'FAI', 'FAR', 'FAT', 'FAY', 'FCA', 'FLG', 'FLL',
                'FLO', 'FNT', 'FSD', 'FSM', 'FWA', 'GCC', 'GEG', 'GFK', 'GGG', 'GJT',
                'GNV', 'GPT', 'GRB', 'GRI', 'GRK', 'GRR', 'GSO', 'GSP', 'GST', 'GTF',
                'GTR', 'GUC', 'HDN', 'HLN', 'HNL', 'HOU', 'HPN', 'HRL', 'HSV', 'IAD',
                'IAH', 'ICT', 'IDA', 'ILG', 'ILM', 'IND', 'ISP', 'ITH', 'ITO', 'IYK',
                'JAC', 'JAN', 'JAX', 'JFK', 'JNU', 'KOA', 'KTN', 'LAN', 'LAS', 'LAX',
                'LBB', 'LCH', 'LEX', 'LFT', 'LGA', 'LGB', 'LIH', 'LIT', 'LNK', 'LRD',
                'LSE', 'LWS', 'MAF', 'MBS', 'MCI', 'MCN', 'MCO', 'MDT', 'MDW', 'MEI',
                'MEM', 'MFE', 'MFR', 'MGM', 'MHT', 'MIA', 'MKE', 'MKG', 'MLB', 'MLI',
                'MLU', 'MOB', 'MOT', 'MQT', 'MRY', 'MSN', 'MSO', 'MSP', 'MSY', 'MTJ',
                'MYR', 'OAJ', 'OAK', 'OGG', 'OKC', 'OMA', 'OME', 'ONT', 'ORD', 'ORF',
                'ORH', 'OTZ', 'OXR', 'PAH', 'PBI', 'PDX', 'PFN', 'PHL', 'PHX', 'PIA',
                'PIH', 'PIT', 'PLN', 'PMD', 'PNS', 'PPG', 'PSC', 'PSE', 'PSG', 'PSP',
                'PVD', 'PWM', 'RAP', 'RDD', 'RDM', 'RDU', 'RFD', 'RHI', 'RIC', 'RKS',
                'RNO', 'ROA', 'ROC', 'ROW', 'RST', 'RSW', 'SAF', 'SAN', 'SAT', 'SAV',
                'SBA', 'SBN', 'SBP', 'SCC', 'SCE', 'SDF', 'SEA', 'SFO', 'SGF', 'SGU',
                'SHV', 'SIT', 'SJC', 'SJT', 'SLC', 'SMF', 'SMX', 'SNA', 'SPI', 'SPS',
                'SRQ', 'STL', 'STT', 'STX', 'SUN', 'SUX', 'SWF', 'SYR', 'TLH', 'TOL',
                'TPA', 'TRI', 'TUL', 'TUS', 'TVC', 'TWF', 'TXK', 'TYR', 'TYS', 'VLD',
                'VPS', 'WRG', 'WYS', 'XNA', 'YAK', 'YUM'
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