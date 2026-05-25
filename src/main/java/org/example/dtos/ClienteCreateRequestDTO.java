package org.example.dtos;

public record ClienteCreateRequestDTO(
        String nombre,
        String apellido,
        String telefono
) {
}
