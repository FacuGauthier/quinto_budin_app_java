package org.example.dtos.ingrediente;

import java.math.BigDecimal;

public interface IngredienteRequest {
    String nombre();
    String marca();
    String unidadMedida();
    BigDecimal costoUnitario();
}
