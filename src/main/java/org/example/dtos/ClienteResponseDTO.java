package org.example.dtos;

public record ClienteResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String telefono,
        boolean activo
) {
}
