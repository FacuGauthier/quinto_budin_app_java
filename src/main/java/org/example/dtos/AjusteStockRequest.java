package org.example.dtos;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AjusteStockRequest(
        @NotNull
        Long idIngrediente,
        @NotNull
        BigDecimal cantidadAjuste,
        @NotNull
        String motivo
) {
}
