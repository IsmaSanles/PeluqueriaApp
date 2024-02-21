package com.Proyect.PeluqueriaApp.Controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.Proyect.PeluqueriaApp.Entities.EstilistaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.Proyect.PeluqueriaApp.Entities.ClienteEntity;
import com.Proyect.PeluqueriaApp.Services.ClienteService;
import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:8001") // para que no dea problemas de CORS policy
@RestController
@RequestMapping("/cliente")
public class ClienteController {
	
	@Autowired
	private ClienteService clienteService;
	
	@GetMapping
	public ResponseEntity<?> listarClientes() {
		
		List<ClienteEntity> listadoClientes = this.clienteService.listarClientes();
		
		if (listadoClientes.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(listadoClientes);
		}
	}

	@GetMapping("/deAlta")
	public ResponseEntity<?> listarClientesDeAlta() {

		List<ClienteEntity> listarClientesDeAlta = this.clienteService.listarClientesDeAlta();

		if (listarClientesDeAlta.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} else {
			return ResponseEntity.status(HttpStatus.OK).body(listarClientesDeAlta);
		}
	}
	
	@GetMapping("/{id}")
	public ClienteEntity obtenerClientePorId(@PathVariable Long id) {
		Optional<ClienteEntity> cliente = this.clienteService.clienteById(id);
		
		if (cliente.isPresent()) {
			return cliente.get();
		} else {
			return null;
		}
	}
	
	@PostMapping("/crear")
	public ResponseEntity<?> crearCliente(@Valid @RequestBody ClienteEntity cliente, BindingResult result) {
		if (result.hasErrors()) {
			// Manejar los errores de validación y devolverlos como parte de la respuesta HTTP
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}

	    cliente.setFechaCreacion(new Date());

		// Si no hay errores de validación, crear el estilista
		ClienteEntity nuevoCliente = clienteService.crearCliente(cliente);
		return ResponseEntity.status(HttpStatus.OK).body(nuevoCliente);
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<?> modificarCliente(@PathVariable Long id, @Valid @RequestBody ClienteEntity nuevoCliente, BindingResult result) {
		if (result.hasErrors()) {
			// Manejar los errores de validación y devolverlos como parte de la respuesta HTTP
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
		}

	    // Busco el cliente por el Id en la BD
	    ClienteEntity clienteRecuperado = obtenerClientePorId(id);

	    if (clienteRecuperado != null) {
	        // añadimos los campos que no queremos que cambien
	    	nuevoCliente.setClienteId(id);
	    	nuevoCliente.setFechaModificacion(new Date());
	    	nuevoCliente.setFechaCreacion(clienteRecuperado.getFechaCreacion());
	    	
	        // Guardo el cliente actualizado en el repositorio
	        clienteRecuperado = this.clienteService.modificarCliente(nuevoCliente);
	        
	        return ResponseEntity.status(HttpStatus.OK).body(clienteRecuperado);
	        
	    }else {
	    	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
    	//Buscamos si existe ese usuario
    	ClienteEntity clienteRecuperado = obtenerClientePorId(id);
    	
    	if(clienteRecuperado != null && clienteRecuperado.isDeAlta() == true) {
    		this.clienteService.eliminarOcultarClienteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(clienteRecuperado);
    	}else if(clienteRecuperado != null && clienteRecuperado.isDeAlta() == false) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(clienteRecuperado);
    	}else {
    		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    	}
    }
}