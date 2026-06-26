document.getElementById('name_input').addEventListener('input', checkInputs);
document.getElementById('surname_input').addEventListener('input', checkInputs);
document.getElementById('dni_type_input').addEventListener('input', checkInputs);
document.getElementById('dni_input').addEventListener('input', checkInputs);
document.getElementById('birthday_input').addEventListener('input', checkInputs);
document.getElementById('address_input').addEventListener('input', checkInputs);
document.getElementById('contact_type_input').addEventListener('input', checkInputs);
document.getElementById('contact_code_input').addEventListener('input', checkInputs);
document.getElementById('campo_input').addEventListener('input', checkInputs);
document.getElementById('contact_type_input_2').addEventListener('input', checkInputs);
document.getElementById('contact_code_input_2').addEventListener('input', checkInputs);
document.getElementById('campo_input_2').addEventListener('input', checkInputs);
document.getElementById('username_input').addEventListener('input', checkInputs);
document.getElementById('password_input').addEventListener('input', checkInputs);
document.getElementById('password_confirm_input').addEventListener('input', checkInputs);

// solamente los campos para persona jurídica
document.getElementById('razon_social_input').addEventListener('input', checkInputs);
document.getElementById('organization_type_input').addEventListener('input', checkInputs);
document.getElementById('rubro_input').addEventListener('input', checkInputs);
document.getElementById('cuit_input').addEventListener('input', checkInputs);

