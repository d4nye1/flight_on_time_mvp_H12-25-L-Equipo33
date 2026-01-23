# ✈️ Flight On Time API - Backend

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-brightgreen?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Container-blue?style=for-the-badge&logo=docker)

**Flight On Time** es una plataforma inteligente que predice la puntualidad de vuelos utilizando modelos de Machine Learning (XGBoost). Este repositorio contiene el **Backend (API REST)** desarrollado en Java Spring Boot, que actúa como orquestador entre el usuario, la base de datos y el motor de IA.

## 🧠 Arquitectura del Sistema

El proyecto utiliza una arquitectura desacoplada:
1.  **Backend (Este servicio):** Spring Boot se encarga de la lógica de negocio, persistencia, validaciones y seguridad.
2.  **Motor de IA:** Microservicio en Python (FastAPI) que ejecuta el modelo predictivo.
3.  **Base de Datos:** PostgreSQL para el almacenamiento persistente (historial y caché de predicciones).

## 🚀 Características Principales

-   **Estrategia Cache-First:** Antes de llamar a la IA, el sistema verifica si la predicción ya existe en la DB para ahorrar recursos y tiempo.
-   **Validación Robusta:** Uso de `@Validated` con grupos secuenciales para asegurar la integridad de los datos de vuelo (IATA codes, fechas futuras, etc.).
-   **Manejo de Errores Global:** Respuestas estandarizadas mediante `ExceptionHandler`.
-   **Docker:** Configuración lista para despliegue mediante contenedores.

## 🛠️ Tecnologías y Librerías

-   **Framework:** Spring Boot 3.2.5
-   **Persistencia:** Spring Data JPA + Hibernate
-   **Seguridad:** Spring Security
-   **Base de Datos:** PostgreSQL / H2 (opcional)
-   **Utilidades:** Lombok, Jackson (JSON handling)
-   **Pruebas:** JUnit 5 + Mockito

## 📋 Endpoints de la API

### Predicciones
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/flights/predict` | Genera una predicción básica de puntualidad. |
| `POST` | `/api/flights/predict-with-stats` | Genera predicción e incluye metadatos estadísticos. |

### Estadísticas
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/stats/summary` | Resumen de predicciones del día actual. |
| `GET` | `/api/stats/historico` | Listado histórico con filtros de fecha opcionales. |
| `GET` | `/api/stats/top-rutas` | Top de rutas más consultadas. |

## 🚀 Guía de Uso Rápido

Para probar la predicción, puedes usar el siguiente JSON en **Postman** o **Insomnia**:

`POST /api/flights/predict`
```json
{
  "aerolinea": "AA",
  "origen": "JFK",
  "destino": "LAX",
  "fechaPartida": "2025-12-24T15:30",
  "distancia": null
}
```

## 🐳 Instalación con Docker

Asegúrate de tener instalado **Docker** y **Docker Compose**. Desde la raíz del proyecto (donde está el archivo `docker-compose.yml`), ejecuta:

```bash
docker-compose up --build
```

## Esto levantará 3 contenedores:

- postgres_db: DB en puerto 5432.

- spring_backend: API en puerto 8080.

- python_ai: Motor de IA en puerto 8000.

## ⚙️ Variables de Entorno
El backend requiere las siguientes variables configuradas en el entorno (o mediante un archivo `.env`):

| Variable | Descripción | Valor Ejemplo |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://db:5432/flight_on_time` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la DB | `flightuser` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la DB | `flightpass` |
| `AI_SERVICE_URL` | Endpoint del microservicio Python | `http://data-science:8000` |


## ⚠️ Notas de Despliegue (Render)

- Debido a que el proyecto utiliza el plan gratuito de Render, los servicios entran en "modo suspensión" tras 15 minutos de inactividad.

- Warm-up: Se ha configurado un ConnectTimeout de 60 segundos en el DataScienceClient para dar tiempo a que el motor de IA despierte durante la primera consulta.

- Latencia inicial: Es normal que la primera petición tras un periodo de inactividad tarde más de lo habitual mientras los contenedores se activan.

## 🛠️ Próximas Mejoras

Para evolucionar la plataforma, se han identificado las siguientes líneas de desarrollo:

🔐 Seguridad Avanzada: Implementación de autenticación basada en JWT (JSON Web Tokens) para proteger los endpoints de estadísticas.

☁️ Integración de Clima en Tiempo Real: Consumir una API meteorológica para añadir el factor "clima" a la predicción de la IA.

📬 Notificaciones: Sistema de alertas vía Email o Push cuando una predicción guardada cambie drásticamente.

## 📝 Licencia
Este proyecto es de código abierto. Siéntete libre de contribuir o usarlo como base para tus propios desarrollos.