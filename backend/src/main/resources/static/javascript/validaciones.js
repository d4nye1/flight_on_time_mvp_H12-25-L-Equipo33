/* ===============================
    FORMATEO INPUT HORA Y VALIDACIONES
================================ */
export function configurarInputHora() {
    const horaInput = document.getElementById("hora");
    if (horaInput) {
        horaInput.addEventListener("input", (e) => {
            let valor = e.target.value.replace(/\D/g, "");
            if (valor.length > 4) valor = valor.slice(0, 4);
            if (valor.length >= 3) valor = valor.slice(0, 2) + ":" + valor.slice(2);
            e.target.value = valor;
        });
    }
}

export function validarCampos(aerolinea, origen, destino, fecha, hora) {
    if (!aerolinea || !origen || !destino || !fecha || !hora) {
        return false;
    }
    return true;
}