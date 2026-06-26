document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        const profileOptions = document.getElementById('profile_options');
        profileOptions.style.display = 'block';
        enviarSolicitudPerfil(loggedInUser);

        if (loggedInUser.userType === 'J') {
            document.getElementById('nav-link-falla-heladera').style.display = 'none';
            document.getElementById('nav-link-suscripcion-heladeras').style.display = 'none';
            document.getElementById('tarjetaSolicitada').style.display = 'none';
        }

        const profilePoints = document.getElementById('profile_points');
        const profilePoints2 = document.getElementById('profile_points_2');
        const colaboratorAction1 = document.getElementById('canjearProducto');
        const colaboratorAction2 = document.getElementById('tarjetaSolicitada');
        const colaboratorAction3 = document.getElementById('misColaboraciones');

        const navLinkInicio = document.getElementById('nav-link-inicio');
        const navLinkColaboraciones = document.getElementById('nav-link-colaboraciones');
        const navLinkFallaHeladera = document.getElementById('nav-link-falla-heladera');
        const navLinkCargarCsv = document.getElementById('nav-link-cargar-csv');
        const navLinkSuscripcionHeladeras = document.getElementById('nav-link-suscripcion-heladeras');

        const mainIcon = document.getElementById('main_icon');
        const navLinkTecnicoIncidente = document.getElementById('nav-link-tecnico-incidente');
        const navLinkTecnicoVisita = document.getElementById('nav-link-tecnico-visita');
        const navLinkAdmin = document.getElementById('nav-link-admin');

        if (loggedInUser.userColaboradorRol !== 'Colaborador') {            
            profilePoints.style.display = 'none';
            profilePoints2.style.display = 'none';
            colaboratorAction1.style.display = 'none';
            colaboratorAction2.style.display = 'none';
            colaboratorAction3.style.display = 'none';

            navLinkColaboraciones.style.display = 'none';
            navLinkFallaHeladera.style.display = 'none';
            navLinkSuscripcionHeladeras.style.display = 'none';

            if (loggedInUser.userAdminRol === 'Administrador') {
                navLinkAdmin.style.display = 'block';
                navLinkInicio.setAttribute('href', '../indexAdmin.html');
                mainIcon.setAttribute('href', '../indexAdmin.html');
                navLinkCargarCsv.style.display = 'block';
            } else {
                navLinkTecnicoIncidente.style.display = 'block';
                navLinkTecnicoVisita.style.display = 'block';
                navLinkInicio.setAttribute('href', '../indexTecnico.html');
                mainIcon.setAttribute('href', '../indexTecnico.html');
                navLinkCargarCsv.style.display = 'none';
            }
        }

        else if (loggedInUser.userColaboradorRol === 'Colaborador' && loggedInUser.userTecnicoRol === 'Técnico') {
            if (loggedInUser.rolActivo === 'Colaborador') {
                profilePoints.style.display = 'block';
                profilePoints2.style.display = 'block';
                colaboratorAction1.style.display = 'block';
                colaboratorAction2.style.display = 'block';
                colaboratorAction3.style.display = 'block';

                navLinkColaboraciones.style.display = 'block';
                navLinkFallaHeladera.style.display = 'block';
                navLinkCargarCsv.style.display = 'none';
                navLinkSuscripcionHeladeras.style.display = 'block';

                navLinkTecnicoIncidente.style.display = 'none';
                navLinkTecnicoVisita.style.display = 'none';
                navLinkAdmin.style.display = 'none';
                navLinkInicio.setAttribute('href', '../index.html');
                mainIcon.setAttribute('href', '../index.html');

            } else if (loggedInUser.rolActivo === 'Técnico') {
                profilePoints.style.display = 'none';
                profilePoints2.style.display = 'none';
                colaboratorAction1.style.display = 'none';
                colaboratorAction2.style.display = 'none';
                colaboratorAction3.style.display = 'none';

                navLinkColaboraciones.style.display = 'none';
                navLinkFallaHeladera.style.display = 'none';
                navLinkCargarCsv.style.display = 'none';
                navLinkSuscripcionHeladeras.style.display = 'none';

                navLinkTecnicoIncidente.style.display = 'block';
                navLinkTecnicoVisita.style.display = 'block';
                navLinkInicio.setAttribute('href', '../indexTecnico.html');
                mainIcon.setAttribute('href', '../indexTecnico.html');
            }
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

function updateProfileView(respuesta) {
    const userNameElement = document.getElementById('colaborator_name_field');
    const profileNameElement = document.querySelector('#username_field span');
    const profileColaboratorElement = document.querySelector('#type_colaborator_field span');
    const profileEmailElement = document.querySelector('#email_field span');
    const profilePhoneElement = document.querySelector('#phone_field span');
    const profileDniElement = document.querySelector('#dni_field span');
    const profileBirthdateElement = document.querySelector('#birthdate_field span');
    const profileAddressElement = document.querySelector('#address_field span');
    const profileRegisterDateElement = document.querySelector('#register_date_field span');
    const profileImageElement = document.getElementById('user_image');
    const profileDescriptionElement = document.querySelector('#description_field');
    const profilePicElement = document.getElementById('profile_pic');
    const profileCardField = document.getElementById('card_field');

    const profileDniField = document.getElementById('dni_field');
    const profileBirthdateField = document.getElementById('birthdate_field');
    const profileCompanyTypeField = document.getElementById('company_type_field');
    const profileRubroField = document.getElementById('rubro_field');

    const profileCompanyTypeElement = document.querySelector('#company_type_field span');
    const profileRubroElement = document.querySelector('#rubro_field span');

    const profilePointsColaborationsField = document.getElementById('user_points_field');

    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));

    if (loggedInUser && respuesta.userType === 'Persona Humana'
        || respuesta.userType === 'Técnico'
        || respuesta.userType === 'Administrador') {
        profileCompanyTypeField.style.display = 'none';
        profileRubroField.style.display = 'none';

        profileDniField.style.display = 'block';
        profileBirthdateField.style.display = 'block';

        userNameElement.textContent = respuesta.name + ' ' + respuesta.surname;
        profileNameElement.textContent = profileNameElement.textContent + ' ' + respuesta.username;
        profileColaboratorElement.textContent = profileColaboratorElement.textContent + ' ' + respuesta.userType;
        profileEmailElement.textContent = profileEmailElement.textContent + ' ' + respuesta.email;
        profilePhoneElement.textContent = profilePhoneElement.textContent + ' ' + respuesta.phone;
        profileDniElement.textContent = profileDniElement.textContent + ' ' + respuesta.dni;
        profileBirthdateElement.textContent = profileBirthdateElement.textContent + ' ' + respuesta.birthdate;
        profileAddressElement.textContent = profileAddressElement.textContent + ' ' + respuesta.address;
        profileRegisterDateElement.textContent = profileRegisterDateElement.textContent + ' ' + respuesta.registerDate;
        profileImageElement.src = '../' + respuesta.userImage;
        profileDescriptionElement.textContent = respuesta.description;
        profilePicElement.src = '../' + respuesta.userImage;

        if (loggedInUser && respuesta.userType === 'Técnico'
            || respuesta.userType === 'Administrador') {
            profileCardField.style.display = 'none';
            profilePointsColaborationsField.style.display = 'none';
        } else {
            profileCardField.style.display = 'block';
            profileCardField.innerHTML = `
                <i class="icono fas fa-id-card"></i> Tarjeta: ${respuesta.card}
            `;
            profilePointsColaborationsField.style.display = 'block';
            profilePointsColaborationsField.innerHTML = `
                <i class="icono fas fa-coins"></i> Puntos: ${respuesta.points}
            `;
        }
    }

    else if (loggedInUser && respuesta.userType === 'Persona Jurídica') {
        profileDniField.style.display = 'none';
        profileBirthdateField.style.display = 'none';
        
        profileCompanyTypeField.style.display = 'block';
        profileRubroField.style.display = 'block';

        profileCardField.style.display = 'none';

        userNameElement.textContent = respuesta.razonSocial;
        profileNameElement.textContent = profileNameElement.textContent + ' ' + respuesta.username;
        profileColaboratorElement.textContent = profileColaboratorElement.textContent + ' ' + respuesta.userType;
        profileEmailElement.textContent = profileEmailElement.textContent + ' ' + respuesta.email;
        profilePhoneElement.textContent = profilePhoneElement.textContent + ' ' + respuesta.phone;
        profileCompanyTypeElement.textContent = profileCompanyTypeElement.textContent + ' ' + respuesta.companyType;
        profileRubroElement.textContent = profileRubroElement.textContent + ' ' + respuesta.rubro;
        profileAddressElement.textContent = profileAddressElement.textContent + ' ' + respuesta.address;
        profileRegisterDateElement.textContent = profileRegisterDateElement.textContent + ' ' + respuesta.registerDate;
        profileImageElement.src = '../' + respuesta.userImage;
        profileDescriptionElement.textContent = respuesta.description;
        profilePicElement.src = '../' + respuesta.userImage;
        profilePointsColaborationsField.style.display = 'block';
        profilePointsColaborationsField.innerHTML = `
            <i class="icono fas fa-coins"></i> Puntos: ${respuesta.points}
        `;
    }
}

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

