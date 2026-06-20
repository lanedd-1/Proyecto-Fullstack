package com.semestral.gestion_direccion.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "direccion")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Direccion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único autogenerado por la base de datos", 
        example = "1", 
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idDireccion;

    @Column(name = "calle",nullable = false)
    @Schema(description = "Nombre de la Calle registrada", example = "Avenida Siempre Viva")
    private String calle;

    @Column(name = "numero",nullable = false)
    @Schema(description = "Numero de la Calle registrada", example = "123")
    private String numero;
    @ManyToOne
    @JoinColumn(name = "idComuna",nullable = false)
    @Schema(description = "Objeto completo de la comuna asignado con sus respectivos detalles")
    private Comuna comuna;

    @Column(name = "idUser")
    @Schema(description = "Identificador numérico del usuario (validado con ms-usuario)", example = "1")
    private Long idUsuario;

    @Column(name = "idEstado")
    @Schema(description = "Identificador numérico del estado actual de la Direccion (validado con ms-estado)", example = "1")
    private Long idEstado;
}
