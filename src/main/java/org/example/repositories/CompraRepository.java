package org.example.repositories;

import org.example.models.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface CompraRepository extends JpaRepository<Compra, Long> {
    List<Compra> findByFechaReciboBetween(Date inicio, Date fin);
}
