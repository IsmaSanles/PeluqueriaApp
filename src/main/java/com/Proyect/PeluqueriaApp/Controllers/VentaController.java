package com.Proyect.PeluqueriaApp.Controllers;

import com.Proyect.PeluqueriaApp.Entities.ProductoEntity;
import com.Proyect.PeluqueriaApp.Entities.VentaEntity;
import com.Proyect.PeluqueriaApp.Entities.VentaProductoEntity;
import com.Proyect.PeluqueriaApp.Services.ProductoService;
import com.Proyect.PeluqueriaApp.Services.VentaProductoService;
import com.Proyect.PeluqueriaApp.Services.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8001") // para que no dea problemas de CORS policy
@RestController
@RequestMapping("/ventas")
public class VentaController {
	
	@Autowired
	private VentaService ventaService;
	@Autowired
	private VentaProductoService ventaProductoService;
	@Autowired
	private ProductoService productoService;

	@GetMapping
	public ResponseEntity<?> listarVentas() {

		List<VentaEntity> listadoVentas = ventaService.getAllVentasConDetalles();

		if (listadoVentas.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(listadoVentas);
		}
	}

	// recupera todos los detalles pasándole el Id
	@GetMapping("/{id}")
	public ResponseEntity<VentaEntity> getVentaConDetallesPorId(@PathVariable Long id) {
		Optional<VentaEntity> ventaOptional = this.ventaService.getVentaConDetallesPorId(id);

		if (ventaOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(ventaOptional.get());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PostMapping("/crear")
	@Transactional(rollbackFor = Exception.class) // Rollback para cualquier excepción
	@Modifying // Esto indica que vamos a realizar algún cambio en la BD como CREAR, MODIFICAR o ELIMINAR
	public ResponseEntity<?> crearVenta(@Valid @RequestBody VentaEntity venta, BindingResult result) {
		try {


			if (result.hasErrors()) {
				// Manejar los errores de validación y devolverlos como parte de la respuesta HTTP
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
			}

			// Añadimos la fecha de Venta
			venta.setFechaVenta(new Date());

			// Añadimos la fecha de Creación a cada VentaProducto
			for (VentaProductoEntity ventaProducto : venta.getProductosVendidos()) {
				// seteamos la fecha de creación
				ventaProducto.setFechaCreacion(new Date());

				// ahora recuperamos los datos del producto por su Id y asignamos el precio en el momento de la venta
				Optional<ProductoEntity> productoOptional = productoService.productoById(ventaProducto.getProducto().getProductoId());
				if (productoOptional.isPresent()) {
					// Si el producto fue encontrado, accedemos a el
					ProductoEntity producto = productoOptional.get();
					// añadimos el precio del producto en el momento de la venta al campo precioVenta
					ventaProducto.setPrecioVenta(producto.getPrecio());
				}
			}

			// crear la venta
			VentaEntity nuevaVenta = ventaService.crearVenta(venta);

			// indicamos en cada producto vendido el Id de la venta que le corresponde
			for (VentaProductoEntity ventaProducto : venta.getProductosVendidos()) {
				ventaProducto.setVenta(nuevaVenta);
			}

			// aquí añadimos el idVenta a los campos para la relación
			VentaEntity ventaGuardada = ventaService.crearVenta(nuevaVenta);

			return ResponseEntity.status(HttpStatus.OK).body(ventaGuardada);
		} catch (Exception e) {
			// Manejar cualquier excepción que ocurra durante el proceso
			System.err.println("ERROR en el controlador CrearVenta: " + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la venta");
		}
	}

	/*
	@PutMapping("/{id}")
	@Transactional(rollbackFor = Exception.class) // Rollback para cualquier excepción
    public ResponseEntity<?> modificarEstilista(@PathVariable Long id, @Valid @RequestBody EstilistaEntity nuevoEstilista, BindingResult result) {

        if (result.hasErrors()) {
            // Manejar los errores de validación y devolverlos como parte de la respuesta HTTP
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
        }

	    // Busco el estilista por el Id
	    EstilistaEntity estilistaRecuperado = obtenerEstilistaPorId(id);

	    if (estilistaRecuperado != null) {
	        // añadimos los campos que no queremos que cambien
	    	nuevoEstilista.setEstilistaId(estilistaRecuperado.getEstilistaId());
	    	nuevoEstilista.setFechaModificacion(new Date());
	    	nuevoEstilista.setFechaCreacion(estilistaRecuperado.getFechaCreacion());
	    	
	        // Guardo el estilista actualizado en el repositorio
	        estilistaRecuperado = this.estilistaService.modificarEstilista(nuevoEstilista);
	        
	        return ResponseEntity.status(HttpStatus.OK).body(estilistaRecuperado);
	    }else {
	    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
	*/
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class) // Rollback para cualquier excepción
    @Modifying // Esto indica que vamos a realizar algún cambio en la BD como CREAR, MODIFICAR o ELIMINAR
    public ResponseEntity<?> eliminarVenta(@PathVariable Long id) {
    	// Buscamos si existe esa venta con sus datos
		Optional<VentaEntity> ventaOptional = this.ventaService.getVentaConDetallesPorId(id);

		if (ventaOptional.isPresent()) {
			// borramos cada ventaProducto con un bucle
			Long ventaProductoId;
			for(VentaProductoEntity ventaProducto : ventaOptional.get().getProductosVendidos()){
				ventaProductoId = ventaProducto.getVentaProductoId();
				this.ventaProductoService.eliminarVentaProducto(ventaProductoId);
			}

			// ahora borramos la venta definitivamente
			this.ventaService.eliminarVenta(id);

			return ResponseEntity.status(HttpStatus.OK).body("Venta eliminada exitosamente.");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
    }

}