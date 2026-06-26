// variable global para almacenar el archivo seleccionado
let selectedFileHeladera;

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

document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        enviarSolicitudPerfil(loggedInUser);
    }
});

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
    if (loggedInUser.rolActivo === 'Colaborador') {
        window.location.href = '../index.html';
    } else {
        window.location.href = '../indexTecnico.html';
    }
        
});

document.addEventListener('DOMContentLoaded', function(event) {
    event.preventDefault();
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    enviarSolicitudIncidentesPendientes(loggedInUser);
});

function enviarSolicitudIncidentesPendientes(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/incidentesPendientesAsignados', {
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
        const tableBody = document.getElementById('incidentesTable').getElementsByTagName('tbody')[0];
        const keys = Object.keys(respuesta);
        const cantidadElementos = keys.length;

        if (cantidadElementos === 0) {
            console.log('No hay incidentes pendientes asignados');
            return;
        }
        
        for (let i = 0; i < cantidadElementos; i++) {
            let key = keys[i];
            let incidente = respuesta[key];
            const row = tableBody.insertRow();
            const cellId = row.insertCell(0);
            const cellIncidenteType = row.insertCell(1);
            const cellHeladera = row.insertCell(2);
            const cellDate = row.insertCell(3);
            const cellDetails = row.insertCell(4);

            cellId.textContent = incidente.id;
            cellIncidenteType.textContent = incidente.tipo;
            cellHeladera.textContent = incidente.heladera;
            cellDate.textContent = incidente.fecha;

            // Crear un botón y agregarlo a la celda cellDetails
            const button = document.createElement('detailsButton');
            button.textContent = 'Revisión';
            button.className = 'btn btn-primary';
            button.addEventListener('click', function() {
                showDetails(incidente);
            });
            cellDetails.appendChild(button);
        }
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function showDetails(incidente) {
    const modal = document.getElementById('modal');
    const modalClose = document.getElementById('modal-close');
    const modalTitle = document.getElementById('modal-title');
    const modalBody = document.getElementById('modal-body');

    modal.style.display = 'block';
    modalTitle.textContent = 'Detalle del incidente';

    let visitasHTML = '';
    for (let i = 0; i < incidente.visitasRealizadas; i++) {
        visitasHTML += `
            <div class="visita-item">
                <p class="paragraph-visita"><strong>Visita ${i + 1}:</strong></p>
                <button class="btn btn-info ml-2" onclick="consultarDetalleVisita(${i + 1}, ${incidente.id})">Consultar Detalle</button></p>
            </div>
        `;
    }

    switch (incidente.tipo) {
        case 'Falla Técnica':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${incidente.id}</p>
                <p><strong>Tipo:</strong> ${incidente.tipo}</p>
                <p><strong>Heladera:</strong> ${incidente.heladera}</p>
                <p><strong>Fecha:</strong> ${incidente.fecha}</p>
                <p><strong>Colaborador que lo reportó:</strong> ${incidente.colaborador}</p>
                ${visitasHTML}
                <button class="btn btn-primary btn-visita-heladera" onclick="realizarVisita(${incidente.id}, '${incidente.heladera}')">Realizar Visita</button>
            `;
            break;
        case 'Alerta Falla en la Conexión':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${incidente.id}</p>
                <p><strong>Tipo:</strong> ${incidente.tipo}</p>
                <p><strong>Heladera:</strong> ${incidente.heladera}</p>
                <p><strong>Fecha:</strong> ${incidente.fecha}</p>
                <p><strong>Última temperatura registrada:</strong> ${incidente.ultimaTemperatura}</p>
                ${visitasHTML}
                <button class="btn btn-primary btn-visita-heladera" onclick="realizarVisita(${incidente.id}, '${incidente.heladera}')">Realizar Visita</button>
            `;
            break;
        case 'Alerta de Fraude':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${incidente.id}</p>
                <p><strong>Tipo:</strong> ${incidente.tipo}</p>
                <p><strong>Heladera:</strong> ${incidente.heladera}</p>
                <p><strong>Fecha:</strong> ${incidente.fecha}</p>
                <p><strong>Cantidad de viandas atracadas:</strong> ${incidente.viandasAtracadas}</p>
                ${visitasHTML}
                <button class="btn btn-primary btn-visita-heladera" onclick="realizarVisita(${incidente.id}, '${incidente.heladera}')">Realizar Visita</button>
            `;
            break;
        case 'Alerta de Temperatura':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${incidente.id}</p>
                <p><strong>Tipo:</strong> ${incidente.tipo}</p>
                <p><strong>Heladera:</strong> ${incidente.heladera}</p>
                <p><strong>Fecha:</strong> ${incidente.fecha}</p>
                <p><strong>Temperatura registrada:</strong> ${incidente.temepraturaRegistrada}</p>
                <p><strong>Diferencia de temperatura:</strong> ${incidente.diferenciaTemperatura}</p>
                ${visitasHTML}
                <button class="btn btn-primary btn-visita-heladera" onclick="realizarVisita(${incidente.id}, '${incidente.heladera}')">Realizar Visita</button>
            `;
            break;
        default:
            break;
    }

    desenfocarFondo(modal);

    modalClose.addEventListener('click', function() {
        restablecerFondo(modal);
    });

    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            restablecerFondo(modal);
        }
    });
}

