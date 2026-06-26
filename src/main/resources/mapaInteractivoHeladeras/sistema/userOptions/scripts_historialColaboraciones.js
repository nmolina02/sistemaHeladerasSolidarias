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
    window.location.href = '../indexTecnico.html';
});

document.addEventListener('DOMContentLoaded', function(event) {
    event.preventDefault();
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    enviarSolicitudColaboracionesRealizadas(loggedInUser);
});

function enviarSolicitudColaboracionesRealizadas(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/colaboracionesRealizadas', {
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
        const tableBody = document.getElementById('colaboracionesTable').getElementsByTagName('tbody')[0];
        const keys = Object.keys(respuesta);
        const cantidadElementos = keys.length;

        if (cantidadElementos === 0) {
            console.log('No hay colaboraciones realizadas');
            return;
        }
        
        for (let i = 0; i < cantidadElementos; i++) {
            let key = keys[i];
            let colaboration = respuesta[key];
            const row = tableBody.insertRow();
            const cellId = row.insertCell(0);
            const cellColaborationType = row.insertCell(1);
            const cellDate = row.insertCell(2);
            const cellDetails = row.insertCell(3);

            cellId.textContent = colaboration.id;
            cellColaborationType.textContent = colaboration.tipo;
            cellDate.textContent = colaboration.fecha;

            // Crear un botón y agregarlo a la celda cellDetails
            const button = document.createElement('detailsButton');
            button.textContent = 'Ver Detalle';
            button.className = 'btn btn-primary';
            button.addEventListener('click', function() {
                showDetails(colaboration);
            });
            cellDetails.appendChild(button);
        }
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function showDetails(colaboration) {
    const modal = document.getElementById('modal');
    const modalClose = document.getElementById('modal-close');
    const modalTitle = document.getElementById('modal-title');
    const modalBody = document.getElementById('modal-body');

    modal.style.display = 'block';
    modalTitle.textContent = 'Detalle de la colaboración';

    switch (colaboration.tipo) {
        case 'Donación de Dinero':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${colaboration.id}</p>
                <p><strong>Tipo:</strong> ${colaboration.tipo}</p>
                <p><strong>Fecha:</strong> ${colaboration.fecha}</p>
                <p><strong>Monto:</strong> ${colaboration.monto}</p>
                <p><strong>Frecuencia:</strong> ${convertirFrecuencia(colaboration.frecuencia)}</p>
            `;
            break;
        case 'Hacerse Cargo':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${colaboration.id}</p>
                <p><strong>Tipo:</strong> ${colaboration.tipo}</p>
                <p><strong>Fecha:</strong> ${colaboration.fecha}</p>
                <p><strong>Heladera:</strong> ${colaboration.heladera}</p>
                <p><strong>Ubicación:</strong> ${colaboration.ubicacion}</p>
            `;
            break;
        case 'Donación de Viandas':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${colaboration.id}</p>
                <p><strong>Tipo:</strong> ${colaboration.tipo}</p>
                <p><strong>Fecha:</strong> ${colaboration.fecha}</p>
                <p><strong>Heladera:</strong> ${colaboration.heladera}</p>
                <p><strong>Cantidad de viandas:</strong> ${colaboration.cantidadViandas}</p>
            `;
            break;
        case 'Distribución de Viandas':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${colaboration.id}</p>
                <p><strong>Tipo:</strong> ${colaboration.tipo}</p>
                <p><strong>Fecha:</strong> ${colaboration.fecha}</p>
                <p><strong>Heladera origen:</strong> ${colaboration.heladeraOrigen}</p>
                <p><strong>Heladera destino:</strong> ${colaboration.heladeraDestino}</p>
                <p><strong>Cantidad de viandas:</strong> ${colaboration.cantidadViandas}</p>
                <p><strong>Motivo:</strong> ${convertirMotivo(colaboration.motivo)}</p>
            `;
            break;
        case 'Ofrecimiento de Producto':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${colaboration.id}</p>
                <p><strong>Tipo:</strong> ${colaboration.tipo}</p>
                <p><strong>Fecha:</strong> ${colaboration.fecha}</p>
                <p><strong>Producto:</strong> ${colaboration.producto}</p>
                <p><strong>Categoría:</strong> ${convertirCategoria(colaboration.categoria)}</p>
                <p><strong>Descripción:</strong> ${colaboration.descripcion}</p>
                <p><strong>Puntos necesarios:</strong> ${colaboration.puntosNecesarios}</p>
            `;
            break;
        case 'Registro de Personas Vulnerables':
            modalBody.innerHTML = `
                <p><strong>ID:</strong> ${colaboration.id}</p>
                <p><strong>Tipo:</strong> ${colaboration.tipo}</p>
                <p><strong>Fecha:</strong> ${colaboration.fecha}</p>
                <p><strong>Persona vulnerable registrada:</strong> ${colaboration.persona}</p>
                <p><strong>Tutor de la persona:</strong> ${colaboration.tutor}</p>
            `;
            break;
        default:
            break;
    }

    modalClose.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
}

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
    const rows = document.querySelectorAll('#colaboracionesTable tbody tr');
    const rowType = document.getElementById('row_type_input').value;

    switch (rowType) {
        case '1':
            rows.forEach(row => {
                const tipoColaboracion = row.cells[1].textContent;
                if (tipoColaboracion.includes(filterValue)) {
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
                const fecha = row.cells[2].textContent;
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
            filterInputColumn.style.display = 'none';
            filterDayColumn.style.display = 'none';
            filterMonthColumn.style.display = 'none';
            filterYearColumn.style.display = 'none';
            break;
        case '1':
            filterInputColumn.style.display = 'block';
            filterInput.placeholder = 'Filtrar por tipo de colaboración';
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
        default:
            filterInput.placeholder = 'Filtrar';
            filterInput.setAttribute('type', 'text');
            filterDayColumn.style.display = 'none';
            filterMonthColumn.style.display = 'none';
            filterYearColumn.style.display = 'none';
            break;
    }
});

function convertirFrecuencia(frecuencia) {
    switch (frecuencia) {
        case 'UNICO':
            return 'Único';
        case 'SEMANAL':
            return 'Semanal';
        case 'MENSUAL':
            return 'Mensual';
        case 'ANUAL':
            return 'Anual';
        default:
            return '';
    }
}

function convertirMotivo(motivo) {
    switch (motivo) {
        case 'DESPERFECTO':
            return 'Desperfecto';
        case 'FALTA_DE_VIANDAS':
            return 'Falta de Viandas';
        default:
            return '';
    }
}

function convertirCategoria(categoria) {
    switch (categoria) {
        case 'ELECTRONICA':
            return 'Electrónica';
        case 'HOGAR':
            return 'Hogar';
        case 'GASTRONOMICO':
            return 'Gastronómico';
        default:
            return '';
    }
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
        window.location.href = 'historialColaboraciones.html';
    }, 3000); // son solo 3 segundos
}