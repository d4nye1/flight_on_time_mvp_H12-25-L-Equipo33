const BASE_URL = "http://localhost:8080/api";

export async function fetchPrediccion(data) {
    const res = await fetch(`${BASE_URL}/flights/predict-with-stats`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });
    const json = await res.json();
    if (!res.ok) throw new Error(json.message || "Error en backend");
    return json;
}

export async function fetchTopRutas() {
    const res = await fetch(`${BASE_URL}/stats/top-rutas`);
    if (!res.ok) throw new Error("Error cargando top rutas");
    return await res.json();
}

export async function fetchStatsSummary() {
    const res = await fetch(`${BASE_URL}/stats/summary`);
    return await res.json();
}