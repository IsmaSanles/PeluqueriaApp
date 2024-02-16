
$(document).ready(function () {
    listarServicios();
    abrirModalCrear();

});

function abrirModalCrear(){
	// Manejador de clic para el botón "Crear Nuevo Servicio"
    $('#crearNuevoServicio').on('click', function () {
        $('#modalCrearServicio').modal('show');

	    // creamos un escuchador al botón crear del modal de Crear Servicio
	    $("#btnCrear").on("click", function() {
	        crearServicio();
	    });
    });
}

// creo la funcion para listar lo servicios
function listarServicios() {
    $.ajax({
        url: "http://localhost:8001/servicio",
        method: "GET",
        dataType: "json",
        success: function (data) {
        	// Limpia el cuerpo de la tabla
            $('#tbodyServicios').empty();

        	console.log(data);
            let content = ``;
            data.forEach(function (servicio) {
                content += `
                <tr>
                    <td>${servicio.nombre}</td>
                    <td>${servicio.precio} €</td>
                    <td>${servicio.duracion} min</td>
                    <td>${servicio.deAlta ? '<i class="bi bi-check-circle-fill text-success"></i>' : '<i class="bi bi-x-circle-fill text-danger"></i>'}</td>
                    <td>${servicio.descripcion}</td>
                    <td class="d-flex">
                        <button class="btn btn-primary mr-2 editarServicioBtn" data-servicio-id="${servicio.servicioId}">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                        <button class="btn btn-danger eliminarServicioBtn" data-servicio-id="${servicio.servicioId}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>`;
            });
            $("#tbodyServicios").html(content);


            // Inicializa el plugin DataTable con las opciones de configuración
            $("#tablaServicios").DataTable({
				...dataTableOptions,
	            columnDefs: [
					{ className: "text-center", targets: "_all" }, // centramos todos los textos de las columnas
			        { targets: [3, 4, 5], orderable: false } // indicamos que las columnas definidas no puedan filtrar
		    	]
		    });


            // Manejador de clic para los botones "Editar Servicio"
		    $('.editarServicioBtn').on('click', function () {
			    let servicioId = $(this).data("servicio-id");
			    //console.log("Capturado evento de Editar para id: " + servicioId);

			    // abrimos modal Editar
			    $('#modalEditarServicio').modal('show');

			    // recupero los datos de ese servicioId y relleno los campos
			    getServicioById(servicioId);

			    // creamos un escuchador al botón crear del modal de Crear Servicio
			    $("#btnEditar").on("click", function() {
			        editarServicio(servicioId);
	    		});
			});

			// Manejador de clic para los botones "Eliminar Servicio"
		    $('.eliminarServicioBtn').on('click', function () {
				let servicioId = $(this).data("servicio-id");
			    console.log("Capturado evento de Eliminar para id: " + servicioId);

				// llamada a la funcion para eliminar (dar de baja)
				eliminarServicio(servicioId);
			});

        },
        error: function (error) {
			// En caso de error, ocultar la tabla y mostrar el mensaje de fallo
            $("#tablaServicios").hide();
            $("#mensajeFallo").show();
            toastr.error("Hubo un error al cargar la tabla de servicios");
        }
    });
};

// recuperamos los datos del servicio
async function getServicioById(id) {
    await $.ajax({
        url: "http://localhost:8001/servicio/" + id,
        method: "GET",
        dataType: "json",
        success: function (data) {
			console.log("nombre"+data.nombre);
            // relleno los campos del formulario
		    $("#nombreEditar").val(data.nombre);
            $("#precioEditar").val(data.precio);
            $("#duracionEditar").val(data.duracion);
            $("#descripcionEditar").val(data.descripcion);

        },
        error: function (error) {
            // Manejo de errores
            //console.error("Error al recuperar los datos del servicio");
            toastr.error("Hubo un error al recuperar los datos del servicio");
        }
    });
}

