import { fetchPrediccion, fetchTopRutas, fetchStatsSummary } from './api.js';
import { showMsg, actualizarGrafica, renderTopRutas } from './ui.js';
import { configurarInputHora, validarCampos } from './validaciones.js';

async function predecir() {
    const aerolinea = document.getElementById("aerolinea").value.trim().toUpperCase();
    const origen = document.getElementById("origen").value.trim().toUpperCase();
    const destino = document.getElementById("destino").value.trim().toUpperCase();
    const fecha = document.getElementById("fecha").value;
    const hora = document.getElementById("hora").value.trim();

    if (!validarCampos(aerolinea, origen, destino, fecha, hora)) {
        showMsg("#fb7185", "CAMPOS INCOMPLETOS", "Llena todos los campos");
        return;
    }

    showMsg(null, "CONSULTANDO...", "Analizando historial real...");

    try {
        const json = await fetchPrediccion({ aerolinea, origen, destino, fecha_partida: `${fecha}T${hora}` });

        // Lógica de Previsión
        const esPuntual = json.prevision.toLowerCase().includes("tiempo");
        showMsg(esPuntual ? "#4ade80" : "#fb7185", json.prevision.toUpperCase(), "");

        // Círculo y Stats
        const prob = json.porcentajeRetrasosRuta ?? 0;
        const circulo = document.getElementById("puntualidad-global");
        circulo.textContent = prob.toFixed(1) + "%";
        circulo.style.borderColor = prob <= 40 ? "#4ade80" : prob <= 70 ? "#f97316" : "#fb7185";

        document.getElementById("total-vuelos").textContent = json.totalVuelosRuta ?? "--";
        document.getElementById("vuelos-retrasados").textContent = json.vuelosRetrasadosRuta ?? "--";

        // Factores clave
        document.getElementById("factores-clave").innerHTML = `
            <ul style="padding:0; list-style:none;">
                <li>Distancia: <strong>${json.distancia ?? "--"} km</strong></li>
                <li>Ruta: <strong>${origen} → ${destino}</strong></li>
                <li>Probabilidad: <strong>${((json.probabilidad ?? 0) * 100).toFixed(1)}%</strong></li>
                <li>Historial: ${json.recomendacion ?? "Sin datos suficientes"}</li>
            </ul>`;

        actualizarGrafica(json);
        const topData = await fetchTopRutas();
        renderTopRutas(topData);

    } catch (err) {
        showMsg("#fb7185", "ERROR", err.message);
    }
}

// Carga inicial
window.onload = async () => {
    configurarInputHora();
    try {
        const stats = await fetchStatsSummary();
        document.getElementById("total-vuelos").textContent = stats.totalVuelos;
        document.getElementById("vuelos-retrasados").textContent = stats.vuelosRetrasados;

        const top = await fetchTopRutas();
        renderTopRutas(top);
    } catch (e) {
        console.error("Error inicial:", e);
    }
};

// Hacer la función predecir global para el botón HTML si no usas addEventListener
window.predecir = predecir;