document.getElementById('avatar_button').addEventListener('click', function(event){
    event.preventDefault();
    document.getElementById('file_input').click();
});

document.getElementById('file_input').addEventListener('change', function(event){
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            const img = document.getElementById('user_image');
            img.src = e.target.result;
            img.style.top = "0";
            img.style.left = "0";
        };

        reader.readAsDataURL(file);
    }

    const formData = new FormData();
    const fileExtension = file.name.split('.').pop();
    const fileName = document.getElementById('profile_name').textContent;
    const newFileName = fileName + '.' + fileExtension;
    formData.append('file', file, newFileName);
    formData.append('motivo', 'imagenUsuario');
    enviarImagenActualizarPerfil(formData);
});

function enviarImagenActualizarPerfil(data) {
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
        mostrarAlertaExito('La imagen de perfil ha sido actualizada');
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
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
            const img = document.getElementById('user_image');
            img.src = base64data;
            img.style.top = "0";
            img.style.left = "0";
        };
        reader.readAsDataURL(respuesta);
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

const descriptionButton = document.getElementById('description_button');
const descriptionField = document.getElementById('description_field');
const descriptionEdit = document.getElementById('description_edit');
const saveButton = document.getElementById('save_button');
const cancelButton = document.getElementById('cancel_button');

let originalDescription = descriptionField.textContent;

