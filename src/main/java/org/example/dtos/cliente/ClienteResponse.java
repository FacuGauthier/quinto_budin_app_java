package org.example.dtos.cliente;

public record ClienteResponse(
        Long id,
        String nombre,
        String apellido,
        String telefono,
        boolean activo
) {
}
