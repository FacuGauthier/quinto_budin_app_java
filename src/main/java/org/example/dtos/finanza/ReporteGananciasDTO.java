package org.example.dtos.finanza;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ReporteGananciasDTO(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal ingresosTotales,
        BigDecimal egresosTotales,
        BigDecimal gananciaNeta,
        int cantidadPedidos,
        int cantidadCompras
) {
}
