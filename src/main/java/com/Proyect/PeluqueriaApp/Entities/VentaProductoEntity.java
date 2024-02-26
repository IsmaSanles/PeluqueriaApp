package com.Proyect.PeluqueriaApp.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Date;

@Entity
@Table(name = "VentaProducto")
public class VentaProductoEntity {

	@Id
	@Column(name="ventaProductoId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ventaProductoId;

	@NotNull
	@Min(value = 1, message = "Indica la cantidad")
	@Column(name="udsVendidas")
	private int udsVendidas;
	
	@Column(name="fechaCreacion", nullable = false)
	private Date fechaCreacion;
	
	@Column(name="fechaModificacion")
	private Date fechaModificacion;
	
// ------------------------------------------------------- RELACIONES --------------------------------------------------------------	

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "venta_id")
	private VentaEntity venta;

	// NO AÑADIR JsonIgnore para que liste los datos del Producto
	@ManyToOne
	@JoinColumn(name = "producto_id")
	private ProductoEntity producto;

// ------------------------------------------------------- GETTERS/SETTERS --------------------------------------------------------------	

	public Long getVentaProductoId() {
		return ventaProductoId;
	}

	public void setVentaProductoId(Long ventaProductoId) {
		this.ventaProductoId = ventaProductoId;
	}

	public int getUdsVendidas() {
		return udsVendidas;
	}

	public void setUdsVendidas(int udsVendidas) {
		this.udsVendidas = udsVendidas;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public VentaEntity getVenta() {
		return venta;
	}

	public void setVenta(VentaEntity venta) {
		this.venta = venta;
	}

	public ProductoEntity getProducto() {
		return producto;
	}

	public void setProducto(ProductoEntity producto) {
		this.producto = producto;
	}
}
