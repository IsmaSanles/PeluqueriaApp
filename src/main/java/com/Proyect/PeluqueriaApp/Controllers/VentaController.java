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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;


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

		// Ordenar las ventas por fecha de manera descendente
		listadoVentas.sort(Comparator.comparing(VentaEntity::getFechaVenta).reversed());

		if (listadoVentas.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron datos en la Base de Datos");
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

			// Verificar disponibilidad de stock para todos los productos vendidos en la venta
			boolean suficienteStock = true;
			for (VentaProductoEntity ventaProducto : venta.getProductosVendidos()) {
				Optional<ProductoEntity> productoOptional = productoService.productoById(ventaProducto.getProducto().getProductoId());
				if (productoOptional.isPresent()) {
					ProductoEntity producto = productoOptional.get();
					if (producto.getStock() < ventaProducto.getUdsVendidas()) {
						suficienteStock = false;
						break;
					}
				} else {
					suficienteStock = false;
					break;
				}
			}

			if (!suficienteStock) {
				// Manejar la falta de stock y devolver una respuesta adecuada 409
				return ResponseEntity.status(HttpStatus.CONFLICT).body("No hay suficiente stock para completar la venta");
			}

			// Actualizar el stock de los productos vendidos
			for (VentaProductoEntity ventaProducto : venta.getProductosVendidos()) {
				// añadimos la fecha de creación a cada venta producto
				ventaProducto.setFechaCreacion(new Date());
				// recupero el producto por su ID
				Optional<ProductoEntity> productoOptional = productoService.productoById(ventaProducto.getProducto().getProductoId());
				if (productoOptional.isPresent()) {
					ProductoEntity producto = productoOptional.get();
					// guardo el valor del precio que tiene el producto en el momento de la venta
					ventaProducto.setPrecioVenta(producto.getPrecio());
					// modifico el stock tras la venta
					int stock = producto.getStock() - ventaProducto.getUdsVendidas();
					producto.setStock(stock);
				}
			}

			// Crear la venta y asignar el ID de venta a los productos vendidos
			VentaEntity nuevaVenta = ventaService.crearVenta(venta);
			for (VentaProductoEntity ventaProducto : venta.getProductosVendidos()) {
				ventaProducto.setVenta(nuevaVenta);
			}

			// Devolver una respuesta exitosa
			return ResponseEntity.status(HttpStatus.OK).body(nuevaVenta);
		} catch (Exception e) {
			// Manejar cualquier excepción que ocurra durante el proceso
			System.err.println("ERROR en el controlador CrearVenta: " + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PutMapping("/{id}")
	@Transactional(rollbackFor = Exception.class) // Rollback para cualquier excepción
	@Modifying // Esto indica que vamos a realizar algún cambio en la BD como CREAR, MODIFICAR o ELIMINAR
    public ResponseEntity<?> modificarEstilista(@PathVariable Long id, @Valid @RequestBody VentaEntity venta, BindingResult result) {

		if (result.hasErrors()) {
			// Manejar los errores de validación y devolverlos como parte de la respuesta HTTP
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}

		return ResponseEntity.status(HttpStatus.OK).body("Venta modificada con éxito");
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class) // Rollback para cualquier excepción
    @Modifying // Esto indica que vamos a realizar algún cambio en la BD como CREAR, MODIFICAR o ELIMINAR
    public ResponseEntity<?> eliminarVenta(@PathVariable Long id) {

		// Buscamos si existe esa venta con sus datos
		Optional<VentaEntity> ventaOptional = this.ventaService.getVentaConDetallesPorId(id);

		if (ventaOptional.isPresent()) {
			try {
				// borramos cada ventaProducto con un bucle
				Long ventaProductoId;
				for (VentaProductoEntity ventaProducto : ventaOptional.get().getProductosVendidos()) {
					// antes de eliminar cada ventaProducto debemos actualizar el stock del Producto
					ventaProducto.getProducto().setStock(ventaProducto.getUdsVendidas() + ventaProducto.getProducto().getStock());

					ventaProductoId = ventaProducto.getVentaProductoId();
					this.ventaProductoService.eliminarVentaProducto(ventaProductoId);
				}
				// ahora borramos la venta definitivamente
				this.ventaService.eliminarVenta(id);

				return ResponseEntity.status(HttpStatus.OK).body("Venta eliminada exitosamente.");
			}catch (Exception e) {
				System.err.println("Error al eliminar la venta: " + e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Venta no encontrada en la Base de Datos");
		}
    }

	@GetMapping("/porFecha/{fecha}")
	public ResponseEntity<?> getAllVentasByFecha(@PathVariable String fecha) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date fechaDate = null;
		try {
			fechaDate = formatter.parse(fecha);

			List<VentaEntity> listadoVentasPorFecha = ventaService.getAllVentasByFecha(fechaDate);

			// Ordenar las ventas por fecha de manera descendente
			listadoVentasPorFecha.sort(Comparator.comparing(VentaEntity::getFechaVenta).reversed());

			if (listadoVentasPorFecha.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron datos en la Base de Datos");
			} else {
				return ResponseEntity.status(HttpStatus.OK).body(listadoVentasPorFecha);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de fecha incorrecto");
		}
	}
}