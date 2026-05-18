package org.example.services;

import org.example.models.*;
import org.example.repositories.DetallePedidoRepository;
import org.example.repositories.PedidoRepository;
import org.example.repositories.ProductoIngredienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;
    private final ClienteService clienteService;
    private final PricingService pricingService;
    private final StockService stockService;

    public PedidoService(PedidoRepository pedidoRepository, DetallePedidoRepository detallePedidoRepository, ProductoIngredienteRepository productoIngredienteRepository, ClienteService clienteService, PricingService pricingService, StockService stockService) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoIngredienteRepository = productoIngredienteRepository;
        this.clienteService = clienteService;
        this.pricingService = pricingService;
        this.stockService = stockService;
    }

    /// METODOS PRINCIPALES
    @Transactional
    public Pedido crearPedido(Pedido pedido, List<DetallePedido> detalles) {
        validarPedido(pedido, detalles);

        pedido.setId(null);
        pedido.setEstado(Estado.PENDIENTE);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        for(DetallePedido detallePedido : detalles) {
            validarDetalle(detallePedido);
            BigDecimal precioActual = detallePedido.getProducto().getPrecioVenta();
            if(precioActual == null || precioActual.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El producto tiene un precio invalido.");
            }
            detallePedido.setId(null);
            detallePedido.setPrecioUnitario(precioActual);
            detallePedido.setPedido(pedidoGuardado);
            detallePedidoRepository.save(detallePedido);
        }

        BigDecimal total = pricingService.calcularTotalPedido(detalles);
        pedidoGuardado.setPrecioFinal(total);

        return pedidoRepository.save(pedidoGuardado);
    }

    @Transactional
    public Pedido cambiarEstadoPedido(Long idPedido, Estado nuevoEstado) {
        if(nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }

        Pedido pedido = pedidoRepository.findById(idPedido).orElseThrow(() -> new IllegalArgumentException("El pedido no existe"));
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getId());

        if(detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido no tiene detalles.");
        }

        Estado estadoActual = pedido.getEstado();

        if(estadoActual == nuevoEstado) {
            throw new IllegalArgumentException("El pedido ya se encuentra en ese estado.");
        }

        switch(estadoActual) {
            case PENDIENTE:
                if(nuevoEstado == Estado.EN_PROCESO || nuevoEstado == Estado.CANCELADO) break;
                throw new IllegalArgumentException(transicionInvalida(estadoActual, nuevoEstado));
            case EN_PROCESO:
                if(nuevoEstado == Estado.COMPLETADO) {completarPedido(pedido); break;}
                if(nuevoEstado == Estado.CANCELADO) break;
                throw new IllegalArgumentException(transicionInvalida(estadoActual, nuevoEstado));
            case COMPLETADO:
                if(nuevoEstado == Estado.ENTREGADO) break;
                if(nuevoEstado == Estado.CANCELADO) {revertirConsumos(pedido); break;}
                throw new IllegalArgumentException(transicionInvalida(estadoActual, nuevoEstado));
            case ENTREGADO:
                throw new IllegalArgumentException("Un pedido ENTREGADO es inmutable. No puede cambiar de estado.");
            case CANCELADO:
                throw new IllegalArgumentException("Un pedido CANCELADO es un estado terminal. No puede modificarse.");
            default:
                throw new IllegalArgumentException("Estado no reconocido: " + estadoActual);
        }

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }

    @Transactional(readOnly = true)
    public Pedido obtenerDetallePedido(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return pedidoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("El pedido no existe."));
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorEstado(Estado estado) {
        if(estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }

        return pedidoRepository.findByEstado(estado);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorFechas(LocalDate inicio, LocalDate fin) {
        if(inicio == null || fin == null) {
            throw new IllegalArgumentException("El fechas no pueden ser nulas.");
        }

        if(inicio.isAfter(fin)) {
            throw new IllegalArgumentException("Las fecha de inicio no puede ser despues que la fecha de final.");
        }

        return pedidoRepository.findByFechaReciboBetween(inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pedido buscarPorId(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        return pedidoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("El pedido no existe."));
    }

    /// METODOS HELPER
    private void completarPedido(Pedido pedido) {
        List<String> faltantes = stockService.validarStockParaPedido(pedido);

        if (!faltantes.isEmpty()) {
            String detalle = String.join(" | ", faltantes);
            throw new IllegalStateException("Stock insuficiente para completar el pedido #" + pedido.getId()
                            + ". Insumos faltantes: " + detalle);
        }

        registrarMovimientosPorReceta(pedido, Tipo.CONSUMO);
    }
    private void revertirConsumos(Pedido pedido) {
        registrarMovimientosPorReceta(pedido, Tipo.REVERSION);
    }
    private void registrarMovimientosPorReceta(Pedido pedido, Tipo tipo) {
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoId(pedido.getId());

        for(DetallePedido detalle : detalles) {
            List<ProductoIngrediente> receta = productoIngredienteRepository.findByProductoId(detalle.getProducto().getId());
            for(ProductoIngrediente item : receta) {
                BigDecimal cantidadTotal = item.getCantidadNecesaria().multiply(detalle.getCantidad());
                stockService.registrarMovimiento(item.getIngrediente(), tipo, cantidadTotal, null, pedido, null);
            }
        }
    }
    private String transicionInvalida(Estado estadoActual, Estado nuevoEstado) {
        return String.format("Transicion invalida: el pedido no puede pasar de %s a %s", estadoActual, nuevoEstado);
    }

    /// METODOS VALIDACION
    private void validarPedido(Pedido pedido, List<DetallePedido> detalles) {
        if(pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }

        if(detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un detalle.");
        }

        if(pedido.getCliente() == null || clienteService.buscarPorId(pedido.getCliente().getId()) == null) {
            throw new IllegalArgumentException("Debe indicar un cliente.");
        }

        if(pedido.getFechaRecibo() == null || pedido.getFechaEntrega() == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        }

        if(pedido.getFechaRecibo().isAfter(pedido.getFechaEntrega())) {
            throw new IllegalArgumentException("La fecha de entrega no puede ser anterior.");
        }

        if(pedido.getDireccion() == null || pedido.getDireccion().isBlank()) {
            throw new IllegalArgumentException("La direccion no puede estar vacia.");
        }
    }
    private void validarDetalle(DetallePedido detalle) {
        if(detalle == null) {
            throw new IllegalArgumentException("Detalle invalido.");
        }

        if(detalle.getProducto()  == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if(detalle.getCantidad() == null || detalle.getCantidad().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
    }
}
