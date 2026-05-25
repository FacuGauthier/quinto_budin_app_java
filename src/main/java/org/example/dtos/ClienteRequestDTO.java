package org.example.dtos;

public record ClienteRequestDTO(
        String nombre,
        String apellido,
        String telefono
) {
}
