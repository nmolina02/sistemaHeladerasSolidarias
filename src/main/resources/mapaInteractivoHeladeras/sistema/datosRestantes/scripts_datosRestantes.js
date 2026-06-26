document.getElementById('dni_type_input').addEventListener('input', checkInputs);
document.getElementById('dni_input').addEventListener('input', checkInputs);
document.getElementById('birthday_input').addEventListener('input', checkInputs);
document.getElementById('address_input').addEventListener('input', checkInputs);

// solamente los campos para persona jurídica
document.getElementById('organization_type_input').addEventListener('input', checkInputs);
document.getElementById('rubro_input').addEventListener('input', checkInputs);
document.getElementById('cuit_input').addEventListener('input', checkInputs);

function checkInputs() {
    const userTypeInput = document.getElementById('user_type_input').value;
    const createAccountButton = document.getElementById('create_account_button');

    if (userTypeInput === "Persona Humana") {
        if (checkIdentityOption()
        && checkAddressOption()) {
            createAccountButton.disabled = false;
        } else {
            createAccountButton.disabled = true;
        }
    } else if (userTypeInput === "Persona Jurídica") {
        if (checkNameJuridicOption()
        && checkAddressOption()) {
            createAccountButton.disabled = false;
        } else {
            createAccountButton.disabled = true;
        }
    } else {
        createAccountButton.disabled = true;
    }
}

function checkNameJuridicOption() {
    const organizationTypeLength = document.getElementById('organization_type_input').value.length;
    const rubroLength = document.getElementById('rubro_input').value.length;

    if (organizationTypeLength > 0 && rubroLength > 0) {
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

//Controlador de muestra de campos
document.getElementById("user_type_input").addEventListener("change", function(event) {
    event.preventDefault();
    const dni_type = document.getElementById("dni_type");
    const dni = document.getElementById("dni");
    const birthdate = document.getElementById("birthdate");
    const domicilio = document.getElementById("domicilio");
    
    const organizationType = document.getElementById("organizationType");
    const rubro = document.getElementById("rubro");
    const cuit = document.getElementById("cuit");

    if (event.target.value === "Persona Humana" || event.target.value === "Persona Jurídica") {
        if (event.target.value === "Persona Humana") {
            organizationType.style.display = "none";
            rubro.style.display = "none";
            cuit.style.display = "none";
            dni_type.style.display = "block";
            dni.style.display = "block";
            birthdate.style.display = "block";
            domicilio.setAttribute('class', 'col-md-6');
        } else {
            organizationType.style.display = "block";
            rubro.style.display = "block";
            cuit.style.display = "block";
            dni_type.style.display = "none";
            dni.style.display = "none";
            birthdate.style.display = "none";
            domicilio.setAttribute('class', 'col-md-12');
        }
        domicilio.style.display = "block";
    } else {
        organizationType.style.display = "none";
        rubro.style.display = "none";
        cuit.style.display = "none";
        dni_type.style.display = "none";
        dni.style.display = "none";
        birthdate.style.display = "none";
        domicilio.style.display = "none";
    }
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
    const userType = document.getElementById('user_type_input').value;
    const userDniType = document.getElementById('dni_type_input').value;
    const userDni = document.getElementById('dni_input').value;
    const userBirthdate = document.getElementById('birthday_input').value; 
    const userAddress = document.getElementById('address_input').value;
    
    const newUser = {
        userType: userType,
        email: '',
        phone: '0',
        whatsapp: 'false',
        telegram: 'false',
        address: userAddress,
        registerDate: new Date().toLocaleString(), 
        username: '',
        password: '',
        userImage: '../images/users_images/imagen_registro.png',
        points: 0,
        description: 'Desea agregar una descripción...',
        name: '',
        surname: '',
        dni: userDni + ' (' + userDniType + ')',
        birthdate: userBirthdate,
        card: 'Desea agregar una tarjeta...',
        currentUserId: localStorage.getItem('currentUserId'),
    };

    return newUser;
}

function createJuridicAccount() {
    const userCompanyType = document.getElementById('organization_type_input').value;
    const userRubro = document.getElementById('rubro_input').value;
    const userType = document.getElementById('user_type_input').value;
    const userAddress = document.getElementById('address_input').value;
    const userCuit = document.getElementById('dni_input').value;
    
    const newUser = {
        userType: userType,
        email: '',
        phone: '0',
        whatsapp: 'false',
        telegram: 'false',
        address: userAddress,
        registerDate: new Date().toLocaleString(), 
        username: '',
        password: '',
        userImage: '../images/users_images/imagen_registro.png',
        points: 0,
        description: 'Desea agregar una descripción...',
        razonSocial: '',
        companyType: userCompanyType,
        rubro: userRubro,
        cuit: userCuit,
        currentUserId: localStorage.getItem('currentUserId'),
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

    enviarDatosRestantesAlServidor(newUser);
}

document.getElementById('back_button').addEventListener('click', function(event){
    event.preventDefault();
    const userConfirmed = confirm('¿Está seguro de que desea salir? Se cancelará la creación de su cuenta.');
    if (userConfirmed) {
        window.location.href = '../index.html';
    }
});

function enviarDatosRestantesAlServidor(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/userSSO', {
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
        let newUserLocalStorage = {
            userColaboradorRol: 'Colaborador',
            userTecnicoRol: '',
            userId: respuesta.id,
            userType: respuesta.tipo,
        }
        
        localStorage.removeItem('currentUserId');
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
        window.location.href = '../index.html';
    }, 3000); // son solo 3 segundos
}
