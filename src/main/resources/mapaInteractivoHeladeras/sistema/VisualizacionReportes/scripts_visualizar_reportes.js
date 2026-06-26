//variable global para saber el ultimo reporte consultado
let lastReport = null;

document.getElementById('tipoSolicitud').addEventListener('change', function(event) {
    event.preventDefault();
    if (event.target.value !== '0') {
        const generateReportButton = document.getElementById('generateReportButton');
        generateReportButton.disabled = false;
    } else {
        const generateReportButton = document.getElementById('generateReportButton');
        generateReportButton.disabled = true;
    }
});

document.getElementById('generateReportButton').addEventListener('click', function(event) {
    event.preventDefault();
    const data = {
        tipoSolicitud: document.getElementById('tipoSolicitud').value,
        solicitante: JSON.parse(localStorage.getItem('loggedInUser')),
    }
    generarReporte(data);
});

document.getElementById('filterTypeInput').addEventListener('input', function(event) {
    event.preventDefault();
    const textoIngresado = event.target.value;
    filtrarPorTipoReporte(textoIngresado);
});

document.getElementById('filterDateInput').addEventListener('input', function(event) {
    event.preventDefault();
    const textoIngresado = event.target.value;
    filtrarPorFecha(textoIngresado);
});

function filtrarPorTipoReporte(textoIngresado) {
    const filterValue = textoIngresado.toLowerCase();
    const rows = document.querySelectorAll('#reportesTable tbody tr');

    let variableComparativa = '';
    if (filterValue === '1') {
        variableComparativa = 'Fallas de Heladera';
    } else if (filterValue === '2') {
        variableComparativa = 'Movimientos de Heladera';
    } else if (filterValue === '3') {
        variableComparativa = 'Viandas Donadas por Colaborador';
    }

    rows.forEach(row => {
        const tipoReporte = row.cells[1].textContent;
        if (tipoReporte === variableComparativa) {
            row.style.display = 'table-row';
        } else if (filterValue === '0') {
            row.style.display = 'table-row';
        } else {
            row.style.display = 'none';
        }
    });
}

function filtrarPorFecha(textoIngresado) {
    const filterValue = textoIngresado.split('-').reverse().join('-')
    const rows = document.querySelectorAll('#reportesTable tbody tr');

    rows.forEach(row => {
        const fechaEmision = row.cells[2].textContent;
        const fechaVencimiento = row.cells[3].textContent;
        if (fechaEmision === filterValue || fechaVencimiento === filterValue) {
            row.style.display = 'table-row';
        } else if (filterValue === '') {
            row.style.display = 'table-row';
        } else {
            row.style.display = 'none';
        }
    });
}

document.getElementById('solicitar_reporte_button').addEventListener('click', function(event) {
    event.preventDefault();
    // Mostrar la ventana emergente
    var myModal = new bootstrap.Modal(document.getElementById('exampleModal'));
    myModal.show();
});

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
        else if (loggedInUser.userAdminRol === 'Administrador' || loggedInUser.rolActivo === 'Técnico') {
            document.getElementById('reportar_falla_heladera').style.display = 'none';
            document.getElementById('suscripcion_heladeras').style.display = 'none';
            document.getElementById('tarjetaSolicitada').style.display = 'none';
            document.getElementById('colaboraciones').style.display = 'none';
            document.getElementById('misColaboraciones').style.display = 'none';
            document.getElementById('canjeoProductos').style.display = 'none';
            document.getElementById("profile_points").style.display = 'none';
            document.getElementById("profile_points_2").style.display = 'none';

            if (loggedInUser.userAdminRol === 'Administrador') {
                document.getElementById('nav-link-cargar-csv').style.display = 'block';
                document.getElementById('nav-link-tecnico-incidente').style.display = 'none';
                document.getElementById('nav-link-tecnico-visita').style.display = 'none';
                document.getElementById('nav-link-admin').style.display = 'block';
            } else {
                document.getElementById('nav-link-cargar-csv').style.display = 'none';
                document.getElementById('nav-link-tecnico-incidente').style.display = 'block';
                document.getElementById('nav-link-tecnico-visita').style.display = 'block';
                document.getElementById('nav-link-admin').style.display = 'none';
            }
        }
    }
});

