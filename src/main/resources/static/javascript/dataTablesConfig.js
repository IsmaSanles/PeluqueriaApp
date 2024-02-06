// aquí solo configuro las opciones de configuración para el dataTable
const dataTableOptions = 
{
	"destroy": true,
	"lengthMenu": [10, 15, 20, 25],
    "scrollCollapse": false, // en 'true' saca un scroll en la DataTable
    "oLanguage": {
        "sProcessing": "Procesando...",
	    "sLengthMenu": "Mostrar _MENU_ registros",
	    "sZeroRecords": "No se encontraron resultados",
	    "sEmptyTable": "Ningún dato disponible en esta tabla",
	    "sInfo": "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
	    "sInfoEmpty": "Mostrando registros del 0 al 0 de un total de 0 registros",
	    "sInfoFiltered": "(filtrado de un total de _MAX_ registros)",
	    "sSearch": "Buscar:",
	    "sLoadingRecords": "Cargando...",
	    "oPaginate": {
	        "sFirst": "Primero",
	        "sLast": "Último",
	        "sNext": "Siguiente",
	        "sPrevious": "Anterior"
	    },
        Aria: {
            "sSortAscending":": Activar para ordenar la columna de manera ascendente",
            "sSortDescending":": Activar para ordenar la columna de manera descendente"
        }
    }
};