document.getElementById('name_input').addEventListener('input', checkInputs);
document.getElementById('surname_input').addEventListener('input', checkInputs);
document.getElementById('identity_input').addEventListener('input', checkInputs);
document.getElementById('dni_type_input').addEventListener('input', checkInputs);
document.getElementById('dni_input').addEventListener('input', checkInputs);
document.getElementById('birthday_input').addEventListener('input', checkInputs);
document.getElementById('age_input').addEventListener('input', checkInputs);
document.getElementById('address_confirm_input').addEventListener('input', checkInputs);
document.getElementById('address_input').addEventListener('input', checkInputs);
document.getElementById('contact_confirm_input').addEventListener('input', checkInputs);
document.getElementById('contact_type_input').addEventListener('input', checkInputs);
document.getElementById('contact_code_input').addEventListener('input', checkInputs);
document.getElementById('campo_input').addEventListener('input', checkInputs);
document.getElementById('contact_type_input_2').addEventListener('input', checkInputs);
document.getElementById('contact_code_input_2').addEventListener('input', checkInputs);
document.getElementById('campo_input_2').addEventListener('input', checkInputs);
document.getElementById('tutor_input').addEventListener('input', checkInputs);

function checkInputs() {
    const createAccountButton = document.getElementById('create_account_vul_button');

    if (checkNameOption() && checkIdentityOption() && checkAddressOption() && checkContactOption() && checkContactOption2() && checkTutorOption()) {
        createAccountButton.disabled = false;
    } else {
        createAccountButton.disabled = true;
    }
}

function checkNameOption() {
    const nameLength = document.getElementById('name_input').value.length;
    const surnameLength = document.getElementById('surname_input').value.length;

    if (nameLength > 0 && surnameLength > 0) {
        return true;
    } else {
        return false;
    }
}

