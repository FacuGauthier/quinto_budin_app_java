package org.example.dtos.ingrediente;

import java.math.BigDecimal;

public record IngredienteResponse(
        Long id,
        String nombre,
        String marca,
        BigDecimal stockActual,
        String unidadMedida,
        BigDecimal costoUnitario,
        boolean activo
) {
}
