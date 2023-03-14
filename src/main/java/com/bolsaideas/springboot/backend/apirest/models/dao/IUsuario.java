package com.bolsaideas.springboot.backend.apirest.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bolsaideas.springboot.backend.apirest.models.entity.Usuario;

public interface IUsuario extends CrudRepository<Usuario, Long>{
	
	public Usuario findByUsuario(String usuario);
	
	@Query("select u from Usuario u where u.usuario=?1")
	public Usuario buscarPorUsuario(String usuario);

}
