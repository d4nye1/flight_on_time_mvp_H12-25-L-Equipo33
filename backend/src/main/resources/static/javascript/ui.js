export function showMsg(color, titulo, descripcion) {
    const box = document.getElementById("result-box");
    const t = document.getElementById("res-titulo");
    const d = document.getElementById("res-desc");

    box.style.display = "flex";
    t.textContent = titulo;
    d.textContent = descripcion || "";
    t.style.color = color || "#38bdf8";
}

export function actualizarGrafica(json) {
    const ctx = document.getElementById("historico-vuelos").getContext("2d");
    if (window.myChart) window.myChart.destroy();

    const datos = json.historialPuntualidad?.length
        ? json.historialPuntualidad
        : [0, 0, 0, json.porcentajeRetrasosRuta ?? 0];

    const etiquetas = json.etiquetasFechas?.length
        ? json.etiquetasFechas
        : ["-3", "-2", "-1", "Hoy"];

    window.myChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: etiquetas,
            datasets: [{
                data: datos,
                borderColor: "#38bdf8",
                backgroundColor: "rgba(56,189,248,0.15)",
                fill: true,
                tension: 0.4,
                pointRadius: 4
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: { callback: v => v + "%" }
                }
            },
            plugins: { legend: { display: false } }
        }
    });
}

export function renderTopRutas(data) {
    const cont = document.getElementById("top-rutas");
    cont.innerHTML = "";
    if (!data || data.length === 0) {
        cont.innerHTML = "<li style='color:#94a3b8;font-size:0.7rem'>Sin datos disponibles</li>";
        return;
    }
    data.forEach(r => {
        cont.innerHTML += `<li><strong>${r.nombre}</strong><span>${r.cantidad} vuelos retrasados</span></li>`;
    });
}