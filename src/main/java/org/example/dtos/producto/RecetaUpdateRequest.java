package org.example.dtos.producto;

import java.util.List;

public record RecetaUpdateRequest(
        List<ProductoCreateRequest.ItemRecetaRequest> itemRecetas
) {
}