descriptionButton.addEventListener('click', () => {
    originalDescription = descriptionField.textContent;
    descriptionField.style.display = 'none';
    descriptionEdit.style.display = 'block';
    descriptionEdit.value = '';
    saveButton.style.display = 'inline-block';
    cancelButton.style.display = 'inline-block';
    descriptionButton.style.display = 'none';
});

saveButton.addEventListener('click', () => {
    descriptionField.textContent = descriptionEdit.value || 'Desea agregar una descripción...';
    
    descriptionEdit.style.display = 'none';
    descriptionField.style.display = 'block';
    saveButton.style.display = 'none';
    cancelButton.style.display = 'none';
    descriptionButton.style.display = 'inline-block';

    const data = {
        usuario: JSON.parse(localStorage.getItem('loggedInUser')),
        descripcion: descriptionEdit.value
    };

    enviarConfirmacionSolicitudDescripcion(data);
});

function enviarConfirmacionSolicitudDescripcion(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/actualizacionDescripcion', {
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
        mostrarAlertaExito('Descripción actualizada');
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
        window.location.href = 'myProfile.html';
    }, 3000); // son solo 3 segundos
}

cancelButton.addEventListener('click', () => {
    descriptionEdit.style.display = 'none';
    descriptionField.style.display = 'block';
    descriptionField.textContent = originalDescription;
    saveButton.style.display = 'none';
    cancelButton.style.display = 'none';
    descriptionButton.style.display = 'inline-block';
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
        updateProfileView(respuesta);
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
    window.location.href = 'myProfile.html';
});