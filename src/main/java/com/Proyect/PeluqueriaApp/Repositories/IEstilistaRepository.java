package com.Proyect.PeluqueriaApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.Proyect.PeluqueriaApp.Entities.EstilistaEntity;

@Repository
public interface IEstilistaRepository extends JpaRepository<EstilistaEntity, Long>{
	// Aquí irán las Querys que vayamos necesitando

    @Modifying
    @Transactional
    @Query("UPDATE EstilistaEntity c SET c.deAlta = false WHERE c.estilistaId = :estilistaId")
    public void eliminarOcultarEstilistaById(Long estilistaId);
}
