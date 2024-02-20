package com.Proyect.PeluqueriaApp.Repositories;

import com.Proyect.PeluqueriaApp.Entities.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IVentaRepository extends JpaRepository<VentaEntity, Long>{
	// Aquí irán las Querys que vayamos necesitando

    /*@Query("SELECT v " +
           "FROM VentaEntity v " +
           "JOIN FETCH v.clienteId c " +
           "JOIN FETCH v.productoId p")
    List<VentaEntity> getAllVentasConDetalles();
    */

    // recuperar las ventas con sus datos
    @Query("SELECT DISTINCT v " +
            "FROM VentaEntity v " +
            "JOIN FETCH v.clienteId c " +
            "JOIN FETCH v.listaProductos p")
    List<VentaEntity> getAllVentasConDetalles();

    // recuperar una venta de un cliente en concreto
    @Query("SELECT DISTINCT v " +
            "FROM VentaEntity v " +
            "JOIN FETCH v.clienteId c " +
            "JOIN FETCH v.listaProductos p " +
            "WHERE c.clienteId = :clienteId " +
            "AND v.ventaId = :ventaId")
    VentaEntity getVentaConProductosPorCliente(@Param("clienteId") Long clienteId, @Param("ventaId") Long ventaId);

}
