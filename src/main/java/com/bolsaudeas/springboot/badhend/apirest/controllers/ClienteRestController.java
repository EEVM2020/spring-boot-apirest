package com.bolsaudeas.springboot.badhend.apirest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.bolsaudeas.springboot.badhend.apirest.models.entity.Cliente;
import com.bolsaudeas.springboot.badhend.apirest.models.services.IClienteService;


@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	@Autowired
	private IClienteService clienteService;

	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();
	}

	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> mostrar(@PathVariable Long id) {
		Cliente cliente=null;
		Map <String,Object> response=new HashMap<>();
		try {
			cliente=clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("Mensaje", "Error al consultar");
			response.put("mensaje", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(cliente==null) {
			response.put("mensaje", "El cliente no fue encontrado");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);

		}
		System.out.println(cliente);
		return new ResponseEntity<Cliente>(cliente,HttpStatus.OK);

	} 

	@PostMapping("/clientes")
	private ResponseEntity<?> guardar(@RequestBody Cliente cliente) {
		System.out.println("crear!! xxxx");
		Cliente nuevoCliente=null;
		Map<String,Object> response=new HashMap<>();
		try {
			nuevoCliente=clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el insert");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente se ha creado");
		response.put("cliente", nuevoCliente);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	}

	@PutMapping("/clientes/{id}")

	public ResponseEntity<?> actualizar(@RequestBody Cliente cliente, @PathVariable Long id) {
		System.out.println("actualizar!! xxxx");

		Cliente clienteActual=clienteService.findById(id);
		Cliente clienteActualizado=null;
		Map<String,Object> response=new HashMap<>();

		if(clienteActual ==null) {
			response.put("mensaje", "No existe el cliente");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}

		try {
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setCorreo(cliente.getCorreo());
			clienteActual.setNombre(cliente.getNombre());

			clienteActualizado=clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar el update");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente se ha actualizado");
		response.put("cliente", clienteActualizado);
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
	} 

	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Map<String,Object> response=new HashMap<>();
		Cliente clienteBorrar=null;
		try {
			 clienteBorrar=clienteService.findById(id);
			clienteService.delete(id);

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el cliente");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("cliente", clienteBorrar);
		response.put("mensaje", "Cliente eliminado con exito");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.OK);

	}

}
