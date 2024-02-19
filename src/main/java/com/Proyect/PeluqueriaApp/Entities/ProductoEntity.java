package com.Proyect.PeluqueriaApp.Entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Productos")
public class ProductoEntity {

	@Id
	@Column(name="productoId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long productoId;
	
	@NotBlank
	@Column(name="nombre", nullable = false)
	private String nombre;
	
	@Column(name="descripcion")
	private String descripcion;
	
	@NotNull
	@Column(name="precio", nullable = false)
	private BigDecimal precio;
	
	@Column(name="stock")
	private int stock;
	
	@Column(name="deAlta")
	private boolean deAlta = true;
	
	@Column(name="fechaCreacion", nullable = false)
	private Date fechaCreacion;
	
	@Column(name="fechaModificacion")
	private Date fechaModificacion;
	
// ------------------------------------------------------- RELACIONES --------------------------------------------------------------	
	/* Un producto estará disponible para multiples Ventas */
	@JsonIgnore
	@OneToMany(mappedBy = "productoId") // La propiedad "productoId" en VentaEntidad
    private List<VentaEntity> listaVentas;

// ------------------------------------------------------- GETTERS/SETTERS --------------------------------------------------------------	
	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
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

	public boolean isDeAlta() {
		return deAlta;
	}

	public void setDeAlta(boolean deAlta) {
		this.deAlta = deAlta;
	}
	
}
