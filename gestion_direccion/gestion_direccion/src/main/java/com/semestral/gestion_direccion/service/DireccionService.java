package com.semestral.gestion_direccion.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.semestral.gestion_direccion.dto.DireccionRequestDTO;
import com.semestral.gestion_direccion.dto.DireccionResponseDTO;
import com.semestral.gestion_direccion.model.Comuna;
import com.semestral.gestion_direccion.model.Direccion;
import com.semestral.gestion_direccion.repository.ComunaRepository;
import com.semestral.gestion_direccion.repository.DireccionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DireccionService {
    private final DireccionRepository direccionRepository;
    private final ComunaRepository comunaRepository;


    public List<Direccion> obtenerTodas(){
        return direccionRepository.findAll();
    }
    public Optional<Direccion> obtenerPorId(Long id){
        return direccionRepository.findById(id);
    }
    
    public DireccionResponseDTO guardar(DireccionRequestDTO dto) {
    Comuna comuna = comunaRepository.findById(dto.getId_comuna())
            .orElseThrow(() -> new RuntimeException("Comuna no encontrada"));
    Direccion dir = new Direccion();
    dir.setCalle(dto.getCalle());
    dir.setNumero(dto.getNumero());
    dir.setComuna(comuna);

    Direccion guardada = direccionRepository.save(dir);
    return new DireccionResponseDTO(
        guardada.getId_direccion(),
        guardada.getCalle(),
        guardada.getNumero(),
        comuna.getId_comuna(), 
        comuna.getRegion().getId_region()
    );
}
    
}
