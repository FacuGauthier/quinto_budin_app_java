package org.example.services;

import org.example.models.DetallePedido;
import org.example.models.Producto;
import org.example.models.ProductoIngrediente;
import org.example.repositories.ProductoIngredienteRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PricingService {
    @Value("${pricing.valor-minuto-trabajo:0.50}")
    private BigDecimal valorMinutoTrabajo;
    private final ProductoIngredienteRepository productoIngredienteRepository;

    public PricingService(ProductoIngredienteRepository productoIngredienteRepository) {
        this.productoIngredienteRepository = productoIngredienteRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularCostoIngredientes(Producto producto) {
        if(producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        List<ProductoIngrediente> receta = productoIngredienteRepository.findByProductoId(producto.getId());

        if(receta.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal costoTotal = BigDecimal.ZERO;

        for(ProductoIngrediente item : receta) {
            BigDecimal costoItem = item.getCantidadNecesaria().multiply(item.getIngrediente().getCostoUnitario());
            costoTotal = costoTotal.add(costoItem);
        }

        return costoTotal;
    }

    public BigDecimal calcularCostoTiempo(Producto producto) {
        if(producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        return BigDecimal.valueOf(producto.getTiempoDesarrollo()).multiply(valorMinutoTrabajo);
    }

    public BigDecimal calcularCostoProduccion(Producto producto) {
        return calcularCostoIngredientes(producto).add(calcularCostoTiempo(producto));
    }

    public BigDecimal calcularPrecioVenta(Producto producto) {
        BigDecimal costoProduccion = calcularCostoProduccion(producto);
        BigDecimal margenGanancia = producto.getMargenGanancia();

        if(margenGanancia == null) {
            throw new IllegalArgumentException("El producto no tiene definido un margen de ganancia.");
        }

        BigDecimal porcentaje = margenGanancia.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal factor = BigDecimal.ONE.add(porcentaje);

        return costoProduccion.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    public void recalcularPrecioProducto(Producto producto) {
        if(producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        producto.setCostoProduccion(calcularCostoProduccion(producto));
        producto.setPrecioVenta(calcularPrecioVenta(producto));
    }

    public BigDecimal calcularSubtotalDetalle(DetallePedido detalle) {
        if(detalle == null) {
            throw new IllegalArgumentException("El detalle pedido no puede ser nulo.");
        }

        if(detalle.getPrecioUnitario() == null) {
            throw new IllegalArgumentException("El precio unitario no esta definido.");
        }

        if(detalle.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }

        return detalle.getPrecioUnitario().multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }

    public BigDecimal calcularTotalPedido(List<DetallePedido> detalles) {
        if(detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La lista de detalles del pedido no puede ser nula ni estar vacia.");
        }

        BigDecimal totalPedido = BigDecimal.ZERO;

        for(DetallePedido detalle : detalles) {
            totalPedido = totalPedido.add(calcularSubtotalDetalle(detalle));
        }

        return totalPedido;
    }
}
