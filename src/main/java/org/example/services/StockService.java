package org.example.services;

import org.example.models.*;
import org.example.repositories.DetallePedidoRepository;
import org.example.repositories.IngredienteRepository;
import org.example.repositories.MovimientoStockRepository;
import org.example.repositories.ProductoIngredienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class StockService {
    private final IngredienteRepository ingredienteRepository;
    private final MovimientoStockRepository movimientoStockRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;

    public StockService(IngredienteRepository ingredienteRepository, MovimientoStockRepository movimientoStockRepository, DetallePedidoRepository detallePedidoRepository, ProductoIngredienteRepository productoIngredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
        this.movimientoStockRepository = movimientoStockRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoIngredienteRepository = productoIngredienteRepository;
    }

    @Transactional
    public void registrarMovimiento(Ingrediente ingrediente, Tipo tipo, BigDecimal cantidad, Compra compra, Pedido pedido, String motivo) {
        if(ingrediente == null) {
            throw new IllegalArgumentException("El ingrediente no puede ser nulo.");
        }
        if(tipo == null) {
            throw new IllegalArgumentException("El tipo del movimiento no puede ser nulo.");
        }
        if(cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad no puede ser cero.");
        }
        if(tipo == Tipo.AJUSTE && motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio para ajustes de stock.");
        }

        switch (tipo) {
            case COMPRA, REVERSION -> ingrediente.sumarStock(cantidad.abs());
            case CONSUMO -> ingrediente.restarStock(cantidad.abs());
            case AJUSTE -> {
                if (cantidad.compareTo(BigDecimal.ZERO) > 0) {
                    ingrediente.sumarStock(cantidad);
                } else {
                    ingrediente.restarStock(cantidad.abs());
                }
            }
            default -> throw new IllegalArgumentException("El tipo de ajuste es incorrecto.");
        }

        MovimientoStock movimiento = new MovimientoStock();

        movimiento.setIngrediente(ingrediente);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setCompra(compra);
        movimiento.setPedido(pedido);
        movimiento.setMotivo(motivo);

        ingredienteRepository.save(ingrediente);
        movimientoStockRepository.save(movimiento);
    }


}
