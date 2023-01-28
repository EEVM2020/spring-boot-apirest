package com.bolsaudeas.springboot.badhend.apirest.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.bolsaudeas.springboot.badhend.apirest.models.entity.Cliente;

public interface IClienteDao extends CrudRepository<Cliente, Long> {

}
