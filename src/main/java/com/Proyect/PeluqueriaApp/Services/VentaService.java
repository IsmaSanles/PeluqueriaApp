package com.Proyect.PeluqueriaApp.Services;

import com.Proyect.PeluqueriaApp.Entities.VentaEntity;
import com.Proyect.PeluqueriaApp.Repositories.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

	@Autowired
	private IVentaRepository ventaRepository;

	// Obtener todas las ventas con sus detalles
	public List<VentaEntity> getAllVentasConDetalles() {
		return ventaRepository.getAllVentasConDetalles();
	}

	public Optional<VentaEntity> getVentaById(Long Id){
		return Optional.ofNullable(ventaRepository.getById(Id));
	}

	// recuperar una Venta por Id con sus detalles
	public Optional<VentaEntity> getVentaConDetallesPorId(Long Id){
		return Optional.ofNullable(ventaRepository.getVentaConDetallesPorId(Id));
	}
	
	// Crear Venta
	public VentaEntity crearVenta(VentaEntity venta) {
		return ventaRepository.save(venta);
	}
	
	// Modificar Venta
	public VentaEntity modificarVenta(VentaEntity venta) {
		return ventaRepository.save(venta);
	}

	// Eliminar Venta
	public void eliminarVenta(Long id) {
		ventaRepository.deleteById(id);
	}

}
