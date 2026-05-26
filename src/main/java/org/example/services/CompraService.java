package org.example.services;

import org.example.dtos.compra.CompraCreateRequest;
import org.example.dtos.compra.CompraResponse;
import org.example.models.Compra;
import org.example.models.DetalleCompra;
import org.example.models.Ingrediente;
import org.example.models.Tipo;
import org.example.repositories.CompraRepository;
import org.example.repositories.DetalleCompraRepository;
import org.example.repositories.IngredienteRepository;
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
    private final IngredienteRepository ingredienteRepository;

    public CompraService(CompraRepository compraRepository,  DetalleCompraRepository detalleCompraRepository, StockService stockService,  IngredienteRepository ingredienteRepository) {
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.stockService = stockService;
        this.ingredienteRepository = ingredienteRepository;
    }

    @Transactional
    public CompraResponse registrarCompra(CompraCreateRequest request) {
        validarCompra(request);

        Compra compra = new Compra();

        compra.setId(null);
        compra.setFechaCompra(request.fechaCompra());

        BigDecimal costoTotal =  BigDecimal.ZERO;

        for(CompraCreateRequest.DetalleCompraRequest detalleRequest : request.detalles()) {
            validarDetalleCompra(detalleRequest);
            BigDecimal subtotal = detalleRequest.cantidadComprada().multiply(detalleRequest.precioUnitario());
            costoTotal = costoTotal.add(subtotal);
        }

        compra.setCostoTotal(costoTotal);

        Compra compraGuardada = compraRepository.save(compra);

        for(CompraCreateRequest.DetalleCompraRequest detalleRequest : request.detalles()) {
            Ingrediente ingrediente = ingredienteRepository.findById(detalleRequest.idIngrediente()).orElseThrow(() -> new IllegalArgumentException("Ingrediente no encontrado"));

            DetalleCompra detalle = new DetalleCompra();

            detalle.setCompra(compra);
            detalle.setIngrediente(ingrediente);
            detalle.setCantidadComprada(detalleRequest.cantidadComprada());
            detalle.setPrecioUnitario(detalleRequest.precioUnitario());

            detalleCompraRepository.save(detalle);

            stockService.registrarMovimiento(
                    detalle.getIngrediente(),
                    Tipo.COMPRA,
                    detalle.getCantidadComprada(),
                    compraGuardada, null,null);
        }

        return toResponseCompra(compraGuardada, request.detalles());
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> verHistorialDeCompras() {
        return compraRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> verHistorialEntreFechas(LocalDate inicio, LocalDate fin) {
        return compraRepository.findByFechaCompraBetween(inicio, fin).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Compra buscarPorId(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo.");
        }
        return compraRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Compra no encontrada."));
    }


    private void validarCompra(CompraCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La compra no puede ser nula.");
        }

        if (request.detalles() == null || request.detalles().isEmpty()) {
            throw new IllegalArgumentException("La compra debe tener al menos un detalle.");
        }

        if (request.fechaCompra() == null) {
            throw new IllegalArgumentException("La fecha de compra no puede ser nula.");
        }
    }
    private void validarDetalleCompra(CompraCreateRequest.DetalleCompraRequest detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle de compra no puede ser nulo.");
        }

        if (detalle.idIngrediente() == null) {
            throw new IllegalArgumentException("El ingrediente no puede ser nulo.");
        }

        if (detalle.cantidadComprada() == null
                || detalle.cantidadComprada().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad comprada debe ser mayor a cero.");
        }

        if (detalle.precioUnitario() == null
                || detalle.precioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero.");
        }
    }

    private CompraResponse toResponseCompra (Compra compra, List<CompraCreateRequest.DetalleCompraRequest> detalles) {

        return new CompraResponse(
                compra.getId(),
                compra.getFechaCompra(),
                detalles
        );
    }
    private CompraResponse toResponse(Compra compra) {
        List<CompraCreateRequest.DetalleCompraRequest> detalles =
                detalleCompraRepository.findByCompra(compra)
                        .stream()
                        .map(detalle -> new CompraCreateRequest.DetalleCompraRequest(
                                detalle.getIngrediente().getId(),
                                detalle.getCantidadComprada(),
                                detalle.getPrecioUnitario()
                        ))
                        .toList();

        return new CompraResponse(
                compra.getId(),
                compra.getFechaCompra(),
                detalles
        );
    }
}
