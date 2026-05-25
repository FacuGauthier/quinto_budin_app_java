package org.example.dtos.compra;

import java.time.LocalDate;
import java.util.List;

public record CompraResponse(
        Long id,
        LocalDate fechaCompra,
        List<CompraCreateRequest.DetalleCompraRequest> detalles
) {
}
