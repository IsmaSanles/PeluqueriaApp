
$(document).ready(function () {
    listarClientes();
    abrirModalCrear();
   	
});

function abrirModalCrear(){
	// Manejador de clic para el botón "Crear Nuevo Cliente"
    $('#crearNuevoCliente').on('click', function () {
        $('#modalCrearCliente').modal('show');
        
        // datepicker de FechaNacimiento
	    $("#fechaNacimientoCrear").datepicker({
	        dateFormat: "dd/mm/yy",
	        changeYear: true, // permite seleccionar el año en un desplegable
	        yearRange: "c-100:c+0" // indicamos cuantos años podemos escoger en el pasado y cuantos al futuro (en este caso cero)
	    });
	    
	    // creamos un escuchador al botón crear del modal de Crear Cliente
	    $("#btnCrear").on("click", function() {
	        crearCliente();
	    });
    });
}

// creo la funcion para listar lo clientes
function listarClientes() {
    $.ajax({
        url: "http://localhost:8001/cliente",
        method: "GET",
        dataType: "json",
        success: function (data) {
        	// Limpia el cuerpo de la tabla
            $('#tbodyClientes').empty();
           
        	//console.log(data);
            let content = ``;
            data.forEach(function (cliente) {
                content += `
                <tr>
                    <td>${cliente.dni}</td>
                    <td>${cliente.nombre}</td>
                    <td>${cliente.apellido1}</td>
                    <td>${cliente.apellido2}</td>
                    <td>${formatoFecha(cliente.fechaNacimiento)}</td>
                    <td>${cliente.grupoEdad}</td>
                    <td>${cliente.deAlta ? '<i class="bi bi-check-circle-fill text-success"></i>' : '<i class="bi bi-x-circle-fill text-danger"></i>'}</td>
                    <td>${cliente.telefono}</td>
                    <td>${cliente.email}</td>
                    <td class="d-flex">
                        <button class="btn btn-primary mr-2 editarClienteBtn" data-cliente-id="${cliente.clienteId}">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                        <button class="btn btn-danger eliminarClienteBtn" data-cliente-id="${cliente.clienteId}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>`;
            });
            $("#tbodyClientes").html(content);
            
            
            // Inicializa el plugin DataTable con las opciones de configuración
            $("#tablaClientes").DataTable({
				...dataTableOptions, 
	            columnDefs: [
					{ className: "text-center", targets: "_all" }, // centramos todos los textos de las columnas
			        { targets: [4, 5, 6, 7, 8, 9], orderable: false } // indicamos que las columnas definidas no puedan filtrar
		    	]
		    });
            
            
            // Manejador de clic para los botones "Editar Cliente"
		    $('.editarClienteBtn').on('click', function () {
			    let clienteId = $(this).data("cliente-id");
			    //console.log("Capturado evento de Editar para id: " + clienteId);
			    
			    // abrimos modal Editar
			    $('#modalEditarCliente').modal('show');
			    
			    // recupero los datos de ese clienteId y relleno los campos
			    getClienteById(clienteId);
			    
			    // datepicker de FechaNacimiento
			    $("#fechaNacimientoEditar").datepicker({
			        dateFormat: "dd/mm/yy",
			        changeYear: true, // permite seleccionar el año en un desplegable
			        yearRange: "c-100:c+0" // indicamos cuantos años podemos escoger en el pasado y cuantos al futuro (en este caso cero)
			    });
			    
			    
			    // creamos un escuchador al botón crear del modal de Crear Cliente
			    $("#btnEditar").on("click", function() {
			        editarCliente(clienteId);
	    		});
			});
			
			// Manejador de clic para los botones "Eliminar Cliente"
		    $('.eliminarClienteBtn').on('click', function () {
				let clienteId = $(this).data("cliente-id");
			    console.log("Capturado evento de Eliminar para id: " + clienteId);
				
				// llamada a la funcion para eliminar (dar de baja)
				eliminarCliente(clienteId);
			});
		    
        },
        error: function (error) {
			// En caso de error, ocultar la tabla y mostrar el mensaje de fallo
            $("#tablaClientes").hide();
            $("#mensajeFallo").show();
            toastr.error("Hubo un error al cargar la tabla de clientes");
        }
    });
};

// recuperamos los datos del cliente
async function getClienteById(id) {
    await $.ajax({
        url: "http://localhost:8001/cliente/" + id,
        method: "GET",
        dataType: "json",
        success: function (data) {
			
            // relleno los campos del formulario
		    $("#nombreEditar").val(data.nombre);
            $("#apellido1Editar").val(data.apellido1);
            $("#apellido2Editar").val(data.apellido2);
            $("#dniEditar").val(data.dni);
            $("#telefonoEditar").val(data.telefono);
            $("#emailEditar").val(data.email);
            $("#grupoEdadEditar").val(data.grupoEdad);
            $("#fechaNacimientoEditar").val(formatoFecha(data.fechaNacimiento));
	            
        },
        error: function (error) {
            // Manejo de errores
            //console.error("Error al recuperar los datos del cliente");
            toastr.error("Hubo un error al recuperar los datos del cliente");
        }
    });
}

