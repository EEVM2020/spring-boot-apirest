package com.bolsaideas.springboot.backend.apirest.models.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
//import jakarta.


@Entity
@Table(name="clientes")
public class Cliente implements Serializable {

	private static final long serialVersionUID = -5064666335035025563L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
   @NotEmpty(message = "no dejar asi!!")
   @Size(min = 2, max=250)
	private String nombre;
	private String apellido;
	
	@NotEmpty
	@Email
	@Column(nullable = false)
	private String correo;
	
	@Column(name="creacion")
	@Temporal(TemporalType.DATE)
	//@NotEmpty(message = "No puede estar vacio")
	private Date creacion;
	
	private String foto;
	
	@NotNull(message = "La region no puede estar vacia.")
	@ManyToOne
	@JoinColumn(name="region_id")
	@JsonIgnoreProperties({"hibernateLazyInicializer","handler"})
	private Region region;
	
	/*
	@PrePersist
	private void prePersist() {
		this.setCreacion(new Date());
	}
*/	
	
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public Date getCreacion() {
		return creacion;
	}
	public void setCreacion(Date creacion) {
		this.creacion = creacion;
	}
	

	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	@Override
	public String toString() {
		return "Cliente [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", correo=" + correo
				+ ", creacion=" + creacion + "]";
	} 
	
	
}
