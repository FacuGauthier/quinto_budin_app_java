package org.example.dtos.cliente;

public record ClienteCreateRequest(
        String nombre,
        String apellido,
        String telefono
) implements ClienteRequest{}
