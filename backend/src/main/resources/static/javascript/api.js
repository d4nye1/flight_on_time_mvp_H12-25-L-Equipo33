const BASE_URL = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1"
    ? "http://localhost:8080/api"
    : "/api";

export async function fetchPrediccion(data) {
    const res = await fetch(`${BASE_URL}/flights/predict-with-stats`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

    const json = await res.json();

    if (!res.ok) {
        if (res.status === 400) {
            const errorValidacion = new Error("Error de validación en servidor");
            errorValidacion.detallesJava = json;
            throw errorValidacion;
        }
        throw new Error(json.message || "Error en backend");
    }
    return json;
}

export async function fetchTopRutas() {
    const res = await fetch(`${BASE_URL}/stats/top-rutas`);
    if (!res.ok) throw new Error("Error cargando top rutas");
    return await res.json();
}

export async function fetchStatsSummary() {
    const res = await fetch(`${BASE_URL}/stats/summary`);
    if (!res.ok) return { totalVuelos: 0, vuelosRetrasados: 0 };
    return await res.json();
}