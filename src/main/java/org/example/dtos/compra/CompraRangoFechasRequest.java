package org.example.dtos.compra;

import java.time.LocalDate;

public record CompraRangoFechasRequest(
        LocalDate inicio,
        LocalDate fin
) {
}
