document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        const profileOptions = document.getElementById('profile_options');
        profileOptions.style.display = 'block';
    }
});

document.getElementById('logout_button').addEventListener('click', function(event){
    logout(event);
});

function logout(event) {
    event.preventDefault();
    const userCircle = document.getElementById('user_circle');
    localStorage.removeItem('loggedInUser');
    userCircle.style.display = 'none';
    window.location.href = '../login.html';
}

let menuOpen = false;
document.getElementById('menu-btn').addEventListener('click', () => {
    const navLinks = document.getElementById('nav-links');
    if (menuOpen){
        navLinks.style.display = 'none';
        menuOpen = false;
    }
    else {
        navLinks.style.display = 'block';
        menuOpen = true;
    }
});

window.addEventListener('resize', resetStyles);
window.addEventListener('load', resetStyles);

function resetStyles() {
    const navLinks = document.getElementById('nav-links');
    // Reiniciar estilos al salir de la vista móvil
    if (window.innerWidth > 913) {
        navLinks.style.display = 'flex';
        menuOpen = false;
    }
    else {
        navLinks.style.display = 'none';
    }
}

function deshabilitarBotonTarjetaSolicitada() {
    const tarjetaSolicitadaButton = document.getElementById('tarjetaSolicitada');
    tarjetaSolicitadaButton.disabled = true;
    tarjetaSolicitadaButton.style.cursor = 'not-allowed';
    tarjetaSolicitadaButton.style.backgroundColor = '#d6d6d6';
    const newButton = tarjetaSolicitadaButton.cloneNode(true);
    tarjetaSolicitadaButton.parentNode.replaceChild(newButton, tarjetaSolicitadaButton);
}

document.getElementById('tarjetaSolicitada').addEventListener('click', tarjetaSolicitada);

function tarjetaSolicitada() {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    enviarConfirmacionSolicitud(loggedInUser);
}

function enviarConfirmacionSolicitud(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/solicitudTarjeta', {
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
        mostrarAlertaExito('Tarjeta de colaborador generada');
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function enviarSolicitudPerfil(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/miPerfil', {
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
        updateDataProfile(respuesta.username, respuesta.points);
        if (respuesta.card !== 'null') {
            deshabilitarBotonTarjetaSolicitada();
        }
        
        const usernameInput = document.getElementById('name_input');
        const emailInput = document.getElementById('email_input');
        const contactCodeInput = document.getElementById('contact_code_input');
        const phoneInput = document.getElementById('phone_input');
        const addressInput = document.getElementById('address_input');

        const whatsappButton = document.getElementById('whatsapp_button');
        const telegramButton = document.getElementById('telegram_button');

        usernameInput.value = respuesta.username;
        if (respuesta.email !== 'null')
            emailInput.value = respuesta.email;
        if (respuesta.phone !== 'null') {
            contactCodeInput.value = respuesta.phone.substring(0, 3);
            phoneInput.value = respuesta.phone.substring(3);
            if (respuesta.whatsapp === respuesta.phone) {
                whatsappButton.classList.toggle('active');
                activeButton(whatsappButton);
            }
            if (respuesta.telegram === respuesta.phone) {
                telegramButton.classList.toggle('active');
                activeButton(telegramButton);
            }
        }

        const contactCode = respuesta.phone.substring(0, 3);
        const options = contactCodeInput.options;
        for (let i = 0; i < options.length; i++) {
            if (options[i].value === contactCode) {
                options[i].selected = true;
                break;
            }
        }
        addressInput.value = respuesta.address;
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        enviarSolicitudPerfil(loggedInUser);
        if (loggedInUser.userType === 'J') {
            document.getElementById('tarjetaSolicitada').style.display = 'none';
        }
    }
});

function updateDataProfile(name, points) {
    const profileNameElement = document.getElementById("profile_name");
    const profileNameElement2 = document.getElementById("profile_name_2");
    const profilePointsElement = document.getElementById("profile_points");
    const profilePointsElement2 = document.getElementById("profile_points_2");

    if (profileNameElement && profileNameElement2) {
        if (name.length > 10) {
            name = name.substring(0, 7) + '...';
        }
        profileNameElement.textContent = name;
        profileNameElement2.textContent = name;
        profilePointsElement.textContent = points + ' puntos';
        profilePointsElement2.textContent = points + ' puntos';
        solicitarImagenPerfil(name + '.png');
    }
}

