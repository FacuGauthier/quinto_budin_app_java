package org.example.dtos.ingrediente;

import java.math.BigDecimal;

public record IngredienteUpdateRequest(
        String nombre,
        String marca,
        String unidadMedida,
        BigDecimal costoUnitario
) implements IngredienteRequest {
}
