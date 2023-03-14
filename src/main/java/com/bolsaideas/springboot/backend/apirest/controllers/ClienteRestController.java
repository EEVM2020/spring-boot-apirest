package com.bolsaideas.springboot.backend.apirest.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.bolsaideas.springboot.backend.apirest.models.entity.Cliente;
import com.bolsaideas.springboot.backend.apirest.models.entity.Region;
import com.bolsaideas.springboot.backend.apirest.models.services.IClienteService;

import jakarta.validation.Valid;


@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/api")
public class ClienteRestController {
	
	private final Logger log=LoggerFactory.getLogger(ClienteRestController.class);
	
	@Autowired
	private IClienteService clienteService;

	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();
	}

	@GetMapping("/clientes/paginar/{pagina}")
	public Page<Cliente> index(@PathVariable Integer pagina){
		return clienteService.findAll(PageRequest.of(pagina, 4));
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
	private ResponseEntity<?> guardar(@Valid @RequestBody Cliente cliente, BindingResult validaciones) {
		System.out.println("crear!! xxxx");
		Cliente nuevoCliente=null;
		Map<String,Object> response=new HashMap<>();
		
		if(validaciones.hasErrors()) {
			List<String>errores=validaciones.getFieldErrors().stream().map(es->"El campo '"+es.getField()+"' "+es.getDefaultMessage()).
					collect(Collectors.toList());
			response.put("errores", errores);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);		
		}
		
		try {
			cliente.setFoto("noFoto.png");
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

	public ResponseEntity<?> actualizar(@Valid @RequestBody Cliente cliente,BindingResult validaciones ,@PathVariable Long id) {
		System.out.println("actualizar!! xxxx");

		Cliente clienteActual=clienteService.findById(id);
		Cliente clienteActualizado=null;
		Map<String,Object> response=new HashMap<>();
		
		if(validaciones.hasErrors()) {
			List<String>errores=validaciones.getFieldErrors().stream().map(es->"El campo '"+es.getField()+"' "+es.getDefaultMessage()).
					collect(Collectors.toList());
			response.put("errores", errores);
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.BAD_REQUEST);		
		}

		if(clienteActual ==null) {
			response.put("mensaje", "No existe el cliente");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NOT_FOUND);
		}

		try {
			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setCorreo(cliente.getCorreo());
			clienteActual.setNombre(cliente.getNombre());
			clienteActual.setCreacion(cliente.getCreacion());
			clienteActual.setRegion(cliente.getRegion());
			
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
				if(clienteBorrar.getFoto()!=null && !clienteBorrar.getFoto().isEmpty()) {
					File archivoAnterior=(Paths.get("cargues").resolve(clienteBorrar.getFoto()).toAbsolutePath()).toFile();
					if(archivoAnterior.exists()) {
						archivoAnterior.delete();
					}
				}
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
	@PostMapping("/clientes/cargue")
	public ResponseEntity<?> cargue(@RequestParam("archivo") MultipartFile archivo, @RequestParam("id") Long id){
		System.out.println("entro a cargue archivo");
		Map<String,Object> response=new HashMap<>();
		Cliente cliente=clienteService.findById(id);
		if(!archivo.isEmpty()) {
			String nombreArchivo=UUID.randomUUID().toString()+"_"+archivo.getOriginalFilename();
			Path rutaArchivo=Paths.get("cargues").resolve(nombreArchivo).toAbsolutePath();
			
			log.warn(rutaArchivo.toString());
			
			try {
				Files.copy(archivo.getInputStream(), rutaArchivo);	
				
			} catch (Exception e) {
				response.put("mensaje", "Error al subir archivo");
				response.put("error", e.getMessage());
				e.printStackTrace();
				return new ResponseEntity<Map<String,Object>>(response,HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			if(cliente.getFoto()!=null && !cliente.getFoto().isEmpty()) {
				Path rutaAnt=Paths.get("cargues").resolve(cliente.getFoto()).toAbsolutePath();
				File archivoAnterior=rutaAnt.toFile();
				
				if(archivoAnterior.exists()) {
					archivoAnterior.delete();
				}
			}
			
			cliente.setFoto(nombreArchivo);
			clienteService.save(cliente);
		}else {
			response.put("mensaje", "No hay archivo");
			return new ResponseEntity<Map<String,Object>>(response,HttpStatus.NO_CONTENT);
		}
		
		response.put("cliente", cliente);
		response.put("mensaje", "Archivo cargado correstamente");
		return new ResponseEntity<Map<String,Object>>(response,HttpStatus.CREATED);
		
	}
	
	@GetMapping("clientes/cargues/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String nombreFoto){
		Path rutaArchivo=Paths.get("cargues").resolve(nombreFoto).toAbsolutePath();
		Resource recurso=null;
		
		try {
			recurso=new UrlResource(rutaArchivo.toUri());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(!recurso.exists() && !recurso.isReadable()) {
			rutaArchivo=Paths.get("src/main/resourses/static/imagenes").resolve("noFoto").toAbsolutePath();
			try {
				recurso=new UrlResource(rutaArchivo.toUri());
			} catch (Exception e) {
				e.printStackTrace();
			}
			//throw new RuntimeException("no se pudo crear el archivo");
		}
		
		HttpHeaders cabecera=new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+recurso.getFilename()+"\"");
		return new ResponseEntity<Resource>(recurso,cabecera,HttpStatus.OK);
 
		
	}

	@GetMapping("/clientes/regiones")
	public List<Region> listarRegiones(){
		return clienteService.buscarTodasRegiones();
	}

	
	public static void main(String[] args) {
		Path rutaArchivo=Paths.get("imagenes").resolve("noFoto.png").toAbsolutePath();
		System.out.println();
	}
	
}
