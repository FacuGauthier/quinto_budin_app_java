package org.example.dtos.cliente;

public record ClienteResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String telefono,
        boolean activo
) {
}
