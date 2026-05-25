package org.example.dtos.cliente;

public record ClienteUpdateRequest(
        String nombre,
        String apellido,
        String telefono,
        boolean activo
) implements ClienteRequest{
}
