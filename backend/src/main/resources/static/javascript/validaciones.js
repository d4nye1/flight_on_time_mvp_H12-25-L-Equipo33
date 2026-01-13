export function validarFormulario({ aerolinea, origen, destino, fecha }) {

    if (!aerolinea || !origen || !destino || !fecha) {
        return "Asegúrate de completar todos los campos";
    }

    if (aerolinea.length !== 2) {
        return "El código de aerolínea debe tener 2 letras (IATA)";
    }

    if (origen.length !== 3 || destino.length !== 3) {
        return "Los aeropuertos deben tener 3 letras (IATA)";
    }

    const fechaSeleccionada = new Date(fecha);
    if (fechaSeleccionada <= new Date()) {
        return "No se pueden predecir vuelos pasados";
    }

    return null; // todo OK
}
