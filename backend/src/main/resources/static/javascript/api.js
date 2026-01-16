export async function predecirVuelo(datos) {
    try {
        console.log("📤 Enviando datos al backend:", datos);

        const response = await fetch('http://localhost:8080/api/flights/predict', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });

        console.log("📥 Respuesta recibida:", response.status, response.statusText);

        // Si la respuesta no es OK, intentar extraer el mensaje de error
        if (!response.ok) {
            let mensajeError = "Error en la respuesta del servidor";

            try {
                const errorData = await response.json();
                console.error("❌ Error del servidor:", errorData);

                // El backend puede devolver diferentes formatos de error
                if (errorData.message) {
                    mensajeError = errorData.message;
                } else if (errorData.error) {
                    mensajeError = errorData.error;
                } else if (errorData.detail) {
                    mensajeError = errorData.detail;
                }
            } catch (e) {
                console.error("No se pudo parsear el error:", e);
            }

            throw new Error(mensajeError);
        }

        const resultado = await response.json();
        console.log("✅ Predicción exitosa:", resultado);

        return resultado;

    } catch (error) {
        console.error("❌ Error en predecirVuelo:", error);
        throw error;
    }
}