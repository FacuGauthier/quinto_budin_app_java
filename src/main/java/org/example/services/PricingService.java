package org.example.services;

import org.example.models.Producto;
import org.example.models.ProductoIngrediente;
import org.example.repositories.ProductoIngredienteRepository;
import org.example.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PricingService {
    @Value("${pricing.valor-minuto-trabajo:0.50}")
    private BigDecimal valorMinutoTrabajo;
    private final ProductoRepository productoRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;

    public PricingService(ProductoRepository productoRepository, ProductoIngredienteRepository productoIngredienteRepository) {
        this.productoRepository = productoRepository;
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

        BigDecimal porcentaje = margenGanancia.divide(new BigDecimal(100), 4, RoundingMode.HALF_UP);
        BigDecimal factor = BigDecimal.ONE.add(porcentaje);

        return costoProduccion.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
