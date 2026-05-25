package org.example.dtos.pedido;

import org.example.models.Estado;

import java.time.LocalDate;
import java.util.List;

public record PedidoRequest(
        Long id,
        String nombreCompletoCliente,
        String nombreProducto,
        LocalDate fechaRecibo,
        LocalDate fechaEntrega,
        Estado estado,
        String direccion,
        List<PedidoCreateRequest.DetallePedidoRequest> detallePedido
) {
}
