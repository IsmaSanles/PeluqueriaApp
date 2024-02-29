package com.Proyect.PeluqueriaApp.Controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Proyect.PeluqueriaApp.Entities.ProductoEntity;
import com.Proyect.PeluqueriaApp.Services.ProductoService;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:8001") // para que no dea problemas de CORS policy
@RestController
@RequestMapping("/producto")
public class ProductoController {
	
	@Autowired
	private ProductoService productoService;
	
	@GetMapping
	public ResponseEntity<?> listarProductos() {
		
		List<ProductoEntity> listadoProductos = this.productoService.listarProductos();
		
		if (listadoProductos.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(listadoProductos);
		}
	}

	@GetMapping("/deAlta")
	public ResponseEntity<?> listarProductosDeAlta() {

		List<ProductoEntity> listarProductosDeAlta = this.productoService.listarProductosDeAlta();

		if (listarProductosDeAlta.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(listarProductosDeAlta);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductoEntity> obtenerProductoPorId(@PathVariable Long id) {
		Optional<ProductoEntity> productoOptional = this.productoService.productoById(id);

		if (productoOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(productoOptional.get());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@PostMapping("/crear")
	public ResponseEntity<ProductoEntity> crearProducto(@Valid @RequestBody ProductoEntity producto) {
		// seteo la fecha de creación por defecto
		producto.setFechaCreacion(new Date());

		// Llama al servicio para crear el producto
		ProductoEntity nuevoProducto = this.productoService.crearProducto(producto);

		// Devuelve un ResponseEntity con el nuevo producto y un estado OK 201
		return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductoEntity> modificarProducto(@Valid @PathVariable Long id, @RequestBody ProductoEntity nuevoProducto) {

		// Busco el producto por el Id
		ResponseEntity<ProductoEntity> responseProducto = obtenerProductoPorId(id);
		if (responseProducto.getStatusCode() == HttpStatus.OK) {
			ProductoEntity productoRecuperado = responseProducto.getBody();

			// Añadir los campos que no queremos que cambien
			nuevoProducto.setProductoId(productoRecuperado.getProductoId());
			nuevoProducto.setFechaCreacion(productoRecuperado.getFechaCreacion());
			nuevoProducto.setFechaModificacion(new Date());

			// Guardar el producto actualizado en el repositorio
			ProductoEntity productoModificado = this.productoService.modificarProducto(nuevoProducto);

			return ResponseEntity.status(HttpStatus.OK).body(productoModificado);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
		// Buscamos si existe ese producto
		ResponseEntity<ProductoEntity> responseProducto = obtenerProductoPorId(id);

		if (responseProducto.getStatusCode() == HttpStatus.OK) {
			ProductoEntity productoRecuperado = responseProducto.getBody();

			if (productoRecuperado.isDeAlta()) {
				this.productoService.eliminarOcultarProductoById(id);
				return ResponseEntity.status(HttpStatus.OK).body(productoRecuperado);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El producto ya está eliminado.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
}