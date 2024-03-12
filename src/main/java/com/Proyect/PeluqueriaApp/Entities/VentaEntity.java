package com.Proyect.PeluqueriaApp.Entities;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

class MetodoPago {
	public static final int TARJETA = 1;
	public static final int EFECTIVO = 0;
}


@Entity
@Table(name = "Ventas")
public class VentaEntity {
	
	@Id
	@Column(name="ventaId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ventaId;
	
	@Column(name="fechaVenta", nullable = false)
	private Date fechaVenta;

	@Column(name="metodoPago")
	private int metodoPago;

	// ------------------------------------------------------- RELACIONES --------------------------------------------------------------
	@ManyToOne
	@JoinColumn(name = "clienteId") // Columna en la tabla Venta que referencia al cliente
	private ClienteEntity cliente;

	@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
	private List<VentaProductoEntity> productosVendidos;

// ------------------------------------------------------- GETTERS/SETTERS --------------------------------------------------------------	
	public Long getVentaId() {
		return ventaId;
	}

	public void setVentaId(Long ventaId) {
		this.ventaId = ventaId;
	}

	public Date getFechaVenta() {
		return fechaVenta;
	}

	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}

	public ClienteEntity getCliente() {
		return cliente;
	}

	public void setCliente(ClienteEntity cliente) {
		this.cliente = cliente;
	}

	public List<VentaProductoEntity> getProductosVendidos() {
		return productosVendidos;
	}

	public void setProductosVendidos(List<VentaProductoEntity> productosVendidos) {
		this.productosVendidos = productosVendidos;
	}

	public int getMetodoPago() {
		return metodoPago;
	}

	public void setMetodoPago(int metodoPago) {
		this.metodoPago = metodoPago;
	}
}