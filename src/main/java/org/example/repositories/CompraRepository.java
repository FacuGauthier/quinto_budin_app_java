package org.example.repositories;

import org.example.models.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByFechaCompraBetween(LocalDate inicio, LocalDate fin);
}
