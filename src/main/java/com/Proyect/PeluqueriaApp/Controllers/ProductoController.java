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
	
	@GetMapping("/{id}")
	public ProductoEntity obtenerProductoPorId(@PathVariable Long id) {
		Optional<ProductoEntity> producto = this.productoService.productoById(id);
		
		if (producto.isPresent()) {
			return producto.get();
		} else {
			return null;
		}
	}
	
	@PostMapping("/crear")
	public ProductoEntity crearProducto(@Valid @RequestBody ProductoEntity producto) {
	    
	    producto.setFechaCreacion(new Date());

	    // Llama al servicio para crear el producto
	    return this.productoService.crearProducto(producto);
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<ProductoEntity> modificarProducto(@Valid @PathVariable Long id, @RequestBody ProductoEntity nuevoProducto) {
		
	    // Busco el producto por el Id
	    ProductoEntity productoRecuperado = obtenerProductoPorId(id);

	    if (productoRecuperado != null) {
	        // añadimos los campos que no queremos que cambien
	    	nuevoProducto.setProductoId(productoRecuperado.getProductoId());
	    	nuevoProducto.setFechaModificacion(new Date());
	    	nuevoProducto.setFechaCreacion(productoRecuperado.getFechaCreacion());
	    	
	        // Guardo el producto actualizado en el repositorio
	        productoRecuperado = this.productoService.modificarProducto(nuevoProducto);
	        
		  return ResponseEntity.status(HttpStatus.OK).body(productoRecuperado);
			        
			    }else {
			    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
    	//Buscamos si existe ese usuario
    	ProductoEntity productoRecuperado = obtenerProductoPorId(id);
    	
    	if(productoRecuperado != null && productoRecuperado.isDeAlta() == true) {
    		this.productoService.eliminarOcultarProductoById(id);
            return ResponseEntity.status(HttpStatus.OK).body(productoRecuperado);
    	}else if(productoRecuperado != null && productoRecuperado.isDeAlta() == false) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(productoRecuperado);
    	}else {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    	}
    }
}