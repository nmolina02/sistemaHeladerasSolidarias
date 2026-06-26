const dropArea = document.getElementById("file_upload_box");
const selectFileButton = document.getElementById("select_og");
const sendButton = document.getElementById("send_button");
let file;


function handleFile(event){
    console.log(event);
    
    let fileType = file.type;
    let fileName = file.name;
    let validExtensions = ["text/csv"];
    let drag_tip = document.getElementById("file_tip");
    let inputLabel = document.getElementById("select_file_button");
    let sendButton = document.getElementById("send_button");

    if (validExtensions.includes(fileType)) {
        drag_tip.innerHTML = fileName;
        drag_tip.classList.add("active");
        inputLabel.innerHTML = "Cambiar archivo";
        inputLabel.classList.add("active");
        sendButton.disabled = false;
        sendButton.classList.add("active");

    }else{
        alert("Formato de archivo invalido, formato esperado: .csv");
        dropArea.classList.remove("active");
    }
}

sendButton.addEventListener("click", function () {
    let drag_tip = document.getElementById("file_tip");
    let inputLabel = document.getElementById("select_file_button");

    drag_tip.innerHTML = "Puede arrastrar y soltar un archivo CSV aquí para añadirlo"
    inputLabel.innerHTML = "Seleccionar archivo CSV de su dispositivo"
    drag_tip.classList.remove("active");
    inputLabel.classList.remove("active");
    sendButton.classList.remove("active");
    dropArea.classList.remove("active");
    sendButton.disabled = true;
    sendButton.classList.remove("active");

    const formData = new FormData();
    formData.append('file', file);
    enviarCSV(formData);

})

function enviarCSV(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/cargarCSV', {
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
        mostrarAlertaExito('Archivo CSV cargado exitosamente');
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

selectFileButton.addEventListener("change", function(event){
    dropArea.classList.add("active");
    file = event.target.files[0];

    handleFile(event);
});


dropArea.addEventListener("dragover", function(event){
    dropArea.classList.add("active");
    event.preventDefault();
})

dropArea.addEventListener("dragleave", function(event){
    dropArea.classList.remove("active");
    event.preventDefault();
})

dropArea.addEventListener("drop", function(event){
    event.preventDefault();
    file = event.dataTransfer.files[0];

    handleFile(event);
})

document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    if (loggedInUser) {
        hideLoginButton();
        enviarSolicitudPerfil(loggedInUser);
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
        window.location.href = 'cargarCSV.html';
    }, 3000); // son solo 3 segundos
}