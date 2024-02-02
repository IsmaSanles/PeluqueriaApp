package com.Proyect.PeluqueriaApp.Entities;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Clientes")
public class ClienteEntity {

	@Id
	@Column(name="clienteId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long clienteId;
	
	@NotBlank(message = "El DNI es un campo obligatorio")
	@Size(min=9, max=9)
	@Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El DNI debe tener 8 dígitos numéricos y terminar con una letra")
	@Column(name="dni", nullable = false)
	private String dni;
	
	@NotBlank(message = "El Nombre es un campo obligatorio")
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El nombre debe contener solo letras")
	@Column(name="nombre", nullable = false)
	private String nombre;
	
	@NotBlank(message = "El primer apellido es un campo obligatorio")
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$", message = "El 1º apellido debe contener solo letras")
	@Column(name="apellido1", nullable = false)
	private String apellido1;
	
	@Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*$", message = "El 2º apellido debe contener solo letras")
	@Column(name="apellido2")
	private String apellido2;
	
	@Column(name="fechaNacimiento", nullable = false)
	private Date fechaNacimiento;
	
	@NotBlank(message = "El grupo de edad es un campo obligatorio")
	@Column(name="grupoEdad", nullable = false)
	private String grupoEdad;
	
	@NotBlank(message = "El Email es un campo obligatorio")
	@Email
	@Column(unique = true, name="email", nullable = false)
	private String email;
	
	@Column(name="telefono")
	private String telefono;
	
	@Column(name="deAlta")
	private boolean deAlta = true;
	
	@Column(name="fechaCreacion", nullable = false)
	private Date fechaCreacion;
	
	@Column(name="fechaModificacion")
	private Date fechaModificacion;
	
// ------------------------------------------------------- RELACIONES --------------------------------------------------------------	
	/* Un cliente estará disponible para multiples Citas */
	@JsonIgnore
	@OneToMany(mappedBy = "clienteId") // La propiedad "clienteId" en CitaEntidad
    private List<CitaEntity> listaCitas;

	
// ------------------------------------------------------- GETTERS/SETTERS --------------------------------------------------------------	
	public Long getClienteId() {
		return clienteId;
	}

	public void setClienteId(Long clienteId) {
		this.clienteId = clienteId;
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

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getGrupoEdad() {
		return grupoEdad;
	}

	public void setGrupoEdad(String grupoEdad) {
		this.grupoEdad = grupoEdad;
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
}