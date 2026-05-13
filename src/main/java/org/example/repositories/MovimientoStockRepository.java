package org.example.repositories;

import org.example.models.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
    List<MovimientoStock> findByIngredienteId(Long ingredienteId);
    List<MovimientoStock> findByTipo(String tipo);
}
