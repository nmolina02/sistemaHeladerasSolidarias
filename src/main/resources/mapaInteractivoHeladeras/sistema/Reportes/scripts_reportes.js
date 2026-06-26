// variable global para almacenar el archivo seleccionado
let selectedFileFallaHeladera;

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
            selectedFileFallaHeladera = file;
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
    };
});


document.getElementById('submit_button').addEventListener('click', function(event) {
    event.preventDefault();
    
    let nombreFileHeladera = "";

    if (selectedFileFallaHeladera !== undefined) {
        const formData = new FormData();
        const heladeraName = document.getElementById('fridge').value;
        const fileExtension = selectedFileFallaHeladera.name.split('.').pop();
        const newFileName = generateUniqueFilename(heladeraName, fileExtension);
        nombreFileHeladera = newFileName;
        formData.append('file', selectedFileFallaHeladera, newFileName);
        formData.append('motivo', 'reportarFallaHeladera');
        enviarImagenFallaHeladera(formData);
    }

    const reporte = {
        heladera: document.getElementById('fridge').value,
        colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
        descripcion: document.getElementById('issue_description').value,
        imagen: nombreFileHeladera,
        tipoGravedad: document.getElementById('issue_type').value
    };

    enviarReporteFalla(reporte);
});

function generateUniqueFilename(healderaName, extension) {
    const timestamp = Date.now();
    const randomPart = Math.random().toString(36).substring(2, 8);
    return `${healderaName}_${timestamp}_${randomPart}.${extension}`;
}

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

function enviarReporteFalla(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/reportarFalla', {
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
        mostrarAlertaExito('Falla reportada');
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function enviarImagenFallaHeladera(data) {
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
        window.location.href = 'reportarFalla.html';
    }, 3000); // son solo 3 segundos
}
