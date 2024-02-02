package com.Proyect.PeluqueriaApp.Services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Proyect.PeluqueriaApp.Entities.EstilistaEntity;
import com.Proyect.PeluqueriaApp.Repositories.IEstilistaRepository;

@Service
public class EstilistaService {

	@Autowired
	private IEstilistaRepository estilistaRepository;
	
	// Listar Todos
	public List<EstilistaEntity> listarEstilistas(){
		return (List<EstilistaEntity>) estilistaRepository.findAll();
	}
	
	// recuperar un estilista por Id
	public Optional<EstilistaEntity> estilistaById(Long Id){
		return estilistaRepository.findById(Id);
	}
	
	// Crear estilista
	public EstilistaEntity crearEstilista(EstilistaEntity estilista) {
		return estilistaRepository.save(estilista);
	}
	
	// Modificar estilista
	public EstilistaEntity modificarEstilista(EstilistaEntity estilista) {
		return estilistaRepository.save(estilista);
	}
	
	// Eliminar/Ocultar Estilista
	public void eliminarOcultarEstilistaById(Long id) {
		estilistaRepository.eliminarOcultarEstilistaById(id);
	}
}