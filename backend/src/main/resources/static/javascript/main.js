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
        actualizarPanelLateral({
                aerolinea: aerolinea,
                origen: origen,
                destino: destino,
                fecha: fecha,
                hora: hora,
                distancia: json.distancia || 1000, // Si el JSON no trae distancia, usa 1000 por defecto
                prevision: json.prevision
            });

        mostrarExplicacionIA(json.explicabilidad || json.explicacion);

        const esPuntual = json.prevision.toLowerCase().includes("tiempo");
        showMsg(esPuntual ? "#4ade80" : "#fb7185", json.prevision.toUpperCase(), "");

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

        document.getElementById("total-vuelos").textContent = json.totalVuelosRuta ?? "--";
        document.getElementById("vuelos-retrasados").textContent = json.vuelosRetrasadosRuta ?? "--";

        document.getElementById("factores-clave").innerHTML = `
            <ul style="padding:0; list-style:none; margin:0;">
                <li>Distancia: <strong>${json.distancia ?? "--"} km</strong></li>
                <li>Ruta: <strong>${origen} → ${destino}</strong></li>
            </ul>`;

        const delayCircle = document.getElementById("delay-circle");
        const delayText = document.getElementById("delay-percent");

        if (delayCircle && delayText) {
            const probabilidad = (json.probabilidad ?? 0) * 100;
            const valor = Math.round(probabilidad);

            delayCircle.style.setProperty("--percent", valor);
            delayText.textContent = `${valor}%`;
        }

        actualizarGrafica(json);
        const topData = await fetchTopRutas();
        renderTopRutas(topData);

    } catch (err) {
        console.error("Error capturado:", err);

        if (err.detallesJava) {
            const camposConError = Object.keys(err.detallesJava);

            camposConError.forEach(campo => {
                if (campo === "fechaPartida") {
                    document.getElementById("fecha").style.borderColor = "#fb7185";
                    document.getElementById("hora").style.borderColor = "#fb7185";
                } else {
                    const input = document.getElementById(campo);
                    if (input) input.style.borderColor = "#fb7185";
                }
            });
            const mensajeServidor = err.detallesJava[camposConError[0]];
            showMsg("#fb7185", "RECHAZADO POR SERVIDOR", mensajeServidor);
        } else {
            showMsg("#fb7185", "ERROR", err.message);
        }
    }
}

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

window.toggleMenu = function() {
    const menu = document.getElementById("side-menu");
    // Si el menú está cerrado (ancho 0), ábrelo a 350px. Si no, ciérralo.
    if (!menu.style.width || menu.style.width === "0px") {
        menu.style.width = "350px";
    } else {
        menu.style.width = "0px";
    }
}

function actualizarPanelLateral(datos) {
    // 1. Info básica
    document.getElementById("side-info-principal").textContent = `${datos.aerolinea}, Vuelo 1 | ${datos.origen} → ${datos.destino}`;
    document.getElementById("side-fecha-vuelo").textContent = datos.fecha;
    document.getElementById("side-origen-label").textContent = datos.origen;
    document.getElementById("side-destino-label").textContent = datos.destino;
    document.getElementById("side-hora-salida").textContent = datos.hora + " HRS";

    // 2. Puerta aleatoria (Ejemplo: Gate B-5)
    const gate = "ABC"[Math.floor(Math.random() * 3)] + "-" + (Math.floor(Math.random() * 15) + 1);
    document.getElementById("side-puerta").textContent = "T1 / " + gate;

    // 3. CALCULO DE LLEGADA (Física)
    const velocidad = 800; // km/h
    const horasDeVuelo = datos.distancia / velocidad;

    // Convertimos la hora de salida (ej: "14:30") a minutos totales
    const [h, m] = datos.hora.split(':').map(Number);
    let minutosTotales = (h * 60) + m + Math.round(horasDeVuelo * 60);

    // Convertimos esos minutos totales otra vez a formato reloj HH:mm
    const horasLlegada = Math.floor((minutosTotales / 60) % 24);
    const minsLlegada = minutosTotales % 60;
    const horaFinal = `${horasLlegada.toString().padStart(2, '0')}:${minsLlegada.toString().padStart(2, '0')}`;

    document.getElementById("side-hora-llegada").textContent = horaFinal + " HRS (Est.)";

    // 4. Radar
    const statusTag = document.getElementById("side-radar-status");
    const radarIcon = document.getElementById("side-radar-icon");
    const esPuntual = datos.prevision.toLowerCase().includes("tiempo") || datos.prevision.toLowerCase().includes("puntual");

    statusTag.textContent = `${datos.aerolinea} - ${datos.prevision.toUpperCase()}`;
    statusTag.className = esPuntual ? "airline-tag green" : "airline-tag red";
    radarIcon.className = esPuntual ? "plane-icon green" : "plane-icon red";
}
// Función para mostrar/ocultar el equipo en el footer
window.toggleTeam = function() {
    const panel = document.getElementById('team-panel');
    if (panel) {
        panel.style.display = panel.style.display === 'none' ? 'block' : 'none';
    }
};