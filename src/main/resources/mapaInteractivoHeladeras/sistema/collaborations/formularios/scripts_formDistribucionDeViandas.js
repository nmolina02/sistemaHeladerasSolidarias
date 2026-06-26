let requestApproved = false;

// Verifica si todos los campos del formulario están completos y si las condiciones adicionales se cumplen
function checkFormValidity() {
    const origen = document.getElementById('origenDataList').value;
    const destino = document.getElementById('destinoDataList').value;
    const cantidadViandas = document.getElementById('cantidadViandas').value;
    const motivo = document.getElementById('motivoDataList').value;

    if (!origen || !destino || !cantidadViandas || !motivo) {
        return false;
    } else
        return true;
}

// Resalta los campos del formulario que están incompletos
function highlightIncompleteFields() {
    const origen = document.getElementById('origenDataList');
    const destino = document.getElementById('destinoDataList');
    const cantidadViandas = document.getElementById('cantidadViandas');
    const motivo = document.getElementById('motivoDataList');
    const requestButtonOrigen = document.getElementById('requestAccessButtonOrigen');
    const requestButtonDestino = document.getElementById('requestAccessButtonDestino');

    if (!origen.value) origen.classList.add('is-invalid');
    else origen.classList.remove('is-invalid');

    if (!destino.value) destino.classList.add('is-invalid');
    else destino.classList.remove('is-invalid');

    if (!cantidadViandas.value) cantidadViandas.classList.add('is-invalid');
    else cantidadViandas.classList.remove('is-invalid');

    if (!motivo.value) motivo.classList.add('is-invalid');
    else motivo.classList.remove('is-invalid');

    if (!requestApproved) {
        requestButtonOrigen.classList.add('btn-danger');
        requestButtonOrigen.textContent = 'Debe solicitar acceso';
        requestButtonDestino.classList.add('btn-danger');
        requestButtonDestino.textContent = 'Debe solicitar acceso';
    } else {
        requestButtonOrigen.classList.remove('btn-danger');
        requestButtonDestino.classList.remove('btn-danger');
    }
}

// Evento para verificar que la cantidad de viandas no exceda la capacidad máxima
document.getElementById('cantidadViandas').addEventListener('input', function() {
    var maxCapacity = 20;
    var cantidadViandas = parseInt(this.value, 10);

    if (cantidadViandas > maxCapacity) {
        alert('La cantidad de viandas no puede exceder la capacidad de la heladera destino. La capacidad máxima es ' + maxCapacity + '.');
        this.value = maxCapacity;
    }
});

// Evento para solicitar acceso a la heladera origen
document.getElementById('requestAccessButtonOrigen').addEventListener('click', function() {
    Swal.fire({
        title: '¿Está seguro de que desea solicitar acceso a la heladera?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, solicitar acceso',
        cancelButtonText: 'Cancelar',
        customClass: {
            confirmButton: 'btn-custom-confirm2',
            cancelButton: 'btn-custom-cancel2'
        }
    }).then((result) => {
        if (result.isConfirmed) {
            this.classList.remove('btn-warning', 'btn-danger');
            this.classList.add('btn-success');
            this.textContent = 'Acceso Solicitado';
            this.style.border = '2px solid green';
            this.disabled = true;
            requestApproved = true;
            Swal.fire({
                title: 'Solicitado',
                text: 'Su solicitud de acceso ha sido enviada.',
                icon: 'success',
                confirmButtonText: 'OK',
                customClass: {
                    confirmButton: 'btn-custom-ok2'
                },
                timer: 3000, // se cierra automáticamente en 3 segundos
                timerProgressBar: true,
            });
        }
    });
});

// Evento para solicitar acceso a la heladera destino
document.getElementById('requestAccessButtonDestino').addEventListener('click', function() {
    Swal.fire({
        title: '¿Está seguro de que desea solicitar acceso a la heladera?',
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, solicitar acceso',
        cancelButtonText: 'Cancelar',
        customClass: {
            confirmButton: 'btn-custom-confirm2',
            cancelButton: 'btn-custom-cancel2'
        }
    }).then((result) => {
        if (result.isConfirmed) {
            this.classList.remove('btn-warning', 'btn-danger');
            this.classList.add('btn-success');
            this.textContent = 'Acceso Solicitado';
            this.style.border = '2px solid green';
            this.disabled = true;
            requestApproved = true;
            Swal.fire({
                title: 'Solicitado',
                text: 'Su solicitud de acceso ha sido enviada.',
                icon: 'success',
                confirmButtonText: 'OK',
                customClass: {
                    confirmButton: 'btn-custom-ok2'
                }
            });
        }
    });
});

