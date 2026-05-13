package com.semestral.productos.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.semestral.productos.dto.CategoriaRequestDTO;
import com.semestral.productos.dto.CategoriaResponseDTO;

import com.semestral.productos.model.Categoria;

import com.semestral.productos.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaService {

   private final CategoriaRepository catRepo;


    public List<CategoriaResponseDTO> findAllCat(){
        return catRepo.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
    }

    public CategoriaResponseDTO saveCat(CategoriaRequestDTO r) {
        Categoria cat = new Categoria();
        cat.setNombreCat(r.getNombreCat());
        return convertToDTO(catRepo.save(cat));
    }

    public Optional<Categoria> findById(Long idCat) {
        return catRepo.findById(idCat);
    }

    public Optional<CategoriaResponseDTO> buscarPorId(Long idCat) {
        return catRepo.findById(idCat).map(this::convertToDTO);
    }

       private CategoriaResponseDTO convertToDTO(Categoria c) {
        return new CategoriaResponseDTO(
            c.getIdCat(),
            c.getNombreCat()
        );
    }
}
