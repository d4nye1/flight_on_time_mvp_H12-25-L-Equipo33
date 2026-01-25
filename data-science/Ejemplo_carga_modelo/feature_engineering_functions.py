import pandas as pd
import numpy as np

def calcular_distancia(df, diccionario, default=0):
  df = df.copy()
  df['origen-destino'] = df['aeropuerto_origen'] + df['aeropuerto_destino']
  df['distancia_millas'] = df['origen-destino'].map(diccionario).fillna(default)
  df = df.drop(columns = ['origen-destino'])
  return df


def extraer_features_fecha(df):
  df = df.copy()

  cyclical_columns = ['mes', 'dia_semana', 'hora_salida']

  df['hora_salida'] = df['fecha_vuelo'].dt.hour
  df['mes'] = df['fecha_vuelo'].dt.month
  df['dia_semana'] = df['fecha_vuelo'].dt.weekday + 1

  df['fin_de_semana'] = df['dia_semana'].isin([6, 7]).astype(bool)

  for column in cyclical_columns:

    max = df[column].max()
    df[column + '_sin'] = np.sin( 2 * np.pi * df[column] / max)
    df[column + '_cos'] = np.cos( 2 * np.pi * df[column] / max)
    df = df.drop(columns = column)

  df = df.drop(columns = ['fecha_vuelo'])

  return df
