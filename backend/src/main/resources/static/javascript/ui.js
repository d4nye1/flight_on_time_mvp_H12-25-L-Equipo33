const resultBox = document.getElementById("result-box");
const resTitulo = document.getElementById("res-titulo");
const resDesc = document.getElementById("res-desc");

export function mostrarMensaje(tipo, titulo, descripcion) {
    resultBox.className = `result-card ${tipo}`;
    resTitulo.textContent = titulo;
    resDesc.textContent = descripcion;
    resultBox.style.display = "flex";
}