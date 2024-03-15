var arrayProductoCantidad; // en esta variable se guardan los productos que se van a vender al crear una venta
$(document).ready(function () {
    // defino variable global
    arrayProductoCantidad = [];

    listarVentasPorDia();
    abrirModalCrear();

    anadirProductosCarrito();
    eliminarProductoCarrito();

    // creamos un escuchador al botón crear del modal de Crear Venta
    $("#btnCrear").on("click", function() {
        crearVenta();
    });

    //editar venta
    modificarVenta();

    // eliminar
    btnEliminarVenta();


});

function abrirModalCrear() {
    $('#crearNuevaVenta').on('click', function () {
        $('#modalCrearVenta').modal('show');

        // Cargar clientes y productos al abrir el modal de crear venta
        cargarClientes();
        cargarProductos();
    });
}

// función principal para filtrar las ventas por dia, en principio carga las ventas del día actual
// y una vez filtramos otro día recupera los datos del día seleccionado
function listarVentasPorDia(){
    // recuperamos la fecha
    let fechaVentaDia = $('#fechaVenta').val().trim();

    // si no ingresamos nada en el filtro, por defecto carga la fecha actual
    if(fechaVentaDia === ''){
        let fechaActual = new Date();
        fechaVentaDia = formatoFecha(fechaActual);
        $('#fechaVenta').val(formatoFecha(fechaActual)); // Establecemos la fecha actual en el campo de fecha
    }

    // Convertir la fecha al formato 'YYYY-MM-DD' que es compatible con la base de datos
    let fechaFormateada = fechaVentaDia.split('/').reverse().join('-');
    // mostramos las ventas del día actual por defecto
    listarVentas(fechaFormateada);

    $('#fechaVenta').datepicker({
        dateFormat: "dd/mm/yy",
        changeYear: true, // permite seleccionar el año en un desplegable
        changeMonth: true, // permite seleccionar el mes en un desplegable
        yearRange: "c-100:c+0" // indicamos cuantos años podemos escoger en el pasado y cuantos al futuro (en este caso cero)
    });

    // manejar evento para cuando pulsemos el botón del filtro de buscar por fecha
    $('#btnBuscarPorDia').on("click", function() {
        // recuperamos la fecha
        fechaVentaDia = $('#fechaVenta').val().trim();

        // si no ingresamos nada en el filtro, por defecto carga la fecha actual
        if(fechaVentaDia === ''){
            let fechaActual = new Date();
            fechaVentaDia = formatoFecha(fechaActual);
            $('#fechaVenta').val(formatoFecha(fechaActual)); // Establecemos la fecha actual en el campo de fecha
        }

        // Convertir la fecha al formato 'YYYY-MM-DD' que es compatible con la base de datos
        fechaFormateada = fechaVentaDia.split('/').reverse().join('-');

        // mostramos las ventas del día seleccionado
        listarVentas(fechaFormateada);
    });
}

