package org.example.dtos.pedido;

import jakarta.validation.constraints.NotNull;
import org.example.models.Estado;

public record PedidoCambioEstadoRequest(
        @NotNull
        Estado nuevoEstado
) {
}
