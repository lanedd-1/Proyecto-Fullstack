package com.semestral.gestion_direccion.service;

import java.util.List;

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


    public List<DireccionResponseDTO> obtenerTodas() {
    List<Direccion> direcciones = direccionRepository.findAll();

    return direcciones.stream().map(dir -> new DireccionResponseDTO(
        dir.getId_direccion(),
        dir.getCalle(),
        dir.getNumero(),
        dir.getComuna().getId_comuna(),
        dir.getComuna().getRegion().getId_region()
    )).toList();
}
    public DireccionResponseDTO obtenerPorId(Long id) {
    return direccionRepository.findById(id)
        .map(dir -> new DireccionResponseDTO(
            dir.getId_direccion(),
            dir.getCalle(),
            dir.getNumero(),
            dir.getComuna().getId_comuna(),
            dir.getComuna().getRegion().getId_region()
        ))
        .orElseThrow(() -> new RuntimeException("Dirección no encontrada con ID: " + id));
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
    public void eliminar(Long id){
        direccionRepository.deleteById(id);
    }
}
