document.getElementById('predictionForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const resultadoDiv = document.getElementById('resultado');

    // 1. Obtener datos del formulario
    const datosVuelo = {
        aerolinea: document.getElementById('aerolinea').value,
        origen: document.getElementById('origen').value,
        destino: document.getElementById('destino').value,
        fecha_partida: document.getElementById('fecha').value
    };

    // 2. Mostrar estado de carga
    resultadoDiv.style.display = 'block';
    resultadoDiv.innerHTML = 'Procesando con IA...';
    resultadoDiv.style.backgroundColor = '#f1f5f9';

    try {
        // 3. Petición a tu API Spring Boot
        const response = await fetch('http://localhost:8080/api/flights/predict', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datosVuelo)
        });

        const data = await response.json();

        if (response.ok) {
            // 4. Mostrar resultado exitoso
            resultadoDiv.style.backgroundColor = '#dcfce7';
            resultadoDiv.innerHTML = `
                <h3 style="color: #166534">¡Predicción Lista!</h3>
                <p>Estado: <strong>${data.prevision}</strong></p>
                <p>Probabilidad: ${(data.probabilidad * 100).toFixed(2)}%</p>
            `;
        } else {
            // 5. Mostrar error de validación/negocio
            resultadoDiv.style.backgroundColor = '#fee2e2';
            resultadoDiv.innerHTML = `<span style="color: #991b1b">Error: ${data.message}</span>`;
        }
    } catch (error) {
        // 6. Error de conexión (ej. API apagada)
        resultadoDiv.style.backgroundColor = '#fee2e2';
        resultadoDiv.innerHTML = '<span style="color: #991b1b">Error de conexión con el servidor</span>';
    }
});