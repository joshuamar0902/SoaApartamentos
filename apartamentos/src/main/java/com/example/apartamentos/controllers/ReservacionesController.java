package com.example.apartamentos.controllers;

import java.time.LocalDate;
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

import com.example.apartamentos.models.ReservacionesModel;
import com.example.apartamentos.models.TipoPropiedad;
import com.example.apartamentos.repositories.IMensajesRepository;
import com.example.apartamentos.repositories.IPagosRepository;
import com.example.apartamentos.repositories.IReservacionesRepository;
import com.example.apartamentos.repositories.IReseñasRepository;
import com.example.apartamentos.services.ReservacionesService;

@RestController
@RequestMapping("/api/reservaciones")
public class ReservacionesController {

    @Autowired
    private ReservacionesService reservacionesService;
    @Autowired
    private IReservacionesRepository reservacionesRepository;
    @Autowired
    private IMensajesRepository mensajesRepository;
    @Autowired
    private IReseñasRepository reseñasRepository;
    @Autowired
    private IPagosRepository pagosRepository;


    // Obtener todas las reservaciones
    @GetMapping
    public List<ReservacionesModel> getAllReservaciones() {
        return reservacionesService.getAllReservaciones();
    }

    // Obtener una reservación por ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservacionesModel> getReservacionById(@PathVariable Long id) {
        Optional<ReservacionesModel> reservacion = reservacionesService.getReservacionById(id);
        return reservacion.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Crear un nuevo mensaje
    @PostMapping
    public ReservacionesModel createReservacion(@RequestBody ReservacionesModel reservacion) {
        return reservacionesService.saveReservacion(reservacion);
    }

    // Actualizar una reservación existente
    @PutMapping("/{id}")
    public ResponseEntity<ReservacionesModel> updateReservacion(@PathVariable Long id, @RequestBody ReservacionesModel reservacionDetails) {
        Optional<ReservacionesModel> reservacionOptional = reservacionesService.getReservacionById(id);
        if (reservacionOptional.isPresent()) {
            ReservacionesModel reservacionToUpdate = reservacionOptional.get();
            reservacionToUpdate.setPago(reservacionDetails.getPago());
            reservacionToUpdate.setResena(reservacionDetails.getResena());
            reservacionToUpdate.setPropiedad(reservacionDetails.getPropiedad());
            reservacionToUpdate.setCliente(reservacionDetails.getCliente());
            reservacionToUpdate.setFechaEntrada(reservacionDetails.getFechaEntrada());
            reservacionToUpdate.setFechaSalida(reservacionDetails.getFechaSalida());
            reservacionToUpdate.setNumeroHuespedes(reservacionDetails.getNumeroHuespedes());
            reservacionToUpdate.setPrecioTotal(reservacionDetails.getPrecioTotal());
            reservacionToUpdate.setEstado(reservacionDetails.getEstado());
            reservacionToUpdate.setFechaReservacion(reservacionDetails.getFechaReservacion());
            reservacionToUpdate.setNotas(reservacionDetails.getNotas());
            reservacionToUpdate.setCodigoReserva(reservacionDetails.getCodigoReserva());
            reservacionToUpdate.setFechaCheckin(reservacionDetails.getFechaCheckin());
            reservacionToUpdate.setFechaCheckout(reservacionDetails.getFechaCheckout());
            
            // Actualizar otros campos según sea necesario

            ReservacionesModel updatedReservacion = reservacionesService.saveReservacion(reservacionToUpdate);
            return ResponseEntity.ok(updatedReservacion);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar una reservación (CON VALIDACIÓN MÚLTIPLE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservaciones(@PathVariable Long id) {

        boolean tieneReseñas = reseñasRepository.existsByReservacionId(id);
        boolean tienePagos = pagosRepository.existsByReservacionId(id);
        boolean tieneMensajes = mensajesRepository.existsByReservacionId(id);

        java.util.List<String> errores = new java.util.ArrayList<>();

        
        if (tieneReseñas) {
            errores.add("La reservación tiene una reseña asociada y no puede ser eliminada.");
        }

        
        if (tienePagos) {
            errores.add("La reservación tiene pagos asociados y no puede ser eliminada.");
        }

        if (tieneMensajes) {
            errores.add("La reservación tiene mensajes asociados y no puede ser eliminada.");
        }

        if (!errores.isEmpty()) {

            java.util.Map<String, Object> respuestaError = new java.util.HashMap<>();
            respuestaError.put("error", "Conflicto al eliminar la reservación");
            respuestaError.put("mensajes", errores); // Aquí va la lista completa de errores

            return new ResponseEntity<>(respuestaError, HttpStatus.CONFLICT); // HTTP 409
        }

        Optional<ReservacionesModel> reservacion = reservacionesService.getReservacionById(id);
        if (reservacion.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        reservacionesService.deleteReservacion(id); 
        
        return ResponseEntity.noContent().build();
    }


    

    //Método por propiedad
    @GetMapping("/tipo/{tipo}")
    public List<ReservacionesModel> getReservacionByApartamento(@PathVariable TipoPropiedad tipo) {
        return reservacionesService.getReservacionByApartamento(tipo);
    }
    //Método para obtener reservacion por fecha
    @GetMapping("fecha/{fechaEntrada}")
    public List<ReservacionesModel> getReservacionByFecha(@PathVariable LocalDate fechaEntrada){
        return reservacionesService.getReservacionByFecha(fechaEntrada);

    }


    @PostMapping("/reservar/validar")
    public ResponseEntity<?> reservarApartamento(@RequestBody ReservacionesModel reservacion) {
        

        if (reservacion.getFechaEntrada() == null || 
            reservacion.getFechaSalida() == null ||
            reservacion.getPropiedad() == null || reservacion.getPropiedad().getId() == null ||
            reservacion.getCliente() == null || reservacion.getCliente().getId() == null) {
            

            return ResponseEntity.badRequest().body("Error: Faltan datos (fechas, propiedad o cliente).");
        }
        if (reservacion.getFechaEntrada().isAfter(reservacion.getFechaSalida())) {
             return ResponseEntity.badRequest().body("Error: La fecha de salida no puede ser anterior a la de entrada.");
        }

        List<ReservacionesModel> solapadas = reservacionesRepository.findReservacionesSolapadas(
                reservacion.getPropiedad().getId(),
                reservacion.getFechaEntrada(),
                reservacion.getFechaSalida()
        );

        if (!solapadas.isEmpty()) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                                 .body("Conflicto: El apartamento no está disponible en esas fechas.");
        }

        ReservacionesModel reservacionGuardada = reservacionesService.saveReservacion(reservacion);

        return new ResponseEntity<>(reservacionGuardada, HttpStatus.CREATED);
    }
}
