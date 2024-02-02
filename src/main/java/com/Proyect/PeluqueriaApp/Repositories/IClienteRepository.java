package com.Proyect.PeluqueriaApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.Proyect.PeluqueriaApp.Entities.ClienteEntity;

@Repository
public interface IClienteRepository extends JpaRepository<ClienteEntity, Long>{
	// Aquí irán las Querys que vayamos necesitando

    @Modifying
    @Transactional
    @Query("UPDATE ClienteEntity c SET c.deAlta = false WHERE c.clienteId = :clienteId")
    public void eliminarOcultarClienteById(Long clienteId);
}