// crear Nuevo Servicio
function crearServicio(){
    // recuperamos los datos para enviar al back
    let nombre = $("#nombreCrear").val();
    let precio = $("#precioCrear").val();
    let duracion = $("#duracionCrear").val();
    let descripcion = $("#descripcionCrear").val();

    console.log(nombre, precio, duracion, descripcion);

    // ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/servicio/crear",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            nombre,
            precio,
            duracion,
            descripcion

        }),
        success: function (data) {
            //console.log(data);

            // Quitar clases de error y mensajes de error al tener éxito
            $(".is-invalid").removeClass("is-invalid"); // Quitar clases de error de todos los campos
            $(".invalid-tooltip").remove(); // Quitar todos los mensajes de error

            // Mostrar mensaje de éxito con Toastr
            toastr.success("Nuevo servicio creado con éxito");

            // Recargar la página después de 1 segundo
            setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function (xhr, status, error) {
        		    console.log(xhr.responseJSON); // Para depurar, verifica la estructura del objeto de error

        		    // Manejar los mensajes de error devueltos por el backend
        		    if (xhr.responseJSON) {
        		        // Limpiar campos y mensajes de error antes de procesar los errores
                        $(".is-invalid").removeClass("is-invalid").css("border-width", ""); // Quitar clases de error y estilo personalizado de todos los campos
                        $(".invalid-tooltip").remove(); // Quitar todos los mensajes de error

                        xhr.responseJSON.forEach(error => {
                            // Obtener el nombre del campo y el mensaje de error
                            let fieldName = error.field;
                            let errorMessage = error.defaultMessage;

                            // Resaltar el campo con error y mostrar el mensaje de error utilizando las clases de Bootstrap
                            let inputField = $("#" + fieldName + "Crear"); // Sabiendo que los ids de los inputs siguen el formato "{nombreCampo}Crear"

                            inputField.css("border-width", "2px"); // Añadir estilo para aumentar el grosor del borde
                            inputField.addClass("is-invalid"); // Agregar clase de Bootstrap para campo inválido
                            inputField.next(".valid-tooltip").remove(); // Eliminar cualquier mensaje de validación anterior
                            inputField.next(".invalid-tooltip").remove(); // Eliminar cualquier mensaje de error anterior

                            // Si el campo está vacío, agregar mensaje predeterminado de campo obligatorio
                            if (!inputField.val().trim() || !inputField.val() == 0) {
                                errorMessage = "Este campo es un campo obligatorio";
                            }

                            inputField.after("<div class='invalid-tooltip'>" + errorMessage + "</div>"); // Mostrar mensaje de error
                        });
                    } else {
                        toastr.error('Ha ocurrido un error al intentar añadir el empleado');
                    }
        		}
            });
        };

// editar Servicio
function editarServicio(id){
	let nombre = $("#nombreEditar").val();
    let precio = $("#precioEditar").val();
    let duracion = $("#duracionEditar").val();
    let descripcion = $("#descripcionEditar").val();

	// ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/servicio/" + id,
        method: "PUT",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            nombre,
            precio,
            duracion,
            descripcion
        }),
        success: function (data) {
            //console.log(data);

            // Quitar clases de error y mensajes de error al tener éxito
            $(".is-invalid").removeClass("is-invalid"); // Quitar clases de error de todos los campos
            $(".invalid-tooltip").remove(); // Quitar todos los mensajes de error

            // Mostrar mensaje de éxito con Toastr
            toastr.success("Datos del servicio editados con éxito");

            // Recargar la página después de 1 segundo
            setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function (xhr, status, error) {
                    //console.log(xhr.responseJSON); // Para depurar, verifica la estructura del objeto de error

                    // Manejar los mensajes de error devueltos por el backend
                    if (xhr.responseJSON) {
                        // Limpiar campos y mensajes de error antes de procesar los errores
                        $(".is-invalid").removeClass("is-invalid").css("border-width", ""); // Quitar clases de error y estilo personalizado de todos los campos
                        $(".invalid-tooltip").remove(); // Quitar todos los mensajes de error

                        xhr.responseJSON.forEach(error => {
                            // Obtener el nombre del campo y el mensaje de error
                            let fieldName = error.field;
                            let errorMessage = error.defaultMessage;

                            // Resaltar el campo con error y mostrar el mensaje de error utilizando las clases de Bootstrap
                            let inputField = $("#" + fieldName + "Editar"); // Sabiendo que los ids de los inputs siguen el formato "{nombreCampo}Crear"

                            inputField.css("border-width", "2px"); // Añadir estilo para aumentar el grosor del borde
                            inputField.addClass("is-invalid"); // Agregar clase de Bootstrap para campo inválido
                            inputField.next(".valid-tooltip").remove(); // Eliminar cualquier mensaje de validación anterior
                            inputField.next(".invalid-tooltip").remove(); // Eliminar cualquier mensaje de error anterior

                            // Si el campo está vacío, agregar mensaje predeterminado de campo obligatorio
                            if (!inputField.val().trim()) {
                                errorMessage = "Este campo es un campo obligatorio";
                            }

                            inputField.after("<div class='invalid-tooltip'>" + errorMessage + "</div>"); // Mostrar mensaje de error
                        });
                    } else {
                        toastr.error('Ha ocurrido un error al intentar editar los datos del empleado');
                    }
                }
            });
        }


// eliminar (dar de baja) Servicio
function eliminarServicio(id) {
    // ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/servicio/" + id,
        method: "DELETE",
        dataType: "json",
        success: function (data) {
            console.log("success: " + JSON.stringify(data));

            // Mostrar mensaje de éxito con Toastr
            toastr.success("Servicio dado de baja con éxito");

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
                toastr.error("El servicio ya está dado de baja");
            } else if (xhr.status === 404) {
                // Código 404 (NotFound)
                toastr.error("Servicio no encontrado en la Base de Datos");
            } else {
                // Otros errores
                toastr.error("Error al dar de baja al servicio");
            }
        }
    });
};