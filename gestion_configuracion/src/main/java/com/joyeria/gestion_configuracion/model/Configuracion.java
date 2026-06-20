package com.joyeria.gestion_configuracion.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Configuracion")
@Schema(description = "Entidad que representa la configuraccion de reglas de contraseña")
public class Configuracion {

    @Id
    @Column(name = "id_configuracion")
    @Schema(
        description = "Identificador único generaod automáticamente por la BD",
        example = "1",
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idConfiguracion;


    @Column(name = "longitud_minima", nullable = false)
    @Schema(
        description = "Longitud mínima permitida para la contraseña",
        example = "8"
    )
    private Integer longitudMinima;


    @Column(name = "longitud_maxima", nullable = false)
    @Schema(
        description = "Longitud máxima permitida para la contraseña",
        example = "20"
    )
    private Integer longitudMaxima;


    @Column(name = "requiere_mayuscula", nullable = false)
    @Schema(
        description = "Indica si la contraseña debe contener al menos una letra mayúscula (ej: A, B, C...)",
        example = "A"
    )
    private Boolean requiereMayuscula;



    @Column(name = "requiere_minuscula", nullable = false)
    @Schema(
        description = "Indica si la contraseña debe contener al menos una letra minúscula (ej: a, b, c...)",
        example = "a"
    )
    private Boolean requiereMinuscula;

    
    @Column(name = "requiere_numero", nullable = false)
    @Schema(
        description = "Indica si la contraseña debe contener al menos un número (ej: 0, 1, 2...)",
        example = "1"
    )
    private Boolean requiereNumero;


    @Column(name = "requiere_caracter_especial", nullable = false)
    @Schema(
        description = "Indica si la contraseña debe contener al menos un carácter especial (ej: !, @, #...)",
        example = "!"
    )
    private Boolean requiereCaracterEspecial;


    @Column(name = "caracteres_especiales_permitidos", length = 100)
    @Schema(
        description = "Lista de caracteres especiales permitidos en la contraseña",
        example = "!@#$%&*"
    )
    private String caracteresEspecialesPermitidos;
}
