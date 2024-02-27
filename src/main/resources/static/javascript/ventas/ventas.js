
$(document).ready(function () {
    listarVentas();
    abrirModalCrear();

    // creamos un escuchador al botón crear del modal de Crear Venta
    $("#btnCrear").on("click", function() {
        crearVenta();
    });
});

function abrirModalCrear() {
    $('#crearNuevaVenta').on('click', function () {
        $('#modalCrearVenta').modal('show');

        // Cargar clientes y productos al abrir el modal de crear venta
        cargarClientes();
        cargarProductos();
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

            let content = ``;
            data.forEach(function (venta) {
                // Crea una variable para almacenar los productos de esta venta
                let productosHtml = '';
                // Itera sobre la lista de productos de esta venta
                venta.listaProductos.forEach(function(producto) {
                    // Agrega cada producto como una fila en la celda
                    productosHtml += `<span>${producto.nombre}</span><br>`;
                });
                // Construye la fila de la tabla con los datos de la venta
                content += `
                <tr>
                    <td>${formatoFecha(venta.fechaVenta)}</td>
                    <td>${formatoHora(venta.fechaVenta)}</td>
                    <td>${venta.clienteId.nombre}</td>
                    <td>${venta.clienteId.apellido1}</td>
                    <td>${venta.clienteId.dni}</td>
                    <td>${productosHtml}</td> <!-- Aquí se insertan los productos -->
                    <td>
                        <ul style="list-style-type: none; padding: 0; margin: 0;">`;
                            // Itera sobre la lista de productos de esta venta para mostrar los precios individuales
                            venta.listaProductos.forEach(function(producto) {
                                content += `<li>${producto.precio}</li>`;
                            });
                            content += `
                        </ul>
                    </td>
                    <td>TOTAL</td>
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
			        { className: "text-center", targets: "_all"}, // centramos todos los textos de las columnas
			        { orderable: false, targets: "_all" } // Deshabilita el filtrado para todas las columnas
			    ]
			});

        },
        error: function (error) {
			// En caso de error, ocultar la tabla y mostrar el mensaje de fallo
            $("#tablaVentas").hide();
            $("#mensajeFallo").show();
            toastr.error("Hubo un error al cargar las Ventas");
        }
    });
};

// crear Nueva Venta
function crearVenta() {
    // Recuperamos los datos para enviar al backend
    let cantidad = $("#cantidad").val();
    let clienteId = $("#selectClientes").val();
    let productoId = $("#selectProductos").val();
    console.log('cantidad: ' + cantidad, 'clienteId: ' + clienteId, 'productoId: ' + productoId);

    // Limpiamos los mensajes de error
    $(".is-invalid").removeClass("is-invalid"); // Quitar clases de error de todos los campos
    $(".invalid-tooltip").remove(); // Quitar todos los mensajes de error

    // Comprobamos si clienteId, productoId o cantidad están vacíos
    let errores = false;

    if (!clienteId) {
        $("#selectClientes").addClass("is-invalid");
        $("#selectClientes").after('<div class="invalid-tooltip">Campo obligatorio</div>');
        errores = true;
    }

    if (!productoId) {
        $("#selectProductos").addClass("is-invalid");
        $("#selectProductos").after('<div class="invalid-tooltip">Campo obligatorio</div>');
        errores = true;
    }

    if (!cantidad) {
        $("#cantidad").addClass("is-invalid");
        $("#cantidad").after('<div class="invalid-tooltip">Campo obligatorio</div>');
        errores = true;
    }

    if (errores) {
        // Mostrar un mensaje general de error usando Bootstrap
        $("#mensajeError").html('<div class="alert alert-danger" role="alert">Por favor, complete todos los campos obligatorios.</div>');
    } else {

        // Ejecución de petición AJAX para la conexión con el backend
        $.ajax({
            url: "http://localhost:8001/ventas/crear",
            method: "POST",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                cliente: { clienteId: clienteId },
                productosVendidos: [
                    {
                        producto: { productoId: productoId },
                        udsVendidas: cantidad
                    }
                ]
            }),
            success: function (data) {

                // Mostrar mensaje de éxito con Toastr
                toastr.success("Venta añadida con éxito");

                // Recargar la página después de 1 segundo
                setTimeout(function () {
                    location.reload();
                }, 1000);
            },
            error: function (xhr, status, error) {
                toastr.error('Ha ocurrido un error al intentar crear la venta');
            }
        });
    }
};

// Función para cargar los clientes desde el backend
function cargarClientes() {
    $.ajax({
        url: "http://localhost:8001/cliente/deAlta",
        method: "GET",
        dataType: "json",
        success: function (data) {

            // Limpiar las opciones existentes en el selector de clientes
            $('#selectClientes').empty();
            // Añadimos una opción para indicar al usuario que seleccione una option
            $('#selectClientes').append(`<option selected disabled value="">Selecciona el cliente</option>`);
            // Agregar las opciones de clientes al selector correspondiente
            data.forEach(function (cliente) {
                let fullName = `${cliente.nombre} ${cliente.apellido1}`;
                if (cliente.apellido2) {
                    fullName += ` ${cliente.apellido2}`;
                }
                $('#selectClientes').append(`<option value="${cliente.clienteId}">${cliente.dni} - ${fullName}</option>`);
            });

            // Aplicar Select2 al selector de clientes
            $('#selectClientes').select2({
                dropdownParent: $('#modalCrearVenta'),
                width: '100%',
                height: '10px'
            });
        },
        error: function (error) {
            console.error("Error al cargar los clientes:", error);
        }
    });
}

function cargarProductos() {
    $.ajax({
        url: "http://localhost:8001/producto/deAlta",
        method: "GET",
        dataType: "json",
        success: function (data) {

            // Limpiar las opciones existentes en el selector de productos
            $('#selectProductos').empty();
            // Añadimos una opción para indicar al usuario que seleccione una option
            $('#selectProductos').append(`<option selected disabled value="">Selecciona el producto</option>`);
            // Agregar las opciones de productos al selector correspondiente
            data.forEach(function (producto) {
                $('#selectProductos').append(`<option value="${producto.productoId}">${producto.nombre}</option>`);
            });

            // Aplicar Select2 al selector de productos
            $('#selectProductos').select2({
                dropdownParent: $('#modalCrearVenta'),
                width: '100%',
                height: '100%'
            });
        },
        error: function (error) {
            console.error("Error al cargar los productos:", error);
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