package com.bolsaideas.springboot.backend.apirest.models.entity;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
@Table(name="roles")
public class Rol implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="nombre", unique = true,length = 20)
	private String nombreRol;
	


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombreRol() {
		return nombreRol;
	}

	public void setNombreRol(String nombreRol) {
		this.nombreRol = nombreRol;
	}	

}
