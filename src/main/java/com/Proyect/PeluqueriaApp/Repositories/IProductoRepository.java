package com.Proyect.PeluqueriaApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.Proyect.PeluqueriaApp.Entities.ProductoEntity;
import java.util.List;

@Repository
public interface IProductoRepository extends JpaRepository<ProductoEntity, Long>{

    // Listar los productos que estan de alta
    @Query("SELECT p FROM ProductoEntity p WHERE p.deAlta = true")
    List<ProductoEntity> listarProductosDeAlta();

    // dar de baja un producto
    @Modifying
    @Transactional
    @Query("UPDATE ProductoEntity p SET p.deAlta = false WHERE p.productoId = :productoId")
    public void eliminarOcultarProductoById(Long productoId);
}
