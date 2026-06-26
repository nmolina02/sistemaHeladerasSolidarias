// INICIO CONSTANTES
// Mapa
let map = L.map('map').setView([-34.6438077,-58.4044591],12)

//Agregar tilelAyer mapa base desde openstreetmap
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png',{
  attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
}).addTo(map);

// Marcadores de heladeras sanas
const customIcon = L.icon({
    iconUrl: '../images/info_icons/marker.png', // Ruta imagen de marcador
    iconSize: [30, 30],
    iconAnchor: [12, 12],
    popupAnchor: [1, -34],
});


// Marcadores de heladeras con alertas
const customAlertIcon = L.icon({
    iconUrl: '../images/info_icons/marker_alerta.png', // Ruta imagen de marcador
    iconSize: [30, 30],
    iconAnchor: [12, 12],
    popupAnchor: [1, -34],
});

// Marcador oficina central
const centralIcon = L.icon({
    iconUrl: '../images/info_icons/sede.png', // Ruta imagen de marcador
    iconSize: [41, 41],
    iconAnchor: [41, 41],
    popupAnchor: [1, -34],
});

// Información de la sedes centrales
let sedesInfo = {
    'sede': {
        title: 'Oficina Central',
        latitud: -34.5956424,
        longitud: -58.3930435,
        calle: 'Avenida Callao',
        altura: '1103',
        owner: 'Grupo 04',
        fechaInauguracion: new Date(2024,2,22),
        img: '../images/sedes/central_office.jpg'
    }
};

// Información de cada heladera
let heladerasInfo = JSON.parse(localStorage.getItem('heladerasInfo'));
// FIN CONSTANTES

document.addEventListener('DOMContentLoaded', fetchAndProcessData);

