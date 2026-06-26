document.getElementById('username_input').addEventListener('input', checkInputs);
document.getElementById('password_input').addEventListener('input', checkInputs);

function checkInputs() {
    const usernameLength = document.getElementById('username_input').value.length;
    const passwordLength = document.getElementById('password_input').value.length;
    const loginButton = document.getElementById('login_button');
    const errorMessage = document.getElementById('error_message');

    errorMessage.style.display = 'none';

    if (usernameLength >= 4 && passwordLength >= 8) {
        loginButton.disabled = false;
    } else {
        loginButton.disabled = true;
    }
}

document.getElementById('login_button').addEventListener('click', function(event){
    login(event);
});

document.getElementById('login_button_google').addEventListener('click', function(event){
    event.preventDefault();
    enviarSolicitudLoginConGoogle("solicitud login por google");
});

document.getElementById('content').addEventListener('keydown', function(event){
    if(event.code === 'Enter' || event.code === 'NumpadEnter'){
        const loginButton = document.getElementById('login_button');
        if (!loginButton.disabled) {
            login(event);
        }
    }
});

function login(event) {
    event.preventDefault();
    
    var username_input = document.getElementById("username_input").value;
    var password_input = document.getElementById("password_input").value;

    const encryptedPassword = CryptoJS.SHA256(password_input).toString();

    const data = {
        username: username_input,
        password: encryptedPassword
    };

    enviarSolicitudLogin(data);
}

function enviarSolicitudLogin(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/solicitudLogin', {
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
        if (respuesta.userType === "Usuario no autenticado") {
            const errorMessage = document.getElementById('error_message');
            errorMessage.style.display = 'block';
        }
        else {
            let userLogueado;
            
            if (respuesta.userId === '1') {
                userLogueado = {
                    userAdminRol: respuesta.userAdminRol,
                    userId: respuesta.userId,
                    rolActivo: 'Administrador',
                }
            } else if (respuesta.userColaboradorRol === '' && respuesta.userTecnicoRol === 'Técnico') {
                userLogueado = {
                    userColaboradorRol: respuesta.userColaboradorRol,
                    userTecnicoRol: respuesta.userTecnicoRol,
                    userId: respuesta.userId,
                    userType: respuesta.userType,
                    rolActivo: 'Técnico',
                }
            } else {
                userLogueado = {
                    userColaboradorRol: respuesta.userColaboradorRol,
                    userTecnicoRol: respuesta.userTecnicoRol,
                    userId: respuesta.userId,
                    userType: respuesta.userType,
                    rolActivo: 'Colaborador',
                }
            }
            
            localStorage.setItem('loggedInUser', JSON.stringify(userLogueado));
            if (respuesta.userType === 'Administrador') {
                window.location.href = 'indexAdmin.html';
            } else if (userLogueado.rolActivo === 'Técnico') {
                window.location.href = 'indexTecnico.html';
            } else {
                window.location.href = 'index.html';
            }
        }
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function enviarSolicitudLoginConGoogle(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/solicitudLoginGoogle', {
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
        window.location.href = respuesta;
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

document.getElementById('back_button').addEventListener('click', function(event){
    event.preventDefault();
    window.location.href = 'index.html';
});

document.addEventListener("DOMContentLoaded", function () {
    const passwordInput = document.getElementById("password_input");
    const togglePassword = document.getElementById("toggle_password");
    const passwordIcon = document.getElementById("password_icon");

    // Alternar visibilidad de la contraseña
    togglePassword.addEventListener("click", () => {
        const isPasswordVisible = passwordInput.type === "password";
        passwordInput.type = isPasswordVisible ? "text" : "password";
        passwordIcon.className = isPasswordVisible ? "fas fa-eye-slash" : "fas fa-eye"; // Cambiar ícono
    });
});
