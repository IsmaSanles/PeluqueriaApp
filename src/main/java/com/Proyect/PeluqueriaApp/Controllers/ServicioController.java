package com.Proyect.PeluqueriaApp.Controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.Proyect.PeluqueriaApp.Entities.ServicioEntity;
import com.Proyect.PeluqueriaApp.Services.ServicioService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:8001") // para que no dea problemas de CORS policy
@RestController
@RequestMapping("/servicio")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public ResponseEntity<?> listarServicios() {

        List<ServicioEntity> listadoServicios = this.servicioService.listarServicios();

        if (listadoServicios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(listadoServicios);
        }
    }

    @GetMapping("/{id}")
    public ServicioEntity obtenerServicioPorId(@PathVariable Long id) {
        Optional<ServicioEntity> servicio = this.servicioService.servicioById(id);

        if (servicio.isPresent()) {
            return servicio.get();
        } else {
            return null;
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearServicio(@Valid @RequestBody ServicioEntity servicio, BindingResult result) {
        if (result.hasErrors()) {
            // Manejar los errores de validación y devolverlos como parte de la respuesta HTTP
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
        }
        servicio.setFechaCreacion(new Date());

        // Si no hay errores de validación, crear el estilista
        ServicioEntity nuevoServicio = servicioService.crearServicio(servicio);
        return ResponseEntity.status(HttpStatus.OK).body(nuevoServicio);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarServicio( @PathVariable Long id, @Valid @RequestBody ServicioEntity nuevoServicio, BindingResult result) {

        if (result.hasErrors()) {
            // Manejar los errores de validación y devolverlos como parte de la respuesta HTTP
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getAllErrors());
        }
        // Busco el servicio por el Id
        ServicioEntity servicioRecuperado = obtenerServicioPorId(id);

        if (servicioRecuperado != null) {
            // añadimos los campos que no queremos que cambien
            nuevoServicio.setServicioId(servicioRecuperado.getServicioId());
            nuevoServicio.setFechaModificacion(new Date());
            nuevoServicio.setFechaCreacion(servicioRecuperado.getFechaCreacion());

            // Guardo el servicio actualizado en el repositorio
            servicioRecuperado = this.servicioService.modificarServicio(nuevoServicio);

            return ResponseEntity.status(HttpStatus.OK).body(servicioRecuperado);

        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long id) {
        //Buscamos si existe ese usuario
        ServicioEntity servicioRecuperado = obtenerServicioPorId(id);

        if(servicioRecuperado != null && servicioRecuperado.isDeAlta() == true) {
            this.servicioService.eliminarOcultarServicioById(id);
            return ResponseEntity.status(HttpStatus.OK).body(servicioRecuperado);
        }else if(servicioRecuperado != null && servicioRecuperado.isDeAlta() == false) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(servicioRecuperado);
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}