package com.example.apartamentos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.apartamentos.models.PagosModel;

@Repository
public interface IPagosRepository extends JpaRepository<PagosModel, Long> {

    
}
