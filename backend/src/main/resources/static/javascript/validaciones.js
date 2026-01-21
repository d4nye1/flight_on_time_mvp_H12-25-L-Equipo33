/* ===============================
    FORMATEO INPUT HORA Y VALIDACIONES
================================ */
export function configurarInputHora() {
    const horaInput = document.getElementById("hora");
    if (horaInput) {
        horaInput.addEventListener("input", (e) => {
            let valor = e.target.value.replace(/\D/g, "");
            if (valor.length > 4) valor = valor.slice(0, 4);
            if (valor.length >= 3) {
                valor = valor.slice(0, 2) + ":" + valor.slice(2);
            }
            e.target.value = valor;
        });
    }
}

export function validarCampos(aerolinea, origen, destino, fecha, hora) {
    const campos = {
        aerolinea: document.getElementById("aerolinea"),
        origen: document.getElementById("origen"),
        destino: document.getElementById("destino"),
        fecha: document.getElementById("fecha"),
        hora: document.getElementById("hora")
    };

    // 1. Limpiar estilos previos
    Object.values(campos).forEach(el => { if (el) el.style.borderColor = ""; });

    // 2. Validaciones de presencia (Leyendas sincronizadas con Java DTO)
    if (!aerolinea) {
        marcarError(campos.aerolinea);
        return { valido: false, mensaje: "La aerolínea es obligatoria" };
    }
    if (!origen) {
        marcarError(campos.origen);
        return { valido: false, mensaje: "El origen es obligatorio" };
    }
    if (!destino) {
        marcarError(campos.destino);
        return { valido: false, mensaje: "El destino es obligatorio" };
    }
    if (!fecha) {
        marcarError(campos.fecha);
        return { valido: false, mensaje: "La fecha de partida es obligatoria" };
    }

    // 3. Validación específica de Hora Partida (Evita error genérico en el servidor)
    if (!hora || hora.length < 5) {
        marcarError(campos.hora);
        return { valido: false, mensaje: "Ingrese el campo hora correctamente (HH:mm)" };
    }

    // Validación extra de coherencia horaria (00:00 - 23:59)
    const [h, m] = hora.split(":").map(Number);
    if (h > 23 || m > 59) {
        marcarError(campos.hora);
        return { valido: false, mensaje: "Hora inválida: Use el formato 24h (00:00 - 23:59)" };
    }

    // 4. Validación de Datalist (Coherencia de códigos IATA)
    const listaAero = document.getElementById("lista-aerolineas");
    const listaPuertos = document.getElementById("lista-aeropuertos");

    if (listaAero && listaPuertos) {
        const aerolineasValidas = Array.from(listaAero.options).map(opt => opt.value.toUpperCase());
        const aeropuertosValidos = Array.from(listaPuertos.options).map(opt => opt.value.toUpperCase());

        if (!aerolineasValidas.includes(aerolinea)) {
            marcarError(campos.aerolinea);
            return { valido: false, mensaje: "Ingrese una aerolínea válida de la lista" };
        }
        if (!aeropuertosValidos.includes(origen)) {
            marcarError(campos.origen);
            return { valido: false, mensaje: "Ingrese un origen válido de la lista" };
        }
        if (!aeropuertosValidos.includes(destino)) {
            marcarError(campos.destino);
            return { valido: false, mensaje: "Ingrese un destino válido de la lista" };
        }
    }

    // 5. Validación de Negocio: No volar al mismo sitio
    if (origen === destino) {
        marcarError(campos.origen);
        marcarError(campos.destino);
        return { valido: false, mensaje: "El aeropuerto de origen y destino no pueden ser iguales" };
    }

    return { valido: true };
}

function marcarError(el) {
    if (el) el.style.borderColor = "#fb7185";
}