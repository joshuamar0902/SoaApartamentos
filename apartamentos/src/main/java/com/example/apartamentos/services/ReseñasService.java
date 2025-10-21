package com.example.apartamentos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.apartamentos.models.ReseñasModel;
import com.example.apartamentos.repositories.IReseñasRepository;

@Service
public class ReseñasService {
    
    @Autowired
    private IReseñasRepository reseñasRepository;

    public List<ReseñasModel> getAllReseñas() {
        return reseñasRepository.findAll();
    }

    public Optional<ReseñasModel> getReseñaById(Long id) {
        return reseñasRepository.findById(id);
    }

    public ReseñasModel saveReseña(ReseñasModel reseña) {
        return reseñasRepository.save(reseña);
    }

    public void deleteReseña(Long id) {
        reseñasRepository.deleteById(id);
    }

    
}