# ✈️ FlightOnTime - MVP H12-25-L-Equipo33

## 🏆 Insignias

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Python](https://img.shields.io/badge/Python-3.9+-3776ab?style=flat-square&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-0.100+-009688?style=flat-square&logo=fastapi&logoColor=white)
![XGBoost](https://img.shields.io/badge/XGBoost-ML-blue?style=flat-square&logo=xgboost&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)
![Status](https://img.shields.io/badge/Status-Producción-brightgreen?style=flat-square)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-336791?style=flat-square&logo=postgresql&logoColor=white)
![Render](https://img.shields.io/badge/Deploy-Render-46E3B7?style=flat-square&logo=render&logoColor=white)

---

## 📌 Índice

- [📙 Descripción del Proyecto](#-descripción-del-proyecto)
- [📚 Documentación Adicional](#-documentación-adicional)
- [🌳 Problemática](#-problemática)
- [🎯 Objetivo del MVP](#-objetivo-del-mvp)
- [📋 Alcance Funcional](#-alcance-funcional)
- [🏗️ Arquitectura General](#️-arquitectura-general)
- [🔌 Contrato de Integración](#-contrato-de-integración)
- [🔌 Integración y Arquitectura del Sistema](#-integración-y-arquitectura-del-sistema)
- [🚀 Cómo Levantar el Entorno](#-cómo-levantar-el-entorno)
- [🐳 Deployment con Docker](#-deployment-con-docker)
- [🌐 Servicios en Producción (Render)](#-servicios-en-producción-render)
- [🗄️ Base de Datos](#️-base-de-datos)
- [👨‍💻 Equipo de Desarrollo](#-equipo-de-desarrollo)
- [📜 Licencia](#-licencia)

---

## 📙 Descripción del Proyecto

FlightOnTime es una solución predictiva cuyo objetivo es estimar si un vuelo despegará de forma **puntual** o con **retraso**, a partir de información básica del vuelo como aerolínea, aeropuertos, fecha y hora de salida y distancia aproximada.

El sistema está pensado para apoyar la toma de decisiones de:
- Pasajeros, mediante alertas tempranas.
- Aerolíneas, optimizando su operación.
- Aeropuertos, mejorando la planificación logística.

  <img src="images/project_overview.png" alt="FlightOnTime - Visión General del Proyecto" width="100%" />
</p>

---

## 📚 Documentación Adicional

Explora los detalles técnicos y operativos del proyecto en los siguientes enlaces:

- 🧠 **[Documentación del Modelo de Machine Learning](data-science/Ejemplo_carga_modelo/documentacion_modelo.md)**: Explicación detallada del modelo XGBoost, variables y métricas.
- 📊 **[Documentación Data Science](data-science/README.md)**: Visión general de los notebooks y análisis exploratorio.
- 🧱 **[Documentación Backend](backend/README.md)**: Estructura y configuración del servicio Spring Boot.

---

## 🌳 Problemática

Los retrasos de vuelos representan un problema significativo que afecta a múltiples actores del ecosistema aeronáutico. El siguiente diagrama ilustra las **causas raíz** que originan los retrasos y los **efectos** que estos generan en pasajeros, aerolíneas y aeropuertos.

<p align="center">
  <img src="images/arbol.png" alt="Árbol de Problemas - Causas y Efectos de los Retrasos de Vuelos" width="80%" />
</p>

**FlightOnTime** busca mitigar estos efectos proporcionando **predicciones anticipadas** que permitan a todos los stakeholders tomar decisiones informadas antes de que el retraso ocurra.

---

## 🎯 Objetivo del MVP

Desarrollar una **API REST** que reciba información de un vuelo y devuelva:
- La previsión del estado del vuelo (`Puntual` o `Retrasado`)
- La probabilidad asociada a dicha previsión

---

## 📋 Alcance Funcional

- Clasificación binaria del estado del vuelo.
- Predicción basada en:
  - Aerolínea
  - Aeropuerto de origen
  - Aeropuerto de destino
  - Fecha y hora de salida
  - Distancia del vuelo
- Comunicación mediante JSON.

---

## 🏗️ Arquitectura General

| Componente | Tecnología |
|------------|------------|
| Backend | Java 17 + Spring Boot |
| API | REST |
| Modelo ML | XGBoost (Python + FastAPI) |

---

## 🔌 Contrato de Integración

### Endpoint Principal

**`POST /predict`**

### Entrada Esperada (JSON)

```json
{
  "aerolinea": "AZ",
  "origen": "GIG",
  "destino": "GRU",
  "fecha_partida": "2025-11-10T14:30:00",
  "distancia_km": 350
}
```

### Salida Esperada (JSON)

```json
{
  "prevision": "Puntual",
  "probabilidad": 0.22
}
```

<p align="center">
  <img src="images/api_contract.png" alt="Contrato de API - Request/Response" width="100%" />
</p>

---

---

## � Integración y Arquitectura del Sistema

El sistema implementa una arquitectura desacoplada que integra el modelo de Machine Learning mediante un servicio independiente, garantizando robustez y resiliencia.

### Arquitectura de Integración

```
Controller
    ↓
Service
    ↓
ModelClient (HTTP)
    ↓
FlightOnTime Data Science Service (FastAPI)
```

- El controller **no conoce el origen de la predicción**.
- El cliente de Data Science está **completamente aislado**.
- La arquitectura permite **volver a un mock** sin cambios estructurales.

<p align="center">
  <img src="images/architecture_integration_week3.png" alt="Arquitectura de Integración - Semana 3" width="50%" />
</p>

### Servicio de Data Science

- Implementado en **FastAPI**
- Modelo cargado desde **joblib**
- Endpoint expuesto: `POST /predict`

**Respuesta del servicio:**
- Predicción real del modelo
- Probabilidad calculada por el modelo de Machine Learning

### Manejo de Errores Externos

Cuando el servicio de Data Science:
- Está caído
- No responde
- Retorna un error inesperado

El backend **NO expone stacktrace** y retorna un **error funcional y controlado**:

```json
{
  "message": "Servicio de predicción no disponible",
  "status": "ERROR"
}
```

<p align="center">
  <img src="images/error_handling.png" alt="Manejo de Errores - Flujo de Resiliencia" width="100%" />
</p>

### Pruebas Realizadas

**Casos funcionales probados:**
- Vuelo puntual
- Vuelo retrasado
- Error del servicio de Data Science

**Herramientas utilizadas:**
- Postman
- cURL
- Pruebas manuales end-to-end

---

## 🚀 Cómo Levantar el Entorno

Sigue estos pasos para ejecutar la aplicación completa en tu máquina local.

### Prerrequisitos
- **Java 17** o superior
- **Maven 3.8**+
- **Python 3.9**+

### 1. Clonar el Repositorio

```bash
git clone https://github.com/d4nye1/flight_on_time_mvp_H12-25-L-Equipo33.git
cd flight_on_time_mvp_H12-25-L-Equipo33
```

### 2. Ejecutar el Servicio de Data Science (FastAPI)

Este microservicio expone el modelo analítico.

```bash
# Navegar al directorio del servicio
cd docs/fastapi

# Crear entorno virtual (opcional pero recomendado)
python -m venv venv
# Windows:
venv\Scripts\activate
# Linux/Mac:
# source venv/bin/activate

# Instalar dependencias
pip install -r requirements.txt

# Levantar el servidor
uvicorn flightontime_microservicio_ds:app --reload
```
*El servicio estará disponible en `http://localhost:8000`.*

### 3. Ejecutar el Backend (Spring Boot)

El backend orquesta las peticiones y se comunica con el servicio de DS.

```bash
# En una nueva terminal, desde la raíz del proyecto:
cd backend

# Ejecutar con Maven
mvn spring-boot:run
```
*La API principal estará disponible en `http://localhost:8080`.*

---

## 🐳 Deployment con Docker

El proyecto incluye un `docker-compose.yml` que levanta todos los servicios necesarios.

### Servicios Definidos

| Servicio | Contenedor | Puerto | Descripción |
|----------|------------|--------|-------------|
| **PostgreSQL** | `postgres_db` | 5432 | Base de datos principal |
| **Backend** | `spring_backend` | 8080 | API REST (Spring Boot) |
| **Data Science** | `python_ai` | 8000 | Microservicio ML (FastAPI) |

### Levantar con Docker

```bash
# Desde la raíz del proyecto
docker-compose up -d

# Verificar que los servicios estén corriendo
docker-compose ps

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

### Variables de Entorno

```env
# Base de Datos
POSTGRES_DB=flight_on_time
POSTGRES_USER=flightuser
POSTGRES_PASSWORD=flightpass

# Conexión Spring Boot → PostgreSQL
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/flight_on_time
SPRING_DATASOURCE_USERNAME=flightuser
SPRING_DATASOURCE_PASSWORD=flightpass

# Conexión Backend → Microservicio ML
AI_SERVICE_URL=http://data-science:8000
```

---

## 🌐 Servicios en Producción (Render)

La aplicación está desplegada en **Render** y disponible públicamente:

| Servicio | URL | Estado |
|----------|-----|--------|
| 🌐 **Aplicación Web** | [flight-on-time-mvp-h12-25-l-equipo33-1.onrender.com](https://flight-on-time-mvp-h12-25-l-equipo33-1.onrender.com/) | ✅ Activo |
| 🗄️ **Base de Datos** | Render PostgreSQL (interno) | ✅ Activo |

> **Nota:** Los servicios en Render pueden tardar ~30 segundos en "despertar" si han estado inactivos.

---

## 🗄️ Base de Datos

El sistema utiliza **PostgreSQL 15** para persistencia de datos.

### Configuración Local (Docker)

```yaml
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_DB: flight_on_time
      POSTGRES_USER: flightuser
      POSTGRES_PASSWORD: flightpass
    ports:
      - "5432:5432"
```

### Producción (Render)

La base de datos en producción está gestionada por **Render PostgreSQL**, con configuración automática de conexión mediante variables de entorno internas.

### Esquema de Base de Datos

#### Tabla `predictions`

Almacena el historial de predicciones realizadas por el sistema.

| Columna | Tipo | Descripción |
|---------|------|-------------|
| `id` | BIGINT (PK, auto) | Identificador único |
| `aerolinea` | VARCHAR | Código IATA de aerolínea (ej: AA, DL) |
| `origen` | VARCHAR | Código IATA aeropuerto origen (ej: JFK) |
| `destino` | VARCHAR | Código IATA aeropuerto destino (ej: LAX) |
| `prevision` | VARCHAR | Resultado: "Puntual" o "Retrasado" |
| `probabilidad` | DOUBLE | Probabilidad de retraso (0.0 - 1.0) |
| `distancia` | DOUBLE | Distancia del vuelo en km |
| `explicabilidad` | TEXT | Explicación SHAP del modelo ML |
| `fechaPartida` | TIMESTAMP | Fecha/hora programada del vuelo |
| `fechaConsulta` | TIMESTAMP | Fecha/hora de la consulta al sistema |

**Índice:** `idx_flight_cache` sobre `(aerolinea, origen, destino, fechaPartida)` para optimizar búsquedas y evitar consultas duplicadas.

> **Nota:** La tabla se crea automáticamente mediante Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

---

## 👨‍💻 Equipo de Desarrollo

| Integrante | Rol | LinkedIn | GitHub |
|------------|-----|----------|--------|
| Angeles Morales | Data Scientist | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/angeles-morales-ab0a7828a) | [@angelesGladin](https://github.com/angelesGladin) |
| Edson Castañeda | Backend Developer | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/edsoncasta%C3%B1eda/) | [@EdsonCasta](https://github.com/EdsonCasta) |
| Juan Mesa | Data Scientist | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/jmesavergara/) | [@Juanmeve837](https://github.com/Juanmeve837) |
| Enrique Oscar Contreras | Backend Developer | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/enrique-oscar-contreras-ab6329b1/) | [@RickiContreras](https://github.com/RickiContreras) |
| Rodrigo García López | Data Scientist | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/rodrigo-garcia-lopez-165b99197/) | [@rogarlop](https://github.com/rogarlop) |
| Ernesto Daniel López | Backend Developer | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ernesto-l%C3%B3pez-bedolla-29548b1a2/) | [@d4nye1](https://github.com/d4nye1) |
| Norma Noemí Salcedo | Backend Developer | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/norma-salcedo/) | [@normins](https://github.com/normins) |
| Sergio Alonso Bravo | Backend Developer | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/sergio-alonso-bravo-858414286) | [@the-serch](https://github.com/the-serch) |
| Oscar Fernando Paye Cahui | Data Engineer | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://pe.linkedin.com/in/oscar-paye01) | [@FerPaye01](https://github.com/FerPaye01) |
| Zaida Donoso Valdivia | Data Scientist | [![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/zaida-donoso-57949461/) | [@Saya-Sayita](https://github.com/Saya-Sayita) |

---

## 📜 Licencia

Este proyecto está bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

---

**¿Preguntas o sugerencias?** Abre un issue en GitHub o contacta al equipo de desarrollo.
