package com.Proyect.PeluqueriaApp.Repositories;

import com.Proyect.PeluqueriaApp.Entities.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IVentaRepository extends JpaRepository<VentaEntity, Long>{

    /*
    @Query("SELECT v FROM Venta v JOIN FETCH v.cliente c JOIN FETCH v.productosVendidos vp JOIN FETCH vp.producto WHERE v.id = :ventaId")
    VentaEntity findVentaWithClienteAndProductosById(Long ventaId);
    */

    // Consulta para obtener todas las ventas con los datos del cliente y los productos
    @Query("SELECT DISTINCT v FROM VentaEntity v JOIN FETCH v.cliente c JOIN FETCH v.productosVendidos pv JOIN FETCH pv.producto")
    List<VentaEntity> getAllVentasConDetalles();

}
