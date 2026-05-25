package org.example.dtos.stock;

import org.example.models.Tipo;

import java.math.BigDecimal;

public record MovimientoStockCreateRequest(
        Long idIngrediente,
        Tipo tipo,
        BigDecimal cantidad,
        Long idCompra,
        Long idPedido,
        String motivo
) {
}
