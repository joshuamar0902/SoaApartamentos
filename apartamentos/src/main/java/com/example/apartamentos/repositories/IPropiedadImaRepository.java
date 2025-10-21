package com.example.apartamentos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.apartamentos.models.PropiedadImaModel;

@Repository
public interface IPropiedadImaRepository extends JpaRepository<PropiedadImaModel, Long> {

    
}
