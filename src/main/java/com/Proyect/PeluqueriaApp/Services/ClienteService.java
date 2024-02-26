package com.Proyect.PeluqueriaApp.Services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Proyect.PeluqueriaApp.Entities.ClienteEntity;
import com.Proyect.PeluqueriaApp.Repositories.IClienteRepository;

@Service
public class ClienteService {

	@Autowired
	private IClienteRepository clienteRepository;
	
	// Listar Todos
	public List<ClienteEntity> listarClientes(){
		return (List<ClienteEntity>) clienteRepository.findAll();
	}

	// Listar Todos lo que están de alta
	public List<ClienteEntity> listarClientesDeAlta(){
		return (List<ClienteEntity>) clienteRepository.listarClientesDeAlta();
	}
	
	// recuperar un cliente por Id
	public Optional<ClienteEntity> clienteById(Long Id){
		return clienteRepository.findById(Id);
	}
	
	// Crear Cliente
	public ClienteEntity crearCliente(ClienteEntity cliente) {
		return clienteRepository.save(cliente);
	}
	
	// Modificar Cliente
	public ClienteEntity modificarCliente(ClienteEntity cliente) {
		return clienteRepository.save(cliente);
	}
		
	/*
	// Eliminar Cliente
	public void eliminarCliente(Long id) {
		clienteRepository.deleteById(id);
	}
	*/
	
	// Eliminar/Ocultar Cliente
	public void eliminarOcultarClienteById(Long id) {
		clienteRepository.eliminarOcultarClienteById(id);
	}
}
