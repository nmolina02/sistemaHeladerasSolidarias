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


let premios = JSON.parse(localStorage.getItem('premios'));

document.addEventListener('DOMContentLoaded', function(event) {
    event.preventDefault();
    const data = {
        solicitud: 'Solicitando premios'
    }; 
    enviarSolicitudPremios(data);
});

function enviarSolicitudPremios(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/premios', {
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
        const messagge = document.getElementById('messagge-premios-vacios');
        if (respuesta === 'No hay premios cargados') {
            messagge.style.display = 'block';
            return;
        }
        messagge.style.display = 'none';
        let premios = [];
        const keys = Object.keys(respuesta);
        const cantidadElementos = keys.length;
        for (let i = 0; i < cantidadElementos; i++) {
            let key = keys[i];
            let premio = respuesta[key];
            premios.push(premio);
        }
        mostrarPremios(premios);
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

function mostrarPremios(premios) {
    let premiosDiv = document.getElementById('premiosDiv');
    premiosDiv.innerHTML = '';
    if (!premios || premios.length === 0) {
        return;
    }
    premios.forEach(premio => {
        let premioDiv = document.createElement('div');
        const premioCategoria = convertirPremioCategoria(premio.categoria);
        premioDiv.className = 'col-md-4 mb-4';
        mostrarPremioConImagen(premioDiv, premio, premioCategoria);
        premiosDiv.appendChild(premioDiv);
    });
    canjearProducto(premios, premiosDiv);
}

function mostrarPremioConImagen(premioDiv, premio, premioCategoria) {
    fetch('http://heladerassolidarias.myvnc.com:4567/receptorDeArchivos/premioCargado/' + premio.imagen, {
        method: 'GET',
    })
    .then(response => response.blob())
    .then(respuesta => {
        const reader = new FileReader();
        reader.onloadend = () => {
            const base64data = reader.result;
            premioDiv.innerHTML = `
            <div class="card premio-card">
                <div class="img_producto">
                    <img src="${base64data}" class="card-img-top" alt="${premio.nombre}">
                </div>
                <div class="card-body">
                    <h5 class="card-title">${premio.nombre}</h5>
                    <h6 class="categoria-premio">Rubro: ${premioCategoria}</h6>
                    <p class="card-text">${premio.descripcion}</p>
                    <p><strong>${premio.puntos_necesarios} puntos</strong></p>
                    <a href="#" class="button-canjear btn btn-primary" id="${premio.id}">Canjear</a>
                </div>
            </div>
        `;
        };
        reader.readAsDataURL(respuesta);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function convertirPremioCategoria(categoria) {
    switch (categoria) {
        case 'GASTRONOMICO':
            return 'Gastronomía';
        case 'ELECTRONICA':
            return 'Electrónica';
        case 'HOGAR':
            return 'Hogar';
        default:
            return 'Otro';
    }
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

function canjearProducto(premios, premiosDiv) {
    premiosDiv.addEventListener('click', function(event) {
        if (event.target && event.target.classList.contains('button-canjear')) {
            event.preventDefault();
            const button = event.target;
            seleccionarProducto(button.id, premios);
        }
    });
}

function seleccionarProducto(id, premios) {
    const premio = premios.find(premio => premio.id === id);
    const data = {
        colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
        premio: premio
    }
    enviarCanjeoProducto(data);
}


function enviarCanjeoProducto(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/canjeoProducto', {
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
        if (respuesta === 'No hay suficientes puntos') {
            mostrarAlertaError('No tienes puntos suficientes para canjear este producto');
            return;
        }
        else if (respuesta === 'No se pudo canjear el premio') {
            mostrarAlertaError('No se pudo canjear el producto');
            return;
        }
        updateDataProfile(respuesta.username, respuesta.points);
        mostrarAlertaExito('Producto canjeado');
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

document.getElementById('premiosCanjeados').addEventListener('click', function() {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    solicitarPremiosCanjeados(loggedInUser);
});

function solicitarPremiosCanjeados(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/solicitarPremiosCanjeados', {
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
        if (respuesta === 'No existen premios canjeados') {
            mostrarAlertaInformativa('No has canjeado premios');
            return;
        }
        mostrarPremiosCanjeados(respuesta);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function mostrarPremiosCanjeados(premiosCanjeados) {
    const premiosList = document.getElementById('premiosList');
    premiosList.innerHTML = ''; // Limpio la lista antes de agregar los premios

    const keys = Object.keys(premiosCanjeados);
    const cantidadElementos = keys.length;

    for (let i = 0; i < cantidadElementos; i++) {
        let key = keys[i];
        let premio = premiosCanjeados[key];
        const div = document.createElement('div');
        div.className = 'col-md-3';
        div.innerHTML = `
            <div>
                <h3>${premio.nombre}</h3>
                <p>Puntos: ${premio.puntos_necesarios}</p>
                <p>Categoría: ${premio.categoria}</p>
                <p>Descripción: ${premio.descripcion}</p>
            </div>
        `;
        premiosList.appendChild(div);
    }

    const modal = document.getElementById('modal');
    desenfocarFondo();

    const span = document.getElementsByClassName('close')[0];
    span.onclick = function() {
        restablecerFondo();
    }

    window.onclick = function(event) {
        if (event.target == modal) {
            restablecerFondo();
        }
    }
}

function desenfocarFondo() {
    const modal = document.getElementById('modal');
    const overlay = document.getElementById('overlay');
    modal.style.display = 'flex';
    overlay.style.display = 'block';
    document.body.classList.add('blur');
}

function restablecerFondo() {
    const modal = document.getElementById('modal');
    const overlay = document.getElementById('overlay');
    modal.style.display = 'none';
    overlay.style.display = 'none';
    document.body.classList.remove('blur');
}

document.getElementById('modal-close').addEventListener('click', restablecerFondo);
document.getElementById('overlay').addEventListener('click', restablecerFondo);

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
        window.location.href = 'premios.html';
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