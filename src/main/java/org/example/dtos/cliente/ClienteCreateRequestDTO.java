package org.example.dtos.cliente;

public record ClienteCreateRequestDTO(
        String nombre,
        String apellido,
        String telefono
) {
}
