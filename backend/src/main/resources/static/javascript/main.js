import { validarFormulario } from "./validaciones.js";
import { predecirVuelo } from "./api.js";
import { mostrarMensaje } from "./ui.js";

document.getElementById("btnPredict").addEventListener("click", async () => {

    const data = {
        aerolinea: document.getElementById("aerolinea").value.trim().toUpperCase(),
        origen: document.getElementById("origen").value.trim().toUpperCase(),
        destino: document.getElementById("destino").value.trim().toUpperCase(),
        fecha_partida: document.getElementById("fecha").value
    };

    const error = validarFormulario({
        aerolinea: data.aerolinea,
        origen: data.origen,
        destino: data.destino,
        fecha: data.fecha_partida
    });

    if (error) {
        mostrarMensaje("rojo", "Error", error);
        return;
    }

    mostrarMensaje("", "Consultando...", "Analizando datos del vuelo");

    try {
        const json = await predecirVuelo(data);

        const puntual = json.prevision === "Puntual";

        mostrarMensaje(
            puntual ? "verde" : "rojo",
            puntual ? "✅ Vuelo Puntual" : "⚠️ Posible Retraso",
            `Ruta de ${json.distancia} Km. Confianza: ${(json.probabilidad * 100).toFixed(1)}%`
        );

    } catch (err) {
        mostrarMensaje("rojo", "Error de conexión", err.message);
    }
});