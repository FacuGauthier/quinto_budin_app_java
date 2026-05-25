package org.example.dtos.producto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoCreateRequest(
        String nombre,
        Integer tiempoDesarrollo,
        BigDecimal margenGanancia,
        List<ItemRecetaRequest> items
) {
    public record ItemRecetaRequest(
            Long idIngrediente,
            BigDecimal cantidadNecesaria
    ){}
}
