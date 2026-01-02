document.getElementById('predictionForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    // Referencias a elementos UI
    const btn = e.target.querySelector('.btn-predict');
    const resultadoDiv = document.getElementById('resultado');
    const originalBtnContent = btn.innerHTML;

    // 1. Iniciar estado de carga visual
    btn.classList.add('btn-loading');
    btn.innerHTML = `<span class="spinner">↻</span> Analizando vuelo...`;
    resultadoDiv.style.display = 'none'; // Ocultar resultados previos

    // 2. Recolectar datos (deben coincidir con tu FlightRequestDTO.java)
    const datosVuelo = {
        aerolinea: document.getElementById('aerolinea').value,
        origen: document.getElementById('origen').value,
        destino: document.getElementById('destino').value,
        fecha_partida: document.getElementById('fecha').value
    };

    try {
        // 3. Petición a la API de Spring Boot
        const response = await fetch('http://localhost:8080/api/flights/predict', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datosVuelo)
        });

        const data = await response.json();

        // 4. Procesar Respuesta
        resultadoDiv.style.display = 'block';

        if (response.ok) {
            // Caso Éxito
            const porcentaje = (data.probabilidad * 100).toFixed(1);

            resultadoDiv.style.backgroundColor = '#dcfce7';
            resultadoDiv.style.border = '1px solid #22c55e';

            resultadoDiv.innerHTML = `
                <h3 style="color: #166534; margin: 0 0 10px 0;">✨ Análisis Finalizado</h3>
                <p style="margin: 5px 0;">Predicción: <strong style="text-transform: uppercase;">${data.prevision}</strong></p>
                <div class="progress-container">
                    <div class="progress-bar" style="width: ${porcentaje}%"></div>
                </div>
                <small style="color: #166534">Confianza del modelo: ${porcentaje}%</small>
            `;
        } else {
            // Caso Error de Validación o Negocio
            throw new Error(data.message || 'Error en el procesamiento');
        }

    } catch (error) {
        // Caso Error de Conexión o Excepción
        resultadoDiv.style.display = 'block';
        resultadoDiv.style.backgroundColor = '#fee2e2';
        resultadoDiv.style.border = '1px solid #ef4444';
        resultadoDiv.innerHTML = `
            <p style="color: #991b1b; margin: 0;">
                <strong>⚠️ Ups! Algo salió mal</strong><br>
                <small>${error.message}</small>
            </p>
        `;
    } finally {
        // 5. Restaurar botón a su estado original
        btn.classList.remove('btn-loading');
        btn.innerHTML = originalBtnContent;
    }
});