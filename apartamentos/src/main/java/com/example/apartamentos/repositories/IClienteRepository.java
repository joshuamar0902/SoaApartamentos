package com.example.apartamentos.repositories;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.apartamentos.models.ClienteModel;
import com.example.apartamentos.models.TipoCliente;



@Repository
public interface IClienteRepository extends JpaRepository<ClienteModel, Long> {
    List<ClienteModel> findAllByTipo(TipoCliente tipo);

    
}
