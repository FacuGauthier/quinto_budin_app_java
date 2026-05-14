package org.example.repositories;

import org.example.models.ProductoIngrediente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoIngredienteRepository extends JpaRepository<ProductoIngrediente, Long> {
    void deleteByProductoId(Long productoId);
    List<ProductoIngrediente> findByProductoId(Long id);
}
