document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        hideLoginButton();
        enviarSolicitudPerfil(loggedInUser);
        if (loggedInUser.userType === 'J') {
            document.getElementById('reportar_falla_heladera').style.display = 'none';
            document.getElementById('suscripcion_heladeras').style.display = 'none';
            document.getElementById('tarjetaSolicitada').style.display = 'none';
        }
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
    window.location.href = '../login.html'; // Redirigir al login después de cerrar sesión
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

function verifySelection() {
    const selectedOptions = document.querySelectorAll('input[name="options"]:checked');
    const selectedOptions2 = document.querySelectorAll('input[name="options2"]:checked');
    const cantViandasDisp = document.getElementById('cant_viandas_disp');
    const cantViandasLlevar = document.getElementById('cant_viandas_llevar');
    const suscriptionButton = document.getElementById('suscription_button');

    let selectedOption1 = false;
    let selectedOption2 = false;

    // verifico que haya seleccionado una opción y que haya ingresado la cantidad de viandas
    // tengo en cuenta todas las combinaciones posibles
    if (selectedOptions.length > 0 && selectedOptions[0].checked && cantViandasDisp.value !== null && cantViandasDisp.value !== "") {
        selectedOption1 = true;
    } else if (selectedOptions.length > 1 && selectedOptions[1].checked && cantViandasDisp.value !== null && cantViandasDisp.value !== "") {
        selectedOption1 = true;
    } else if (selectedOptions.length > 2 && selectedOptions[2].checked && cantViandasDisp.value !== null && cantViandasDisp.value !== "") {
        selectedOption1 = true;
    } else if (selectedOptions.length > 0 && selectedOptions[0].value !== 'CANT_VIANDAS_DISP' ) {
        selectedOption1 = true;
    } else if (selectedOptions.length > 1 && selectedOptions[1].value !== 'CANT_VIANDAS_DISP' ) {
        selectedOption1 = true;
    } else if (selectedOptions.length > 2 && selectedOptions[2].value !== 'CANT_VIANDAS_DISP' ) {
        selectedOption1 = true;
    }

    // verifico que haya seleccionado una opción y que haya ingresado la cantidad de viandas
    // tengo en cuenta todas las combinaciones posibles
    if (selectedOptions.length > 0 && selectedOptions[0].checked && cantViandasLlevar.value !== null && cantViandasLlevar.value !== "") {
        selectedOption2 = true;
    } else if (selectedOptions.length > 1 && selectedOptions[1].checked && cantViandasLlevar.value !== null && cantViandasLlevar.value !== "") {
        selectedOption2 = true;
    } else if (selectedOptions.length > 2 && selectedOptions[2].checked && cantViandasLlevar.value !== null && cantViandasLlevar.value !== "") {
        selectedOption2 = true;
    } else if (selectedOptions.length > 0 && selectedOptions[0].value !== 'CANT_VIANDAS_PARA_LLENAR' ) {
        selectedOption2 = true;
    } else if (selectedOptions.length > 1 && selectedOptions[1].value !== 'CANT_VIANDAS_PARA_LLENAR' ) {
        selectedOption2 = true;
    } else if (selectedOptions.length > 2 && selectedOptions[2].value !== 'CANT_VIANDAS_PARA_LLENAR' ) {
        selectedOption2 = true;
    }

    if (selectedOptions.length > 0 && selectedOptions2.length > 0 && selectedOption1 && selectedOption2) {
        suscriptionButton.disabled = false;
        suscriptionButton.style.backgroundColor = '#04d300';
    } else {
        suscriptionButton.disabled = true;
        suscriptionButton.style.backgroundColor = '#afafaf';
    }
}

document.querySelectorAll('input[name="options"]').forEach((checkbox) => {
    checkbox.addEventListener('change', verifySelection);
});

document.querySelectorAll('input[name="options2"]').forEach((checkbox) => {
    checkbox.addEventListener('change', verifySelection);
});

document.getElementById('cantViandasDispCheckbox').addEventListener('input', function(event) {
    event.preventDefault();
    const cantViandasDisp = document.getElementById('cant_viandas_disp');
    if (event.target.checked) {
        cantViandasDisp.disabled = false;
    } else {
        cantViandasDisp.disabled = true;
        cantViandasDisp.value = "none";
    }
});

document.getElementById('cantViandasLlenarCheckbox').addEventListener('input', function(event) {
    event.preventDefault();
    const cantViandasLlevar = document.getElementById('cant_viandas_llevar');
    if (event.target.checked) {
        cantViandasLlevar.disabled = false;
    } else {
        cantViandasLlevar.disabled = true;
        cantViandasLlevar.value = "none";
    }
});

function suscribe(event) {
    event.preventDefault();
    const options1 = document.querySelectorAll('input[name="options"]:checked');
    const selectedOptions1= [...options1].map(option => option.value);
    const cantViandasDisp = document.getElementById('cant_viandas_disp').value;

    const options2 = document.querySelectorAll('input[name="options2"]:checked');
    const selectedOptions2= [...options2].map(option => option.value);
    const cantViandasLlevar = document.getElementById('cant_viandas_llevar').value;

    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    const newSuscription = {
        colaborador: loggedInUser,
        suscriptionsOptions: selectedOptions1,
        messaggeOptions: selectedOptions2,
        cantViandasDisp: cantViandasDisp,
        cantViandasLlevar: cantViandasLlevar,
    };

    enviarSuscripcion(newSuscription);
}

document.getElementById('suscription_button').addEventListener('click', function(event){
    suscribe(event);
});

document.getElementById('create-account-section').addEventListener('keydown', pressEnter);
    
function pressEnter(event){
    if(event.code === 'Enter' || event.code === 'NumpadEnter'){
        const suscriptionButton = document.getElementById('suscription_button');
        if (!suscriptionButton.disabled) {
            suscribe(event);
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser && loggedInUser.suscripcion === 'suscripto') {
        const suscriptionButton = document.getElementById('suscription_button');
        suscriptionButton.textContent = 'Actualizar suscripción';
    }
});

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

function enviarSuscripcion(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/suscripcion', {
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
        if (respuesta === 'No se encontró el colaborador') {
            mostrarAlertaError('No se encontró al colaborador');
            return;
        } else if (respuesta === 'Suscripción realizada correctamente') {
            mostrarAlertaExito('¡Gracias por suscribirte!');
        } else if (respuesta === 'Suscripción actualizada correctamente') {
            mostrarAlertaExito('Suscripción actualizada');
        }
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
        changeRolOption.style.display = 'block';
        changeRolOption.textContent = 'Cambiar a Técnico';
    } else {
        changeRolOption.style.display = 'none';
    }
}

document.getElementById('change_rol').addEventListener('click', function(event) {
    event.preventDefault();
    window.location.href = '../indexTecnico.html';
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
        window.location.href = 'suscripcionHeladeras.html';
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
