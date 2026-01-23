const BASE_URL = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1"
    ? "http://localhost:8080/api"  // Si estás en tu PC (Docker local)
    : "/api";                      // Si estás en Render (Ruta relativa)

export async function fetchPrediccion(data) {
    const res = await fetch(`${BASE_URL}/flights/predict-with-stats`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

    const json = await res.json();

    if (!res.ok) {
        // Si el error es 400, enviamos el JSON completo (el Mapa de errores)
        // para que main.js pueda marcar los bordes rojos.
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
    if (!res.ok) return { totalVuelos: 0, vuelosRetrasados: 0 }; // Manejo básico de error
    return await res.json();
}