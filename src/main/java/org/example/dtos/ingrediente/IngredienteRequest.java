package org.example.dtos.ingrediente;

import java.math.BigDecimal;

public interface IngredienteRequest {
    String nombre();
    String marca();
    BigDecimal stockActual();
    String unidadMedida();
    BigDecimal costoUnitario();
}
