package com.Proyect.PeluqueriaApp.Controllers;

import com.Proyect.PeluqueriaApp.Entities.VentaProductoEntity;
import com.Proyect.PeluqueriaApp.Services.VentaProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8001") // para que no dea problemas de CORS policy
@RestController
@RequestMapping("/ventaProducto")
public class VentaProductoController {
	
	@Autowired
	private VentaProductoService ventaProductoService;

	// recupera pasándole el Id
	@GetMapping("/{id}")
	public ResponseEntity<VentaProductoEntity> obtenerVentaProductoById(@PathVariable Long id) {
		Optional<VentaProductoEntity> ventaProductoOptional = this.ventaProductoService.getVentaProductoById(id);

		if (ventaProductoOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(ventaProductoOptional.get());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@DeleteMapping("/{id}")
	@Transactional(rollbackFor = Exception.class) // Rollback para cualquier excepción
	public ResponseEntity<?> eliminarVentaProducto(@PathVariable Long id) {
		// Buscamos si existe
		Optional<VentaProductoEntity> ventaProductoOptional = ventaProductoService.getVentaProductoById(id);

		if (ventaProductoOptional.isPresent()) {
			ventaProductoService.eliminarVentaProducto(id);
			return ResponseEntity.status(HttpStatus.OK).body("VentaProducto eliminado correctamente");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ningún VentaProducto con el id proporcionado");
		}
	}


}
