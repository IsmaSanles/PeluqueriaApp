package com.Proyect.PeluqueriaApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Proyect.PeluqueriaApp.Entities.ServicioEntity;

@Repository
public interface IServicioRepository extends JpaRepository<ServicioEntity, Long>{
    // Aquí irán las Querys que vayamos necesitando

    @Modifying
    @Transactional
    @Query("UPDATE ServicioEntity c SET c.deAlta = false WHERE c.servicioId = :servicioId")
    public void eliminarOcultarServicioById(Long servicioId);
}
