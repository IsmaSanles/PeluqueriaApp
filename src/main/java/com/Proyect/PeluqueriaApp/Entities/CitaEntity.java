package com.Proyect.PeluqueriaApp.Entities;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Citas")
public class CitaEntity {
	
	@Id
	@Column(name="citaId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long citaId;
	
	@Future(message = "La fecha de la cita no puede ser anterior al día actual")
	@Column(name="fechaHoraCita", nullable = false)
	private Date fechaCita;
	
	@Column(name="fechaCreacion", nullable = false)
	private Date fechaCreacion;
	
	
// ------------------------------------------------------- RELACIONES --------------------------------------------------------------	
	
	// Una cita SI puede tener varios servicios a la vez
	// En cita me traigo los servicios creando una relación y a su vez una nueva tabla
	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(
			name = "citasServicios", joinColumns = @JoinColumn(name = "citaId", referencedColumnName = "citaId"),
			inverseJoinColumns = @JoinColumn(name = "servicioId", referencedColumnName = "servicioId")
	)
    private List<ServicioEntity> listaServicios;
	
	/* cada Cita tendrá un solo Estilista */
	@JsonIgnore
	@ManyToOne
	@NotNull(message = "El estilista no puede ser nulo") // utilizamos NotNull para valores numéricos
    @JoinColumn(name = "estilistaId", nullable = false)
	private EstilistaEntity estilistaId;
	
	/* cada Cita tendrá un solo Cliente */
	@JsonIgnore
	@ManyToOne
	@NotNull(message = "El cliente no puede ser nulo") // utilizamos NotNull para valores numéricos
    @JoinColumn(name = "clienteId", nullable = false)
	private ClienteEntity clienteId;


// ------------------------------------------------------- GETTERS/SETTERS --------------------------------------------------------------	
	
	public Long getCitaId() {
		return citaId;
	}

	public void setCitaId(Long citaId) {
		this.citaId = citaId;
	}

	public Date getFechaCita() {
		return fechaCita;
	}

	public void setFechaCita(Date fechaCita) {
		this.fechaCita = fechaCita;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public List<ServicioEntity> getListaServicios() {
		return listaServicios;
	}

	public void setListaServicios(List<ServicioEntity> listaServicios) {
		this.listaServicios = listaServicios;
	}

	public EstilistaEntity getEstilistaId() {
		return estilistaId;
	}

	public void setEstilistaId(EstilistaEntity estilistaId) {
		this.estilistaId = estilistaId;
	}

	public ClienteEntity getClienteId() {
		return clienteId;
	}

	public void setClienteId(ClienteEntity clienteId) {
		this.clienteId = clienteId;
	}
}