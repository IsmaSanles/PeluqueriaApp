package com.Proyect.PeluqueriaApp.Repositories;

import com.Proyect.PeluqueriaApp.Entities.VentaProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IVentaProductoRepository extends JpaRepository<VentaProductoEntity, Long>{


}
