package com.semestral.gestion_usuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Table(name = "rol")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Entidad que representa los roles o perfiles de acceso asignados a los usuarios")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único del rol, autogenerado por la base de datos", 
        example = "1", 
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idRol;

    @Column(nullable = false, unique = true)
    @Schema(description = "Nombre único que identifica al rol dentro del sistema", example = "ADMIN")
    private String nombreRol;
}