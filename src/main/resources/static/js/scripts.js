// /static/js/scripts.js
// Script para inicializar o campo de telefone com seleção de país e validações
const input = document.querySelector("#telefone");
const iti = window.intlTelInput(input, {
    initialCountry: "auto",
    separateDialCode: true,
    utilsScript: "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js"
});

iti.promise.then(() => {
    // Atualizar a legenda do telefone com o formato correto
    input.addEventListener("countrychange", function() {
        const exampleNumber = iti.getSelectedCountryData().exampleNumber;
        document.getElementById("phoneHelp").textContent = `Formato: ${exampleNumber}`;
    });
});

// Função de validação do formulário no lado do cliente
function validateForm() {
    if (!iti.isValidNumber()) {
        alert("Por favor, insira um número de telefone válido.");
        return false;
    }
    return true;
}

// Atualizar o campo de telefone no backend com o código do país
document.querySelector("form").addEventListener("submit", function() {
    input.value = iti.getNumber();
});
