package com.semestral.inventario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioRequestDTO {

    @Schema(
        description = "ID del producto",
        example = "1"
    )
    @NotNull(message = "El ID del producto no puede ser nulo")
    private Long idProd;   
    
    @Schema(
    description = "ID del pasillo",
    example = "1"
    )
    @NotNull(message = "El ID del pasillo no puede ser nulo")
    private Long idPasillo;    

    @Schema(
    description = "ID del estante",
    example = "1"
    )
    @NotNull(message = "El ID del estante no puede ser nulo")
    private Long idEstante;    

    @Schema(
    description = "Cantidad del producto",
    example = "10"
    )
    @NotNull(message = "La cantidad no puede ser nula")
    private Integer cantidad;  

}
