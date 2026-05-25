package org.example.dtos.stock;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SugerenciaCompraDTO(
        LocalDate fechaGeneracion,
        int cantidadPedidos,
        List<ItemSugerenciaDTO> items
){
    public record ItemSugerenciaDTO(
            Long idIngrediente,
            String nombreIngrediente,
            String unidadMedida,
            BigDecimal cantidadNecesaria,
            BigDecimal stockActual,
            BigDecimal cantidadAcomprar
    ){}
}
