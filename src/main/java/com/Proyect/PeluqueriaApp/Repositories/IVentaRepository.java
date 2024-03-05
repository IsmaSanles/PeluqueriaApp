package com.Proyect.PeluqueriaApp.Repositories;

import com.Proyect.PeluqueriaApp.Entities.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IVentaRepository extends JpaRepository<VentaEntity, Long>{

    // Consulta para obtener todas las ventas con los datos del cliente y los productos
    @Query("SELECT DISTINCT v FROM VentaEntity v JOIN FETCH v.cliente c JOIN FETCH v.productosVendidos pv JOIN FETCH pv.producto p")
    List<VentaEntity> getAllVentasConDetalles();

    // recuperamos la venta por su Id con todos los datos de la misma
    @Query("SELECT DISTINCT v FROM VentaEntity v JOIN FETCH v.cliente c JOIN FETCH v.productosVendidos pv JOIN FETCH pv.producto p WHERE v.ventaId = :ventaId")
    VentaEntity getVentaConDetallesPorId(@Param("ventaId") Long ventaId);

}
