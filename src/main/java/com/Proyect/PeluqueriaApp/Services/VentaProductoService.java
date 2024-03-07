package com.Proyect.PeluqueriaApp.Services;

import com.Proyect.PeluqueriaApp.Entities.ClienteEntity;
import com.Proyect.PeluqueriaApp.Entities.VentaProductoEntity;
import com.Proyect.PeluqueriaApp.Repositories.IVentaProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class VentaProductoService {

	@Autowired
	private IVentaProductoRepository ventaProductoRepository;

	// recuperar por Id
	public Optional<VentaProductoEntity> getVentaProductoById(Long Id){
		return ventaProductoRepository.findById(Id);
	}

	// Eliminar
	public void eliminarVentaProducto(Long id) {
		ventaProductoRepository.deleteById(id);
	}

}
