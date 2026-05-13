package com.semestral.productos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombreCat;
}
