package com.bolsaideas.springboot.backend.apirest.models.services;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.bolsaideas.springboot.backend.apirest.models.dao.IUsuario;
import com.bolsaideas.springboot.backend.apirest.models.entity.Usuario;


public class UsuarioService implements UserDetailsService {

	@Autowired
	private IUsuario usuarioDao;
	
	private Logger log=LoggerFactory.getLogger(UsuarioService.class);
	
	@Override
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Usuario usuario=usuarioDao.findByUsuario(username);
		if(usuario==null) {
			throw new UsernameNotFoundException("Usuario no encontrado");
		}
		List<GrantedAuthority> permisos=usuario.getRoles().stream().map(
				rol-> new SimpleGrantedAuthority(rol.getNombreRol())
				).peek(autorizacion->log.info(autorizacion.getAuthority()))				
				.collect(Collectors.toList());
		
		return new User(usuario.getUsuario(),usuario.getClave(),usuario.isHabilitado(),true,true,true,permisos);
	}

}
