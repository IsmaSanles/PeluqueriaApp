package com.Proyect.PeluqueriaApp.Entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.NonNull;

@Entity
@Table(name = "Servicios")
public class ServicioEntity {
	
	@Id
	@Column(name="servicioId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long servicioId;
	
	@NotBlank
	@Column(name="nombre", nullable = false)
	private String nombre;
	
	@NotNull
	@Column(name="precio", nullable = false)
	private BigDecimal precio;
	
	@NotNull
  @Positive
	@Column(name="duracion", nullable = false)
	private Integer duracion;
	
	@Column(name="descripcion")
	private String descripcion;

	@Column(name="deAlta")
	private boolean deAlta = true;
	
	@Column(name="fechaCreacion", nullable = false)
	private Date fechaCreacion;


	@Column(name="fechaModificacion")
	private Date fechaModificacion;

	
// ------------------------------------------------------- RELACIONES --------------------------------------------------------------	
	// Una cita SI puede tener varios servicios a la vez
	// Hacemos referencia al atributo 'listaServicios' de citaEntity para indicar la relación
	@JsonIgnore
	@ManyToMany(mappedBy = "listaServicios")
    private List<CitaEntity> listaCitas;

// ------------------------------------------------------- GETTERS/SETTERS --------------------------------------------------------------	
	public Long getServicioId() {
		return servicioId;
	}

	public void setServicioId(Long servicioId) {
		this.servicioId = servicioId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public BigDecimal getPrecio() {
		return precio;
	}

	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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

	public List<CitaEntity> getListaCitas() {
		return listaCitas;
	}

	public void setListaCitas(List<CitaEntity> listaCitas) {
		this.listaCitas = listaCitas;
	}
}