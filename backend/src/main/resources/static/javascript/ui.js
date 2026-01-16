// ========================================
// DATOS GLOBALES
// ========================================
let aerolineasDisponibles = [];
let aeropuertosDisponibles = [];

// ========================================
// CARGAR METADATA DEL MODELO
// ========================================
export async function cargarOpcionesDesdeDS() {
    try {
        const response = await fetch('http://127.0.0.1:8000/metadata');
        const data = await response.json();

        aerolineasDisponibles = data.aerolineas || [];
        aeropuertosDisponibles = data.aeropuertos || [];

        console.log(`✅ Metadata cargada: ${aerolineasDisponibles.length} aerolíneas, ${aeropuertosDisponibles.length} aeropuertos`);

        // Inicializar autocompletado para cada campo
        inicializarAutocompletado('aerolinea', aerolineasDisponibles);
        inicializarAutocompletado('origen', aeropuertosDisponibles);
        inicializarAutocompletado('destino', aeropuertosDisponibles);

    } catch (error) {
        console.error("❌ Error cargando metadatos:", error);
    }
}

// ========================================
// AUTOCOMPLETADO INTELIGENTE
// ========================================
function inicializarAutocompletado(inputId, opciones) {
    const input = document.getElementById(inputId);
    const suggestionsDiv = document.getElementById(`suggestions-${inputId}`);

    if (!input || !suggestionsDiv) return;

    let currentFocus = -1;

    // Evento: Cuando el usuario escribe
    input.addEventListener('input', function() {
        const valor = this.value.toUpperCase();
        currentFocus = -1;

        if (!valor) {
            cerrarSugerencias(suggestionsDiv);
            return;
        }

        // Filtrar y ordenar opciones
        const coincidencias = filtrarCoincidencias(valor, opciones);

        if (coincidencias.length === 0) {
            cerrarSugerencias(suggestionsDiv);
            return;
        }

        // Mostrar las primeras 7 sugerencias
        mostrarSugerencias(suggestionsDiv, coincidencias.slice(0, 7), valor, input);
    });

    // Evento: Navegación con teclado (flechas arriba/abajo y Enter)
    input.addEventListener('keydown', function(e) {
        const items = suggestionsDiv.getElementsByClassName('suggestion-item');

        if (e.key === 'ArrowDown') {
            e.preventDefault();
            currentFocus++;
            agregarClaseActiva(items, currentFocus);
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            currentFocus--;
            agregarClaseActiva(items, currentFocus);
        } else if (e.key === 'Enter') {
            e.preventDefault();
            if (currentFocus > -1 && items[currentFocus]) {
                items[currentFocus].click();
            }
        } else if (e.key === 'Escape') {
            cerrarSugerencias(suggestionsDiv);
        }
    });

    // Cerrar sugerencias al hacer clic fuera
    document.addEventListener('click', function(e) {
        if (e.target !== input) {
            cerrarSugerencias(suggestionsDiv);
        }
    });
}

// ========================================
// FILTRAR COINCIDENCIAS
// ========================================
function filtrarCoincidencias(texto, opciones) {
    const textoMin = texto.toLowerCase();

    return opciones
        .filter(opcion => opcion.toLowerCase().includes(textoMin))
        .sort((a, b) => {
            const aMin = a.toLowerCase();
            const bMin = b.toLowerCase();

            // Prioridad 1: Coincidencia exacta al inicio
            const aEmpieza = aMin.startsWith(textoMin);
            const bEmpieza = bMin.startsWith(textoMin);

            if (aEmpieza && !bEmpieza) return -1;
            if (!aEmpieza && bEmpieza) return 1;

            // Prioridad 2: Orden alfabético
            return a.localeCompare(b);
        });
}

// ========================================
// MOSTRAR SUGERENCIAS
// ========================================
function mostrarSugerencias(contenedor, coincidencias, textoIngresado, inputElement) {
    contenedor.innerHTML = '';

    coincidencias.forEach(opcion => {
        const item = document.createElement('div');
        item.className = 'suggestion-item';

        // Resaltar la parte que coincide
        const regex = new RegExp(`(${textoIngresado})`, 'gi');
        item.innerHTML = opcion.replace(regex, '<strong>$1</strong>');

        // Click en sugerencia
        item.addEventListener('click', function() {
            inputElement.value = opcion;
            cerrarSugerencias(contenedor);
            inputElement.focus();
        });

        contenedor.appendChild(item);
    });

    contenedor.classList.add('active');
}

// ========================================
// CERRAR SUGERENCIAS
// ========================================
function cerrarSugerencias(contenedor) {
    contenedor.classList.remove('active');
    contenedor.innerHTML = '';
}

// ========================================
// NAVEGACIÓN CON TECLADO
// ========================================
function agregarClaseActiva(items, indice) {
    if (!items || items.length === 0) return;

    // Remover clase 'selected' de todos
    for (let i = 0; i < items.length; i++) {
        items[i].classList.remove('selected');
    }

    // Ajustar índice circular
    if (indice >= items.length) indice = 0;
    if (indice < 0) indice = items.length - 1;

    // Agregar clase al elemento actual
    if (items[indice]) {
        items[indice].classList.add('selected');
        items[indice].scrollIntoView({ block: 'nearest' });
    }
}

// ========================================
// MOSTRAR RESULTADO DE PREDICCIÓN
// ========================================
export function mostrarResultado(resultado) {
    const box = document.getElementById("result-box");
    const titulo = document.getElementById("res-titulo");
    const desc = document.getElementById("res-desc");

    const probabilidad = Math.round(resultado.probabilidad * 100);

    // Lógica de color según probabilidad
    if (probabilidad >= 50) {
        box.className = "result-card rojo";
        titulo.textContent = `🔴 Riesgo Alto de Retraso (${probabilidad}%)`;
        desc.textContent = `Hay una alta probabilidad de que este vuelo se retrase. Distancia: ${resultado.distancia} km.`;
    } else {
        box.className = "result-card verde";
        titulo.textContent = `🟢 Vuelo A Tiempo (${100 - probabilidad}% puntualidad)`;
        desc.textContent = `Este vuelo tiene buenas probabilidades de salir a tiempo. Distancia: ${resultado.distancia} km.`;
    }

    box.style.display = "flex";
}

// ========================================
// MOSTRAR ERROR
// ========================================
export function mostrarError(mensaje) {
    const box = document.getElementById("result-box");
    const titulo = document.getElementById("res-titulo");
    const desc = document.getElementById("res-desc");

    box.className = "result-card rojo";
    titulo.textContent = "⚠️ Error";
    desc.textContent = mensaje;

    box.style.display = "flex";
}