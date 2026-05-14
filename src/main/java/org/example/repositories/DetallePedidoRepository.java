package org.example.repositories;

import org.example.models.DetallesPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetallePedidoRepository extends JpaRepository<DetallesPedido, Long>{
}
