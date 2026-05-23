package com.joyeria.gestion_configuracion.model;

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
public class Configuracion {
    @Id
    @Column(name = "id_configuracion")
    private Long idConfiguracion;

    @Column(name = "longitud_minima", nullable = false)
    private Integer longitudMinima;

    @Column(name = "longitud_maxima", nullable = false)
    private Integer longitudMaxima;

    @Column(name = "requiere_mayuscula", nullable = false)
    private Boolean requiereMayuscula;

    @Column(name = "requiere_minuscula", nullable = false)
    private Boolean requiereMinuscula;

    @Column(name = "requiere_numero", nullable = false)
    private Boolean requiereNumero;

    @Column(name = "requiere_caracter_especial", nullable = false)
    private Boolean requiereCaracterEspecial;

    @Column(name = "caracteres_especiales_permitidos", length = 100)
    private String caracteresEspecialesPermitidos;
}