function checkIdentityOption() {
    const identityOption = document.getElementById('identity_input').value;
    const dniType = document.getElementById('dni_type_input').value;
    const dni = document.getElementById('dni_input').value;
    const birthdate = document.getElementById('birthday_input').value;
    const age = document.getElementById('age_input').value;
    const tutor = document.getElementById('tutor_input').value;

    if (identityOption === '1') {
        if (dniType.length > 0 && dni.length > 0 && birthdate.length > 0) {
            return true;
        } else {
            return false;
        }
    } else if (identityOption === '2') {
        if (age.length > 0) {
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
}

function checkAddressOption() {
    const addressOption = document.getElementById('address_confirm_input').value;
    const address = document.getElementById('address_input').value;

    if (addressOption === '1') {
        if (address.length > 0) {
            return true;
        } else {
            return false;
        }
    } else if (addressOption === '0') {
        return false;
    } else {
        return true;
    }
}

function checkContactOption() {
    const contactOption = document.getElementById('contact_confirm_input').value;
    const contactType = document.getElementById('contact_type_input').value;
    const contactCode = document.getElementById('contact_code_input').value;
    const campo = document.getElementById('campo_input').value;

    if (contactOption === '1') {
        if (contactType.length > 0 && contactCode.length > 0 && campo.length > 0) {
            return true;
        } else {
            return false;
        }
    } else if (contactOption === '0') {
        return false;
    } else {
        return true;
    }
}

function checkContactOption2() {
    const addContactButton = document.getElementById("add-contact-button");
    const addContactButtonStyle = window.getComputedStyle(addContactButton);
    const AddContactButtonbackgroundColor = addContactButtonStyle.backgroundColor;
    const contactType = document.getElementById('contact_type_input_2').value;
    const contactCode = document.getElementById('contact_code_input_2').value;
    const campo = document.getElementById('campo_input_2').value;

    if (AddContactButtonbackgroundColor === "rgb(195, 11, 11)") {
        if (contactType.length > 0 && contactCode.length > 0 && campo.length > 0) {
            return true;
        } else {
            return false;
        }
    } else {
        return true;
    }
}

function checkTutorOption() {
    const identityOption = document.getElementById('identity_input').value;
    const birthday = document.getElementById('birthday_input').value;
    const age = document.getElementById('age_input').value;
    const tutor = document.getElementById("tutor_input").value;
    
    const edadCalculada = new Date().getFullYear() - new Date(birthday).getFullYear();

    if ((identityOption === '1' && edadCalculada < 18) || (identityOption === '2' && age < 18)) {
        if(tutor.length > 0) {
            return true;
        } else {
            return false;
        }
    } else {
        return true;
    }
}

document.getElementById('birthday_input').addEventListener('input', function(event){
    event.preventDefault();
    showTutorOption();
});

document.getElementById('age_input').addEventListener('input', function(event){
    event.preventDefault();
    showTutorOption();
});

function showTutorOption() {
    const identityOption = document.getElementById('identity_input').value;
    const birthday = document.getElementById('birthday_input').value;
    const age = document.getElementById('age_input').value;
    
    const listPersVuln = document.getElementById("list_pers_vuln");
    const tutor = document.getElementById('tutor');

    const edadCalculada = new Date().getFullYear() - new Date(birthday).getFullYear();

    if ((identityOption === '1' && edadCalculada < 18) || (identityOption === '2' && age < 18)) {
        listPersVuln.style.display = 'block';
        tutor.style.display = 'block';
    } else {
        listPersVuln.style.display = 'none';
        tutor.style.display = 'none';
    }
}

document.getElementById('create_account_vul_button').addEventListener('click', function(event){
    createAccount(event);
});

document.getElementById('colaboration-section').addEventListener('keydown', pressEnter);

function pressEnter(event){
    if(event.code === 'Enter' || event.code === 'NumpadEnter'){
        const createAccountButton = document.getElementById('create_account_vul_button');
        if (!createAccountButton.disabled) {
            createAccount(event);
        }
    }
}

document.getElementById("dni_type_input").addEventListener("change", function(event) {
    event.preventDefault();
    const dniInput = document.getElementById("dni_input");

    if (event.target.value === "DNI") {
        dniInput.setAttribute('placeholder', 'DNI');
    } else if (event.target.value === "LC") {
        dniInput.setAttribute('placeholder', 'LC');
    } else if (event.target.value === "LE") {
        dniInput.setAttribute('placeholder', 'LE');
    } else if (event.target.value === "0") {
        dniInput.setAttribute('placeholder', 'Documento');
    }
});

function createAccount(event) {
    event.preventDefault();
    
    const userName = document.getElementById('name_input').value;
    const userSurname = document.getElementById('surname_input').value;
    const userDniType = document.getElementById('dni_type_input').value;
    const userDni = document.getElementById('dni_input').value;
    const userBirthdate = document.getElementById('birthday_input').value;
    const userAge = document.getElementById('age_input').value;
    const userAddress = document.getElementById('address_input').value;
    
    const userContactType = document.getElementById('contact_type_input').value;
    const userCodePhone1 = document.getElementById('contact_code_input').value;
    const userCampo1 = document.getElementById('campo_input').value;
    
    const userCodePhone2 = document.getElementById('contact_code_input_2').value;
    const userCampo2 = document.getElementById('campo_input_2').value;

    let userEmail = '';
    let userPhone = '';
    let userWhatsapp = '';
    let userTelegram = '';

    if (userContactType === 'Mail') {
        userEmail = userCampo1;
        userPhone = userCodePhone2 + userCampo2;

        const whatsappButton2 = document.getElementById('whatsapp_button_2');

        if (isButtonActive(whatsappButton2)){
            userWhatsapp = 'true';
        } else {
            userWhatsapp = 'false';
        }
        
        const telegramButton2 = document.getElementById('telegram_button_2');

        if (isButtonActive(telegramButton2)){
            userTelegram = 'true';
        } else {
            userTelegram = 'false';
        }
    }

    else {
        userEmail = userCampo2;
        userPhone = userCodePhone1 + userCampo1;

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
    }

    const newUser = {
        name: userName,
        surname: userSurname,
        userType: 'Persona Vulnerable',
        email: userEmail,
        phone: userPhone,
        whatsapp: userWhatsapp,
        telegram: userTelegram,
        dni: userDni + ' (' + userDniType + ')',
        birthdate: userBirthdate,
        age: userAge,
        address: userAddress,
        registerDate: new Date().toLocaleString(), 
        userImage: '../../images/users_images/imagen_registro.png'
    };

    const data = {
        colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
        tipoColaboracion: 'REGISTRO_DE_PERSONAS_VULNERABLES',
        personaVulnerable: newUser,
        esMenor: userAge < 18,
        tutor: document.getElementById('tutor_input').value
    };

    enviarConfirmacionRegistroPersonaVulnerable(data);
}

document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        hideLoginButton();
    }
});

function hideLoginButton() {
    const loginButton = document.getElementById('login_button');
    const createAccountButton = document.getElementById('create_account_button');
    const userCircle = document.getElementById('user_circle');
    const profileOptions = document.getElementById('profile_options');
    
    loginButton.style.display = 'none';
    createAccountButton.style.display = 'none';
    userCircle.style.display = 'flex';
    profileOptions.style.display = 'block';
}

document.getElementById('logout_button').addEventListener('click', function(event){
    logout(event);
});

