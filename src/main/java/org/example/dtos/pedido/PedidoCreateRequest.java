package org.example.dtos.pedido;

import java.time.LocalDate;
import java.util.List;

public record PedidoCreateRequest(
        Long idCliente,
        Long idProducto,
        LocalDate fechaRecibo,
        LocalDate fechaEntrega,
        String direccion,
        List<DetallePedidoRequest> detalles
) {
    public record DetallePedidoRequest(
            Long idProducto,
            Integer cantidad
    ) {}
}
