package org.example.dtos.stock;

import org.example.models.Tipo;

public record MovimientoStockResponse(
        Long id,
        String nombreIngrediente,
        Tipo tipo,
        Long idCompra,
        Long idPedido,
        String motivo
) {
}
