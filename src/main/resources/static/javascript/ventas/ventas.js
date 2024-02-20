
$(document).ready(function () {
    listarVentas();
    abrirModalCrear();

});

function abrirModalCrear() {
	// Manejador de clic para el botón "Crear Nueva Venta"
    $('#crearNuevaVenta').on('click', function () {
        $('#modalCrearVenta').modal('show');

	    // creamos un escuchador al botón crear del modal de Crear Venta
	    $("#btnCrear").on("click", function() {
	        crearVenta();
	    });
    });
}

// creo la funcion para listar las ventas
function listarVentas() {
    $.ajax({
        url: "http://localhost:8001/ventas",
        method: "GET",
        dataType: "json",
        success: function (data) {
        	// Limpia el cuerpo de la tabla
            $('#tbodyVentas').empty();

        	//console.log(data);
            let content = ``;
            data.forEach(function (venta) {
				//console.log(venta); // log
                content += `
                <tr>
                    <td>${formatoFecha(venta.fechaVenta)}</td>
                    <td>${formatoHora(venta.fechaVenta)}</td>
                    <td>${venta.clienteId.nombre}</td>
                    <td>${venta.clienteId.apellido1}</td>
                    <td>${venta.clienteId.dni}</td>
                    <td>${venta.productoId.nombre}</td>
                    <td>${venta.udsVendidas}</td>
                    <td>${venta.productoId.precio}</td>
                    <td>${calcularPrecioTotal(venta.udsVendidas, venta.productoId.precio)}</td>
                    <td class="d-flex">
                        <button class="btn btn-primary mr-2 editarVentaBtn" data-estilista-id="${venta.ventaId}">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                        <button class="btn btn-danger eliminarVentaBtn" data-estilista-id="${venta.ventaId}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>`;
            });
            $("#tbodyVentas").html(content);

            // Inicializa el plugin DataTable con las opciones de configuración
			$("#tablaVentas").DataTable({
			    ...dataTableOptions,
			    columnDefs: [
			        { className: "text-center", targets: "_all" , orderable: false}, // centramos todos los textos de las columnas
			    ]
			});

        },
        error: function (error) {
			// En caso de error, ocultar la tabla y mostrar el mensaje de fallo
            $("#tablaVentas").hide();
            $("#mensajeFallo").show();
            toastr.error("Hubo un error al cargar la lista de Ventas");
        }
    });
};

// crear Nuevo Venta
function crearVenta(){
    // recuperamos los datos para enviar al back
    let udsVendidas = $("#udsVendidasCrear").val();
    let clienteId = $("#clienteCrear").val();
    let productoId = $("#productoCrear").val();

    //console.log(nombre, apellido1 ,apellido2, dni, telefono, email, fechaNacimiento);

    // ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/ventas/crear",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            udsVendidas,
            clienteId,
            productoId
        }),
        success: function (data) {
            //console.log(data);

            // Quitar clases de error y mensajes de error al tener éxito
            $(".is-invalid").removeClass("is-invalid"); // Quitar clases de error de todos los campos
            $(".invalid-tooltip").remove(); // Quitar todos los mensajes de error

            // Mostrar mensaje de éxito con Toastr
            toastr.success("Nueva venta añadida con éxito");

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
                    let inputField = $("#" + fieldName + "Crear"); // Sabiendo que los ids de los inputs siguen el formato "{nombreCampo}Crear"

                    inputField.css("border-width", "2px"); // Añadir estilo para aumentar el grosor del borde
                    inputField.addClass("is-invalid"); // Agregar clase de Bootstrap para campo inválido
                    inputField.next(".valid-tooltip").remove(); // Eliminar cualquier mensaje de validación anterior
                    inputField.next(".invalid-tooltip").remove(); // Eliminar cualquier mensaje de error anterior

                    // Si el campo está vacío, agregar mensaje predeterminado de campo obligatorio
                    if (!inputField.val().trim()) {
                        errorMessage = "Este es un campo obligatorio";
                    }

                    inputField.after("<div class='invalid-tooltip'>" + errorMessage + "</div>"); // Mostrar mensaje de error
                });
            } else {
                toastr.error('Ha ocurrido un error al intentar crear la venta');
            }
		}
    });
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
}

//Método para mostrar la hora 'HH:mm'
function formatoHora(fecha) {
    // Convierte la cadena de fecha a un objeto Date
    const date = new Date(fecha);

    // Obtiene los componentes de la hora
    const horas = date.getHours();
    const minutos = date.getMinutes();

    // Formatea la hora como "HH:mm"
    const formattedTime = `${horas < 10 ? '0' : ''}${horas}:${minutos < 10 ? '0' : ''}${minutos}`;

    return formattedTime;
}

// Calcula el precio
function calcularPrecioTotal(udsVendidas, precio) {
    return udsVendidas * precio;
}