document.getElementById('main_icon').addEventListener('click', function() {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if(loggedInUser) {
        if (loggedInUser.userType === 'Administrador') {
            window.location.href = 'indexAdmin.html';
        } else if (loggedInUser.userType === 'Técnico') {
            window.location.href = 'indexTecnico.html';
        }
        else {
            window.location.href = 'index.html';
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

function generarReporte(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/generarReporte', {
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
        mostrarAlertaExito('Reporte generado');
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

document.getElementById('exportarPDFButton').addEventListener('click', function(event){
    event.preventDefault();
    const data = {
        solicitante: JSON.parse(localStorage.getItem('loggedInUser')),
        reporte: lastReport,
    }
    exportarReportePdf(data);
});

function exportarReportePdf(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/exportarReportePdf', {
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
        lastReport = null;
        mostrarAlertaExito('Reporte exportado a PDF');
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

document.addEventListener('DOMContentLoaded', function() {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    solicitarReportesExistentes(loggedInUser);
});

function solicitarReportesExistentes(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/solicitarReportesExistentes', {
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
        agregarReportes(respuesta);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function agregarReportes(reportes) {
    const tableBody = document.getElementById('reportesTable').getElementsByTagName('tbody')[0];
    const keys = Object.keys(reportes);
    const cantidadElementos = keys.length;

    for (let i = 0; i < cantidadElementos; i++) {
        let key = keys[i];
        let reporte = reportes[key];
        const row = tableBody.insertRow();
        const cellId = row.insertCell(0);
        const cellReporteType = row.insertCell(1);
        const cellFechaSolicitud = row.insertCell(2);
        const cellFechaVencimiento = row.insertCell(3);
        const cellSolicitante = row.insertCell(4);
        const cellDetails = row.insertCell(5);

        cellId.textContent = reporte.id;
        cellReporteType.textContent = reporte.tipoReporte;
        cellFechaSolicitud.textContent = reporte.fechaSolicitud;
        cellFechaVencimiento.textContent = reporte.fechaVencimiento;
        cellSolicitante.textContent = reporte.solicitante;

        // Crear un botón y agregarlo a la celda cellDetails
        const button = document.createElement('detailsButton');
        button.textContent = 'Ver Detalle';
        button.className = 'btn btn-primary';
        button.addEventListener('click', function() {
            solicitarDetalleReporte(reporte.id, reporte.tipoReporte, reporte.solicitante);
            desenfocarFondo();

            const span = document.getElementById('modal-close');
            span.onclick = function() {
                modal.style.display = 'none';
                restablecerFondo();
            }

            window.onclick = function(event) {
                if (event.target == modal) {
                    modal.style.display = 'none';
                    restablecerFondo();
                }
            }
        });
        cellDetails.appendChild(button);
    }
}

function desenfocarFondo() {
    const modal = document.getElementById('modal');
    const overlay = document.getElementById('overlay');
    modal.style.display = 'flex';
    overlay.style.display = 'block';
    document.body.classList.add('blur');
    document.body.classList.add('no-scroll');
}

function restablecerFondo() {
    const modal = document.getElementById('modal');
    const overlay = document.getElementById('overlay');
    modal.style.display = 'none';
    overlay.style.display = 'none';
    document.body.classList.remove('blur');
    document.body.classList.remove('no-scroll');
}

document.getElementById('modal-close').addEventListener('click', restablecerFondo);
document.getElementById('overlay').addEventListener('click', restablecerFondo);

function showDetails(reporte) {
    const modal = document.getElementById('modal');
    const modalClose = document.getElementById('modal-close');
    const modalTitle = document.getElementById('modal-title');
    const modalBody = document.getElementById('modal-body');
    const exportarPdfButton = document.getElementById('exportarPDFButton');

    exportarPdfButton.style.display = 'block';

    modal.style.display = 'block';
    modalTitle.textContent = 'Reporte ' + reporte.tipoReporte + ' (' + reporte.fechaSolicitud + ' - ' + reporte.fechaVencimiento + ')';

    switch (reporte.tipoReporte) {
        case 'Fallas de Heladera':
            modalBody.innerHTML = `
                <table class="table table-striped" id="reportesTable">
                    <thead>
                        <tr class="header_table">
                            <th>Id</th>
                            <th>Heladera</th>
                            <th>Cantidad de fallas</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${mostrarLineaPorLinea(reporte.detalleReporte, reporte.tipoReporte)}
                    </tbody>
                </table>
            `;
            break;
        case 'Movimientos de Heladera':
            modalBody.innerHTML = `
                <table class="table table-striped" id="reportesTable">
                    <thead>
                        <tr class="header_table">
                            <th>Id</th>
                            <th>Heladera</th>
                            <th>Cantidad de viandas ingresadas</th>
                            <th>Cantidad de viandas retiradas</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${mostrarLineaPorLinea(reporte.detalleReporte, reporte.tipoReporte)}
                    </tbody>
                </table>
            `;
            break;
        case 'Viandas Donadas por Colaborador':
            modalBody.innerHTML = `
                <table class="table table-striped" id="reportesTable">
                    <thead>
                        <tr class="header_table">
                            <th>Id</th>
                            <th>Colaborador</th>
                            <th>Cantidad de viandas donadas</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${mostrarLineaPorLinea(reporte.detalleReporte, reporte.tipoReporte)}
                    </tbody>
                </table>
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

function mostrarLineaPorLinea(detalleReporte, reporteTipo) {
    let lineas = '';
    const keys = Object.keys(detalleReporte);
    const cantidadElementos = keys.length;
    if (reporteTipo === 'Fallas de Heladera') {
        for (let i = 0; i < cantidadElementos; i++) {
            let key = keys[i];
            let detalle = detalleReporte[key];
            lineas += `
                <tr>
                    <td>${detalle.id}</td>
                    <td>${detalle.heladera}</td>
                    <td>${detalle.cantidadFallas}</td>
                </tr>
            `;
        }
    }
    else if (reporteTipo === 'Movimientos de Heladera') {
        for (let i = 0; i < cantidadElementos; i++) {
            let key = keys[i];
            let detalle = detalleReporte[key];
            lineas += `
                <tr>
                    <td>${detalle.id}</td>
                    <td>${detalle.heladera}</td>
                    <td>${detalle.cantidadViandasIngresadas}</td>
                    <td>${detalle.cantidadViandasRetiradas}</td>
                </tr>
            `;
        }
    }
    else if (reporteTipo === 'Viandas Donadas por Colaborador') {
        for (let i = 0; i < cantidadElementos; i++) {
            let key = keys[i];
            let detalle = detalleReporte[key];
            lineas += `
                <tr>
                    <td>${detalle.id}</td>
                    <td>${detalle.colaborador}</td>
                    <td>${detalle.cantidadViandasDonadas}</td>
                </tr>
            `;
        }
    }
    return lineas;
}

function solicitarDetalleReporte(idReporte, tipoReporte, solicitante) {
    const data = {
        idReporte: idReporte,
        tipoReporte: tipoReporte,
        solicitante: solicitante,
    }
    enviarSolicitudDetalleReporte(data);
}

function enviarSolicitudDetalleReporte(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/solicitarDetalleReporte', {
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
        lastReport = data.idReporte;
        showDetails(respuesta);
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
    window.location.href = 'visualizarReportes.html';
});

document.addEventListener('DOMContentLoaded', function() {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    const navLinkInicio = document.getElementById('nav-link-inicio');
    const mainIcon = document.getElementById('main_icon');
    if (loggedInUser && loggedInUser.rolActivo === 'Técnico') {
        navLinkInicio.setAttribute('href', '../indexTecnico.html');
        mainIcon.setAttribute('href', '../indexTecnico.html');
    } else if (loggedInUser && loggedInUser.rolActivo === 'Colaborador') {
        navLinkInicio.setAttribute('href', '../index.html');
        mainIcon.setAttribute('href', '../index.html');
    } else {
        navLinkInicio.setAttribute('href', '../indexAdmin.html');
        mainIcon.setAttribute('href', '../indexAdmin.html');
    }
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
        window.location.href = 'visualizarReportes.html';
    }, 3000); // son solo 3 segundos
}
