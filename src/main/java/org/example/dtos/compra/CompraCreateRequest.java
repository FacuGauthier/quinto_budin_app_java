package org.example.dtos.compra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CompraCreateRequest(
        LocalDate fechaCompra,
        List<DetalleCompraRequest> detalles
) {
    public record DetalleCompraRequest(
            Long idIngrediente,
            BigDecimal cantidadComprada,
            BigDecimal precioUnitario
    ) {}
}
