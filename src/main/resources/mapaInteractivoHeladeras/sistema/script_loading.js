// elimino el currentUserId del localStorage (se supone que ya tuvo que haber ingresado)
document.addEventListener('DOMContentLoaded', function (event) {
    event.preventDefault();
    enviarCurrentUserId();
    usuarioLogueadoPorGoogle();
});

function usuarioLogueadoPorGoogle() {
    fetch('http://heladerassolidarias.myvnc.com:4567/loggedInUserGoogle', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify("Solicitar usuario logueado por google"),
    })
    .then(response => {
        // Revisa si la respuesta tiene éxito
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(respuesta => {
        if (respuesta === 'No se ha logueado algún colaborador por Google') {
            return;
        }
        else if (respuesta.currentUserId === 'false') {
            return;
        }

        let newUserLocalStorage;

        newUserLocalStorage = {
            userColaboradorRol: respuesta.userColaboradorRol,
            userTecnicoRol: respuesta.userTecnicoRol,
            userId: respuesta.userId,
            userType: respuesta.userType,
            rolActivo: 'Colaborador',
        }
        
        localStorage.removeItem('currentUserId');
        localStorage.setItem('loggedInUser', JSON.stringify(newUserLocalStorage));
        setTimeout(() => {
            window.location.href = "index.html";
        }, 2000); // son solo 2 segundos
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}

function enviarCurrentUserId() {
    fetch('http://heladerassolidarias.myvnc.com:4567/solicitudCurrentUserId', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify("solicito el currentUserId"),
    })
    .then(response => {
        // Revisa si la respuesta tiene éxito
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(respuesta => {
        if (respuesta === 'No se ha logueado algún colaborador por Google') {
            return;
        }
        else if (respuesta.currentUserId === 'true') {
            return;
        }
        localStorage.setItem('currentUserId', respuesta.currentUserId);
        setTimeout(() => {
            window.location.href = "datosRestantes/datosRestantes.html";
        }, 2000); // son solo 2 segundos
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });
}
