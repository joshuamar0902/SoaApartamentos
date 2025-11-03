package com.example.apartamentos.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.apartamentos.models.PropiedadesModel;
import com.example.apartamentos.models.TipoPropiedad;
import com.example.apartamentos.repositories.IReservacionesRepository;
import com.example.apartamentos.services.PropiedadService;



@RestController
@RequestMapping("/api/propiedades")
public class PropiedadController {
    
    @Autowired
    private PropiedadService propiedadService;

    @Autowired
    private IReservacionesRepository reservacionesRepository;

    @GetMapping
    public List<PropiedadesModel> getAllPropiedades() {
        return propiedadService.getAllPropiedades();
    }

    //Propiedad por ID
    //... Similar a ClienteController
    @GetMapping("/{id}")
    public ResponseEntity<PropiedadesModel> getPropiedadesById(@PathVariable Long id) {
        Optional<PropiedadesModel> propiedad = propiedadService.getPropiedadById(id);
        return propiedad.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<PropiedadesModel>> getPropiedadesByTipo(@PathVariable TipoPropiedad tipo){
        List<PropiedadesModel> propiedades = propiedadService.getPropiedadesByTipo(tipo);
        if (propiedades.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(propiedades);
        }
    }
    // Crear nueva propiedad
    @PostMapping
    public PropiedadesModel createPropiedad(@RequestBody PropiedadesModel propiedad) {
        return propiedadService.savePropiedad(propiedad);
    }

    // Actualizar propiedad existente
    @PutMapping("/{id}")
    public ResponseEntity<PropiedadesModel> updatePropiedad(@PathVariable Long id, @RequestBody PropiedadesModel propiedadDetails) {
        Optional<PropiedadesModel> propiedadOptional = propiedadService.getPropiedadById(id);
        if (propiedadOptional.isPresent()) {
            PropiedadesModel propiedadToUpdate = propiedadOptional.get();
            propiedadToUpdate.setDireccion(propiedadDetails.getDireccion());
            propiedadToUpdate.setTipo(propiedadDetails.getTipo());
            // Actualizar otros campos según sea necesario

            PropiedadesModel updatedPropiedad = propiedadService.savePropiedad(propiedadToUpdate);
            return ResponseEntity.ok(updatedPropiedad);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar una propiedad (CON VALIDACIÓN)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePropiedad(@PathVariable Long id) {
        
        boolean tieneReservaciones = reservacionesRepository.existsByPropiedadId(id);
        
        if (tieneReservaciones) {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("error", "La propiedad tiene una reservación activa y no puede ser eliminada.");
            
            return new ResponseEntity<>(error, HttpStatus.CONFLICT); // HTTP 409
        }
        Optional<PropiedadesModel> propiedad = propiedadService.getPropiedadById(id);
        if (propiedad.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        propiedadService.deletePropiedad(id); 
        
        return ResponseEntity.noContent().build();
    }

}
    
    

    

