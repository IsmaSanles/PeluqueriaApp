package com.Proyect.PeluqueriaApp.Controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.Proyect.PeluqueriaApp.Entities.EstilistaEntity;
import com.Proyect.PeluqueriaApp.Services.EstilistaService;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:8001") // para que no dea problemas de CORS policy
@RestController
@RequestMapping("/estilista")
public class EstilistaController {
	
	@Autowired
	private EstilistaService estilistaService;
	
	@GetMapping
	public ResponseEntity<?> listarEstilistas() {
		
		List<EstilistaEntity> listadoEstilistas = this.estilistaService.listarEstilistas();
		
		if (listadoEstilistas.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(listadoEstilistas);
		}
	}
	
	@GetMapping("/{id}")
	public EstilistaEntity obtenerEstilistaPorId(@PathVariable Long id) {
		Optional<EstilistaEntity> estilista = this.estilistaService.estilistaById(id);
		
		if (estilista.isPresent()) {
			return estilista.get();
		} else {
			return null;
		}
	}
	
	@PostMapping("/crear")
    public ResponseEntity<?> crearEstilista(@Valid @RequestBody EstilistaEntity estilista, BindingResult result) {
        if (result.hasErrors()) {
            // Manejar los errores de validación y devolverlos como parte de la respuesta HTTP
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
        }
        
        // Si no hay errores de validación, crear el estilista
        EstilistaEntity nuevoEstilista = estilistaService.crearEstilista(estilista);
        return ResponseEntity.status(HttpStatus.OK).body(nuevoEstilista);
    }
	
	@PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEstilista(@PathVariable Long id) {
    	//Buscamos si existe ese usuario
    	EstilistaEntity estilistaRecuperado = obtenerEstilistaPorId(id);
    	
    	if(estilistaRecuperado != null && estilistaRecuperado.isDeAlta() == true) {
    		this.estilistaService.eliminarOcultarEstilistaById(id);
            return ResponseEntity.status(HttpStatus.OK).body(estilistaRecuperado);
    	}else if(estilistaRecuperado != null && estilistaRecuperado.isDeAlta() == false) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(estilistaRecuperado);
    	}else {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    	}
    }
}