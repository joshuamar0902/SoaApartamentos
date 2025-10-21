package com.example.apartamentos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.apartamentos.models.ReservacionesModel;

@Repository
public interface IReservacionesRepository extends JpaRepository<ReservacionesModel, Long> {

    
}

