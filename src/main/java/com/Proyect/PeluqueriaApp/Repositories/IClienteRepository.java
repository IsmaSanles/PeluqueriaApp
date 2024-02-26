package com.Proyect.PeluqueriaApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.Proyect.PeluqueriaApp.Entities.ClienteEntity;
import java.util.List;

@Repository
public interface IClienteRepository extends JpaRepository<ClienteEntity, Long>{

    //Listar los clientes que estan de alta
    @Query("SELECT c FROM ClienteEntity c WHERE c.deAlta = true")
    List<ClienteEntity> listarClientesDeAlta();

    // Da de baja por el ID
    @Modifying
    @Transactional
    @Query("UPDATE ClienteEntity c SET c.deAlta = false WHERE c.clienteId = :clienteId")
    public void eliminarOcultarClienteById(Long clienteId);
}
