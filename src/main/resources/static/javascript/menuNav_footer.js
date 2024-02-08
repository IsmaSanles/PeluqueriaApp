const header = $("header");
const footer = $("footer");

// código para el menú de navegación
header.html( `
<nav class="navbar navbar-expand-lg navbar-dark" style="background-color: #333;">
    <div class="container">
        <a class="navbar-brand" href="#">Peluquería Zitane</a>
        
        <!-- Botón de hamburguesa para pantallas pequeñas -->
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        
        <!-- Pestañas de navegación -->
        <div class="collapse navbar-collapse" id="navbarNav">
            <!-- Actualiza los atributos href a atributos data-view -->
			<ul class="navbar-nav ml-auto">
			    <li class="nav-item">
			        <a class="nav-link" href="../index/index.html" data-view="">Inicio</a>
			    </li>
			    <li class="nav-item">
			        <a class="nav-link" href="../clientes/clientes.html" data-view="">Cliente</a>
			    </li>
			    <li class="nav-item">
			        <a class="nav-link" href="../estilistas/estilistas.html" data-view="">Empleados</a>
			    </li>
			    <li class="nav-item">
			        <a class="nav-link" href="#" data-view="citas.html">Citas</a>
			    </li>
			    <li class="nav-item">
			        <a class="nav-link" href="../productos/productos.html" data-view="productos.html">Productos</a>
			    </li>
			    <li class="nav-item">
			        <a class="nav-link" href="#" data-view="servicios.html">Servicios</a>
			    </li>
			</ul>
        </div>
    </div>
</nav>
`);

// código para el footer
footer.html(`
<footer class="d-flex flex-wrap justify-content-between align-items-center py-3 border-top bg-dark text-white">
	 <div class="col-md-4 d-flex align-items-center">
		<a href="/" class="mb-3 me-2 mb-md-0 text-white text-decoration-none lh-1">
	  		<svg class="bi" width="30" height="24"><use xlink:href="#bootstrap"></use></svg>
		</a>
		<span class="mb-3 mb-md-0">© 2022 Company, Inc</span>
	 </div>
	
	 <ul class="nav col-md-4 justify-content-end list-unstyled d-flex">
		<li class="ms-3"><a href="https://www.facebook.com/login.php"><i class="bi bi-facebook"  style="color:white;"></i></a></li>
		<li class="ms-3"><a href="https://www.instagram.com/?hl=en"><i class="bi bi-instagram"  style="margin-left:10px; color:white;"></i></a></li>
	 </ul>
</footer>
`);
