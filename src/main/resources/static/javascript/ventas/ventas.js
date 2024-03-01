 var arrayProductoCantidad;
$(document).ready(function () {
    // defino variable global
     arrayProductoCantidad = [];

    listarVentas();
    abrirModalCrear();

    anadirProductosCarrito();
    eliminarProductoCarrito();

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
                //console.log('venta ' + JSON.stringify(venta)); // comprobar que llega

                // Crea una variable para almacenar los productos de esta venta y otra para las unidades
                let productosHtml = '';
                let udsVentaHtml = '';
                let totalVenta = 0; // Variable para almacenar el total de la venta
                // Itera sobre la lista de productos de esta venta
                venta.productosVendidos.forEach(function(objeto) {
                    // Agrega cada producto como una fila en la celda
                    productosHtml += `<span>${objeto.producto.nombre}</span><br>`;

                    udsVentaHtml += `<span>${objeto.udsVendidas}</span><br>`;
                    // Calcula el precio total del producto (cantidad * precio) y suma al total de la venta
                    totalVenta += objeto.udsVendidas * objeto.producto.precio;
                });
                // Construye la fila de la tabla con los datos de la venta
                content += `
                <tr>
                    <td>${formatoFecha(venta.fechaVenta)}</td>
                    <td>${formatoHora(venta.fechaVenta)}</td>
                    <td>${venta.cliente.nombre}</td>
                    <td>${venta.cliente.apellido1}</td>
                    <td>${venta.cliente.dni}</td>
                    <td>${productosHtml}</td> <!-- Aquí se insertan los productos -->
                    <td>${udsVentaHtml}</td> <!-- Aquí se insertan las udsVenta -->
                    <td>
                        <ul style="list-style-type: none; padding: 0; margin: 0;">`;
                            // Itera sobre la lista de productos de esta venta para mostrar los precios individuales
                            venta.productosVendidos.forEach(function(objeto) {
                                content += `<li>${objeto.producto.precio.toFixed(2)}</li>`;
                            });
                            content += `
                        </ul>
                    </td>
                    <td>${totalVenta.toFixed(2)}</td> <!-- Muestra el total de la venta -->
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

// Función para cargar los clientes de la BD al SELECT
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

            // Aplicar libreria 'Select2' al selector de clientes
            $('#selectClientes').select2({
                dropdownParent: $('#modalCrearVenta'),
                width: '100%'
            });
        },
        error: function (error) {
            console.error("Error al cargar los clientes:", error);
        }
    });
}

// Función para cargar los productos de la BD al SELECT
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

            // Aplicar libreria 'Select2' al selector de productos
            $('#selectProductos').select2({
                dropdownParent: $('#modalCrearVenta'),
                width: '100%'
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

// añade los productos a la tabla del Modal crearVenta cada vez que pulsamos el botón Añadir
function anadirProductosCarrito(){
     // Variable para mantener un contador de filas agregadas
     let contadorFilas = 0;

    // Evento de clic para el botón btnAnadirAlCarrito
    $('#btnAnadirAlCarrito').click(function() {

        // Recuperamos los datos
        let cantidad = $("#cantidad").val();
        let productoId = $("#selectProductos").val();

        // Realizamos una llamada AJAX para recuperar los datos del producto
        $.ajax({
            url: "http://localhost:8001/producto/" + productoId,
            method: "GET",
            dataType: "json",
            success: function (producto) {
                // Calculamos el total
                let total = (producto.precio * cantidad).toFixed(2);

                contadorFilas++; // aumentamos el contador

                // añadimos la fila, la cantidad, el productoId y el total al array
                arrayProductoCantidad.push(
                    {
                        "fila": contadorFilas,
                        "cantidad": cantidad,
                        "producto": producto,
                        "total": total
                    }
                );
                console.log('array añadir: ' + JSON.stringify(arrayProductoCantidad)); // para ver los datos del array

                // Creamos la fila HTML con los datos del producto
                let fila =
                    `<tr data-fila="${contadorFilas}" style="text-align:center">
                        <td>${producto.nombre}</td>
                        <td>${cantidad}</td>
                        <td>${producto.precio.toFixed(2)} €</td>
                        <td>${total} €</td>
                        <td><button class="btn btn-danger btnEliminarProductoCarrito" data-fila="${contadorFilas}"><i class="bi bi-x-circle"></i> Eliminar</button></td>
                    </tr>`;

                // añadimos la fila a la tabla
                $('#tbodyProductosCarrito').append(fila);

                // Limpiamos los campos después de agregar el producto al carrito
                $("#cantidad").val("");
                $("#selectProductos").val("").trigger("change");

                // llamada al metodo para la suma total de la venta
                precioTotalVenta(arrayProductoCantidad);
                // si quiero eliminar alguno

            },
            error: function (xhr, status, error) {
                console.error("Error al recuperar el producto: " + error);
                let errorMessage = "Error al cargar los datos del producto: ";
                if (xhr.status == 400) {
                    errorMessage += "Error de solicitud. Por favor, verifique los datos enviados.";
                } else if (xhr.status == 404) {
                    errorMessage += "No se encontraron datos.";
                } else if (xhr.status == 500) {
                    errorMessage += "Error interno del servidor. Por favor, inténtelo de nuevo más tarde.";
                }
            }
        });
    });


}

// elimina el producto de la tabla del Modal crearVenta al pulsar el botón Eliminar
function eliminarProductoCarrito(){

     console.log('array eliminar: ' + JSON.stringify(arrayProductoCantidad));

    // Evento de clic para el botón de eliminar producto
    $('#tbodyProductosCarrito').on('click', '.btnEliminarProductoCarrito', function() {

        // Recuperamos el identificador único de la fila a eliminar
        let filaEliminar = $(this).data('fila');
        console.log('Pulsado boton eliminar "data-fila" ' + filaEliminar);

        // Buscamos la fila correspondiente y la eliminamos
        $(`tr[data-fila="${filaEliminar}"]`).remove();

        // Eliminamos el elemento correspondiente del array
        arrayProductoCantidad = arrayProductoCantidad.filter(item => item.fila !== filaEliminar);
        console.log('array despues de eliminar: ' + JSON.stringify(arrayProductoCantidad));

        // Llamamos a la función precioTotalVenta para actualizar el precio total de la venta
        precioTotalVenta(arrayProductoCantidad);

        return arrayProductoCantidad;
    });
}

// recupera del array el total de cada fila y los suma para obtener el total de toda la venta
function precioTotalVenta(array){
    let totalVenta = 0;
    for(let x of array){
        totalVenta += parseFloat(x.total);
    }
    // Establecer estilos para el texto y la variable
    $('#precioTotalVenta').html('<strong>Precio Total: </strong> <span style="font-size: 20px;">' + totalVenta.toFixed(2) + ' €</span>');

    // Establecer estilos adicionales para el texto y la variable
    $('#precioTotalVenta strong').css('font-weight', 'bold');
    $('#precioTotalVenta span').css('font-size', '20px');
}


