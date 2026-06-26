document.addEventListener('DOMContentLoaded', function() {
    var requestApproved = false;

    // Evento para habilitar/deshabilitar campos
    document.getElementById('knowCalories').addEventListener('change', function() {
        var caloriesInput = document.getElementById('calories');
        caloriesInput.disabled = !this.checked;
    });
    document.getElementById('knowWeight').addEventListener('change', function() {
        var weightInput = document.getElementById('weight');
        weightInput.disabled = !this.checked;
    });

// Evento para validar el formulario
    document.getElementById('donationForm').addEventListener('submit', function(event) {
        event.preventDefault(); // Prevenir el envío del formulario

        if (!checkFormValidity()) {
            highlightIncompleteFields();
            return;
        }

        const buttonAccess = document.getElementById('requestAccessButton');
        const data = {
            colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
            tipoColaboracion: 'DONACION_DE_VIANDAS',
            comida: document.getElementById('productDescription').value,
            calorias: document.getElementById('calories').value,
            peso: document.getElementById('weight').value,
            fechaVencimiento: document.getElementById('expirationDate').value,
            heladera: document.getElementById('fridge').value,
            accesoSolicitado: buttonAccess.textContent
        };
        
        enviarConfirmacionDonacionDeViandas(data);
    });


   // Evento para solicitar acceso a la heladera
    document.getElementById('requestAccessButton').addEventListener('click', function() {
        const button = this;
        Swal.fire({
            title: '¿Está seguro de que desea solicitar acceso a la heladera?',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Sí, solicitar',
            cancelButtonText: 'Cancelar',
            customClass: {
                confirmButton: 'btn-custom-confirm2',
                cancelButton: 'btn-custom-cancel2'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                button.classList.remove('btn-warning', 'btn-danger');
                button.classList.add('btn-success');
                button.textContent = 'Acceso Solicitado';
                button.style.border = '2px solid green';
                button.disabled = true; // Deshabilitar el botón
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


    // Función para verificar la validez del formulario
    function checkFormValidity() {
        var productDescription = document.getElementById('productDescription').value;
        var expirationDate = document.getElementById('expirationDate').value;

        return productDescription && expirationDate && requestApproved;
    }

    // Función para resaltar los campos incompletos
    function highlightIncompleteFields() {
        const productDescription = document.getElementById('productDescription');
        const expirationDate = document.getElementById('expirationDate');
        const requestButton = document.getElementById('requestAccessButton');

        if (!productDescription.value) {
            productDescription.classList.add('is-invalid');
        } else {
            productDescription.classList.remove('is-invalid');
        }

        if (!expirationDate.value) {
            expirationDate.classList.add('is-invalid');
        } else {
            expirationDate.classList.remove('is-invalid');
        }

        if (!requestApproved) {
            requestButton.classList.add('btn-danger');
            requestButton.textContent = 'Debe solicitar acceso';
        } else {
            requestButton.classList.remove('btn-danger');
        }
    }

    // Eventos para verificar la validez del formulario en tiempo real
    document.getElementById('productDescription').addEventListener('input', checkFormValidity);
    document.getElementById('expirationDate').addEventListener('input', checkFormValidity);
});

function enviarConfirmacionDonacionDeViandas(data) {
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
            tipoColaboracion: 'DONACION_DE_VIANDAS',
            heladera: document.getElementById('fridge').value,
        };
        recibirActualizacionDeViandas(data2);
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

//mostar heladeras
document.addEventListener('DOMContentLoaded', function() {
    const heladerasContainer = document.getElementById('fridge');
    mostrarHeladerasDisponibles(heladerasContainer);
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

document.getElementById('requestAccessButton').addEventListener('click', solcitarAccesoHeladera);

function solcitarAccesoHeladera() {
    const data = {
        colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
        heladera: document.getElementById('fridge').value
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
        let cantidadViandas = respuesta.cantidadViandas;
    
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

document.getElementById('requestRecomendationButton').addEventListener('click', function() {    
    fetch(`https://ipinfo.io/json?token=6788d34c9d39b0`)
    .then(response => response.json())
    .then(geolocalizacion => {
        const [latitude, longitude] = geolocalizacion.loc.split(',');
        const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
        const data = {
            latitud: parseFloat(latitude),
            longitud: parseFloat(longitude),
            colaborador: loggedInUser,
        };
        solicitarPuntosRecomendados(data);
    })
    .catch((error) => {
        console.error('Error al obtener la ubicación:', error);
    });
});

function solicitarPuntosRecomendados(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/recomendadorDonaciones', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(respuesta => {
        if (respuesta === 'No se encontraron heladeras recomendadas') {
            mostrarAlertaError('No se encontraron puntos recomendados en su ubicación');
            return;
        } else if (respuesta === 'No se encontró el colaborador') {
            mostrarAlertaError('No se encontró el colaborador');
        } else {
            obtenerPuntosRecomendados(respuesta);
        }
    })
    .catch(error => {
        console.error('Error en la solicitud:', error);
    });
}

document.getElementById('requestRecomendationButton').addEventListener('click', function(event) {
    event.preventDefault();
    // Mostrar la ventana emergente
    document.getElementById('myModal').style.display = 'block';
});

// Cerrar la ventana emergente cuando se hace clic en la "x"
document.querySelector('.close').addEventListener('click', function() {
    document.getElementById('myModal').style.display = 'none';
});

// Cerrar la ventana emergente cuando se hace clic fuera de ella
window.addEventListener('click', function(event) {
    if (event.target == document.getElementById('myModal')) {
        document.getElementById('myModal').style.display = 'none';
    }
});

function obtenerPuntosRecomendados(respuesta) {
    let recomendaciones = {};

    const keys = Object.keys(respuesta);
    const cantidadElementos = keys.length;

    for (let i = 0; i < cantidadElementos; i++) {
        let key = keys[i];
        let punto = respuesta[key];

        let altura = punto.altura;
        let calle = punto.calle;
        let ciudad = punto.ciudad;
        let nombre = punto.nombre;

        let recomendacion = {
            nombre: nombre,
            calle: calle,
            altura: altura,
            ciudad: ciudad,
        };

        recomendaciones = { ...recomendaciones, [i] : recomendacion };
    }

    // Mostrar los puntos recomendados en la lista
    llenarListaRecomendaciones(recomendaciones);
}

function llenarListaRecomendaciones(recomendaciones) {
    const listaRecomendaciones = document.getElementById('puntosRecomendadosList');
    listaRecomendaciones.innerHTML = ''; // Limpiar la lista existente

    Object.keys(recomendaciones).forEach(recomendacionId => {
        const recomendacion = recomendaciones[recomendacionId];
        const elementoLista = document.createElement('div');
        elementoLista.className = 'punto-recomendado';
        elementoLista.textContent = `${recomendacion.nombre} (${recomendacion.calle} ${recomendacion.altura}, ${recomendacion.ciudad})`;
        elementoLista.addEventListener('click', function() {
            alert(`Has seleccionado: ${recomendacion.nombre} (${recomendacion.calle} ${recomendacion.altura}, ${recomendacion.ciudad})`);
            completarHeladeraSeleccionada(recomendacion.nombre);
            document.getElementById('myModal').style.display = 'none';
        });
        listaRecomendaciones.appendChild(elementoLista);
    });
}

function completarHeladeraSeleccionada(nombreHeladera) {
    const ubicacionHeladera = document.getElementById('fridge');
    const opciones = ubicacionHeladera.options;

    for (let i = 0; i < opciones.length; i++) {
        if (opciones[i].value === nombreHeladera) {
            opciones[i].selected = true;
            break;
        }
    }
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
        window.location.href = 'formDonacionDeViandas.html';
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
