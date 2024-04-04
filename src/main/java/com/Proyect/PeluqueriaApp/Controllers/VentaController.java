package com.Proyect.PeluqueriaApp.Controllers;

import com.Proyect.PeluqueriaApp.Entities.ProductoEntity;
import com.Proyect.PeluqueriaApp.Entities.VentaEntity;
import com.Proyect.PeluqueriaApp.Entities.VentaProductoEntity;
import com.Proyect.PeluqueriaApp.Services.ProductoService;
import com.Proyect.PeluqueriaApp.Services.VentaProductoService;
import com.Proyect.PeluqueriaApp.Services.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


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
	public ResponseEntity<?> modificarVenta(@PathVariable Long id, @Valid @RequestBody VentaEntity ventaModificada, BindingResult result) {
		try {
			if (result.hasErrors()) {
				// Manejar los errores de validación y devolverlos como parte de la respuesta HTTP
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
			}

			// Verificar si la venta existe
			Optional<VentaEntity> ventaOptional = ventaService.getVentaConDetallesPorId(ventaModificada.getVentaId());
			if (!ventaOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Venta no encontrada en la Base de Datos");
			}

			// Obtener la venta existente
			VentaEntity ventaExistente = ventaOptional.get();

			// Actualizar los campos de la venta existente con los valores de la venta modificada
			ventaExistente.setMetodoPago(ventaModificada.getMetodoPago());

			// Actualizar los productos asociados a la venta
			actualizarProductosVenta(ventaExistente, ventaModificada.getProductosVendidos());

			// Guardar los cambios en la venta
			ventaService.modificarVenta(ventaExistente);

			// Devuelve una respuesta exitosa
			return ResponseEntity.status(HttpStatus.OK).body(ventaExistente);
		} catch (DataAccessException e) {
			System.err.println("Error al modificar la venta: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al acceder a la base de datos");
		} catch (Exception e) {
			// Manejar cualquier otra excepción que ocurra durante el proceso
			System.err.println("Error al modificar la venta: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Transactional(rollbackFor = Exception.class) // Rollback para cualquier excepción
	@Modifying // Esto indica que vamos a realizar algún cambio en la BD como CREAR, MODIFICAR o ELIMINAR
	private void actualizarProductosVenta(VentaEntity ventaExistente, List<VentaProductoEntity> productosModificados) {

		// Paso 1: Obtener la lista de productos actualmente asociados a la venta
		List<VentaProductoEntity> productosActuales = ventaExistente.getProductosVendidos();

		// Paso 2: Identificar y crear nuevos productos que están en la lista modificada pero no en la lista actual
		for (VentaProductoEntity productoModificado : productosModificados) {
			boolean encontrado = false;
			for (VentaProductoEntity productoActual : productosActuales) {
				if (productoModificado.getProducto().getProductoId().equals(productoActual.getProducto().getProductoId())) {
					//System.out.println("Producto encontrado");
					encontrado = true;
					break;
				}
			}
			// Si el producto modificado no se encuentra en la lista de productos actuales, se debe crear
			if (!encontrado && productoModificado.getVentaProductoId() == null) {
				//System.out.println("Producto NO encontrado, debe ser creado");

				// recuperar el producto por id
				Optional<ProductoEntity> productoOptional = productoService.productoById(productoModificado.getProducto().getProductoId());
				// si existe el producto en la base de datos lo modificamos
				if (productoOptional.isPresent()) {
					ProductoEntity producto = productoOptional.get();
					// Descontar la cantidad del stock del producto modificado
					int stock = producto.getStock() - productoModificado.getUdsVendidas();
					producto.setStock(stock);

					// Crear un nuevo producto y asociarlo a la venta
					VentaProductoEntity nuevoProducto = new VentaProductoEntity();
					nuevoProducto.setVenta(ventaExistente);
					nuevoProducto.setProducto(producto);
					nuevoProducto.setUdsVendidas(productoModificado.getUdsVendidas());
					nuevoProducto.setPrecioVenta(producto.getPrecio());
					nuevoProducto.setFechaCreacion(new Date());
					// Agregar el nuevo producto a la lista de productos actuales
					productosActuales.add(nuevoProducto);
				}
			}
		}

		// Paso 3: Comparar las listas de productos actuales y modificados para identificar los productos a eliminar
		List<VentaProductoEntity> productosAEliminar = new ArrayList<>();
		for (VentaProductoEntity productoActual : productosActuales) {
			boolean encontrado = false;
			for (VentaProductoEntity productoModificado : productosModificados) {
				if (productoActual.getProducto().getProductoId().equals(productoModificado.getProducto().getProductoId())) {
					encontrado = true;
					// actualizamos el stock en caso necesario
					if (productoModificado.getUdsVendidas() > productoActual.getUdsVendidas()) {
						int difStock = productoModificado.getUdsVendidas() - productoActual.getUdsVendidas();
						productoActual.getProducto().setStock(productoActual.getProducto().getStock() - difStock);
					} else if (productoModificado.getUdsVendidas() < productoActual.getUdsVendidas()) {
						int difStock = productoActual.getUdsVendidas() - productoModificado.getUdsVendidas();
						productoActual.getProducto().setStock(productoActual.getProducto().getStock() + difStock);
					}

					productoActual.setUdsVendidas(productoModificado.getUdsVendidas());
					break;
				}
			}
			// Si el producto actual no se encuentra en la lista de productos modificados, se debe eliminar
			if (!encontrado) {
				productosAEliminar.add(productoActual);
			}
		}

		// Eliminar los productos que ya no están en la lista modificada
		for (VentaProductoEntity productoAEliminar : productosAEliminar) {
			// Recuperar el stock del producto actual y agregar la cantidad al stock del Producto
			int stock = productoAEliminar.getProducto().getStock() + productoAEliminar.getUdsVendidas();
			productoAEliminar.getProducto().setStock(stock);
			// actualizamos el STOCK modificando el producto
			productoService.modificarProducto(productoAEliminar.getProducto());
			// eliminamos el productoVendido de la Base de datos
			ventaProductoService.eliminarVentaProducto(productoAEliminar.getVentaProductoId());
			// Eliminar el productoVendido actual de la lista
			productosActuales.remove(productoAEliminar);
		}
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
