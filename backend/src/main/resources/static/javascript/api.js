const API_URL = "http://localhost:8080/api/flights/predict";

export async function predecirVuelo(data) {
    const response = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

    const body = await response.json();

    if (!response.ok) {
        throw new Error(body.message || "Error del servidor");
    }

    return body;
}