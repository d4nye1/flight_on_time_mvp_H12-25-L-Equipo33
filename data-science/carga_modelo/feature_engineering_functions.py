import pandas as pd
import numpy as np

def calcular_distancia(df, diccionario, default=0):
  df = df.copy()

  if 'distancia' not in df.columns:
    df['distancia'] = np.nan

  df['origen-destino'] = df['aeropuerto_origen'] + df['aeropuerto_destino']
  df['distancia_kms'] = np.nan

  mask = df['distancia'].notna()
  df.loc[mask, 'distancia_kms'] = df.loc[mask, 'distancia']

  df.loc[~mask, 'distancia_kms'] = (df.loc[~mask, 'origen-destino'].map(diccionario).fillna(default) * 1.60934)

  df['distancia'] = df['distancia_kms']

  return df.drop(columns = ['origen-destino', 'distancia_kms'])


def extraer_features_fecha(df):
  df = df.copy()

  df['hora_salida'] = df['fecha_vuelo'].dt.hour
  df['mes'] = df['fecha_vuelo'].dt.month
  df['dia_semana'] = df['fecha_vuelo'].dt.weekday + 1

  df['fin_de_semana'] = df['dia_semana'].isin([6, 7]).astype(bool)

  fixed_measurements = {'hora_salida': 24, 'mes': 12, 'dia_semana': 7}

  for col, period in fixed_measurements.items():
    
    df[f'{col}_sin'] = np.sin( 2 * np.pi * df[col] / period)
    df[f'{col}_cos'] = np.cos( 2 * np.pi * df[col] / period)

  df = df.drop(columns = ['fecha_vuelo', 'hora_salida', 'mes', 'dia_semana'])

  return df