function logout(event) {
    event.preventDefault();
    const userCircle = document.getElementById('user_circle');
    localStorage.removeItem('loggedInUser');
    userCircle.style.display = 'none';
    window.location.href = '../../login.html'; // Redirigir al login después de cerrar sesión
}

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

//Controlador de muestra de campos
document.getElementById("identity_input").addEventListener("change", function(event) {
    event.preventDefault();
    const dniType = document.getElementById("dni_type");
    const dni = document.getElementById("dni");
    const birthdate = document.getElementById("birthdate");
    const age = document.getElementById("age");

    if (event.target.value === "1") {
        dniType.style.display = "block";
        dni.style.display = "block";
        birthdate.style.display = "block";
        age.style.display = "none";
    }
    else if (event.target.value === "2") {
        dniType.style.display = "none";
        dni.style.display = "none";
        birthdate.style.display = "none";
        age.style.display = "block";
    }
    else {
        dniType.style.display = "none";
        dni.style.display = "none";
        birthdate.style.display = "none";
        age.style.display = "none";
    }
});

document.getElementById("address_confirm_input").addEventListener("change", function(event) {
    event.preventDefault();
    const domicilio = document.getElementById("domicilio");

    if (event.target.value === "1") {
        domicilio.style.display = "block";
    }
    else {
        domicilio.style.display = "none";
    }
});

document.getElementById("contact_confirm_input").addEventListener("change", function(event) {
    event.preventDefault();
    const contactType = document.getElementById("contact_type");
    const contactType2 = document.getElementById("contact_type_2");

    const contactPhoneCode = document.getElementById("contact_phone_code");
    const contact = document.getElementById("contact");
    
    const contactPhoneCode2 = document.getElementById("contact_phone_code_2");
    const contact2 = document.getElementById("contact_2");
    const addContactButton = document.getElementById("add_contact_button");

    const botonesApps = document.getElementById("botones_apps");
    const botonesApps2 = document.getElementById("botones_apps_2");

    if (event.target.value === "1") {
        contactType.style.display = "block";
        addContactButton.style.display = "block";
    }
    else {
        contactType.style.display = "none";
        contactType2.style.display = "none";
        addContactButton.style.display = "none";
        contactPhoneCode.style.display = "none";
        contact.style.display = "none";
        contactPhoneCode2.style.display = "none";
        contact2.style.display = "none";
        botonesApps.style.display = "none";
        botonesApps2.style.display = "none";
    }
});

document.getElementById("add-contact-button").addEventListener("click", function(event) {
    event.preventDefault();
    const contactType2 = document.getElementById("contact_type_2");
    const addContactButton = document.getElementById("add-contact-button");
    const addContactButtonStyle = window.getComputedStyle(addContactButton);
    const AddContactButtonbackgroundColor = addContactButtonStyle.backgroundColor;
    const contactPhoneCode2 = document.getElementById("contact_phone_code_2");
    const contact2 = document.getElementById("contact_2");
    const botonesApps2 = document.getElementById("botones_apps_2");

    const contactTypeInput = document.getElementById("contact_type_input");

    if (AddContactButtonbackgroundColor === "rgb(13, 175, 10)") {
        if (contactTypeInput.value === "Teléfono") {
            contactPhoneCode2.style.display = "none";
            botonesApps2.style.display = "none";
        } else {
            contactPhoneCode2.style.display = "block";
            botonesApps2.style.display = "block";
        }
        addContactButton.textContent = '- Quitar opción de contacto';
        addContactButton.style.backgroundColor = '#c30b0b';
        contactType2.style.display = "block";
        contact2.style.display = "block";
    }
    else {
        contactType2.style.display = "none";
        addContactButton.textContent = '+ Agregar opción de contacto';
        addContactButton.style.backgroundColor = '#0daf0a';
        contactPhoneCode2.style.display = "none";
        contact2.style.display = "none";
        botonesApps2.style.display = "none";
    }

});

