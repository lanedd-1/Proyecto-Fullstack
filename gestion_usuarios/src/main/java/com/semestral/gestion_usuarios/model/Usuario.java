package com.semestral.gestion_usuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Table(name = "usuario")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa a un usuario dentro del sistema de gestión")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
        description = "Identificador único autogenerado por la base de datos", 
        example = "1", 
        accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long idUsuario;

    @Column(nullable = false)
    @Schema(description = "Nombre completo del usuario registrado", example = "José Recabarren")
    private String nombreU;

    @Column(nullable = false)
    @Schema(description = "RUT del usuario con guion y dígito verificador", example = "12345678-9")
    private String rutU;

    @Column(nullable = false,unique = true)
    @Schema(description = "Correo electrónico institucional o de contacto único", example = "jose.recabarren@duocuc.cl")
    private String correoU;

    @Column(nullable = false)
    @Schema(description = "Contraseña almacenada de forma segura (encriptada mediante BCrypt)", example = "aaA123!!")
    private String claveU;

    @ManyToOne
    @JoinColumn(name = "idRol", nullable = false)
    @Schema(description = "Objeto completo del rol asignado con sus respectivos detalles")
    private Rol rol;

    @Column(name = "idEstado", nullable = false)
    @Schema(description = "Identificador numérico del estado actual del usuario (validado con ms-estado)", example = "1")
    private Long idEstado;
}