function checkInputs() {
    const userTypeInput = document.getElementById('user_type_input').value;
    const createAccountButton = document.getElementById('create_account_button');

    if (userTypeInput === "Persona Humana") {
        if (checkNameOption()
        && checkIdentityOption()
        && checkAddressOption()
        && checkContactOption()
        && checkContactOption2()
        && checkLoginMethodOption()) {
            createAccountButton.disabled = false;
        } else {
            createAccountButton.disabled = true;
        }
    } else if (userTypeInput === "Persona Jurídica") {
        if (checkNameJuridicOption()
        && checkAddressOption()
        && checkContactOption()
        && checkContactOption2()
        && checkLoginMethodOption()) {
            createAccountButton.disabled = false;
        } else {
            createAccountButton.disabled = true;
        }
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

function checkNameJuridicOption() {
    const razonSocialLength = document.getElementById('razon_social_input').value.length;
    const organizationTypeLength = document.getElementById('organization_type_input').value.length;
    const rubroLength = document.getElementById('rubro_input').value.length;
    const cuitLength = document.getElementById('cuit_input').value.length;

    if (razonSocialLength > 0 && organizationTypeLength > 0 && rubroLength > 0 && cuitLength > 0) {
        return true;
    } else {
        return false;
    }
}

function checkLoginMethodOption() {
    const usuarioLength = document.getElementById('username_input').value.length;
    const password = document.getElementById('password_input').value;
    const confirmPassword = document.getElementById('password_confirm_input').value;

    if (usuarioLength > 0 && password.length >= 8 && confirmPassword === password) {
        return true;
    } else {
        return false;
    }
}

function checkIdentityOption() {
    const dniType = document.getElementById('dni_type_input').value;
    const dni = document.getElementById('dni_input').value;
    const birthdate = document.getElementById('birthday_input').value;

    if (dniType.length > 0 && dni.length > 0 && birthdate.length > 0) {
        return true;
    } else {
        return false;
    }
}

function checkAddressOption() {
    const address = document.getElementById('address_input').value;

    if (address.length > 0) {
        return true;
    } else {
        return false;
    }
}

function checkContactOption() {
    const contactType = document.getElementById('contact_type_input').value;
    const contactCode = document.getElementById('contact_code_input').value;
    const campo = document.getElementById('campo_input').value;

    if (contactType.length > 0 && contactCode.length > 0 && campo.length > 0) {
        return true;
    } else {
        return false;
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

document.getElementById('create-account-section').addEventListener('keydown', pressEnter);
document.getElementById('create_account_button').addEventListener('click', function(event) {
    event.preventDefault();
    createAccount(event);
});

function pressEnter(event){
    if(event.code === 'Enter' || event.code === 'NumpadEnter'){
        const createAccountButton = document.getElementById('create_account_button');
        if (!createAccountButton.disabled) {
            createAccount(event);
        }
    }
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
document.getElementById("user_type_input").addEventListener("change", function(event) {
    event.preventDefault();
    const name = document.getElementById("name");
    const surname = document.getElementById("surname");
    const dni_type = document.getElementById("dni_type");
    const dni = document.getElementById("dni");
    const birthdate = document.getElementById("birthdate");
    const domicilio = document.getElementById("domicilio");
    const contactType = document.getElementById("contact_type");

    const razonSocial = document.getElementById("razonSocial");
    const organizationType = document.getElementById("organizationType");
    const rubro = document.getElementById("rubro");
    const cuit = document.getElementById("cuit");

    const addContactButton = document.getElementById("add_contact_button");

    const username = document.getElementById("username");
    const password = document.getElementById("password");
    const passwordConfirm = document.getElementById("password_confirm");

    if (event.target.value === "Persona Humana" || event.target.value === "Persona Jurídica") {
        if (event.target.value === "Persona Humana") {
            name.style.display = "block";
            surname.style.display = "block";
            razonSocial.style.display = "none";
            organizationType.style.display = "none";
            rubro.style.display = "none";
            cuit.style.display = "none";
            dni_type.style.display = "block";
            dni.style.display = "block";
            birthdate.style.display = "block";
            domicilio.setAttribute('class', 'col-md-6');
        } else {
            name.style.display = "none";
            surname.style.display = "none";
            razonSocial.style.display = "block";
            organizationType.style.display = "block";
            rubro.style.display = "block";
            cuit.style.display = "block";
            dni_type.style.display = "none";
            dni.style.display = "none";
            birthdate.style.display = "none";
            domicilio.setAttribute('class', 'col-md-12');
        }
        domicilio.style.display = "block";
        contactType.style.display = "block";
        addContactButton.style.display = "block";
        username.style.display = "block";
        password.style.display = "block";
        passwordConfirm.style.display = "block";
    } else {
        name.style.display = "none";
        surname.style.display = "none";
        razonSocial.style.display = "none";
        organizationType.style.display = "none";
        rubro.style.display = "none";
        cuit.style.display = "none";
        dni_type.style.display = "none";
        dni.style.display = "none";
        birthdate.style.display = "none";
        domicilio.style.display = "none";
        contactType.style.display = "none";
        addContactButton.style.display = "none";
        username.style.display = "none";
        password.style.display = "none";
        passwordConfirm.style.display = "none";
    }
});

document.addEventListener("DOMContentLoaded", function () {
    const passwordInput = document.getElementById("password_input");
    const passwordInfo = document.getElementById("password_info");

    passwordInput.addEventListener("focus", () => {
        passwordInfo.style.display = "block";
    });

    passwordInput.addEventListener("blur", () => {
        passwordInfo.style.display = "none";
    });
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
        contact.setAttribute('class', 'col-md-9');
        contactTypeInput2.value = "Teléfono";
        campoInput.setAttribute('type', 'email');
        campoInput.setAttribute('placeholder', 'Correo Electrónico');
        contact2.setAttribute('class', 'col-md-5');
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
        contact.setAttribute('class', 'col-md-5');
        contactTypeInput2.value = "Mail";
        campoInput.setAttribute('type', 'number');
        campoInput.setAttribute('placeholder', 'Teléfono');
        contactPhoneCode2.style.display = "none";
        contact2.setAttribute('class', 'col-md-9');
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
        img.src = '../images/info_icons/whatsapp_disabled.png';
        img.alt = 'WhatsApp disabled';
    } else if (button.id === 'telegram_button' || button.id === 'telegram_button_2') {
        img.src = '../images/info_icons/telegram_disabled.png';
        img.alt = 'Telegram disabled';
    }
}

function activeButton(button) {
    const img = button.querySelector('img');
    
    if (button.id === 'whatsapp_button' || button.id === 'whatsapp_button_2') {
        img.src = '../images/info_icons/whatsapp_enabled.png';
        img.alt = 'WhatsApp enabled';
    } else if (button.id === 'telegram_button' || button.id === 'telegram_button_2') {
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

document.getElementById('whatsapp_button_2').addEventListener('click', function(event) {
    event.preventDefault();
    toggleButton(document.getElementById('whatsapp_button_2'));
});

document.getElementById('telegram_button_2').addEventListener('click', function(event) {
    event.preventDefault();
    toggleButton(document.getElementById('telegram_button_2'));
});

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

function createHumanAccount() {
    const userName = document.getElementById('name_input').value;
    const userSurname = document.getElementById('surname_input').value;
    const userType = document.getElementById('user_type_input').value;
    const userDniType = document.getElementById('dni_type_input').value;
    const userDni = document.getElementById('dni_input').value;
    const userBirthdate = document.getElementById('birthday_input').value; 
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
    
    const username_input = document.getElementById("username_input").value;
    const password_input = document.getElementById("password_input").value;

    const encryptedPassword = CryptoJS.SHA256(password_input).toString();
    
    const newUser = {
        userType: userType,
        email: userEmail,
        phone: userPhone,
        whatsapp: userWhatsapp,
        telegram: userTelegram,
        address: userAddress,
        registerDate: new Date().toLocaleString(), 
        username: username_input,
        password: encryptedPassword,
        userImage: 'imagen_registro.png',
        points: 0,
        description: 'Desea agregar una descripción...',
        name: userName,
        surname: userSurname,
        dni: userDni + ' (' + userDniType + ')',
        birthdate: userBirthdate,
        card: 'Desea agregar una tarjeta...',
    };

    return newUser;
}

function createJuridicAccount() {
    const userRazonSocial = document.getElementById('razon_social_input').value;
    const userCompanyType = document.getElementById('organization_type_input').value;
    const userRubro = document.getElementById('rubro_input').value;
    const userCuit = document.getElementById('cuit_input').value;
    const userType = document.getElementById('user_type_input').value;
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

    const username_input = document.getElementById("username_input").value;
    const password_input = document.getElementById("password_input").value;
    
    const encryptedPassword = CryptoJS.SHA256(password_input).toString();
    
    const newUser = {
        userType: userType,
        email: userEmail,
        phone: userPhone,
        whatsapp: userWhatsapp,
        telegram: userTelegram,
        address: userAddress,
        registerDate: new Date().toLocaleString(), 
        username: username_input,
        password: encryptedPassword,
        userImage: 'imagen_registro.png',
        points: 0,
        description: 'Desea agregar una descripción...',
        razonSocial: userRazonSocial,
        companyType: userCompanyType,
        rubro: userRubro,
        cuit: userCuit,
    };

    return newUser;
}

function createAccount(event) {
    event.preventDefault();

    var userType = document.getElementById('user_type_input').value;

    let newUser;
    
    if (userType === 'Persona Humana') {
        newUser = createHumanAccount();
    } else if (userType === 'Persona Jurídica') {
        newUser = createJuridicAccount();
    }
    
    enviarUsuarioAlServidor(newUser);
}

document.getElementById('back_button').addEventListener('click', function(event){
    event.preventDefault();
    window.location.href = 'index.html';
});

function enviarUsuarioAlServidor(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/user', {
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
        if (respuesta === 'El nombre de usuario ya existe') {
            mostrarAlertaError('El nombre de usuario ya existe, por favor seleccione otro');
            return;
        } else if (respuesta === 'La documentación ya fue registrada') {
            mostrarAlertaError('La documentación ya fue registrada, por favor verifique los datos ingresados');
            return;
        } else if (respuesta === 'El CUIT ya fue registrado') {
            mostrarAlertaError('El CUIT ya fue registrado, por favor verifique los datos ingresados');
            return;
        } else if (respuesta.tipo === 'Desconocido') {
            alert(respuesta.error);
            return;
        }

        let newUserLocalStorage = {
            userColaboradorRol: 'Colaborador',
            userTecnicoRol: '',
            userId: respuesta.id,
            userType: respuesta.tipo,
        }
        
        localStorage.setItem('loggedInUser', JSON.stringify(newUserLocalStorage));
        mostrarAlertaExito('Usuario creado');
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
        window.location.href = 'index.html';
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