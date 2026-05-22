package org.example.services;

import org.example.models.Compra;
import org.example.models.DetalleCompra;
import org.example.models.Tipo;
import org.example.repositories.CompraRepository;
import org.example.repositories.DetalleCompraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CompraService {
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final StockService stockService;

    public CompraService(CompraRepository compraRepository,  DetalleCompraRepository detalleCompraRepository, StockService stockService) {
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.stockService = stockService;
    }

    @Transactional
    public Compra registrarCompra(Compra compra, List<DetalleCompra> detalles) {
        validarCompra(compra, detalles);

        compra.setId(null);

        BigDecimal costoTotal =  BigDecimal.ZERO;

        for(DetalleCompra detalle : detalles) {
            validarDetalleCompra(detalle);
            BigDecimal subtotal = detalle.getCantidadComprada().multiply(detalle.getPrecioUnitario());
            costoTotal = costoTotal.add(subtotal);
        }

        compra.setCostoTotal(costoTotal);

        Compra compraGuardada = compraRepository.save(compra);

        for(DetalleCompra detalle : detalles) {
            detalle.setCompra(compraGuardada);
            detalleCompraRepository.save(detalle);
            stockService.registrarMovimiento(
                    detalle.getIngrediente(),
                    Tipo.COMPRA,
                    detalle.getCantidadComprada(),
                    compraGuardada, null,null);
        }

        return compraGuardada;
    }

    @Transactional(readOnly = true)
    public List<Compra> verHistorialDeCompras() {
        return compraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Compra> verHistorialEntreFechas(LocalDate inicio, LocalDate fin) {
        return compraRepository.findByFechaCompraBetween(inicio, fin);
    }

    @Transactional(readOnly = true)
    public Compra buscarPorId(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo.");
        }
        return compraRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Compra no encontrada."));
    }


    private void validarCompra(Compra compra, List<DetalleCompra> detalles) {
        if(compra == null) throw new IllegalArgumentException("La compra no puede ser nula.");
        if(detalles == null || detalles.isEmpty()) throw new IllegalArgumentException("La compra debe tener al menos un detalle.");
        if(compra.getFechaCompra() == null) throw new IllegalArgumentException("La fecha de compra no puede ser nula.");
    }
    private void validarDetalleCompra(DetalleCompra detalle) {
        if(detalle == null) throw new IllegalArgumentException("El detalle de compra no puede ser nulo.");
        if(detalle.getIngrediente() == null) throw new IllegalArgumentException("El ingrediente no puede ser nulo.");
        if(detalle.getCantidadComprada() == null || detalle.getCantidadComprada().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("La cantidad comprada debe ser mayora a cero.");
        if(detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El precio unitario debe ser mayora a cero.");
    }
}