function desenfocarFondo(modal) {
    const overlay = document.getElementById('overlay');
    modal.style.display = 'flex';
    overlay.style.display = 'block';
    document.body.classList.add('blur');
}

function restablecerFondo(modal) {
    const overlay = document.getElementById('overlay');
    modal.style.display = 'none';
    overlay.style.display = 'none';
    document.body.classList.remove('blur');
}

document.getElementById('modal-close').addEventListener('click', function() {
    const modal = document.getElementById('modal');
    restablecerFondo(modal);
});

document.getElementById('closeDropbox').addEventListener('click', function() {
    const myModalDropbox = document.getElementById('myModalDropbox');
    restablecerFondo(myModalDropbox);
});

document.getElementById('overlay').addEventListener('click', function() {
    const modal = document.getElementById('modal');
    const myModalDropbox = document.getElementById('myModalDropbox');
    restablecerFondo(modal);
    restablecerFondo(myModalDropbox);
});

document.getElementById('filter_input').addEventListener('input', function(event) {
    event.preventDefault();
    const textoIngresado = event.target.value;
    filtrarPor(textoIngresado);
});

document.getElementById('filter_month').addEventListener('input', function(event) {
    event.preventDefault();
    const textoIngresado = event.target.value;
    filtrarPor(textoIngresado);
});

document.getElementById('filter_year').addEventListener('input', function(event) {
    event.preventDefault();
    const textoIngresado = event.target.value;
    filtrarPor(textoIngresado);
});

document.getElementById('filter_day').addEventListener('input', function(event) {
    event.preventDefault();
    const textoIngresado = event.target.value;
    filtrarPor(textoIngresado);
});

function filtrarPor(textoIngresado) {
    const filterValue = textoIngresado.toLowerCase();
    const filterDay = document.getElementById('filter_day').value;
    const filterMonth = document.getElementById('filter_month').value;
    const filterYear = document.getElementById('filter_year').value;
    const rows = document.querySelectorAll('#incidentesTable tbody tr');
    const rowType = document.getElementById('row_type_input').value;

    switch (rowType) {
        case '1':
            rows.forEach(row => {
                const tipoIncidente = row.cells[1].textContent.toLowerCase();
                if (tipoIncidente.includes(filterValue)) {
                    row.style.display = 'table-row';
                } else {
                    row.style.display = 'none';
                }
            });
            break;
        case '2':
            rows.forEach(row => {
                const id = row.cells[0].textContent;
                if (id.includes(filterValue)) {
                    row.style.display = 'table-row';
                } else {
                    row.style.display = 'none';
                }
            });
            break;
        case '3':
            rows.forEach(row => {
                const fecha = row.cells[3].textContent;
                const [day, month, rest] = fecha.split('-');
                const [year] = rest.split(' ');
        
                const yearMatches = filterYear === '' || year.startsWith(filterYear);
                const monthMatches = filterMonth === '' || month.startsWith(filterMonth);
                const dayMatches = filterDay === '' || day.startsWith(filterDay);
        
                if (yearMatches && monthMatches && dayMatches) {
                    row.style.display = 'table-row';
                } else {
                    row.style.display = 'none';
                }
            });
            break;
        case '4':
            rows.forEach(row => {
                const heladera = row.cells[2].textContent;
                if (heladera.includes(filterValue)) {
                    row.style.display = 'table-row';
                } else {
                    row.style.display = 'none';
                }
            });
            break;
        default:
            break;
    }
}

