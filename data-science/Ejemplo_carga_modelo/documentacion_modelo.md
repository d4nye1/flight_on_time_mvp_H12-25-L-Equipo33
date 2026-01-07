## Documentación del modelo de Machine Learning utilizado en el proyecto

### **1) Base de datos**

<p align="justify">
Conjunto de datos para la predicción de retraso de vuelos. La base de datos propuesta consta originalmente de 7079081 registros de vuelo dentro de U.S.A. pertenecientes al año 2024. La base de datos consta de 34 columnas, las cuales contienen información
tanto previa al vuelo (hora programada del vuelo, fecha, origen y destino, etc) como una vez el vuelo ya ha ocurrido y se conocen otros datos (como la cantidad de tiempo en el aire, o el tiempo desde el aterrizaje hasta el descenso de pasajeros). La fuente de
datos original puede ser consultada <a href="https://www.transtats.bts.gov/">aquí</a> y la tabla de la que fue extraída la información con los campos descritos en el paso 2, fue la tabla <a href="https://www.transtats.bts.gov/DL_SelectFields.aspx?gnoyr_VQ=FGJ&QO_fu146_anzr=b0-gvzr">Reporting Carrier On-Time Performance (1987-present)</a>
de la cual fueron filtrados los vuelos solamente del año 2024 por la gran cantidad de información presente. Cabe recalcar que dicha base de datos contiene vuelos solamente internamente en U.S.A. y utilizarla para realizar predicciones sobre aeropuertos
fuera de este país, sería incorrecto al no contar con datos para desarrollar un modelo predictivo de esta índole correctamente.
</p>

### **2) Análisis exploratorio de datos**

Como etapa previa al modelado, se realizó un Análisis Exploratorio de Datos (EDA) con el objetivo de **comprender la estructura del dataset**, **validar coherencia operativa**, **identificar patrones relevantes** y **definir un conjunto de variables ex-ante** que pudiera ser utilizado en un modelo predictivo sin introducir fuga de información.

El EDA se realizó sobre la base de datos de vuelos 2024, que contiene más de 7 millones de registros y 35 variables. Debido al volumen del dataset, el análisis combinó **revisión estadística**, **validación de formato**, y **visualizaciones sobre muestras representativas** para mantener eficiencia computacional sin perder interpretabilidad.

### 2.1 Revisión inicial del dataset

Se generó una **copia de seguridad** del dataframe crudo con el objetivo de preservar el estado original ante posibles errores durante limpieza o transformación de datos.

Como primer paso de exploración, se revisaron:

- **Vista preliminar** de registros (`head()`), para comprender estructura y formatos.
- **Dimensión del dataset** (`shape`), confirmando un volumen mayor a 7M de filas y 35 columnas.
- **Tipos de datos y nulos** (`info()`), identificando variables numéricas, categóricas y temporales, y detectando columnas con valores faltantes.

La inspección inicial mostró un consumo de memoria superior a 1.8 GB, lo cual justificó la necesidad de realizar limpieza y optimización antes de procesos intensivos de modelado.


### 2.2 Normalización semántica de columnas y diccionario de variables

Con el objetivo de mejorar la legibilidad y consistencia del análisis, se realizó un **renombrado de columnas** a español, con nombres descriptivos y uniformes (por ejemplo: `origin → aeropuerto_origen`, `dep_delay → retraso_salida`, etc.).

Posteriormente se construyó un **diccionario de datos**, detallando la descripción operativa de cada variable. Esto permitió realizar una “limpieza conceptual”, es decir:

- comprender el rol de cada columna dentro del proceso real del vuelo,
- identificar variables conocidas **antes** del despegue vs variables conocidas **después** del evento,
- evitar eliminar información válida por interpretar erróneamente valores nulos esperados.

### 2.3 Revisión y estandarización de tipos de dato

Durante la exploración se detectaron ajustes necesarios en tipos de datos para reflejar de forma correcta su naturaleza:

- **Variable temporal:** `fecha_vuelo` fue convertida a tipo `datetime` para habilitar análisis por fecha y extracción de componentes.
- **Variables binarias:** `cancelado` y `desviado` fueron convertidas a booleanos, ya que representan estados lógicos (sí/no).
- **Identificador:** `numero_vuelo` se ajustó a entero con soporte de nulos (`Int64`) ya que no representa una magnitud numérica continua sino un identificador.

Se ignoraron advertencias de tipo (`DtypeWarning`) en columnas con tipos mixtos que no forman parte del dataset final de modelado.


### 2.4 Análisis de valores nulos y consistencia operativa

Se calculó el conteo de valores faltantes por columna. Se observó que los nulos se concentran principalmente en:

- horarios reales (salida/llegada),
- retrasos,
- variables operativas (taxi, ruedas, tiempo en aire).

