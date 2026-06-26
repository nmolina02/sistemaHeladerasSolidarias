document.getElementById('username_input').addEventListener('input', checkInputs);
document.getElementById('password_input').addEventListener('input', checkInputs);
document.getElementById('password_input_2').addEventListener('input', checkInputs);
document.getElementById('security_code_input').addEventListener('input', checkInputs);

function checkInputs() {
    const usernameLength = document.getElementById('username_input').value.length;
    const password = document.getElementById('password_input').value;
    const password2 = document.getElementById('password_input_2').value;
    const changePasswordButton = document.getElementById('change_password_button');

    const securityCodeLength = document.getElementById('security_code_input').value.length;
    const changesConfirmedButton = document.getElementById('changes_confirmed_button');

    if (usernameLength >= 4 && password.length >= 8 && password === password2) {
        changePasswordButton.disabled = false;
    } else {
        changePasswordButton.disabled = true;
    }

    if (securityCodeLength > 0) {
        changesConfirmedButton.disabled = false;
    } else {
        changesConfirmedButton.disabled = true;
    }
}

document.getElementById('change_password_button').addEventListener('click', function(event){
    event.preventDefault();
    data = {
        username: document.getElementById('username_input').value,
    };
    enviarSolicitudCambioPassword(data);
});

document.getElementById('changes_confirmed_button').addEventListener('click', function(event){
    event.preventDefault();
    const password_input = document.getElementById('password_input').value;
    const encryptedPassword = CryptoJS.SHA256(password_input).toString();
    data = {
        username: document.getElementById('username_input').value,
        password: encryptedPassword,
        codigoRecuperacion: document.getElementById('security_code_input').value,
    };
    enviarConfirmacionCambio(data);
});

function enviarConfirmacionCambio(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/confirmacionContrasenia', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    })
    .then(response => {
        // Revisa si la respuesta tiene éxito
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(respuesta => {
        if (respuesta === "Recuperación exitosa") {
            mostrarAlertaExito('Contraseña recuperada');
        } else {
            mostrarAlertaError('Error al recuperar la contraseña');
        }
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

document.getElementById('back_button').addEventListener('click', function(event){
    event.preventDefault();
    window.location.href = 'login.html';
});

document.addEventListener("DOMContentLoaded", function () {
    const passwordInput = document.getElementById("password_input");
    const togglePassword = document.getElementById("toggle_password");
    const passwordIcon = document.getElementById("password_icon");

    // Alternar visibilidad de la contraseña
    togglePassword.addEventListener("click", () => {
        const isPasswordVisible = passwordInput.type === "password";
        passwordInput.type = isPasswordVisible ? "text" : "password";
        passwordIcon.className = isPasswordVisible ? "fas fa-eye-slash" : "fas fa-eye"; // Cambiar ícono
    });
});

function enviarSolicitudCambioPassword(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/solicitudRecuperoIngreso', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    })
    .then(response => {
        // Revisa si la respuesta tiene éxito
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(respuesta => {
        if (respuesta === "Envio solicitud por mail") {
            mostrarAlertaInformativa('Se ha enviado un mail a su casilla registrada');
        } else if (respuesta === "Envio solicitud por whatsapp") {
            mostrarAlertaInformativa('Se ha enviado un mensaje a su número registrado');
        } else {
            mostrarAlertaError('Error al enviar la solicitud');
        }
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function mostrarAlertaExito(mensaje) {
    Swal.fire({
        title: '¡Éxito!',
        text: mensaje,
        icon: 'success',
        confirmButtonText: 'Aceptar',
        customClass: {
            confirmButton: 'custom-confirm-button'
        },
        timer: 3000, // se cierra automáticamente en 3 segundos
        timerProgressBar: true,
    });
    setTimeout(() => {
        window.location.href = 'login.html';
    }, 3000); // son solo 3 segundos
}

function mostrarAlertaError(mensaje) {
    Swal.fire({
        title: '¡Error!',
        text: mensaje,
        icon: 'error',
        confirmButtonText: 'Intentar nuevamente',
        customClass: {
            confirmButton: 'custom-error-button'
        },
        timer: 3000, // se cierra automáticamente en 3 segundos
        timerProgressBar: true,
    });
}

function mostrarAlertaInformativa(mensaje) {
    Swal.fire({
        title: 'Información',
        text: mensaje,
        icon: 'info',
        confirmButtonText: 'Entendido',
        customClass: {
            confirmButton: 'custom-info-button'
        },
        timer: 3000, // se cierra automáticamente en 3 segundos
        timerProgressBar: true,
    });
}