
$(document).ready(function () {
    listarEstilistas();
    abrirModalCrear();
   	
});

function abrirModalCrear(){
	// Manejador de clic para el botón "Crear Nuevo Cliente"
    $('#crearNuevoEstilista').on('click', function () {
        $('#modalCrearEstilista').modal('show');
        
        // datepicker de FechaNacimiento
	    $("#fechaNacimientoCrear").datepicker({
	        dateFormat: "dd/mm/yy",
	        changeYear: true, // permite seleccionar el año en un desplegable
	        yearRange: "c-100:c+0" // indicamos cuantos años podemos escoger en el pasado y cuantos al futuro (en este caso cero)
	    });
	    
	    // creamos un escuchador al botón crear del modal de Crear Cliente
	    $("#btnCrear").on("click", function() {
	        crearEstilista();
	    });
    });
}

// creo la funcion para listar lo clientes
function listarEstilistas() {
    $.ajax({
        url: "http://localhost:8001/estilista",
        method: "GET",
        dataType: "json",
        success: function (data) {
        	// Limpia el cuerpo de la tabla
            $('#tbodyEstilistas').empty();
           
        	//console.log(data);
            let content = ``;
            data.forEach(function (estilista) {
				console.log(estilista);
                content += `
                <tr>
                    <td>${estilista.dni}</td>
                    <td>${estilista.nombre}</td>
                    <td>${estilista.apellido1}</td>
                    <td>${estilista.apellido2}</td>
                    <td>${formatoFecha(estilista.fechaNacimiento)}</td>
                    <td>${estilista.deAlta ? '<i class="bi bi-check-circle-fill text-success"></i>' : '<i class="bi bi-x-circle-fill text-danger"></i>'}</td>
                    <td>${estilista.telefono}</td>
                    <td>${estilista.email}</td>
                    <td class="d-flex">
                        <button class="btn btn-primary mr-2 editarEstilistaBtn" data-estilista-id="${estilista.estilistaId}">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                        <button class="btn btn-danger eliminarEstilistaBtn" data-estilista-id="${estilista.estilistaId}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>`;
            });
            $("#tbodyEstilistas").html(content);
            // Inicializa el plugin DataTable con las opciones de configuración
            $("#tablaEstilistas").DataTable(dataTableOptions);
            
            
            // Manejador de clic para los botones "Editar Cliente"
		    $('.editarEstilistaBtn').on('click', function () {
			    let estilistaId = $(this).data("estilista-id");
			    //console.log("Capturado evento de Editar para id: " + estilistaId);
			    
			    // abrimos modal Editar
			    $('#modalEditarEstilista').modal('show');
			    
			    // recupero los datos de ese clienteId y relleno los campos
			    getEstilistaById(estilistaId);
			    
			    // datepicker de FechaNacimiento
			    $("#fechaNacimientoEditar").datepicker({
			        dateFormat: "dd/mm/yy",
			        changeYear: true, // permite seleccionar el año en un desplegable
			        yearRange: "c-100:c+0" // indicamos cuantos años podemos escoger en el pasado y cuantos al futuro (en este caso cero)
			    });
			    
			    
			    //  Manejador de clic para los botones "Editar Estilista"
			    $("#btnEditar").on("click", function() {
			        editarEstilista(estilistaId);
	    		});
			});
			
			// Manejador de clic para los botones "Eliminar Estilista"
		    $('.eliminarEstilistaBtn').on('click', function () {
				let estilistaId = $(this).data("estilista-id");
			    console.log("Capturado evento de Eliminar para id: " + estilistaId);
				
				// llamada a la funcion para eliminar (dar de baja)
				eliminarEstilista(estilistaId);
			});
		    
        },
        error: function (error) {
			// En caso de error, ocultar la tabla y mostrar el mensaje de fallo
            $("#tablaEstilistas").hide();
            $("#mensajeFallo").show();
            toastr.error("Hubo un error al cargar la tabla de empleados");
        }
    });
};

// recuperamos los datos del cliente
async function getEstilistaById(id) {
    await $.ajax({
        url: "http://localhost:8001/estilista/" + id,
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
            $("#fechaNacimientoEditar").val(formatoFecha(data.fechaNacimiento));
	            
        },
        error: function (error) {
            // Manejo de errores
            //console.error("Error al recuperar los datos del cliente");
            toastr.error("Hubo un error al recuperar los datos del empleado");
        }
    });
}

