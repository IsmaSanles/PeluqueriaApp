package com.Proyect.PeluqueriaApp.Services;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyect.PeluqueriaApp.Entities.ServicioEntity;
import com.Proyect.PeluqueriaApp.Repositories.IServicioRepository;

@Service
public class ServicioService {

    @Autowired
    private IServicioRepository servicioRepository;

    // Listar Todos
    public List<ServicioEntity> listarServicios(){
        return (List<ServicioEntity>) servicioRepository.findAll();
    }

    // recuperar un product por Id
    public Optional<ServicioEntity> servicioById(Long Id){
        return servicioRepository.findById(Id);
    }

    // Crear Servicio
    public ServicioEntity crearServicio(ServicioEntity servicio) {
        return servicioRepository.save(servicio);
    }

    // Modificar Servicio
    public ServicioEntity modificarServicio(ServicioEntity servicio) {
        return servicioRepository.save(servicio);
    }



    // Eliminar/Ocultar Servicio
    public void eliminarOcultarServicioById(Long id) {
        servicioRepository.eliminarOcultarServicioById(id);
    }
}