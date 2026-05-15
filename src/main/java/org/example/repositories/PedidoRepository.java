package org.example.repositories;

import org.example.models.Estado;
import org.example.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstado(Estado estado);
    List<Pedido> findByFechaReciboBetween(LocalDate inicio, LocalDate fin);
}
