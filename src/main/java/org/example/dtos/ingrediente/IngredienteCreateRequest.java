package org.example.dtos.ingrediente;

import java.math.BigDecimal;

public record IngredienteCreateRequest(
        String nombre,
        String marca,
        BigDecimal stockActual,
        String unidadMedida,
        BigDecimal costoUnitario
) implements IngredienteRequest {
}