document.getElementById("row_type_input").addEventListener("change", function(event) {
    event.preventDefault();

    const filterInput = document.getElementById('filter_input');
    const filterInputColumn = document.getElementById('filterInputColumn');
    const filterDayColumn = document.getElementById('dayFilterInputColumn');
    const filterMonthColumn = document.getElementById('monthFilterInputColumn');
    const filterYearColumn = document.getElementById('yearFilterInputColumn');

    switch (event.target.value) {
        case '0':
            filterInput.placeholder = 'Filtrar';
            filterInput.setAttribute('type', 'text');
            filterDayColumn.style.display = 'none';
            filterMonthColumn.style.display = 'none';
            filterYearColumn.style.display = 'none';
            break;
        case '1':
            filterInputColumn.style.display = 'block';
            filterInput.placeholder = 'Filtrar por tipo de incidente';
            filterInput.setAttribute('type', 'text');
            filterDayColumn.style.display = 'none';
            filterMonthColumn.style.display = 'none';
            filterYearColumn.style.display = 'none';
            break;
        case '2':
            filterInputColumn.style.display = 'block';
            filterInput.placeholder = 'Filtrar por id';
            filterInput.setAttribute('type', 'number');
            filterDayColumn.style.display = 'none';
            filterMonthColumn.style.display = 'none';
            filterYearColumn.style.display = 'none';
            break;
        case '3':
            filterInputColumn.style.display = 'none';
            filterDayColumn.style.display = 'block';
            filterMonthColumn.style.display = 'block';
            filterYearColumn.style.display = 'block';
            break;
        case '4':
            filterInputColumn.style.display = 'block';
            filterInput.placeholder = 'Filtrar por heladera';
            filterInput.setAttribute('type', 'text');
            filterDayColumn.style.display = 'none';
            filterMonthColumn.style.display = 'none';
            filterYearColumn.style.display = 'none';
            break;
        default:
            filterInput.placeholder = 'Filtrar';
            filterInput.setAttribute('type', 'text');
            filterDayColumn.style.display = 'none';
            filterMonthColumn.style.display = 'none';
            filterYearColumn.style.display = 'none';
            break;
    }
});

// Función para consultar el detalle de la visita
function consultarDetalleVisita(visitaNumero, incidente) {
    const data = {
        visita: visitaNumero,
        incidente: incidente,
        usuario: JSON.parse(localStorage.getItem('loggedInUser'))
    };
    solicitarDetalleVisita(data);
}

function solicitarDetalleVisita(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/detalleVisitaHeladera', {
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
        mostrarDetalleVisita(respuesta);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function mostrarDetalleVisita(visita) {
    const modal = document.getElementById('modal');
    const modalClose = document.getElementById('modal-close');
    const modalTitle = document.getElementById('modal-title');
    const modalBody = document.getElementById('modal-body');

    modalBody.innerHTML = '';

    modal.style.display = 'block';
    modalTitle.textContent = 'Detalle de la visita';

    mostrarDetalleVisitaConImagenHeladera(modalBody, visita);

    desenfocarFondo(modal);

    modalClose.addEventListener('click', function() {
        restablecerFondo(modal);
    });

    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            restablecerFondo(modal);
        }
    });
}

