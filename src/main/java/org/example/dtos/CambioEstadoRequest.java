package org.example.dtos;

import jakarta.validation.constraints.NotNull;
import org.example.models.Estado;

public record CambioEstadoRequest(
        @NotNull
        Estado nuevoEstado
) {
}
