// variable global para almacenar el archivo seleccionado
let selectedFilePremio;

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
    if (window.innerWidth > 767) {
        navLinks.style.display = 'flex';
        menuOpen = false;
    }
    else {
        navLinks.style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('ofrecerProductoForm').addEventListener('submit', function(event) {
        event.preventDefault(); // Prevenir el envío del formulario

        if (!checkFormValidity()) {
            highlightIncompleteFields();
            return;
        }

        let nombreFilePremio = "";

        if (selectedFilePremio !== undefined) {
            const formData = new FormData();
            const premioName = document.getElementById('productName').value;
            const fileExtension = selectedFilePremio.name.split('.').pop();
            const newFileName = generateUniqueFilename(premioName, fileExtension);
            nombreFilePremio = newFileName;
            formData.append('file', selectedFilePremio, newFileName);
            formData.append('motivo', 'premioCargado');
            enviarImagenPremio(formData);
        }

        if (nombreFilePremio === "") {
            nombreFilePremio = "premioSinImagen.jpg";
        }

        const data = {
            colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
            tipoColaboracion: 'OFRECER_PRODUCTO_RECONOCIMIENTO',
            productName: document.getElementById('productName').value,
            descripcion: document.getElementById('productDescription').value,
            puntosNecesarios: document.getElementById('productPoints').value,
            category: document.getElementById('productCategory').value,
            productPhoto: nombreFilePremio,
        };

        enviarConfirmacionOfrecerProductoReconocimiento(data);

    });

    // Función para verificar la validez del formulario
    function checkFormValidity() {
        const productName = document.getElementById('productName').value;
        const productDescription = document.getElementById('productDescription').value;
        const productCategory = document.getElementById('productCategory').value;

        return productName && productDescription && productCategory;
    }

    // Función para resaltar los campos incompletos
    function highlightIncompleteFields() {
        const fields = ['productName', 'productDescription', 'productCategory'];

        fields.forEach(function(fieldId) {
            const field = document.getElementById(fieldId);
            if (!field.value) {
                field.classList.add('is-invalid');
            } else {
                field.classList.remove('is-invalid');
            }
        });
    }
});

function generateUniqueFilename(healderaName, extension) {
    const timestamp = Date.now();
    const randomPart = Math.random().toString(36).substring(2, 8);
    return `${healderaName}_${timestamp}_${randomPart}.${extension}`;
}

function enviarConfirmacionOfrecerProductoReconocimiento(data) {
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
            selectedFilePremio = file;
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

function enviarImagenPremio(data) {
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
        window.location.href = 'formOfrecerProductoReconocimiento.html';
    }, 3000); // son solo 3 segundos
}
