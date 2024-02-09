package com.Proyect.PeluqueriaApp.Entities;

import java.util.Date;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Estilistas")
public class EstilistaEntity {
	
	@Id
	@Column(name="estilistaId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long estilistaId;
	
	@NotBlank
	@Size(min=9, max=9, message="el tamaño debe ser exactamente de 9 caracteres")
	@Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe tener 8 dígitos numéricos y terminar con una letra")
	@Column(name="dni", nullable = false)
	private String dni;
	
	@NotBlank
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre debe contener solo letras")
	@Column(name="nombre", nullable = false)
	private String nombre;
	
	@NotBlank
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El 1º apellido debe contener solo letras")
	@Column(name="apellido1", nullable = false)
	private String apellido1;
	
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*$", message = "El 2º apellido debe contener solo letras")
	@Column(name="apellido2")
	private String apellido2;
	
	@NotBlank
	@Email
	@Column(unique = true, name="email", nullable = false)
	private String email;

	@Past
	@NotNull
	@Column(name="fechaNacimiento", nullable = false)
	private Date fechaNacimiento;
	
	@Column(name="telefono")
	private String telefono;
	
	@Column(name="deAlta")
	private boolean deAlta = true;
	
	@Column(name="fechaCreacion", nullable = false)
	private Date fechaCreacion;
	
	@Column(name="fechaModificacion")
	private Date fechaModificacion;
	
// ------------------------------------------------------- RELACIONES --------------------------------------------------------------	
	
	/* Un Estilista estará disponible para multiples Citas */
	@OneToMany(mappedBy = "estilistaId") // La propiedad "estilistaId" en CitaEntidad
	@JsonIgnore
    private List<CitaEntity> listaCitas;

	
// ------------------------------------------------------- GETTERS/SETTERS --------------------------------------------------------------
	public Long getEstilistaId() {
		return estilistaId;
	}

	public void setEstilistaId(Long estilistaId) {
		this.estilistaId = estilistaId;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido1() {
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public boolean isDeAlta() {
		return deAlta;
	}

	public void setDeAlta(boolean deAlta) {
		this.deAlta = deAlta;
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

	public List<CitaEntity> getListaCitas() {
		return listaCitas;
	}

	public void setListaCitas(List<CitaEntity> listaCitas) {
		this.listaCitas = listaCitas;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}
	
}
