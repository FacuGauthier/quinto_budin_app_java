package org.example.dtos.producto;

import java.math.BigDecimal;

public record ProductoUpdateRequest(
        String nombre,
        Integer tiempoDesarrollo,
        BigDecimal margenGanacia
) {
}
