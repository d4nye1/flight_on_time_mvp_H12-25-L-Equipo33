import pandas as pd
import numpy as np

def calcular_distancia(df, diccionario, default=0):
  df = df.copy()

  if 'distancia' not in df.columns:
    filter_by_distance = pd.Series([False]*len(df))
  else:
    filter_by_distance = df['distancia'].notna() 
    df.loc[filter_by_distance, 'distancia_kms'] = df.loc[filter_by_distance, 'distancia'] * 1.60934
  
  df.loc[~filter_by_distance, 'origen-destino'] = (df.loc[~filter_by_distance, 'aeropuerto_origen'] + df.loc[~filter_by_distance, 'aeropuerto_destino'])
  df.loc[~filter_by_distance, 'distancia_kms'] = df.loc[~filter_by_distance, 'origen-destino'].map(diccionario).fillna(default) * 1.60934
  
  try:
    df = df.drop(columns = ['origen-destino'])
  except:
    pass

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

