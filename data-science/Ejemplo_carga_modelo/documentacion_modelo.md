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
una utilizada para extraer las variables determinadas como las más informativas a partir de la fecha, y la otra para generar o recuperar la distancia a partir del par aeropuerto origen-destino.
</p>
<p align="justify">
También, en dicho notebook, se realizó un remuestreo de datos con la finalidad de realizar un balanceo de clases. Este proceso es de utilidad ya que un gran desbalanceo tiende a predecir la categoría más frecuente en la base de datos. Esto vuelve de poca
o nula aplicabilidad y utilidad un modelo en entornos del mundo real. A pesar de que existen técnicas avanzadas para dicho remuestreo, muchas de ellas están basadas en la medición de distancias entre puntos (lo que involucra la necesidad de calcular la diferencia
entre cada registro, es decir, cada fila; con todos los demás, lo que lo vuelve computacionalmente pesado) por lo que al contar con una gran cantidad de registros, se optó por realizar un submuestreo de la clase mayoritaria (en nuestro caso, vuelos a tiempo) 
de una manera aleatoria, para igualar la cantidad de ejemplos de la clase minoritaria (vuelos retrasados en nuestra base de datos).
</p>