Esta distribución fue considerada **esperada**, ya que:

- los vuelos cancelados no presentan valores reales operativos,
- algunos procesos operativos (taxi/ruedas) pueden estar incompletos en ciertos registros.

Esto guió las decisiones de filtrado posteriores, aplicadas únicamente sobre campos críticos para la definición de retraso.


### 2.5 Filtrado de vuelos no útiles para el MVP

Dado que el objetivo es predecir **retrasos en vuelos operados**, se filtraron del análisis aquellos vuelos:

- **cancelados**, y
- **desviados**,

ya que no cuentan con tiempos reales confiables y no representan operaciones completas comparables con vuelos regulares. Este filtrado permite que el modelo aprenda patrones consistentes del sistema operativo real de puntualidad.



### 2.6 Eliminación de variables con fuga de información (data leakage)

Se identificaron variables que contienen información posterior al evento o explican directamente el retraso una vez ocurrido, por ejemplo:

- retrasos por causa (`retraso_clima`, `retraso_aerolinea`, etc.)
- `codigo_cancelacion`

Estas variables fueron eliminadas debido a que introducirlas en el modelado causaría **sobreajuste** y rendimiento artificialmente alto, al incluir información que no está disponible en un escenario de predicción previa al despegue.


### 2.7 Tratamiento de valores nulos críticos

Para poder definir correctamente la variable objetivo y realizar el análisis de retrasos, se eliminaron registros con valores faltantes en columnas críticas:

- `hora_salida_real`
- `hora_llegada_real`
- `retraso_salida`
- `retraso_llegada`

Se eligió eliminación (drop) en lugar de imputación porque estas variables representan resultados operativos que no pueden ser estimados de manera confiable sin introducir sesgo.


### 2.8 Definición de la variable objetivo

El objetivo del proyecto es predecir retrasos **antes del despegue**, por lo que se definió la variable objetivo en función del retraso de salida.

Se definió como:

- **0 (Puntual):** `retraso_salida ≤ 15` minutos  
- **1 (Retrasado):** `retraso_salida > 15` minutos  

Este umbral corresponde al criterio estándar de puntualidad utilizado en la industria para definir vuelos “late” (más de 15 minutos de retraso).  
Referencia: https://www.oag.com/airline-on-time-performance-defining-late


### 2.9 Ingeniería de variables temporales ex-ante

Para representar patrones operativos sin introducir información posterior al evento, se transformó la hora programada de salida (formato HHMM) en componentes interpretables:

- `hora_salida` (0–23)
- `minuto_salida` (0–59)
- `fin_de_semana` (indicador de operación sábado/domingo)

Además, se realizó una validación ligera del formato HHMM, detectando un solo registro fuera de rango, el cual fue eliminado por ser insignificante en proporción al total y por evitar ruido.

Estas transformaciones permiten capturar patrones recurrentes como:

- acumulación de demoras a lo largo del día,
- comportamiento semanal,
- ciclos operativos predecibles.


### 2.10 Análisis descriptivo de variables numéricas continuas

Se aplicó `describe()` exclusivamente sobre variables numéricas continuas relevantes para validar rangos, dispersión y coherencia operativa:

- retrasos (salida/llegada),
- taxi (entrada/salida),
- duraciones (programada/real/en aire),
- distancia.

Se observó:

- distribuciones asimétricas y alta variabilidad, esperables en operación aérea,
- medianas negativas en retrasos pese a medias positivas (más del 50% sale/llega antes, pero existe una cola de retrasos grandes),
- coherencia entre tiempos programados y reales,
- distancias sesgadas hacia vuelos cortos y medianos.

Los valores extremos fueron interpretados como eventos operativos reales y se conservaron.

### 2.11 Visualizaciones exploratorias y hallazgos operativos

Para análisis visual se utilizaron muestras representativas (por eficiencia computacional) y se exploraron relaciones relevantes:

