
$(document).ready(function () {
    listarProductos();
    abrirModalCrear();
   	
});

function abrirModalCrear(){
	// Manejador de clic para el botón "Crear Nuevo Producto"
    $('#crearNuevoProducto').on('click', function () {
        $('#modalCrearProducto').modal('show');
	    
	    // creamos un escuchador al botón crear del modal de Crear Producto
	    $("#btnCrear").on("click", function() {
	        crearProducto();
	    });
    });
}

// creo la funcion para listar lo productos
function listarProductos() {
    $.ajax({
        url: "http://localhost:8001/producto",
        method: "GET",
        dataType: "json",
        success: function (data) {
        	// Limpia el cuerpo de la tabla
            $('#tbodyProductos').empty();
           
        	console.log(data);
            let content = ``;
            data.forEach(function (producto) {
                content += `
                <tr>
                    <td>${producto.nombre}</td>
                    <td>${producto.precio}</td>
                    <td>${producto.stock}</td>
                    <td>${producto.deAlta ? '<i class="bi bi-check-circle-fill text-success"></i>' : '<i class="bi bi-x-circle-fill text-danger"></i>'}</td>
                    <td>${producto.descripcion}</td>
                    <td class="d-flex">
                        <button class="btn btn-primary mr-2 editarProductoBtn" data-producto-id="${producto.productoId}">
                            <i class="bi bi-pencil-square"></i>
                        </button>
                        <button class="btn btn-danger eliminarProductoBtn" data-producto-id="${producto.productoId}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </td>
                </tr>`;
            });
            $("#tbodyProductos").html(content);
            
            
            // Inicializa el plugin DataTable con las opciones de configuración
            $("#tablaProductos").DataTable({
				...dataTableOptions, 
	            columnDefs: [
					{ className: "text-center", targets: "_all" }, // centramos todos los textos de las columnas
			        { targets: [4, 5], orderable: false } // indicamos que las columnas definidas no puedan filtrar
		    	]
		    });
            
            
            // Manejador de clic para los botones "Editar Producto"
		    $('.editarProductoBtn').on('click', function () {
			    let productoId = $(this).data("producto-id");
			    //console.log("Capturado evento de Editar para id: " + productoId);
			    
			    // abrimos modal Editar
			    $('#modalEditarProducto').modal('show');
			    
			    // recupero los datos de ese productoId y relleno los campos
			    getProductoById(productoId);			    
			    
			    // creamos un escuchador al botón crear del modal de Crear Producto
			    $("#btnEditar").on("click", function() {
			        editarProducto(productoId);
	    		});
			});
			
			// Manejador de clic para los botones "Eliminar Producto"
		    $('.eliminarProductoBtn').on('click', function () {
				let productoId = $(this).data("producto-id");
			    console.log("Capturado evento de Eliminar para id: " + productoId);
				
				// llamada a la funcion para eliminar (dar de baja)
				eliminarProducto(productoId);
			});
		    
        },
        error: function (error) {
			// En caso de error, ocultar la tabla y mostrar el mensaje de fallo
            $("#tablaProductos").hide();
            $("#mensajeFallo").show();
            toastr.error("Hubo un error al cargar la tabla de productos");
        }
    });
};

// recuperamos los datos del producto
async function getProductoById(id) {
    await $.ajax({
        url: "http://localhost:8001/producto/" + id,
        method: "GET",
        dataType: "json",
        success: function (data) {
			console.log("nombre"+data.nombre);
            // relleno los campos del formulario
		    $("#nombreEditar").val(data.nombre);
            $("#precioEditar").val(data.precio);
            $("#stockEditar").val(data.stock);
            $("#descripcionEditar").val(data.descripcion);
	            
        },
        error: function (error) {
            // Manejo de errores
            //console.error("Error al recuperar los datos del producto");
            toastr.error("Hubo un error al recuperar los datos del producto");
        }
    });
}

// crear Nuevo Producto	
function crearProducto(){
    // recuperamos los datos para enviar al back
    let nombre = $("#nombreCrear").val();
    let precio = $("#precioCrear").val();
    let stock = $("#stockCrear").val();
    let descripcion = $("#descripcionCrear").val();
    
    console.log(nombre, precio, stock, descripcion);

    // ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/producto/crear",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            nombre,
            precio,
            stock,
            descripcion
                        
        }),
        success: function (data) {
            //console.log(data);
            
            // Mostrar mensaje de éxito con Toastr
            toastr.success("Nuevo producto creado con éxito");
            
            // Recargar la página después de 1 segundo
            setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function (xhr, status, error) {
            let errorMessage = xhr.responseText;
            console.log(errorMessage);
            // Mostrar mensaje de éxito con Toastr
            toastr.error("A ocurrido un error al crear el producto");
        }
    });
};

// editar Producto	
function editarProducto(id){
	let nombre = $("#nombreEditar").val();
    let precio = $("#precioEditar").val();
    let stock = $("#stockEditar").val();
    let descripcion = $("#descripcionEditar").val();

	// ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/producto/" + id,
        method: "PUT",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            nombre,
            precio,
            stock,
            descripcion
        }),
        success: function (data) {
            //console.log(data);
            
            // Mostrar mensaje de éxito con Toastr
            toastr.success("Datos del producto editados con éxito");
            
            // Recargar la página después de 1 segundo
            setTimeout(function() {
                location.reload();
            }, 1000);
        },
        error: function (xhr, status, error) {
            let errorMessage = xhr.responseText;
            console.log(errorMessage);
            // Mostrar mensaje de éxito con Toastr
            toastr.error("Ocurrió un error al editar los datos del producto");
        }
    });
}


// eliminar (dar de baja) Producto
function eliminarProducto(id) {
    // ejecucion de peticion ajax para la conexión con el backend
    $.ajax({
        url: "http://localhost:8001/producto/" + id,
        method: "DELETE",
        dataType: "json",
        success: function (data) {
            console.log("success: " + JSON.stringify(data));
            
            // Mostrar mensaje de éxito con Toastr
            toastr.success("Producto dado de baja con éxito");
            
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
                toastr.error("El producto ya está dado de baja");
            } else if (xhr.status === 404) {
                // Código 404 (NotFound)
                toastr.error("Producto no encontrado en la Base de Datos");
            } else {
                // Otros errores
                toastr.error("Error al dar de baja al producto");
            }
        }
    });
};