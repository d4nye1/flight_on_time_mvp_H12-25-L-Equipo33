import { predecirVuelo } from "./api.js";
import { mostrarResultado, mostrarError, cargarOpcionesDesdeDS } from "./ui.js";

// ========================================
// EVENTO DE PREDICCIÓN
// ========================================
document.getElementById("btnPredict").addEventListener("click", async () => {
    const aerolinea = document.getElementById("aerolinea").value.trim();
    const origen = document.getElementById("origen").value.trim();
    const destino = document.getElementById("destino").value.trim();
    const fecha = document.getElementById("fecha").value;

    if (!aerolinea || !origen || !destino || !fecha) {
        mostrarError("Por favor, completa todos los campos del formulario.");
        return;
    }

    const datosVuelo = {
        aerolinea: aerolinea.toUpperCase(),
        origen: origen.toUpperCase(),
        destino: destino.toUpperCase(),
        fecha_partida: fecha
    };

    console.log("🚀 Iniciando predicción con:", datosVuelo);

    try {
        const resultado = await predecirVuelo(datosVuelo);
        mostrarResultado(resultado);
    } catch (error) {
        console.error("❌ Error en predicción:", error);

        // Mostrar el mensaje de error específico si está disponible
        let mensajeError = "Error al conectar con el servicio de predicción.";

        if (error.message) {
            mensajeError = error.message;
        }

        // Si es un error de red
        if (error.message.includes("Failed to fetch") || error.message.includes("NetworkError")) {
            mensajeError = "No se pudo conectar con el servidor. Verifica que el backend esté ejecutándose en http://localhost:8080";
        }

        mostrarError(mensajeError);
    }
});

// ========================================
// CARGAR METADATA AL INICIAR
// ========================================
document.addEventListener("DOMContentLoaded", () => {
    console.log("🚀 Iniciando FlightOnTime...");
    cargarOpcionesDesdeDS();
});