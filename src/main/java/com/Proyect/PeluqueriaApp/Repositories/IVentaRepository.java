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
    //@Query("SELECT DISTINCT v FROM VentaEntity v JOIN FETCH v.cliente c JOIN FETCH v.productosVendidos pv JOIN FETCH pv.producto")
    //@Query("SELECT vp.id AS venta_producto_id, vp.fechaCreacion AS fecha_creacion, vp.fechaModificacion AS fecha_modificacion, vp.cantidad AS uds_vendidas, vp.producto.id AS producto_id, vp.producto.nombre AS producto_nombre, vp.producto.precio AS producto_precio, vp.venta.id AS venta_id FROM VentaProducto vp")
    @Query("SELECT DISTINCT v FROM VentaEntity v JOIN FETCH v.cliente c JOIN FETCH v.productosVendidos pv JOIN FETCH pv.producto p")
    List<VentaEntity> getAllVentasConDetalles();

    //@Query("SELECT vp.id AS venta_producto_id, vp.fechaCreacion AS fecha_creacion, vp.fechaModificacion AS fecha_modificacion, vp.cantidad AS uds_vendidas, vp.producto.id AS producto_id, vp.producto.nombre AS producto_nombre, vp.producto.precio AS producto_precio, vp.venta.id AS venta_id FROM VentaProducto vp")

}