// crear Nuevo Estilista
function crearEstilista(){
    // recuperamos los datos para enviar al back
    let nombre = $("#nombreCrear").val();
    let apellido1 = $("#apellido1Crear").val();
    let apellido2 = $("#apellido2Crear").val();
    let dni = $("#dniCrear").val();
    let telefono = $("#telefonoCrear").val();
    let email = $("#emailCrear").val();
    let fechaNacimiento = $("#fechaNacimientoCrear").val();
    fechaNacimiento = convertirFechaStringToDate(fechaNacimiento);
    
    console.log(nombre, apellido1 ,apellido2, dni, telefono, email, fechaNacimiento);

    // ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/estilista/crear",
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
            fechaNacimiento
        }),
        success: function (data) {
            //console.log(data);
            
            // Mostrar mensaje de éxito con Toastr
            toastr.success("Nuevo empleado añadido con éxito");
            
            // Recargar la página después de 1 segundo
            setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function (xhr, status, error) {
            let errorMessage = xhr.responseText;
            console.log(errorMessage);
            // Mostrar mensaje de éxito con Toastr
            toastr.error("A ocurrido un error al intentar añadir el empleado");
        }
    });
};

// editar Estilista	
function editarEstilista(id){
	let nombre = $("#nombreEditar").val();
	let apellido1 = $("#apellido1Editar").val();
	let apellido2 = $("#apellido2Editar").val();
	let dni = $("#dniEditar").val();
	let telefono = $("#telefonoEditar").val();
	let email = $("#emailEditar").val();
	let fechaNacimiento = $("#fechaNacimientoEditar").val();
	fechaNacimiento = convertirFechaStringToDate(fechaNacimiento);

	// ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/estilista/" + id,
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
            fechaNacimiento
        }),
        success: function (data) {
            //console.log(data);
            
            // Mostrar mensaje de éxito con Toastr
            toastr.success("Datos del empleado editados con éxito");
            
            // Recargar la página después de 1 segundo
            setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function (xhr, status, error) {
            let errorMessage = xhr.responseText;
            console.log(errorMessage);
            // Mostrar mensaje de éxito con Toastr
            toastr.error("Ocurrió un error al editar los datos del empleado");
        }
    });
}


// eliminar (dar de baja) Cliente
function eliminarEstilista(id) {
    // ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/estilista/" + id,
        method: "DELETE",
        dataType: "json",
        success: function (data) {
            console.log("success: " + JSON.stringify(data));
            
            // Mostrar mensaje de éxito con Toastr
            toastr.success("Empleado dado de baja con éxito");
            
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
                toastr.error("El empleado ya está dado de baja");
            } else if (xhr.status === 404) {
                // Código 404 (NotFound)
                toastr.error("Empleado no encontrado en la Base de Datos");
            } else {
                // Otros errores
                toastr.error("Error al dar de baja al empleado");
            }
        }
    });
}


// aquí solo configuro las opciones de configuración para el dataTable
const dataTableOptions = {
	"destroy": true,
	"lengthMenu": [10, 15, 20, 25],
	"aoColumnDefs": [{className: "text-center", targets: "_all"}], // centramos todos los textos de las columnas
	//"scrollY": "400px", // Ajusta la altura según tus necesidades
	//"paging": false, // crea un scroll vertical
    "scrollCollapse": false, // en 'true' saca un scroll en la DataTable
    "oLanguage": {
        "sProcessing":"Procesando...",
        "sLengthMenu":"Mostrar _MENU_ registros",
        "sZeroRecords":"No se encontraron resultados",
        "sEmptyTable":"Ningún dato disponible en esta tabla",
        "sInfo":"Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
        "sInfoEmpty":"Mostrando registros del 0 al 0 de un total de 0 registros",
        "sInfoFiltered":"", // dejamos este campo vacío para evitar redundancia
        "sSearch":"Buscar:",
        "sLoadingRecords":"Cargando...",
        "oPaginate": {
            "sFirst":"Primero",
            "sLast":"Último",
            "sNext":"Siguiente",
            "sPrevious":"Anterior"
        },
        Aria: {
            "sSortAscending":": Activar para ordenar la columna de manera ascendente",
            "sSortDescending":": Activar para ordenar la columna de manera descendente"
        }
    }
};


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
