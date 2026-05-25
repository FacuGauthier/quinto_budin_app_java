package org.example.dtos.producto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoResponse(
        Long id,
        String nombre,
        Integer tiempoDesarrollo,
        BigDecimal margenGanancia,
        boolean activo,
        List<ProductoCreateRequest.ItemRecetaRequest> items
) {
}