- [**Relación  entre retraso de salida vs retraso de llegada:**](https://drive.google.com/file/d/1Io-MHWGqzY7l1GKAnBuI-Az42uDtstQi/view?usp=drive_link)
relación positiva consistente; evidencia recuperación parcial en algunos vuelos.
- [**Distribución de la variable objetivo:**]() se confirmó un desbalance moderado (~20% retrasados vs ~80% puntuales).
- **Retraso por aerolínea (Top 10):** diferencias significativas sugieren patrones operativos aprendibles.
- **Retraso por hora del día:** tendencia creciente conforme avanza el día, consistente con acumulación de demoras.
- **Distancia vs retraso:** comparación por boxplot para identificar diferencias estructurales.
- **Semana vs fin de semana:** diferencias en la tasa de retraso; se confirma que `fin_de_semana` aporta señal útil.


### 2.12 Correlación exploratoria con la variable objetivo

Se calculó correlación entre variables numéricas ex-ante y la variable objetivo para identificar asociaciones lineales útiles como guía inicial (sin implicar causalidad), incluyendo:

- distancia,
- tiempo programado,
- hora/minuto de salida,
- variables temporales transformadas.

Como es esperable en sistemas operativos complejos, se observaron correlaciones bajas a moderadas. Esto respalda que el modelo debe capturar relaciones **no lineales** y combinaciones de variables (por ejemplo, modelos basados en árboles).


### 2.13 Exportación del dataset final y cierre de etapa

Como producto de la fase de limpieza y EDA se generó un dataset final para modelado: `df_clean_modelo`, que:

- contiene únicamente vuelos operados (no cancelados ni desviados),
- excluye variables con fuga de información,
- incluye únicamente variables disponibles **antes del despegue**,
- contiene variable objetivo binaria `vuelo_retrasado`,
- no presenta nulos críticos.

Se conservaron dos variantes:

1. **Dataset para modelado (`df_clean_modelo`)**: contiene únicamente variables permitidas + objetivo.  
2. **Dataset completo limpio (`df_clean`)**: se conserva opcionalmente para auditoría/EDA, ya que mantiene variables operativas usadas para análisis exploratorio.


### **3) Selección de características (features) o variables predictoras**

<p align="justify">
Como base de datos previa, se utilizaron los registros limpios, provenientes del notebook "DataScience.ipynb". Dicho notebook realiza la limpieza de registros duplicados, faltantes, formatos, y sugiere las variables a utilizar en el modelo a desarrollar.
Posteriormente se realizó la escritura del notebook "DataScience_seleccion-features.ipynb". En este segundo notebook, se realizó una matriz de correlación, así como la prueba de Cramers V, para determinar la idoneidad de elección de variables que tuvieran
una alta asociación. La matriz de correlación se realizó para variables numéricas, y la prueba de Cramers V para variables categóricas. Con base en ello, se seleccionaron las variables predictoras como aquellas con mayor asociación (o capacidad de distinción) 
con la clasificación requerida (retraso, a tiempo).
</p>
<p align="justify">
En el mismo notebook, se decidió que las variables temporales serían codificadas de manera cíclica para guardar la temporalidad presente en los datos. Para más detalle puede consultarse dicho notebook. De igual forma, se discutió y decidió que dentro de los procesos
internos del modelo, de preparación de datos previo al entrenamiento (pipeline) se incluiría la extracción de variables numéricas a partir de una variable de tipo hora y fecha, así como la extracción de la distancia entre aeropuertos al ser éste un dato
imposible de saber a priori por el usuario final.
</p>

### **4) Selección y entrenamiento del modelo**

<p align="justify">
Una vez determinados los hallazgos de la sección 3, se procedió con el entrenamiento de modelos. Estos procesos son realizados y discutidos detalladamente en el notebook "DataScience_ModelosML.ipynb". En este notebook, se obtiene una base de datos limpia, como
lo realizó la sección 2, pero incluyendo los campos relevantes para el desarrollo del modelo final. Es decir, se incluyeron las variables de fecha y hora, aeropuerto origen, aeropuerto destino, aerolínea y la distancia (misma que se genera directamente dentro del
entrenamiento del modelo, pero requiere ser visualizada durante la fase del entrenamiento mediante un diccionario para estar en posibilidad de recuperarla posteriormente, en fase de prueba). En dichos experimentos, se realizaron las definiciones de dos funciones:
una utilizada para extraer las variables determinadas como las más informativas a partir de la fecha, y la otra para generar o recuperar la distancia a partir del par aeropuerto origen-destino. Esta última función funciona mediante un diccionario, guardado al serializar el modelo, que asocia la clave del aeropuerto origen-destino con su distancia correspondiente.
</p>
<p align="justify">
También, en dicho notebook, se realizó un remuestreo de datos con la finalidad de realizar un balanceo de clases. Este proceso es de utilidad ya que un gran desbalanceo tiende a predecir la categoría más frecuente en la base de datos. Esto vuelve de poca
o nula aplicabilidad y utilidad un modelo en entornos del mundo real. A pesar de que existen técnicas avanzadas para dicho remuestreo, muchas de ellas están basadas en la medición de distancias entre puntos (lo que involucra la necesidad de calcular la diferencia
entre cada registro, es decir, cada fila; con todos los demás, lo que lo vuelve computacionalmente pesado) por lo que al contar con una gran cantidad de registros, se optó por realizar un submuestreo de la clase mayoritaria (en nuestro caso, vuelos a tiempo) 
de una manera aleatoria, para igualar la cantidad de ejemplos de la clase minoritaria (vuelos retrasados en nuestra base de datos). Al utilizar esta técnica de submuestreo, se elimina la carga computacional debida a los métodos de remuestreo basados en distancia, y se aseguró una cantidad equiparable de ejemplos para ambas categorías, para asegurar un aprendizaje equilibrado del modelo.
</p>
<p align="justify">
Para la parte de preprocesamiento de datos, previo al entrenamiento del modelo, se utilizaron dos estrategias: Standard Scaler y ordinal encoder. La estrategia de escalado estándar fue utilizada para cambiar la escala de las variables numéricas de cualquier escala para normalizarlas a tener una media de cero y una desviación estándar de 1. Esto es útil pues permite evitar que una característica en una escala mucho mayor que otra, domine el entrenamiento, y de cierta forma normaliza su impacto en las predicciones. Además, la gran mayoría de modelos de sklearn (librería seleccionada para realizar el entrenamiento de modelos) requiere datos dentro de cierto rango, y si bien existen modelos invariantes a escalas, se optó por estandarizar este paso y de cualquier forma escalar las entradas numéricas. En lo que respecta a la codificación ordinal, esto fue seleccionado ya que en el paso 3 se observó una gran cantidad de categorías tanto para aeropuertos de origen-destino, como para aerolíneas. Lo ideal habría sido utilizar otra técnica de codificación como One Hot Encoder, pero esto resultaría en un problema de aumento de la dimensionalidad muy problemático, tanto para el tiempo de entrenamiento como para la redundancia y tamaño horizontal (en features) del modelo. Por eso y con la finalidad de mantenerlo simple, se optó por codificar las variables categóricas como ordinales, a pesar de no contar con un orden definido previo.
</p>
<p align="justify">
Se probaron distintos modelos durante el entrenamiento inicial como candidatos para servir como modelo a mejorar. Estos incluyeron pero no se limitaron, a random forest, regresión logística, XGB Classifier y LGBM Classifier. En la notebook "Optimizando_modelos_NoRNN.ipynb" también se realizaron pruebas con distintas proporciones de datos y modelos. En ella se determinó y se realizó un análisis muestral en el que se determinó que a partir de ciertas muestras, el incremento en el desempeño del modelo dejaba de ser significativamente mejor (350000 registros bastan para entrenar un modelo lo suficientemente potente), alcanzando resultados similares en la métrica primaria definida (Área AUC-ROC) en ambas propuestas. Retomando la libreta "DataScience_ModelosML.ipynb", se determinó que el mejor modelo base sin ajuste fue XGBoost. Este modelo fue el que fue optimizado en hiperparámetros, y se validó su desempeño mediante validación cruzada. Se realizó una búsqueda de hiperparámetros en un espacio de parámetros definido y se realizó un ajuste de hiperparámetros aleatorio. Esto no asegura contar con el mejor modelo, pero al contar con una gran cantidad de hiperparámetros a ajustar, otras estrategias de ajuste (por ejemplo, grid search, la cual realiza todas las combinaciones posibles definidas) son más costosas computacionalmente. El ajuste aleatorio permite un balance entre un modelo lo suficientemente potente y una disminución en el tiempo de entrenamiento al disminuir las combinaciones probadas. En particular, durante este ajuste, se probaron 30 combinaciones aleatorias de hiperparámetros diferentes y se validó su desempeño con una validación cruzada con k = 3, es decir, con tres iteraciones o entrenamientos del modelo sobre distintas particiones de datos. Con esta estrategia se logró alcanzar un modelo que alcanzó un AUC-ROC de 0.718 en el conjunto de prueba, después de recuperar el modelo con hiperparámetros ajustados. Finalmente, se probó que el modelo generara predicciones completamente aleatorias (a esta prueba se le llamó Dummy model) y se validó que la métrica de AUC-ROC superara el umbral alcanzado por ese modelo "Dummy". También se realizó una prueba para "envolver" el modelo desarrollado en una clase que personalizara las salidas de dicho modelo, en las que se retornaran las etiquetas como variable string de datos, en lugar de 0s y 1s (es decir, retornar a tiempo y retrasado) así como la probabilidad de ocurrencia de la clase seleccionada. Cabe mencionar que dicha probabilidad de ocurrencia depende de la etiqueta de clase. Es decir, si la etiqueta de clase es "A tiempo", la probabilidad estará en el rango de 0.5-1 y si la etiqueta de clase es "Retrasado", la probabilidad también estará en el rango de 0.5-1. Esto es de esta forma pues la probabilidad de clase representa la probabilidad o la confiabilidad con la que el modelo predice una etiqueta en particular, y siempre se muestra la etiqueta predicha por el modelo y la confianza del modelo para esa predicción en particular (esto representa un porcentaje donde 0.5 representa el 50% de confianza, 0.65 el 65%, 0.78 el 78% y 1 el 100%).

</p>