// crear Nuevo Cliente	
function crearCliente(){
    // recuperamos los datos para enviar al back
    let nombre = $("#nombreCrear").val();
    let apellido1 = $("#apellido1Crear").val();
    let apellido2 = $("#apellido2Crear").val();
    let dni = $("#dniCrear").val();
    let telefono = $("#telefonoCrear").val();
    let email = $("#emailCrear").val();
    let grupoEdad = $("#grupoEdadCrear").val();
    let fechaNacimiento = $("#fechaNacimientoCrear").val();
    fechaNacimiento = convertirFechaStringToDate(fechaNacimiento);
    
    console.log(nombre, apellido1,apellido2,  dni, telefono, email, grupoEdad, fechaNacimiento);

    // ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/cliente/crear",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            nombre,
            apellido1,
            apellido2,
            dni,
            telefono,
            email,
            grupoEdad,
            fechaNacimiento
        }),
        success: function (data) {
            //console.log(data);
            
            // Mostrar mensaje de éxito con Toastr
            toastr.success("Nuevo cliente creado con éxito");
            
            // Recargar la página después de 1 segundo
            setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function (xhr, status, error) {
            let errorMessage = xhr.responseText;
            console.log(errorMessage);
            // Mostrar mensaje de éxito con Toastr
            toastr.error("A ocurrido un error al crear el cliente");
        }
    });
};

// editar Cliente	
function editarCliente(id){
	let nombre = $("#nombreEditar").val();
	let apellido1 = $("#apellido1Editar").val();
	let apellido2 = $("#apellido2Editar").val();
	let dni = $("#dniEditar").val();
	let telefono = $("#telefonoEditar").val();
	let email = $("#emailEditar").val();
	let grupoEdad = $("#grupoEdadEditar").val();
	let fechaNacimiento = $("#fechaNacimientoEditar").val();
	fechaNacimiento = convertirFechaStringToDate(fechaNacimiento);

	// ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/cliente/" + id,
        method: "PUT",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            nombre,
            apellido1,
            apellido2,
            dni,
            telefono,
            email,
            grupoEdad,
            fechaNacimiento
        }),
        success: function (data) {
            //console.log(data);
            
            // Mostrar mensaje de éxito con Toastr
            toastr.success("Datos del cliente editados con éxito");
            
            // Recargar la página después de 1 segundo
            setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function (xhr, status, error) {
            let errorMessage = xhr.responseText;
            console.log(errorMessage);
            // Mostrar mensaje de éxito con Toastr
            toastr.error("Ocurrió un error al editar los datos del cliente");
        }
    });
}


// eliminar (dar de baja) Cliente
function eliminarCliente(id) {
    // ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/cliente/" + id,
        method: "DELETE",
        dataType: "json",
        success: function (data) {
            console.log("success: " + JSON.stringify(data));
            
            // Mostrar mensaje de éxito con Toastr
            toastr.success("Cliente dado de baja con éxito");
            
            // Recargar la página después de 1 segundo
           	setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function (xhr, status, error) {
            let errorMessage = xhr.responseText;
            console.log("Status: " + status);
            console.log("Error: " + error);
            console.log("Response Text: " + errorMessage);

            // Comprobar el código de estado HTTP
            if (xhr.status === 400) {
                // Código 400 (BadRequest)
                toastr.error("El cliente ya está dado de baja");
            } else if (xhr.status === 404) {
                // Código 404 (NotFound)
                toastr.error("Cliente no encontrado en la Base de Datos");
            } else {
                // Otros errores
                toastr.error("Error al dar de baja al cliente");
            }
        }
    });
}

//Método para mostrar la fecha 'dd-MM-yyyy'
function formatoFecha(fecha) {
    // Convierte la cadena de fecha a un objeto Date
    const date = new Date(fecha);

    // Obtiene los componentes de la fecha
    const dia = date.getDate();
    const mes = date.getMonth() + 1; // Los meses en JavaScript son indexados desde 0
    const anio = date.getFullYear();

    // Formatea la fecha como "dd-MM-yyyy"
    const formattedDate = `${dia < 10 ? '0' : ''}${dia}/${mes < 10 ? '0' : ''}${mes}/${anio}`;

    return formattedDate;
};

// convertir fecha de String a Date
function convertirFechaStringToDate(fechaTexto) {
    // Divide la cadena en día, mes y año
    let partesFecha = fechaTexto.split('/');
    // Crea un nuevo objeto Date
    let fecha = new Date(partesFecha[2], partesFecha[1] - 1, partesFecha[0]);
  	//console.log(fecha);  
    return fecha;
};
