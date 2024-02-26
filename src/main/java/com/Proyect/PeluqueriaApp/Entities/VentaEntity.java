package com.Proyect.PeluqueriaApp.Entities;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Ventas")
public class VentaEntity {
	
	@Id
	@Column(name="ventaId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ventaId;
	
	@Column(name="fechaVenta", nullable = false)
	private Date fechaVenta;

	
// ------------------------------------------------------- RELACIONES --------------------------------------------------------------		
	/* cada Venta tendrá un solo Cliente */
	/*@ManyToOne
	@NotNull// utilizamos NotNull para valores numéricos
	@JsonIgnoreProperties({"ventas"}) // Evitar bucle infinito al serializar ClienteEntity
    @JoinColumn(name = "clienteId", nullable = false)
	private ClienteEntity cliente;

	@ManyToMany
	@JsonIgnoreProperties({"listaVentas"}) // Evitar bucle infinito al serializar ProductoEntity
	@JoinTable(name = "Venta_Producto",
			joinColumns = @JoinColumn(name = "ventaId"),
			inverseJoinColumns = @JoinColumn(name = "productoId"))
	private List<ProductoEntity> listaProductos;
	*/


	@ManyToOne
	@JoinColumn(name = "cliente_id") // Columna en la tabla Venta que referencia al cliente
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
}