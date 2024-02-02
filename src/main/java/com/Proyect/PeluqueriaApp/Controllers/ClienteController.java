package com.Proyect.PeluqueriaApp.Controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
	public ClienteEntity crearCliente(@Valid @RequestBody ClienteEntity cliente) {
	    
	    cliente.setFechaCreacion(new Date());

	    // Llama al servicio para crear el cliente
	    return this.clienteService.crearCliente(cliente);
	}
	
	@PutMapping("/{id}")
    public ResponseEntity<ClienteEntity> modificarCliente(@Valid @PathVariable Long id, @RequestBody ClienteEntity nuevoCliente) {
		
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