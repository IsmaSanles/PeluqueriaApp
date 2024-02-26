package com.Proyect.PeluqueriaApp.Services;

import java.util.List;
import java.util.Optional;

import com.Proyect.PeluqueriaApp.Entities.ClienteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Proyect.PeluqueriaApp.Entities.ProductoEntity;
import com.Proyect.PeluqueriaApp.Repositories.IProductoRepository;

@Service
public class ProductoService {

	@Autowired
	private IProductoRepository productoRepository;
	
	// Listar Todos
	public List<ProductoEntity> listarProductos(){
		return (List<ProductoEntity>) productoRepository.findAll();
	}

	// Listar Todos lo que están de alta
	public List<ProductoEntity> listarProductosDeAlta(){
		return (List<ProductoEntity>) productoRepository.listarProductosDeAlta();
	}
	
	// recuperar un producto por Id
	public Optional<ProductoEntity> productoById(Long Id){
		return productoRepository.findById(Id);
	}
	
	// Crear Producto
	public ProductoEntity crearProducto(ProductoEntity producto) {
		return productoRepository.save(producto);
	}
	
	// Modificar Producto
	public ProductoEntity modificarProducto(ProductoEntity producto) {
		return productoRepository.save(producto);
	}
		
	
	
	// Eliminar/Ocultar Producto
	public void eliminarOcultarProductoById(Long id) {
		productoRepository.eliminarOcultarProductoById(id);
	}
}