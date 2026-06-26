document.addEventListener('DOMContentLoaded', function() {
    const optionTitles = document.querySelectorAll('.option-title');
    
    optionTitles.forEach(title => {
        title.addEventListener('click', function() {
            const content = this.nextElementSibling;
            const arrow = this.querySelector('.toggle-arrow');
            
            if (content.style.display === 'block') {
                content.style.display = 'none';
                arrow.textContent = '▼';
            } else {
                content.style.display = 'block';
                arrow.textContent = '▲';
            }
        });
    });
});

//* Redirecciones de registro de usuario
document.addEventListener('DOMContentLoaded', function() {
    function isUserRegistered() {
        // Aquí deberías verificar si el usuario está registrado
        return localStorage.getItem('loggedInUser') !== null;
    }
    
    const colaboracionButtons = document.querySelectorAll('.btn-colaboracion-sm');

    colaboracionButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            if (!isUserRegistered()) {
                const targetPage = 'formularioColaboracion.html';
                console.log('Usuario no registrado, redirigiendo a login');
                window.location.href = `../login.html?redirect=${encodeURIComponent(targetPage)}`;
            } else {
                window.location.href = 'formularioColaboracion.html';
            }
        });
    });
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

document.addEventListener('DOMContentLoaded', () => {
    const loggedInUser = JSON.parse(localStorage.getItem('loggedInUser'));
    const colaborationsSection = document.getElementById('colaborations-section');
    if (loggedInUser.userType === 'J') {
        colaborationsSection.innerHTML = `
            <h1>Colaboraciones</h1>

            <div class="row">
                <div class="col-md-4 mb-4">
                    <div class="colaboracion-card">
                        <div class="colaboracion-card-body">
                            <h5 class="card-title">Donar Dinero</h5>
                            <p>Realice su donación para colaborar con el mantenimiento de nuestras heladeras</p>
                            <a href="../collaborations/formularios/formDonarDinero.html" class="btn btn-primary">Realizar Colaboración</a>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 mb-4">
                    <div class="colaboracion-card">
                        <div class="colaboracion-card-body">
                            <h5 class="card-title">Hacerse Cargo</h5>
                            <p>Coloque una nueva heladera</p>
                            <a href="../collaborations/formularios/formHacerseCargo.html" class="btn btn-primary">Realizar Colaboración</a>
                        </div>
                    </div>
                </div>

                <div class="col-md-4 mb-4">
                    <div class="colaboracion-card">
                        <div class="colaboracion-card-body">
                            <h5 class="card-title">Publicar Producto/Servicio</h5>
                            <p>Ofrezca productos para poder ser canjeados por los colaboradores reconociendo su ayuda a la sociedad</p>
                            <a href="../collaborations/formularios/formOfrecerProductoReconocimiento.html" class="btn btn-primary">Realizar Colaboración</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        `;
    }
    else {
        colaborationsSection.innerHTML = `
            <h1>Colaboraciones</h1>

            <div class="row">
                <div class="col-md-6 mb-6">
                    <div class="colaboracion-card">
                        <div class="colaboracion-card-body">
                            <h5 class="card-title">Donar Dinero</h5>
                            <p>Realice su donación para colaborar con el mantenimiento de nuestras heladeras</p>
                            <a href="../collaborations/formularios/formDonarDinero.html" class="btn btn-primary">Realizar Colaboración</a>
                        </div>
                    </div>
                </div>

                <div class="col-md-6 mb-6">
                    <div class="colaboracion-card">
                        <div class="colaboracion-card-body">
                            <h5 class="card-title">Donación de Viandas</h5>
                            <p>Done viandas para que distintas personas extraerlas</p>
                            <a href="../collaborations/formularios/formDonacionDeViandas.html" class="btn btn-primary">Realizar Colaboración</a>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-6">
                    <div class="colaboracion-card">
                        <div class="colaboracion-card-body">
                            <h5 class="card-title">Distribución de Viandas</h5>
                            <p>Cambie la ubicación de las viandas para facilitar su acceso</p>
                            <a href="../collaborations/formularios/formDistribucionDeViandas.html" class="btn btn-primary">Realizar Colaboración</a>
                        </div>
                    </div>
                </div>

                <div class="col-md-6 mb-6">
                    <div class="colaboracion-card">
                        <div class="colaboracion-card-body">
                            <h5 class="card-title">Registrar Persona Vulnerable</h5>
                            <p>Registre un nuevo usuario que necesite hacer uso de las viandas donadas</p>
                            <a href="../collaborations/formularios/registroPersonaVulnerable.html" class="btn btn-primary">Realizar Colaboración</a>
                        </div>
                    </div>
                </div>
            </div>
        `;
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
        window.location.href = 'colaboraciones.html';
    }, 3000); // son solo 3 segundos
}