function solicitarImagenPerfil(username) {
    fetch('http://heladerassolidarias.myvnc.com:4567/receptorDeArchivos/imagenUsuario/' + username, {
        method: 'GET',
    })
    .then(response => response.blob())
    .then(respuesta => {
        const reader = new FileReader();
        reader.onloadend = () => {
            const base64data = reader.result;
            const profilePictureElement = document.getElementById("profile_pic");
            profilePictureElement.src = base64data;
        };
        reader.readAsDataURL(respuesta);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

document.addEventListener('DOMContentLoaded', changeRol);

function changeRol() {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    const changeRolOption = document.getElementById('change_rol');
    if (loggedInUser && loggedInUser.userColaboradorRol === 'Colaborador' && loggedInUser.userTecnicoRol === 'Técnico') {
        if (loggedInUser.rolActivo === 'Colaborador') {
            changeRolOption.style.display = 'block';
            changeRolOption.textContent = 'Cambiar a Técnico';
        } else {
            changeRolOption.style.display = 'block';
            changeRolOption.textContent = 'Cambiar a Colaborador';
        }
    } else {
        changeRolOption.style.display = 'none';
    }
}

document.getElementById('change_rol').addEventListener('click', function(event) {
    event.preventDefault();
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    const changeRolOption = document.getElementById('change_rol');
    if (loggedInUser.rolActivo === 'Colaborador') {
        loggedInUser.rolActivo = 'Técnico';
        changeRolOption.textContent = 'Cambiar a Técnico';
    } else {
        loggedInUser.rolActivo = 'Colaborador';
        changeRolOption.textContent = 'Cambiar a Colaborador';
    }
    localStorage.setItem('loggedInUser', JSON.stringify(loggedInUser));
    window.location.href = 'configAccount.html';
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

document.addEventListener('DOMContentLoaded', function () {
    // Vinculamos los eventos para el ícono de lápiz
    document.getElementById('edit_username_icon').addEventListener('click', function () {
        enableSectionEdit('username-section', 'save_username_button', 'cancel_username_button');
    });
    document.getElementById('edit_contact_icon').addEventListener('click', function () {
        enableSectionEdit('contact-section', 'save_contact_button', 'cancel_contact_button');
    });
    document.getElementById('edit_address_icon').addEventListener('click', function () {
        enableSectionEdit('address-section', 'save_address_button', 'cancel_address_button');
    });
    document.getElementById('edit_password_icon').addEventListener('click', function () {
        enableSectionEdit('password-section', 'save_password_button', 'cancel_password_button');
    });

    // Vinculamos los eventos de los botones de guardar con confirmación
    document.getElementById('save_username_button').addEventListener('click', function () {
        confirmChanges('username-section', 'save_username_button', 'cancel_username_button');
    });
    document.getElementById('save_contact_button').addEventListener('click', function () {
        confirmChanges('contact-section', 'save_contact_button', 'cancel_contact_button');
    });
    document.getElementById('save_address_button').addEventListener('click', function () {
        confirmChanges('address-section', 'save_address_button', 'cancel_address_button');
    });
    document.getElementById('save_password_button').addEventListener('click', function () {
        confirmChanges('password-section', 'save_password_button', 'cancel_password_button');
    });

    // Vinculamos los eventos de cancelar cambios
    document.getElementById('cancel_username_button').addEventListener('click', function () {
        cancelSectionChanges('username-section', 'save_username_button', 'cancel_username_button');
    });
    document.getElementById('cancel_contact_button').addEventListener('click', function () {
        cancelSectionChanges('contact-section', 'save_contact_button', 'cancel_contact_button');
    });
    document.getElementById('cancel_address_button').addEventListener('click', function () {
        cancelSectionChanges('address-section', 'save_address_button', 'cancel_address_button');
    });
    document.getElementById('cancel_password_button').addEventListener('click', function () {
        cancelSectionChanges('password-section', 'save_password_button', 'cancel_password_button');
    });
});

// Función para validar la contraseña con SweetAlert2
function passwordValidation() {
    const passwordInput = document.getElementById('password_input');
    const passwordConfirmInput = document.getElementById('password_confirm_input');
    const password = passwordInput.value;
    const passwordConfirm = passwordConfirmInput.value;

    // Validación de longitud de contraseña
    if (password.length < 8) {
        mostrarAlertaError('La contraseña debe tener al menos 8 caracteres');
        return false; // Fallo en la validación
    }

    // Validación de coincidencia de contraseñas
    if (password !== passwordConfirm) {
        mostrarAlertaError('Las contraseñas no coinciden');
        return false; // Fallo en la validación
    }

    // Si pasa todas las validaciones
    return true;
}

// Función para mostrar el mensaje de confirmación con la opción de "No mostrar de nuevo"
function confirmChanges(sectionId, saveButtonId, cancelButtonId) {
    // Mostrar el cuadro de confirmación
    Swal.fire({
        title: '¿Guardar cambios?',
        text: 'Esta acción sobrescribirá los datos actuales.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Guardar',
        cancelButtonText: 'Cancelar',
    }).then((result) => {
        if (result.isConfirmed) {
            saveSectionChanges(sectionId, saveButtonId, cancelButtonId);
            Swal.fire(
                'Guardado',
                'Los cambios han sido guardados exitosamente.',
                'success'
            );
        } else {
            Swal.fire(
                'Cancelado',
                'No se realizaron cambios.',
                'info'
            );
        }
    });
}

// Función para guardar cambios
function saveSectionChanges(sectionId, saveButtonId, cancelButtonId) {
    const section = document.getElementById(sectionId);
    section.querySelectorAll('input, select').forEach(input => {
        input.dataset.originalValue = input.value; // Actualizamos el valor original
        input.setAttribute('readonly', 'readonly');
        input.setAttribute('disabled', 'disabled');
    });
    // Ocultamos los botones de acción
    document.getElementById(saveButtonId).style.display = 'none';
    document.getElementById(cancelButtonId).style.display = 'none';

    if (saveButtonId === 'save_username_button') {
        const nameInput = document.getElementById('name_input').value;
        cambiarDatosPersonales(nameInput, "username");
    } else if (saveButtonId === 'save_contact_button') {
        const emailInput = document.getElementById('email_input').value;
        const contactCodeInput = document.getElementById('contact_code_input').value;
        const phoneInput = document.getElementById('phone_input').value;

        let userWhatsapp = '';
        let userTelegram = '';

        const whatsappButton = document.getElementById('whatsapp_button');

        if (isButtonActive(whatsappButton)){
            userWhatsapp = 'true';
        } else {
            userWhatsapp = 'false';
        }
        
        const telegramButton = document.getElementById('telegram_button');

        if (isButtonActive(telegramButton)){
            userTelegram = 'true';
        } else {
            userTelegram = 'false';
        }
       
        const contactData = {
            email: emailInput,
            phone: contactCodeInput + phoneInput,
            whatsapp: userWhatsapp,
            telegram: userTelegram,
        };
        cambiarDatosPersonales(contactData, "contactData");
    } else if (saveButtonId === 'save_address_button') {
        const addressInput = document.getElementById('address_input').value;
        cambiarDatosPersonales(addressInput, "address");
    } else if (saveButtonId === 'save_password_button') {
        const passwordInput = document.getElementById('password_input').value;
        const encryptedPassword = CryptoJS.SHA256(passwordInput).toString();
        cambiarDatosPersonales(encryptedPassword, "dataKey");
    }

}

// Función para habilitar la edición (sin cambios)
function enableSectionEdit(sectionId, saveButtonId, cancelButtonId) {
    const section = document.getElementById(sectionId);
    section.querySelectorAll('input, select').forEach(input => {
        input.removeAttribute('readonly');
        input.removeAttribute('disabled');
        input.dataset.originalValue = input.value;
    });
    document.getElementById(saveButtonId).style.display = 'inline-block';
    document.getElementById(cancelButtonId).style.display = 'inline-block';
}

// Función para cancelar cambios (sin cambios)
function cancelSectionChanges(sectionId, saveButtonId, cancelButtonId) {
    const section = document.getElementById(sectionId);
    section.querySelectorAll('input, select').forEach(input => {
        input.value = input.dataset.originalValue; // Revertimos al valor original
        input.setAttribute('readonly', 'readonly');
        input.setAttribute('disabled', 'disabled');
    });
    // Ocultamos los botones de acción
    document.getElementById(saveButtonId).style.display = 'none';
    document.getElementById(cancelButtonId).style.display = 'none';
}

function cambiarDatosPersonales(dato, tipoDato) {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    const data = {
        usuario: loggedInUser,
        tipoDato: tipoDato,
        dato: dato,
    };
    enviarCambioDeDatosPersonales(data);
}

function enviarCambioDeDatosPersonales(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/configuracion', {
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
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        const profileOptions = document.getElementById('profile_options');
        profileOptions.style.display = 'block';
        enviarSolicitudPerfil(loggedInUser);

        if (loggedInUser.userType === 'J') {
            document.getElementById('nav-link-falla-heladera').style.display = 'none';
            document.getElementById('nav-link-suscripcion-heladeras').style.display = 'none';
        }

        const profilePoints = document.getElementById('profile_points');
        const profilePoints2 = document.getElementById('profile_points_2');
        const colaboratorAction1 = document.getElementById('canjearProducto');
        const colaboratorAction2 = document.getElementById('tarjetaSolicitada');
        const colaboratorAction3 = document.getElementById('misColaboraciones');

        const navLinkInicio = document.getElementById('nav-link-inicio');
        const navLinkColaboraciones = document.getElementById('nav-link-colaboraciones');
        const navLinkFallaHeladera = document.getElementById('nav-link-falla-heladera');
        const navLinkCargarCsv = document.getElementById('nav-link-cargar-csv');
        const navLinkSuscripcionHeladeras = document.getElementById('nav-link-suscripcion-heladeras');

        const mainIcon = document.getElementById('main_icon');
        const navLinkTecnicoIncidente = document.getElementById('nav-link-tecnico-incidente');
        const navLinkTecnicoVisita = document.getElementById('nav-link-tecnico-visita');
        const navLinkAdmin = document.getElementById('nav-link-admin');

        if (loggedInUser.userColaboradorRol !== 'Colaborador') {            
            profilePoints.style.display = 'none';
            profilePoints2.style.display = 'none';
            colaboratorAction1.style.display = 'none';
            colaboratorAction2.style.display = 'none';
            colaboratorAction3.style.display = 'none';

            navLinkColaboraciones.style.display = 'none';
            navLinkFallaHeladera.style.display = 'none';
            navLinkSuscripcionHeladeras.style.display = 'none';

            if (loggedInUser.userAdminRol === 'Administrador') {
                navLinkAdmin.style.display = 'block';
                navLinkInicio.setAttribute('href', '../indexAdmin.html');
                mainIcon.setAttribute('href', '../indexAdmin.html');
                navLinkCargarCsv.style.display = 'block';
            } else {
                navLinkTecnicoIncidente.style.display = 'block';
                navLinkTecnicoVisita.style.display = 'block';
                navLinkInicio.setAttribute('href', '../indexTecnico.html');
                mainIcon.setAttribute('href', '../indexTecnico.html');
                navLinkCargarCsv.style.display = 'none';
            }
        }

        else if (loggedInUser.userColaboradorRol === 'Colaborador' && loggedInUser.userTecnicoRol === 'Técnico') {
            if (loggedInUser.rolActivo === 'Colaborador') {
                profilePoints.style.display = 'block';
                profilePoints2.style.display = 'block';
                colaboratorAction1.style.display = 'block';
                colaboratorAction2.style.display = 'block';
                colaboratorAction3.style.display = 'block';

                navLinkColaboraciones.style.display = 'block';
                navLinkFallaHeladera.style.display = 'block';
                navLinkCargarCsv.style.display = 'none';
                navLinkSuscripcionHeladeras.style.display = 'block';

                navLinkTecnicoIncidente.style.display = 'none';
                navLinkTecnicoVisita.style.display = 'none';
                navLinkAdmin.style.display = 'none';
                navLinkInicio.setAttribute('href', '../index.html');
                mainIcon.setAttribute('href', '../index.html');

            } else if (loggedInUser.rolActivo === 'Técnico') {
                profilePoints.style.display = 'none';
                profilePoints2.style.display = 'none';
                colaboratorAction1.style.display = 'none';
                colaboratorAction2.style.display = 'none';
                colaboratorAction3.style.display = 'none';

                navLinkColaboraciones.style.display = 'none';
                navLinkFallaHeladera.style.display = 'none';
                navLinkCargarCsv.style.display = 'none';
                navLinkSuscripcionHeladeras.style.display = 'none';

                navLinkTecnicoIncidente.style.display = 'block';
                navLinkTecnicoVisita.style.display = 'block';
                navLinkInicio.setAttribute('href', '../indexTecnico.html');
                mainIcon.setAttribute('href', '../indexTecnico.html');
            }
        }
    }
});

function toggleButton(button) {
    button.classList.toggle('active');

    if (button.classList.contains('active')) {
        activeButton(button);
    } else {
        desactiveButton(button);
    }
}

function desactiveButton(button) {
    const img = button.querySelector('img');

    if (button.id === 'whatsapp_button') {
        img.src = '../images/info_icons/whatsapp_disabled.png';
        img.alt = 'WhatsApp disabled';
    } else if (button.id === 'telegram_button') {
        img.src = '../images/info_icons/telegram_disabled.png';
        img.alt = 'Telegram disabled';
    }
}

function activeButton(button) {
    const img = button.querySelector('img');
    
    if (button.id === 'whatsapp_button') {
        img.src = '../images/info_icons/whatsapp_enabled.png';
        img.alt = 'WhatsApp enabled';
    } else if (button.id === 'telegram_button') {
        img.src = '../images/info_icons/telegram_enabled.png';
        img.alt = 'Telegram enabled';
    }
}

// Función para verificar el estado de un botón
function isButtonActive(button) {
    return button.classList.contains('active');
}

document.getElementById('whatsapp_button').addEventListener('click', function(event) {
    event.preventDefault();
    toggleButton(document.getElementById('whatsapp_button'));
});

document.getElementById('telegram_button').addEventListener('click', function(event) {
    event.preventDefault();
    toggleButton(document.getElementById('telegram_button'));
});

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
        window.location.href = 'configAccount.html';
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