package com.bolsaudeas.springboot.badhend.apirest.models.services;
import java.util.List;

import com.bolsaudeas.springboot.badhend.apirest.models.entity.Cliente;
public interface IClienteService {
	
	public List<Cliente> findAll();

}