function mostrarDetalleVisitaConImagenHeladera(modalBody, visita) {
    fetch('http://heladerassolidarias.myvnc.com:4567/receptorDeArchivos/arregloHeladera/' + visita.foto, {
        method: 'GET',
    })
    .then(response => response.blob())
    .then(respuesta => {
        const reader = new FileReader();
        reader.onloadend = () => {
            const base64data = reader.result;
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${visita.id}</p>
                <p><strong>Heladera:</strong> ${visita.heladera}</p>
                <p><strong>Fecha:</strong> ${visita.fecha}</p>
                <p><strong>Descripción:</strong> ${visita.descripcion}</p>
                <p><strong>Id del incidente asociado:</strong> ${visita.incidenteId}</p>
                <p><strong>Imagen posterior a la visita:</strong></p>
                <img src="${base64data}" alt="Foto de la visita">
            `;
        };
        reader.readAsDataURL(respuesta);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

// Función para consultar el detalle de la visita
function realizarVisita(incidente, heladera) {
    const incidenteId = document.getElementById('idIncidente');
    const heladeraId = document.getElementById('idHeladera');
    incidenteId.setAttribute('value', incidente);
    heladeraId.setAttribute('value', heladera);
    mostrarVentanaEmergente();
    const modal = document.getElementById('modal');
    restablecerFondo(modal);
}

document.getElementById('submitWithoutImage').addEventListener('click', function(event) {
    event.preventDefault();
    const incidenteId = document.getElementById('idIncidente').value;
    const data = {
        incidente: incidenteId,
        imagen: '',
        usuario: JSON.parse(localStorage.getItem('loggedInUser'))
    };
    solicitarRealizarVisita(data);
    const myModalDropbox = document.getElementById('myModalDropbox');
    restablecerFondo(myModalDropbox);
});

document.getElementById('submitImage').addEventListener('click', function(event) {
    event.preventDefault();
    const heladera = document.getElementById('idHeladera').value;
    const formData = new FormData();
    const heladeraName = heladera;
    const fileExtension = selectedFileHeladera.name.split('.').pop();
    const newFileName = generateUniqueFilename(heladeraName, fileExtension);
    formData.append('file', selectedFileHeladera, newFileName);
    formData.append('motivo', 'arregloHeladera');
    enviarImagenArregloHeladera(formData);

    const incidenteId = document.getElementById('idIncidente').value;
    const data = {
        incidente: incidenteId,
        imagen: newFileName,
        usuario: JSON.parse(localStorage.getItem('loggedInUser'))
    };

    solicitarRealizarVisita(data);
    const myModalDropbox = document.getElementById('myModalDropbox');
    restablecerFondo(myModalDropbox);
});

function generateUniqueFilename(healderaName, extension) {
    const timestamp = Date.now();
    const randomPart = Math.random().toString(36).substring(2, 8);
    return `${healderaName}_${timestamp}_${randomPart}.${extension}`;
}

function solicitarRealizarVisita(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/realizarVisitaHeladera', {
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
        window.location.href = 'realizarVisitaHeladera.html';
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function enviarImagenArregloHeladera(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/receptorDeArchivos', {
        method: 'POST',
        body: data,
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

// logica de control del dropzone
document.addEventListener('DOMContentLoaded', () => {
    const dropzone = document.getElementById('dropzone');
    const fileInput = document.getElementById('fileInput');
    const preview = document.getElementById('preview');
    const dropzoneMessage = document.getElementById('dropzone-message');
    let selectedFile;

    dropzone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropzone.classList.add('dragover');
    });

    dropzone.addEventListener('dragleave', () => {
        dropzone.classList.remove('dragover');
    });

    dropzone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropzone.classList.remove('dragover');
        handleFile(e.dataTransfer.files[0]);
    });

    dropzone.addEventListener('click', () => {
        fileInput.click();
    });

    fileInput.addEventListener('change', () => {
        if (fileInput.files.length > 0) {
            handleFile(fileInput.files[0]);
        }
    });

    function handleFile(file) {
        const validTypes = ['image/png', 'image/jpg', 'image/jpeg'];
        if (file && validTypes.includes(file.type)) {
            selectedFile = file;
            selectedFileHeladera = file;
            const reader = new FileReader();
            reader.onload = (e) => {
                preview.innerHTML = `
                    <div class="image-container">
                        <img src="${e.target.result}" alt="Preview">
                        <button class="remove-button" onclick="removeImage(event)">
                            <i class="fa fa-times"></i>
                        </button>
                    </div>
                `;
                dropzoneMessage.style.display = 'none';
            };
            reader.readAsDataURL(file);
            const submitImage = document.getElementById('submitImage');
            submitImage.disabled = false;
        } else {
            alert('Por favor selecciona un archivo de imagen válido (JPG, PNG, JPEG).');
        }
    }

    window.removeImage = function(event) {
        event.stopPropagation(); // Evita que el clic se propague al dropzone
        selectedFile = null;
        preview.innerHTML = '';
        dropzoneMessage.style.display = 'block';
        fileInput.value = ''; // Resetea el input de archivo
        const submitImage = document.getElementById('submitImage');
        submitImage.disabled = true;
    };
});

function mostrarVentanaEmergente() {
    // Mostrar la ventana emergente
    document.getElementById('myModalDropbox').style.display = 'block';
}

// Cerrar la ventana emergente cuando se hace clic en la "x"
document.querySelector('.closeDropbox').addEventListener('click', function() {
    document.getElementById('myModalDropbox').style.display = 'none';
});

// Cerrar la ventana emergente cuando se hace clic fuera de ella
window.addEventListener('click', function(event) {
    if (event.target == document.getElementById('myModalDropbox')) {
        document.getElementById('myModalDropbox').style.display = 'none';
    }
});
