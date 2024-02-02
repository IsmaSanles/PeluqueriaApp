package com.Proyect.PeluqueriaApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Proyect.PeluqueriaApp.Entities.ProductoEntity;

@Repository
public interface IProductoRepository extends JpaRepository<ProductoEntity, Long>{
	// Aquí irán las Querys que vayamos necesitando

    @Modifying
    @Transactional
    @Query("UPDATE ProductoEntity c SET c.deAlta = false WHERE c.productoId = :productoId")
    public void eliminarOcultarProductoById(Long productoId);
}
