package com.Proyect.PeluqueriaApp.Repositories;

import com.Proyect.PeluqueriaApp.Entities.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface IVentaRepository extends JpaRepository<VentaEntity, Long>{
	// Aquí irán las Querys que vayamos necesitando

    @Query("SELECT v " +
           "FROM VentaEntity v " +
           "JOIN FETCH v.clienteId c " +
           "JOIN FETCH v.productoId p")
    List<VentaEntity> getAllVentasConDetalles();

}
