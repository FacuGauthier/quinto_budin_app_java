package org.example.services;

import org.example.dtos.finanza.ReporteGananciasDTO;
import org.example.models.Compra;
import org.example.models.Estado;
import org.example.models.Pedido;
import org.example.repositories.CompraRepository;
import org.example.repositories.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class FinanzasService {
    private final PedidoRepository pedidoRepository;
    private final CompraRepository compraRepository;

    public FinanzasService(PedidoRepository pedidoRepository, CompraRepository compraRepository) {
        this.pedidoRepository = pedidoRepository;
        this.compraRepository = compraRepository;
    }

    @Transactional(readOnly = true)
    public ReporteGananciasDTO generarReporteGanancias(LocalDate inicio, LocalDate fin) {
        if(inicio == null || fin == null) throw new IllegalArgumentException("Las fechas no pueden ser nulas.");
        if(inicio.isAfter(fin)) throw new IllegalArgumentException("Las fecha de inicio no puede ser posterior a la fecha fin.");

        List<Pedido> pedidos = pedidoRepository.findByFechaReciboBetween(inicio, fin);

        BigDecimal ingresosTotales = pedidos.stream()
                .filter(p -> p.getEstado() == Estado.COMPLETADO || p.getEstado() == Estado.ENTREGADO)
                .map(Pedido::getPrecioFinal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int cantidadPedidos = pedidos.size();

        List<Compra> compras = compraRepository.findByFechaCompraBetween(inicio, fin);

        BigDecimal egresosTotales = compras.stream()
                .map(Compra::getCostoTotal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int cantidadCompras = compras.size();

        BigDecimal gananciaNeta = ingresosTotales.subtract(egresosTotales);

        return new ReporteGananciasDTO(inicio, fin, ingresosTotales, egresosTotales, gananciaNeta, cantidadPedidos, cantidadCompras);
    }
}
