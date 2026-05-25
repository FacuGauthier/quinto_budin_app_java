package org.example.services;

import org.example.dtos.stock.SugerenciaCompraDTO;
import org.example.models.*;
import org.example.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockService {
    private final IngredienteRepository ingredienteRepository;
    private final MovimientoStockRepository movimientoStockRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;

    public StockService(IngredienteRepository ingredienteRepository, MovimientoStockRepository movimientoStockRepository, PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository, ProductoIngredienteRepository productoIngredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
        this.movimientoStockRepository = movimientoStockRepository;
        this.pedidoRepository = pedidoRepository;
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

    @Transactional(readOnly = true)
    public List<String> validarStockParaPedido(Pedido pedido) {
        if(pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }

        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getId());
        Map<Ingrediente, BigDecimal> consumoNecesario = new HashMap<>();

        for(DetallePedido detalle : detalles) {
            Producto producto = detalle.getProducto();
            BigDecimal cantidad = detalle.getCantidad();

            List<ProductoIngrediente> receta = productoIngredienteRepository.findByProductoId(detalle.getProducto().getId());
            for(ProductoIngrediente item : receta) {
                Ingrediente ingrediente = item.getIngrediente();
                BigDecimal cantidadReceta = item.getCantidadNecesaria();
                BigDecimal consumoTotal = cantidadReceta.multiply(cantidad);
                consumoNecesario.merge(ingrediente, consumoTotal, BigDecimal::add);
            }
        }

        List<String> errores = new ArrayList<>();

        for(Map.Entry<Ingrediente, BigDecimal> entry : consumoNecesario.entrySet()) {
            Ingrediente ingrediente = entry.getKey();
            BigDecimal necesario = entry.getValue();
            BigDecimal disponible = ingrediente.getStockActual();

            if(disponible.compareTo(necesario) < 0) {
                BigDecimal faltante = necesario.subtract(disponible);
                errores.add("Stock insuficiente de "
                        + ingrediente.getNombre()
                        + ". Faltan "
                        + faltante
                        + " "
                        + ingrediente.getUnidadMedida());
            }
        }

        return errores;
    }

    @Transactional(readOnly = true)
    public SugerenciaCompraDTO calcularSugerenciaDeCompra() {
        List<Pedido> pedidosPendientes = pedidoRepository.findByEstado(Estado.PENDIENTE);

        Map<Ingrediente, BigDecimal> necesidades = new HashMap<>();

        for(Pedido pedido : pedidosPendientes) {
            for(DetallePedido  detallePedido : detallePedidoRepository.findByPedidoId(pedido.getId())) {
                Producto producto = detallePedido.getProducto();
                BigDecimal cantidadPedida = detallePedido.getCantidad();
                List<ProductoIngrediente> receta = productoIngredienteRepository.findByProductoId(detallePedido.getProducto().getId());
                for(ProductoIngrediente item : receta) {
                    Ingrediente ingrediente = item.getIngrediente();
                    BigDecimal consumo = item.getCantidadNecesaria().multiply(cantidadPedida);
                    necesidades.merge(ingrediente, consumo, BigDecimal::add);
                }
            }
        }

        List<SugerenciaCompraDTO.ItemSugerenciaDTO> items = new ArrayList<>();

        for(Map.Entry<Ingrediente, BigDecimal> entry : necesidades.entrySet()) {
            Ingrediente ingrediente = entry.getKey();
            BigDecimal cantidadNecesaria = entry.getValue();
            BigDecimal stockActual = ingrediente.getStockActual();
            if(cantidadNecesaria.compareTo(stockActual) > 0) {
                BigDecimal cantidadAcomprar = cantidadNecesaria.subtract(stockActual);
                items.add(new SugerenciaCompraDTO.ItemSugerenciaDTO(
                        ingrediente.getId(),
                        ingrediente.getNombre(),
                        ingrediente.getUnidadMedida(),
                        cantidadNecesaria,
                        stockActual,
                        cantidadAcomprar
                ));
            }
        }

        return new SugerenciaCompraDTO(LocalDate.now(), pedidosPendientes.size(), items);
    }

    @Transactional
    public void ejecutarAjusteManual(Long idIngrediente, BigDecimal cantidadAjuste, String motivo) {
        if(idIngrediente == null) {
            throw new IllegalArgumentException("El ID del ingrediente no puede ser nulo.");
        }

        if(cantidadAjuste == null || cantidadAjuste.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("La cantidad del ajuste no puede ser cero.");
        }

        if(motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo es obligatorio para ajustes.");
        }

        Ingrediente ingrediente = ingredienteRepository.findById(idIngrediente).orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado."));

        registrarMovimiento(ingrediente,Tipo.AJUSTE,cantidadAjuste,null,null,motivo);
    }

    @Transactional(readOnly = true)
    public List<MovimientoStock> obtenerHistorialPorIngrediente(Long idIngrediente) {
        if(idIngrediente == null) {
            throw new IllegalArgumentException("El ID del ingrediente no puede ser nulo.");
        }

        return movimientoStockRepository.findByIngredienteId(idIngrediente);
    }
}
