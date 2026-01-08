# Data Science

## Base de datos
Conjunto de datos para la predicción de retraso de vuelos. La base de datos propuesta consta originalmente de 7079081 registros de vuelo dentro de U.S.A. pertenecientes al año 2024. 
El detalle de las columnas originales y su significado se desglosa a continuación.

1) year &rarr; año del vuelo

2) month &rarr; mes del vuelo (1-12)

3) day_of_month &rarr; día de la semana (1 - lunes, 7 - domingo)

4) fl_date &rarr; fecha de vuelo

5) op_unique_carrier &rarr; códigos de aerolíneas en formato IATA

6) op_carrier_fl_num &rarr; número de vuelo

7) origin &rarr; aeropuerto de origen. Siglas (ejemplo: JFK)

8) origin_city_name &rarr; nombre de ciudad de aeropuerto de origen (ciudad-estado, ejemplo: New York-NY)

9) origin_state_nm &rarr; nombre de estado de aeropuerto de origen

10) dest &rarr; &rarr; aeropuerto de destino. Siglas (ejemplo: JFK)

11) dest_city_name &rarr; nombre de ciudad de aeropuerto de destino (ciudad-estado, ejemplo: New York-NY)

12) dest_state_nm &rarr; nombre de estado de aeropuerto de destino

13) crs_dep_time &rarr; hora de egreso de vuelo agendada (hora:minutos, ejemplo 14:30 se representa como 1430)

14) dep_time &rarr; hora de egreso real (hora:minutos, ejemplo 14:30 se representa como 1430)

15) dep_delay &rarr; diferencia entre hora de egreso agendada (crs_dep_time) y hora de egreso real (dep_time). Si se egresó antes, el valor es negativo. Los retrasos son positivos.

16) taxi_out &rarr; tiempo transcurrido entre dejar las puertas de abordar y el despegue

17) wheels_off &rarr; hora en la que las llantas dejaron el suelo (hora:minutos, ejemplo 14:30 se representa como 1430)

18) wheels_on &rarr;hora en la que las llantas vuelven a tocar el suelo (hora:minutos, ejemplo 14:30 se representa como 1430)

19) taxi_in &rarr;  tiempo transcurrido entre el aterrizaje y llegar a las puertas de abordar/desabordar

20) crs_arr_time &rarr; tiempo de llegada programado (hora:minutos, ejemplo 14:30 se representa como 1430)

21) arr_time &rarr; tiempo real de llegada (hora:minutos, ejemplo 14:30 se representa como 1430)

22) arr_delay &rarr; tiempo transcurrido entre el tiempo de llegada agendado y tiempo real

23) cancelled &rarr; 1 - vuelo cancelado 0 - sin cancelar

24) cancellation_code &rarr; A: Problemas de la aerolínea B: clima C: Afectado por razones del sistema aéreo nacional D: razones de seguridad

25) diverted &rarr; 1 si el vuelo fue desviado de su destino original, 0 otro caso

26) crs_elapsed_time &rarr; tiempo estimado de vuelo total (taxi out + taxi in + air time)

27) actual_elapsed_time &rarr; tiempo real de vuelo total (taxi out + taxi in + air time)

28) air_time &rarr; tiempo de vuelo en minutos

29) distance &rarr; distancia entre aeropuertos (millas)

30) carrier_delay &rarr; retraso de la aerolínea (minutos)

31) weather_delay &rarr; retraso debido al clima (minutos)

32) nas_delay &rarr; retraso debido al sistema aéreo nacional (minutos)

33) security_delay &rarr; retraso por razones de seguridad (minutos)

34) late_aircraft_delay &rarr; retraso debido a que el vuelo anterior llegó tarde (minutos)

## Análisis exploratorio de datos

## Selección y entrenamiento de modelo
