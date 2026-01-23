import { fetchPrediccion, fetchTopRutas, fetchStatsSummary } from './api.js';
import { showMsg, actualizarGrafica, renderTopRutas, mostrarExplicacionIA } from './ui.js';
import { configurarInputHora, validarCampos } from './validaciones.js';

async function predecir() {
    const aerolinea = document.getElementById("aerolinea").value.trim().toUpperCase();
    const origen = document.getElementById("origen").value.trim().toUpperCase();
    const destino = document.getElementById("destino").value.trim().toUpperCase();
    const fecha = document.getElementById("fecha").value;
    const hora = document.getElementById("hora").value.trim();

    // 1. Validar campos localmente (Capa 1)
    const resultadoValidacion = validarCampos(aerolinea, origen, destino, fecha, hora);

    if (!resultadoValidacion.valido) {
        showMsg("#fb7185", "CAMPOS INCORRECTOS", resultadoValidacion.mensaje);
        return;
    }

    showMsg(null, "CONSULTANDO...", "Analizando historial real...");

    try {
        const json = await fetchPrediccion({
            aerolinea,
            origen,
            destino,
            fecha_partida: `${fecha}T${hora}`
        });

        // --- EXPLICABILIDAD (METADATOS) ---
        mostrarExplicacionIA(json.explicabilidad || json.explicacion);

        // 2. Lógica de Previsión
        const esPuntual = json.prevision.toLowerCase().includes("tiempo");
        showMsg(esPuntual ? "#4ade80" : "#fb7185", json.prevision.toUpperCase(), "");

        // 3. Círculo de % de Retraso Global
        const porcentajeRetraso = Math.round(json.porcentajeRetrasosRuta ?? 0);

        const punctualityCircle = document.getElementById("puntualidad-circle");
        const punctualityText = document.getElementById("puntualidad-global");

        if (punctualityCircle && punctualityText) {
            const valor = porcentajeRetraso;

            punctualityCircle.style.setProperty("--percent", valor);
            punctualityText.textContent = `${valor}%`;

            const color =
                valor >= 70 ? "#fb7185" :
                valor >= 40 ? "#f97316" :
                "#4ade80";

            punctualityCircle.style.background = `
                conic-gradient(${color} calc(${valor} * 1%), #334155 0)
            `;
        }



        // 4. Stats numéricas
        document.getElementById("total-vuelos").textContent = json.totalVuelosRuta ?? "--";
        document.getElementById("vuelos-retrasados").textContent = json.vuelosRetrasadosRuta ?? "--";

        // 5. Factores clave
        document.getElementById("factores-clave").innerHTML = `
            <ul style="padding:0; list-style:none; margin:0;">
                <li>Distancia: <strong>${json.distancia ?? "--"} km</strong></li>
                <li>Ruta: <strong>${origen} → ${destino}</strong></li>
            </ul>`;


        // 7. Círculo de Probabilidad de Retraso (NUEVO)
        const delayCircle = document.getElementById("delay-circle");
        const delayText = document.getElementById("delay-percent");

        if (delayCircle && delayText) {
            const probabilidad = (json.probabilidad ?? 0) * 100;
            const valor = Math.round(probabilidad);

            delayCircle.style.setProperty("--percent", valor);
            delayText.textContent = `${valor}%`;
        }


        // 6. Actualizar UI adicional
        actualizarGrafica(json);
        const topData = await fetchTopRutas();
        renderTopRutas(topData);

    } catch (err) {
        console.error("Error capturado:", err);

        // Capa 2: Manejo de errores que vienen del servidor (Java DTO)
        if (err.detallesJava) {
            const camposConError = Object.keys(err.detallesJava);

            camposConError.forEach(campo => {
                // Sincronización con el campo LocalDateTime de Java
                if (campo === "fechaPartida") {
                    document.getElementById("fecha").style.borderColor = "#fb7185";
                    document.getElementById("hora").style.borderColor = "#fb7185";
                } else {
                    const input = document.getElementById(campo);
                    if (input) input.style.borderColor = "#fb7185";
                }
            });

            // Extraemos la leyenda exacta definida en el DTO de Java
            const mensajeServidor = err.detallesJava[camposConError[0]];
            showMsg("#fb7185", "RECHAZADO POR SERVIDOR", mensajeServidor);
        } else {
            showMsg("#fb7185", "ERROR", err.message);
        }
    }
}

// Carga inicial
window.onload = async () => {
    configurarInputHora();
    try {
        const stats = await fetchStatsSummary();
        document.getElementById("total-vuelos").textContent = stats.totalVuelos ?? "0";
        document.getElementById("vuelos-retrasados").textContent = stats.vuelosRetrasados ?? "0";

        const top = await fetchTopRutas();
        renderTopRutas(top);
    } catch (e) {
        console.error("Error inicial:", e);
    }
};

window.predecir = predecir;