// función para listar las ventas de un día pasado por parámetro
function listarVentas(fechaFormateada){
    $.ajax({
        url: "http://localhost:8001/ventas/porFecha/" + fechaFormateada,
        method: "GET",
        dataType: "json",
        success: function (data) {
            // Limpia el cuerpo de la tabla
            $('#tbodyVentas').empty();

            let content = ``;
            let totalVentasEfectivo = 0;
            let totalVentasTarjeta = 0;

            data.forEach(function (venta) {
                //console.log('venta ' + JSON.stringify(venta.fechaVenta)); // comprobar que llega

                // Crea una variable para almacenar los productos de esta venta y otra para las unidades
                let productosHtml = '';
                let udsVentaHtml = '';
                let totalVenta = 0; // Variable para almacenar el total de la venta
                // Itera sobre la lista de productos de esta venta
                venta.productosVendidos.forEach(function(objeto) {
                    // Agrega cada producto como una fila en la celda y las unidades compradas
                    productosHtml += `<span>${objeto.producto.nombre}</span><br>`;
                    udsVentaHtml += `<span>${objeto.udsVendidas}</span><br>`;
                    // Calcula el precio total del producto (cantidad * precio) y suma al total de la venta
                    totalVenta += objeto.udsVendidas * objeto.precioVenta;
                });
                // Construye la fila de la tabla con los datos de la venta
                content += `
                <tr style="text-align:center">
                    <td>${venta.cliente.dni}</td>
                    <td>${venta.cliente.nombre}</td>
                    <td>${venta.cliente.apellido1}</td>
                    <td>${formatoFecha(venta.fechaVenta)}</td>
                    <td>${formatoHora(venta.fechaVenta)}</td>
                    <td>${productosHtml}</td> <!-- Aquí se insertan los productos -->
                    <td>${udsVentaHtml}</td> <!-- Aquí se insertan las udsVenta -->
                    <td>
                        <ul style="list-style-type: none; padding: 0; margin: 0;">`;
                            // Itera sobre la lista de productos de esta venta para mostrar los precios individuales
                            venta.productosVendidos.forEach(function(objeto) {
                                content += `<li>${objeto.precioVenta.toFixed(2).replace('.',',')} €</li>`;
                            });
                            content += `
                        </ul>
                    </td>`;
                    // Agregar la columna de método de pago
                    if (venta.metodoPago === 1) {
                        totalVentasTarjeta += totalVenta; // Suma el total de la venta a las ventas con tarjeta
                        content += `<td><i class="bi bi-credit-card-fill"></i> Tarjeta</td>`;
                    } else if (venta.metodoPago === 0) {
                        totalVentasEfectivo += totalVenta; // Suma el total de la venta a las ventas en efectivo
                        content += `<td><i class="bi bi-currency-exchange"></i> Efectivo</td>`;
                    }

                    content += `
                    <td>${totalVenta.toFixed(2).replace('.',',')} €</td> <!-- Muestra el total de la venta -->
                    <td class="d-flex">
                        <button class="btn btn-primary mr-2 editarVentaBtn" data-venta-id="${venta.ventaId}">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                        <button class="btn btn-danger eliminarVentaBtn" data-venta-id="${venta.ventaId}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>`;
            });
            // Establecer estilos para el texto y la variable
            $('#precioTotalVentaEfectivo').html('<strong>Total efectivo: </strong> <span class="total-venta-cantidad">' + totalVentasEfectivo.toFixed(2).replace('.',',') + ' €</span>');
            $('#precioTotalVentaTarjeta').html('<strong>Total tarjeta: </strong> <span class="total-venta-cantidad">' + totalVentasTarjeta.toFixed(2).replace('.',',') + ' €</span>');

            // rellenamos la tabla con los datos
            $("#tbodyVentas").html(content);

        },
       error: function (xhr, status, error) {
           if (xhr.status === 404) {
               toastr.error("No existen ventas asociadas a ese día");
           } else if (xhr.status === 400) {
               toastr.error("La solicitud no se pudo procesar correctamente");
           } else if (xhr.status === 500) {
               toastr.error("Error interno del servidor");
           } else {
               // Si el error no es específico de los códigos 400, 404 o 500, muestro mensaje de error genérico
               toastr.error('Error al procesar la solicitud: ' + error);
           }
       }
    });
}