// Evento para verificar la validez del formulario al intentar enviarlo
document.getElementById('submitButton').addEventListener('click', function(event) {
    event.preventDefault();
    if (!checkFormValidity()) {
        highlightIncompleteFields();
        return;
    }

    const data = {
        colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
        tipoColaboracion: 'DISTRIBUCION_DE_VIANDAS',
        heladeraOrigen: document.getElementById('origenDataList').value,
        heladeraDestino: document.getElementById('destinoDataList').value,
        cantidadViandas: document.getElementById('cantidadViandas').value,
        motivoDistribucion: document.getElementById('motivoDataList').value
    };

    enviarConfirmacionDistribucionDeViandas(data);
});

document.addEventListener('DOMContentLoaded', function() {
    const heladerasContainerOrigen = document.getElementById('origenDataList');
    const heladerasContainerDestino = document.getElementById('destinoDataList');
    mostrarHeladerasDisponibles(heladerasContainerOrigen);
    mostrarHeladerasDisponibles(heladerasContainerDestino);
});

function mostrarHeladerasDisponibles(heladerasContainer) {
    heladerasContainer.innerHTML = ''; // Limpiar las opciones existentes
    let heladerasInfo = JSON.parse(localStorage.getItem('heladerasInfo')) || {};

    const opcionBase = document.createElement('option');
    opcionBase.value = "";
    opcionBase.textContent = "Seleccione una opción";
    heladerasContainer.appendChild(opcionBase);

    const keys = Object.keys(heladerasInfo);
    const cantidadElementos = keys.length;

    if (cantidadElementos === 0) {
        console.log('No hay heladeras disponibles.');
        return;
    }

    Object.keys(heladerasInfo).forEach(heladeraKey => {
        const heladera = heladerasInfo[heladeraKey];
        const nombreHeladera = heladera.title;
        const estadoHeladera = heladera.status;
    
        // Solo agregar heladeras que no estén en estado DE_BAJA
        if (estadoHeladera !== 'DE_BAJA') {
            const heladeraOption = document.createElement('option');
            heladeraOption.value = nombreHeladera;
            heladeraOption.textContent = nombreHeladera;
            heladerasContainer.appendChild(heladeraOption);
        }
    });
}


function enviarConfirmacionDistribucionDeViandas(data) {
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
        const data2 = {
            tipoColaboracion: 'DISTRIBUCION_DE_VIANDAS',
            heladera: document.getElementById('origenDataList').value,
            heladera2: document.getElementById('destinoDataList').value
        };
        recibirActualizacionDeViandas(data2);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
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


document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        const profileOptions = document.getElementById('profile_options');
        profileOptions.style.display = 'block';
    }
});

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

document.getElementById('requestAccessButtonOrigen').addEventListener('click', solcitarAccesoHeladeraOrigen);
document.getElementById('requestAccessButtonDestino').addEventListener('click', solcitarAccesoHeladeraDestino);

function solcitarAccesoHeladeraOrigen() {
    const data = {
        colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
        heladera: document.getElementById('origenDataList').value
    };
    enviarSolicitudAperturaHeladera(data);
}

function solcitarAccesoHeladeraDestino() {
    const data = {
        colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
        heladera: document.getElementById('destinoDataList').value
    };
    enviarSolicitudAperturaHeladera(data);
}

function enviarSolicitudAperturaHeladera(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/solicitudAperturaHeladera', {
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


function recibirActualizacionDeViandas(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/viandas', {
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
        const heladeraEncontrada = buscarHeladeraPorNombre(respuesta.heladera);
        const heladeraEncontrada2 = buscarHeladeraPorNombre(respuesta.heladera2);
        let cantidadViandas = respuesta.cantidadViandas;
        let cantidadViandas2 = respuesta.cantidadViandas2;
    
        let heladeras = JSON.parse(localStorage.getItem('heladerasInfo'));
        if (!Array.isArray(heladeras)) {
            heladeras = Object.values(heladeras);
        }
    
        const heladeraIndex = heladeras.findIndex(heladera => heladera.id === heladeraEncontrada.id);
        if (heladeraIndex !== -1) {
            heladeras[heladeraIndex].currentCapacity = cantidadViandas;
            localStorage.removeItem('heladerasInfo');
            localStorage.setItem('heladerasInfo', JSON.stringify(heladeras));
        }

        const heladeraIndex2 = heladeras.findIndex(heladera => heladera.id === heladeraEncontrada2.id);
        if (heladeraIndex2 !== -1) {
            heladeras[heladeraIndex2].currentCapacity = cantidadViandas2;
            localStorage.removeItem('heladerasInfo');
            localStorage.setItem('heladerasInfo', JSON.stringify(heladeras));
        }
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function buscarHeladeraPorNombre(nombreHeladera) {
    const heladerasInfo = JSON.parse(localStorage.getItem('heladerasInfo'));
    if (!heladerasInfo) {
        return null;
    }

    const heladera = Object.values(heladerasInfo).find(heladera => heladera.title === nombreHeladera);
    return heladera || null;
}

document.getElementById('back_button').addEventListener('click', function(event){
    event.preventDefault();
    window.location.href = '../colaboraciones.html';
});

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
        window.location.href = 'formDistribucionDeViandas.html';
    }, 3000); // son solo 3 segundos
}
