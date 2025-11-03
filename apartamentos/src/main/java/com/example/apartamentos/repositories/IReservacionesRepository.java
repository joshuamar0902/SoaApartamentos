package com.example.apartamentos.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.apartamentos.models.ReservacionesModel;
import com.example.apartamentos.models.TipoPropiedad;



@Repository
public interface IReservacionesRepository extends JpaRepository<ReservacionesModel, Long> {
    //Mi Método
    List<ReservacionesModel> findByPropiedad_Tipo(TipoPropiedad tipo);

    @Query("SELECT r FROM ReservacionesModel r " +
           "WHERE r.propiedad.id = :propiedadId " +
           "AND r.estado <> 'CANCELADA' " + // Asume que 'CANCELADA' es un String
           "AND r.fechaEntrada < :fechaSalida " +
           "AND r.fechaSalida > :fechaEntrada")
    List<ReservacionesModel> findReservacionesSolapadas(
            @Param("propiedadId") Long propiedadId,
            @Param("fechaEntrada") LocalDate fechaEntrada,
            @Param("fechaSalida") LocalDate fechaSalida);

    List<ReservacionesModel> findByFechaEntrada(LocalDate fechaEntrada);

    boolean existsByClienteId(Long clienteId);
    boolean existsByPropiedadId(Long propiedadId);



    
}