// crear Nueva Venta
function crearVenta() {
    // Recuperamos los datos para enviar al backend
    let clienteId = $("#selectClientes").val();
    let metodoPago =  $("#selectMetodoPago").val();
    //console.log('clienteId: ' + clienteId, 'productos: ' + JSON.stringify(arrayProductoCantidad), 'metodo de pago: ' + metodoPago);

    // Limpiamos los mensajes de error
    $(".is-invalid").removeClass("is-invalid"); // Quitar clases de error de todos los campos
    $(".invalid-tooltip").remove(); // Quitar todos los mensajes de error

    // Comprobamos errores
    let errores = false;

    // Comprobamos si clienteId está vacío
    if (!clienteId) {
        $("#selectClientes").addClass("is-invalid");
        $("#selectClientes").after('<div class="invalid-tooltip">Campo obligatorio</div>');
        errores = true;
    }

    // Comprobamos si metodoPago está vacío
    if (!metodoPago) {
        $("#selectMetodoPago").addClass("is-invalid");
        $("#selectMetodoPago").after('<div class="invalid-tooltip">Campo obligatorio</div>');
        errores = true;
    }

    // Comprobamos si el carrito está vacío
    if (arrayProductoCantidad.length === 0) {
        toastr.warning('El carrito está vacío, añade algún producto');
        errores = true;
    }

    if (!errores) {
        // Construir la lista de productos para enviar al backend
        let productosVendidos = [];
        arrayProductoCantidad.forEach(function(productoCantidad) {
            productosVendidos.push({
                producto: { productoId: productoCantidad.producto.productoId },
                udsVendidas: productoCantidad.cantidad
            });
        });

        // Ejecución de petición AJAX para la conexión con el backend
        $.ajax({
            url: "http://localhost:8001/ventas/crear",
            method: "POST",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                cliente: { clienteId: clienteId },
                productosVendidos: productosVendidos,
                metodoPago: metodoPago
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
                // Manejar errores de la solicitud HTTP
                console.error('Error al hacer la solicitud:', error);

                // Verificar si el error es debido a falta de stock
                if (xhr.status === 409) {
                    toastr.error("No hay suficiente stock para completar la venta");
                } else {
                    // Si el error no es de falta de stock, mostrar un mensaje de error genérico
                    toastr.error('Error al crear la venta: ' + error.message);
                }
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
            toastr.error("Error al cargar los datos de los clientes en el desplegable");
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
            $('#selectProductosEditar').empty();
            // Añadimos una opción para indicar al usuario que seleccione una option
            $('#selectProductos').append(`<option selected disabled value="">Selecciona el producto</option>`);
            $('#selectProductosEditar').append(`<option selected disabled value="">Selecciona el producto</option>`);
            // Agregar las opciones de productos al selector correspondiente
            data.forEach(function (producto) {
                $('#selectProductos').append(`<option value="${producto.productoId}">${producto.nombre} (Stock: ${producto.stock})</option>`);
                $('#selectProductosEditar').append(`<option value="${producto.productoId}">${producto.nombre} (Stock: ${producto.stock})</option>`);
            });
            // Aplicar libreria 'Select2' al selector de productos en ModalCrearVenta
            $('#selectProductos').select2({
                dropdownParent: $('#modalCrearVenta'),
                width: '100%'
            });
            // Aplicar libreria 'Select2' al selector de productos en ModalEditarVenta
            $('#selectProductosEditar').select2({
                dropdownParent: $('#modalEditarVenta'),
                width: '100%'
            });

        },
        error: function (error) {
            console.error("Error al cargar los productos:", error);
            toastr.error("Error al cargar los datos de los productos en el desplegable");
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

        // Limpiamos los mensajes de error
        $(".is-invalid").removeClass("is-invalid"); // Quitar clases de error de todos los campos
        $(".invalid-tooltip").remove(); // Quitar todos los mensajes de error

        // Recuperamos los datos
        let cantidad = $("#cantidad").val();
        let productoId = $("#selectProductos").val();

        // Comprobamos si productoId y cantidad están vacíos
        let errores = false;

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

        if(!errores){
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
        }
    });
}

// elimina el producto de la tabla del Modal crearVenta al pulsar el botón Eliminar
function eliminarProductoCarrito(){
     //console.log('array eliminar: ' + JSON.stringify(arrayProductoCantidad));

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

// Eliminar una venta
function btnEliminarVenta() {
    // Utiliza la delegación de eventos para manejar el clic en los botones de eliminar venta
    $(document).on('click', '.eliminarVentaBtn', function() {
        // Obtener el id de la venta que se va a eliminar del atributo data-venta-id del botón
        let ventaId = $(this).data('venta-id');

        // Mensaje de confirmación con SweetAlert2
        Swal.fire({
            title: '¿Estás seguro?',
            text: 'Esta acción eliminará permanentemente la venta. ¿Estás seguro de que deseas continuar?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#00a33d',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, eliminar venta',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                // Realizar una petición AJAX para eliminar la venta
                $.ajax({
                    url: 'http://localhost:8001/ventas/' + ventaId, // Ruta donde se encuentra el controlador para eliminar la venta
                    type: 'DELETE',
                    success: function (response) {
                        // Mostrar mensaje de éxito con Toastr
                        toastr.success("Venta eliminada con éxito");

                        // Recargar la página después de 1 segundo
                        setTimeout(function () {
                            location.reload();
                        }, 1000);
                    },
                    error: function (xhr, status, error) {
                        toastr.error("Ocurrió un error al intentar eliminar la venta");
                        console.error(xhr.responseText);
                    }
                });
            }
        });
    });
}

function modificarVenta() {

    // Utiliza la delegación de eventos para manejar el clic en los botones de editar venta
    $(document).on('click', '.editarVentaBtn', function () {
        // Obtener el id de la venta que se va a eliminar del atributo data-venta-id del botón
        let ventaId = $(this).data('venta-id');
        $('#modalEditarVenta').modal('show');

        // Variable para mantener un contador de filas agregadas
        let contadorFilas = 0;

        // recuperamos la venta
        $.ajax({
            url: "http://localhost:8001/ventas/" + ventaId,
            method: "GET",
            dataType: "json",
            success: function (venta) {
                console.log('Recupera del back la venta: ' + JSON.stringify(venta) );

                contadorFilas++; // aumentamos el contador

                // cubrimos el campo Cliente con los datos
                $('#ventaClienteEditar').val(venta.cliente.dni.concat(' - ').concat(venta.cliente.nombre,' ', venta.cliente.apellido1,' ', venta.cliente.apellido2));
                // cargamos todos los productos en el select
                cargarProductos();
//aqui estoy
                // añadimos la fila, la cantidad, el productoId y el total al array
                /*arrayProductoCantidad.push(
                    {
                        "fila": contadorFilas,
                        "cantidad": cantidad,
                        "producto": producto,
                        "total": total
                    }
                );*/
                //console.log('array añadir: ' + JSON.stringify(arrayProductoCantidad)); // para ver los datos del array

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
            },
            error: function (xhr, status, error) {
                console.error("Error al recuperar la venta: " + error);
                let errorMessage = "Error al cargar los datos de la venta ";
                if (xhr.status == 400) {
                    errorMessage += "Error de solicitud. Por favor, verifique los datos enviados.";
                } else if (xhr.status == 404) {
                    errorMessage += "No se encontraron datos";
                } else if (xhr.status == 500) {
                    errorMessage += "Error interno del servidor. Por favor, inténtelo de nuevo más tarde.";
                }
            }
        });
    });
}
