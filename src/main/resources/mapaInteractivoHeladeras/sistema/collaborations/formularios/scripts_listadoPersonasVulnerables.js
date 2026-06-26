document.addEventListener('DOMContentLoaded', function(event) {
    event.preventDefault();
    const data = {
        colaborador: JSON.parse(localStorage.getItem('loggedInUser')),
        tipoColaboracion: 'LISTADO_PERSONAS_VULNERABLES'
    };
    enviarSolicitudListadoVulnerables(data);
});

function enviarSolicitudListadoVulnerables(data) {
    fetch('http://heladerassolidarias.myvnc.com:4567/listadoPersonasVulnerables', {
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
        const tableBody = document.getElementById('vulnerablesTable').getElementsByTagName('tbody')[0];
        const keys = Object.keys(respuesta);
        const cantidadElementos = keys.length;

        if (cantidadElementos === 0) {
            console.log('No hay personas vulnerables registradas');
            return;
        }
        
        for (let i = 0; i < cantidadElementos; i++) {
            let key = keys[i];
            let personaVulnerable = respuesta[key];
            const row = tableBody.insertRow();
            const cellId = row.insertCell(0);
            const cellNombre = row.insertCell(1);
            const cellApellido = row.insertCell(2);
            const cellDeocumentacion = row.insertCell(3);

            cellId.textContent = personaVulnerable.id;
            cellNombre.textContent = personaVulnerable.nombre;
            cellApellido.textContent = personaVulnerable.apellido;
            cellDeocumentacion.textContent = personaVulnerable.dni;
        }
    })
    .catch((error) => {
        console.error('Error al enviar el JSON:', error);
    });

    // Cerrar la ventana después de 1 minuto
    setTimeout(() => {
        window.close();
    }, 300000); // 60000 milisegundos = 1 minuto
    // la esta cerrando a los 5 minutos
}

document.getElementById('filter_input').addEventListener('input', function(event) {
    event.preventDefault();
    const textoIngresado = event.target.value;
    filtrarPor(textoIngresado);
});

function filtrarPor(textoIngresado) {
    const filterValue = textoIngresado.toLowerCase();
    const rows = document.querySelectorAll('#vulnerablesTable tbody tr');
    const rowType = document.getElementById('row_type_input').value;

    switch (rowType) {
        case '1':
            rows.forEach(row => {
                const nombre = row.cells[1].textContent.toLowerCase();
                const apellido = row.cells[2].textContent.toLowerCase();
                const nombreCompleto = nombre + ' ' + apellido;
                if (nombreCompleto.includes(filterValue)) {
                    row.style.display = 'table-row';
                } else {
                    row.style.display = 'none';
                }
            });
            break;
        case '2':
            rows.forEach(row => {
                const dni = row.cells[3].textContent;
                if (dni.includes(filterValue)) {
                    row.style.display = 'table-row';
                } else {
                    row.style.display = 'none';
                }
            });
            break;
        case '3':
            rows.forEach(row => {
                const id = row.cells[0].textContent;
                if (id.includes(filterValue)) {
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

    switch (event.target.value) {
        case '1':
            filterInput.placeholder = 'Filtrar por nombre y apellido';
            filterInput.setAttribute('type', 'text');
            break;
        case '2':
            filterInput.placeholder = 'Filtrar por documentación';
            filterInput.setAttribute('type', 'number');
            break;
        case '3':
            filterInput.placeholder = 'Filtrar por id';
            filterInput.setAttribute('type', 'number');
            break;
        default:
            filterInput.placeholder = 'Filtrar';
            filterInput.setAttribute('type', 'text');
            break;
    }
});