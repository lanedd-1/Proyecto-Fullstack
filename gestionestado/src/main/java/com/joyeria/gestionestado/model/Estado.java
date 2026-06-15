package com.joyeria.gestionestado.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Estados")
@Schema(description = "Entidad que representa un estado de compra")
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único generado automáticamente por la BD",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY    
    )
    private Long idEstado;

    @Column(name = "nombre_estado", nullable = false, length = 100)
    @Schema(
        description = "Nombre descriptivo del estado",
        example = "Activo"
    )
    private String nombreEstado;
    
}