document.getElementById("contact_type_input").addEventListener("change", function(event) {
    event.preventDefault();
    const contactPhoneCode = document.getElementById("contact_phone_code");
    const contact = document.getElementById("contact");
    
    const contactPhoneCode2 = document.getElementById("contact_phone_code_2");
    const contact2 = document.getElementById("contact_2");

    const contactTypeInput2 = document.getElementById("contact_type_input_2");
    
    const campoInput = document.getElementById("campo_input");
    const campoInput2 = document.getElementById("campo_input_2");

    const botonesApps = document.getElementById("botones_apps");
    const botonesApps2 = document.getElementById("botones_apps_2");

    const addContactButton = document.getElementById("add-contact-button");
    const addContactButtonStyle = window.getComputedStyle(addContactButton);
    const AddContactButtonbackgroundColor = addContactButtonStyle.backgroundColor;

    if (event.target.value === "Mail") {
        contactPhoneCode.style.display = "none";
        contact.style.display = "block";
        contact.setAttribute('class', 'col-md-7');
        contactTypeInput2.value = "Teléfono";
        campoInput.setAttribute('type', 'email');
        campoInput.setAttribute('placeholder', 'Correo Electrónico');
        contact2.setAttribute('class', 'col-md-4');
        campoInput2.setAttribute('type', 'number');
        campoInput2.setAttribute('placeholder', 'Teléfono');
        botonesApps.style.display = "none";
        if (AddContactButtonbackgroundColor === "rgb(195, 11, 11)" && contactTypeInput2.value === "Teléfono") {
            contactPhoneCode2.style.display = "block";
            botonesApps2.style.display = "block";
        }
    }
    else if (event.target.value === "Teléfono") {
        contactPhoneCode.style.display = "block";
        contact.style.display = "block";
        contact.setAttribute('class', 'col-md-4');
        contactTypeInput2.value = "Mail";
        campoInput.setAttribute('type', 'number');
        campoInput.setAttribute('placeholder', 'Teléfono');
        contactPhoneCode2.style.display = "none";
        contact2.setAttribute('class', 'col-md-7');
        campoInput2.setAttribute('type', 'email');
        campoInput2.setAttribute('placeholder', 'Correo Electrónico');
        botonesApps.style.display = "block";
        if (AddContactButtonbackgroundColor === "rgb(195, 11, 11)" && contactTypeInput2.value === "Mail") {
            contactPhoneCode2.style.display = "none";
            botonesApps2.style.display = "none";
        }
    }
    else {
        contactPhoneCode.style.display = "none";
        contact.style.display = "none";
        contactPhoneCode2.style.display = "none";
        contact2.style.display = "none";
        contactTypeInput2.value = "0";
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

    if (button.id === 'whatsapp_button' || button.id === 'whatsapp_button_2') {
        img.src = '../../images/info_icons/whatsapp_disabled.png';
        img.alt = 'WhatsApp disabled';
    } else if (button.id === 'telegram_button' || button.id === 'telegram_button_2') {
        img.src = '../../images/info_icons/telegram_disabled.png';
        img.alt = 'Telegram disabled';
    }
}

function activeButton(button) {
    const img = button.querySelector('img');
    
    if (button.id === 'whatsapp_button' || button.id === 'whatsapp_button_2') {
        img.src = '../../images/info_icons/whatsapp_enabled.png';
        img.alt = 'WhatsApp enabled';
    } else if (button.id === 'telegram_button' || button.id === 'telegram_button_2') {
        img.src = '../../images/info_icons/telegram_enabled.png';
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

document.getElementById('whatsapp_button_2').addEventListener('click', function(event) {
    event.preventDefault();
    toggleButton(document.getElementById('whatsapp_button_2'));
});

document.getElementById('telegram_button_2').addEventListener('click', function(event) {
    event.preventDefault();
    toggleButton(document.getElementById('telegram_button_2'));
});

//Receptores
function enviarConfirmacionRegistroPersonaVulnerable(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/points', {
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
        mostrarAlertaExito('Los puntos fueron cargados a su cuenta');
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
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

document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        enviarSolicitudPerfil(loggedInUser);
        if (loggedInUser.userType === 'J') {
            document.getElementById('reportar_falla_heladera').style.display = 'none';
            document.getElementById('suscripcion_heladeras').style.display = 'none';
            document.getElementById('tarjetaSolicitada').style.display = 'none';
        }
    }
});

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
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

document.getElementById('back_button').addEventListener('click', function(event){
    event.preventDefault();
    window.location.href = '../colaboraciones.html';
});

document.getElementById('list_pers_vuln_button').addEventListener('click', function(event){
    event.preventDefault();
    window.open('listadoPersonasVulnerables.html', '_blank');
});

document.addEventListener('DOMContentLoaded', changeRol);

function changeRol() {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    const changeRolOption = document.getElementById('change_rol');
    if (loggedInUser && loggedInUser.userColaboradorRol === 'Colaborador' && loggedInUser.userTecnicoRol === 'Técnico') {
        changeRolOption.style.display = 'block';
        changeRolOption.textContent = 'Cambiar a Técnico';
    } else {
        changeRolOption.style.display = 'none';
    }
}

document.getElementById('change_rol').addEventListener('click', function(event) {
    event.preventDefault();
    window.location.href = '../../indexTecnico.html';
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
        window.location.href = 'registroPersonaVulnerable.html';
    }, 3000); // son solo 3 segundos
}