function fetchAndProcessData() {
    // Obtener los datos de la heladera y agregar un marcador

    const dataSolicitud = {
        solicitud: 'Solicitando marcadores'
    };

    fetch('http://heladerassolidarias.myvnc.com:4567/heladera', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(dataSolicitud),
    })
    .then(response => {
        // Revisa si la respuesta tiene éxito
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        const keys = Object.keys(data);
        const cantidadElementos = keys.length;

        if (cantidadElementos === 0) {
            console.log('No se agregó ninguna heladera nueva.');
            return;
        }

        heladerasInfo = {};
        localStorage.removeItem('heladerasInfo');
        
        for (let i = 0; i < cantidadElementos; i++) {
            let key = keys[i];
            let heladera = data[key];
            let lat = parseFloat(heladera.latitud);
            let lon = parseFloat(heladera.longitud);
            // tomamos los valores para formar la fecha de inauguracion
            let fechaInauguracionString = heladera.fechaInauguracion;
            let partesFecha = fechaInauguracionString.split('-');
            let anio = parseInt(partesFecha[0]);
            let mes = parseInt(partesFecha[1]);
            let dia = parseInt(partesFecha[2]);

            // Crear un objeto Date con los valores obtenidos
            let fecha = new Date(anio, mes - 1, dia); // Restar 1 al mes porque los meses en JavaScript son 0-indexados
            
            let heladeraNueva = {
                title: heladera.nombreHeladera,
                latitud: lat,
                longitud: lon,
                calle: heladera.calle,
                altura: heladera.altura,
                status: heladera.estadoHeladera,
                owner: heladera.propietario,
                currentCapacity: heladera.viandas,
                maxCapacity: heladera.capacidadMaxima,
                fechaInauguracion: fecha,
                img: heladera.img,
                alertas: heladera.alertas
            }

            agregarMarcador(heladeraNueva);
        }
        
        localStorage.setItem('heladerasInfo', JSON.stringify(heladerasInfo));
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

// INICIO FUNCIONES
// FUNCION PARA AGREGAR MARCADORES
function agregarMarcador(heladera) {
    const heladeraNombre = heladera.title.toLowerCase().replace(/ /g, "_");
    // Agregar nueva heladera a heladerasInfo
    heladerasInfo = { ...heladerasInfo, [heladeraNombre] : heladera };
    placeMarkers(heladerasInfo);
    llenarListaHeladeras();
    updateHeladerasCount();
}
// FIN FUNCION PARA AGREGAR MARCADORES

function llenarListaHeladeras() {
    const listaHeladeras = document.getElementById('heladeras-list');
    listaHeladeras.innerHTML = ''; // Limpiar la lista existente

    if (heladerasInfo === null) {
        return;
    }

    Object.keys(heladerasInfo).forEach(heladeraId => {
        const heladera = heladerasInfo[heladeraId];
        const elementoLista = document.createElement('li');
        elementoLista.innerHTML = `<img src="../images/info_icons/heladeras.png" alt="Item"><a href="#" onclick="moveToLocation('${heladeraId}', 'heladera'); showHeladeraInfo('${heladeraId}')">${heladera.title}</a><span>${heladera.calle + ' ' + heladera.altura}</span>`;
        listaHeladeras.appendChild(elementoLista);
    });
}

// Llamar a llenarListaHeladeras() para inicializar la lista cuando la página carga o cuando se actualiza heladerasInfo
document.addEventListener('DOMContentLoaded', llenarListaHeladeras);

// FUNCION PARA COLOCAR LOS MARCADORES
function placeMarkers(heladerasInfo) {
    for (let key in sedesInfo) {
        let infoOffice = sedesInfo[key];
        let locationOffice = { lat: infoOffice.latitud, lng: infoOffice.longitud };
        let centralOffice = L.marker([locationOffice.lat, locationOffice.lng], { icon: centralIcon }).addTo(map);
        centralOffice.on('click', enableMapInteraction);
        centralOffice.on('click', moveToLocation.bind(this, key, 'sede'));
        centralOffice.on('click', showSedeInfo.bind(this, key));
    }
    for (let key in heladerasInfo) {
        if (heladerasInfo.hasOwnProperty(key)) {
            let info = heladerasInfo[key];
            let location = { lat: info.latitud, lng: info.longitud };
            let marker
            if (info.status === 'FUNCIONAMIENTO') {
                marker = L.marker([location.lat, location.lng], { icon: customIcon }).addTo(map);
            } else {
                marker = L.marker([location.lat, location.lng], { icon: customAlertIcon }).addTo(map);
            }
            marker.on('click', enableMapInteraction);
            marker.on('click', moveToLocation.bind(this, key), 'heladera');
            marker.on('click', showHeladeraInfo.bind(this, key));
        }
    }
}
// FIN FUNCION PARA COLOCAR LOS MARCADORES

// FUNCION PARA MOVER EL MAPA A LA UBICACIÓN DE UNA HELADERA O UNA SEDE
// tipoLugar puede ser 'heladera' o 'sede'
function moveToLocation(elementId, tipoLugar) {
    if(tipoLugar === 'sede') {
        const info = sedesInfo[elementId];
        if (info) {
            const location = { lat: info.latitud, lng: info.longitud };
            map.flyTo([location.lat, location.lng], 18);
        }
    }
    else {
        const info = heladerasInfo[elementId];
        if (info) {
            const location = {lat: info.latitud, lng: info.longitud};
            map.flyTo([location.lat, location.lng], 18);
        }
    }
}
// FIN FUNCION PARA MOVER EL MAPA A LA UBICACIÓN DE UNA HELADERA O UNA SEDE

// FUNCION ACTUALIZAR CONTEO DE HELADERAS
function updateHeladerasCount() {
    const heladerasList = document.getElementById('heladeras-list');
    const heladerasCount = document.getElementById('heladeras-count');
    heladerasCount.textContent = heladerasList.getElementsByTagName('li').length;
}

// Se la invoca cuando la página termina de cargar
document.addEventListener('DOMContentLoaded', updateHeladerasCount);
// FIN FUNCION ACTUALIZAR CONTEO DE HELADERAS


// FUNCION DESACTIVAR/ACTIVAR INTERACCION DEL MAPA
// Desactivar interacción del mapa
function disableMapInteraction() {
    map.dragging.disable(); 
    map.scrollWheelZoom.disable();
    map.touchZoom.disable();
    map.doubleClickZoom.disable();
    map.boxZoom.disable();
}

// Activar interacción del mapa
function enableMapInteraction() {
    map.dragging.enable();
    map.scrollWheelZoom.enable();
    map.touchZoom.enable(); 
    map.doubleClickZoom.enable();
    map.boxZoom.enable();
}

disableMapInteraction();

map.on('click', enableMapInteraction);

document.addEventListener('keydown', function(event) {
    if (event.code === 'Escape') {
        disableMapInteraction();
    }
});
// FIN FUNCION DESACTIVAR INTERACCIÓN DEL MAPA

// FUNCION PARA COMPROBAR EL ESTADO DE LA HELADERA
function verificarStatus(estadoHeladera) {
    switch (estadoHeladera) {
        case 'En Funcionamiento':
            return "../images/info_icons/funcionamiento_on.png";
        case 'Fuera de Servicio':
            return "../images/info_icons/funcionamiento_off.png";
        default:
            return "../images/info_icons/funcionamiento_incierto.png";
    }
}
// FIN FUNCION PARA COMPROBAR EL ESTADO DE LA HELADERA

// FUNCION PARA MOSTRAR LA INFO DE UNA HELADERA
// Función para mostrar la información de la heladera en la ventana emergente
function showHeladeraInfo(heladeraId) {
    const info = heladerasInfo[heladeraId];
    if (info) {
        const popupImage = document.getElementById('popup-image');
        const popupInfo = document.getElementById('popup-info');
        const estadoHeladera = calcular_estado_heladera(info.status);
        const statusImage = verificarStatus(estadoHeladera);
        const antiguedadHeladera = calcular_antiguedad_heladera(info.fechaInauguracion);
        const data = {
            imagen: info.img,
            motivo: 'creacionHeladera',
        };
        solicitarImagenHeladera(data, popupImage);

        let ultimaAlertaRegistrada;

        if (info.alertas !== 'No hay alertas registradas') {
            ultimaAlertaRegistrada = 'Última alerta registrada: ' + info.alertas;
        } else {
            ultimaAlertaRegistrada = info.alertas;
        }


        popupInfo.innerHTML = `
            <h2>${info.title}</h2>
            <p><img src=${statusImage} alt="TipoFuncionamiento">${estadoHeladera}</p>
            <p><img src="images/info_icons/propietario.png" alt="Propietario">${info.owner}</p>
            <img src="images/info_icons/viandas.png" alt="Viandas"><label for="capacity-bar">${info.currentCapacity}</label>
            <progress id="capacity-bar" class="capacity-bar" value=${info.currentCapacity} max=${info.maxCapacity}></progress>
            <label for="capacity-bar">${info.maxCapacity}</label>
            <p class= "beforelastOptionInfoPopup"><img src="images/info_icons/calendario.png" alt="Calendario">${antiguedadHeladera}</p>
            <p><img src="images/info_icons/alerta.png" alt="Alerta">${ultimaAlertaRegistrada}</p>
        `;
        document.getElementById('info-popup').style.display = 'block';
    }
}

function solicitarImagenHeladera(data, popupImage) {
    fetch('http://heladerassolidarias.myvnc.com:4567/receptorDeArchivos/' + data.motivo + '/' + data.imagen, {
        method: 'GET',
    })
    .then(response => response.blob())
    .then(respuesta => {
        const reader = new FileReader();
        reader.onloadend = () => {
            const base64data = reader.result;
            popupImage.style.backgroundImage = `url(${base64data})`;
        };
        reader.readAsDataURL(respuesta);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

// FUNCION PARA MOSTRAR INFO DE LA SEDE CENTRAL
// Función para mostrar la información de la sede en la ventana emergente
function showSedeInfo(sedeId) {
    const info = sedesInfo[sedeId];
    if (info) {
        const popupImage = document.getElementById('popup-image');
        const popupInfo = document.getElementById('popup-info');
        const antiguedadSede = calcular_antiguedad_heladera(info.fechaInauguracion);
        popupImage.style.backgroundImage = `url(${info.img})`;
        popupInfo.innerHTML = `
            <h2>${info.title}</h2>
            <p><img src="../images/info_icons/propietario.png" alt="Propietario">${info.owner}</p>
            <p><img src="images/info_icons/ubicacion.png" alt="Ubicacion">${info.calle} ${info.altura}</p>
            <p><img src="images/info_icons/calendario.png" alt="Calendario">${antiguedadSede}</p>
        `;
        document.getElementById('info-popup').style.display = 'block';
    }
}

// Función para cerrar la ventana emergente
function closePopup() {
    document.getElementById('info-popup').style.display = 'none';
}

// FIN FUNCION PARA MOSTRAR LA INFO DE UNA HELADERA

// FUNCION PARA CALCULAR ANTIGUEDAD HELADERA
function calcular_antiguedad_heladera(fechaInauguracion) {
    const fechaActual = new Date();
    const fechaInauguracionFormateada = new Date(fechaInauguracion);
    const diferenciaEnMilisegundos = fechaActual.getTime() - fechaInauguracionFormateada.getTime();

    // Convertir milisegundos a días
    const diferenciaEnDias = diferenciaEnMilisegundos / (1000 * 3600 * 24);
    if(diferenciaEnDias < 0){
        return `0 días`;
    }
    else if(diferenciaEnDias === 1) {
        return `${Math.floor(diferenciaEnDias)} día`;
    }
    else if(diferenciaEnDias <= 31 && diferenciaEnDias > 0) {
        return `${Math.floor(diferenciaEnDias)} días`;
    }
    else if (diferenciaEnDias < 62) {
        return `${Math.floor(diferenciaEnDias / 31)} mes`;
    }
    else if(diferenciaEnDias <= 365) {
        return `${Math.floor(diferenciaEnDias / 31)} meses`;
    }
    else if (diferenciaEnDias < 730) {
        return `${Math.floor(diferenciaEnDias / 365)} año`;
    }
    else {
        return `${Math.floor(diferenciaEnDias / 365)} años`;
    }
}
// FIN FUNCION PARA CALCULAR ANTIGUEDAD HELADERA

// FUNCION PARA CALCULAR EL ESTADO DE LA HELADERA
function calcular_estado_heladera(estado) {
    switch (estado) {
        case 'FUNCIONAMIENTO':
            return 'En Funcionamiento';
        case 'DE_BAJA':
            return 'Fuera de Servicio';
        case 'EN_REPARACION':
            return 'Fuera de Servicio';
        default:
            return 'Indefinido';
    }
}
// FIN FUNCION PARA CALCULAR EL ESTADO DE LA HELADERA
// FIN FUNCIONES

// INICIO ACCIONES
// COLOCAR LOS MARCADORES LUEGO DE QUE CARGA EL MAPA
map.on('load', function() {
    placeMarkers(heladerasInfo);
});

if (map.whenReady) {
    map.whenReady(() => {
        map.fire('load');
    });
}
// FIN DE COLOCAR LOS MARCADORES LUEGO DE QUE CARGA EL MAPA
// FIN ACCIONES

document.getElementById('main_icon').addEventListener('click', function() {
    window.location.href = 'indexTecnico.html';
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
    window.location.href = 'login.html'; // Redirigir al login después de cerrar sesión
}

function updateDataProfile(name) {
    const profileNameElement = document.getElementById("profile_name");
    const profileNameElement2 = document.getElementById("profile_name_2");

    if (profileNameElement && profileNameElement2) {
        if (name.length > 10) {
            name = name.substring(0, 7) + '...';
        }
        profileNameElement.textContent = name;
        profileNameElement2.textContent = name;
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

document.addEventListener('DOMContentLoaded', cambioDeEstadoHeladera);

function cambioDeEstadoHeladera() {
    const dataSolicitud = {
        solicitud: 'Solicitando estados heladera'
    };
    
    fetch('http://heladerassolidarias.myvnc.com:4567/estadoHeladera', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(dataSolicitud),
    })
    .then(response => {
        // Revisa si la respuesta tiene éxito
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(respuesta => {
        const keys = Object.keys(respuesta);
        const cantidadElementos = keys.length;
        
        for (let i = 0; i < cantidadElementos; i++) {
            let key = keys[i];
            let heladera = respuesta[key];
            const heladeraEncontrada = buscarHeladeraPorNombre(heladera.nombre);
            if (heladeraEncontrada === null) {
                return;
            }
            else {
                let estadoHeladera = heladera.estado;

                let heladeras = JSON.parse(localStorage.getItem('heladerasInfo'));
                if (!Array.isArray(heladeras)) {
                    heladeras = Object.values(heladeras);
                }
            
                const heladeraIndex = heladeras.findIndex(heladera => heladera.id === heladeraEncontrada.id);
                if (heladeraIndex !== -1) {
                    heladeras[heladeraIndex].status = estadoHeladera;
                    localStorage.removeItem('heladerasInfo');
                    localStorage.setItem('heladerasInfo', JSON.stringify(heladeras));
                }
            }
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

document.addEventListener('DOMContentLoaded', changeRol);

function changeRol() {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    const changeRolOption = document.getElementById('change_rol');
    if (loggedInUser && loggedInUser.userColaboradorRol === 'Colaborador' && loggedInUser.userTecnicoRol === 'Técnico') {
        changeRolOption.style.display = 'block';
        changeRolOption.textContent = 'Cambiar a Colaborador';
    } else {
        changeRolOption.style.display = 'none';
    }
}

document.getElementById('change_rol').addEventListener('click', function(event) {
    event.preventDefault();
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    loggedInUser.rolActivo = 'Colaborador';
    localStorage.setItem('loggedInUser', JSON.stringify(loggedInUser));
    window.location.href = 'index.html';
});

document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        hideLoginButton();
        enviarSolicitudPerfil(loggedInUser);
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
        updateDataProfile(respuesta.username);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}