package org.example.services;

import org.example.models.Producto;
import org.example.models.ProductoIngrediente;
import org.example.repositories.ProductoIngredienteRepository;
import org.example.repositories.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;
    private final PricingService pricingService;

    public ProductoService(ProductoRepository productoRepository, ProductoIngredienteRepository productoIngredienteRepository, PricingService pricingService) {
        this.productoRepository = productoRepository;
        this.productoIngredienteRepository = productoIngredienteRepository;
        this.pricingService = pricingService;
    }

    @Transactional
    public Producto crearProductoConReceta(Producto producto, List<ProductoIngrediente> receta) {
        validarDatosCreacion(producto);

        if(receta == null || receta.isEmpty()){
            throw new IllegalArgumentException("El receta debe contener al menos un ingrediente.");
        }

        producto.setId(null);
        producto.setActivo(true);
        Producto prodGuardado = productoRepository.save(producto);

        for(ProductoIngrediente itemReceta : receta){
            if(itemReceta == null) {
                throw new IllegalArgumentException("La receta contiene un item invalido.");
            }
            if(itemReceta.getIngrediente() == null){
                throw new IllegalArgumentException("Cada item debe contener un ingrediente.");
            }
            if(itemReceta.getCantidadNecesaria().compareTo(BigDecimal.ZERO) <= 0){
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            itemReceta.setProducto(prodGuardado);
            productoIngredienteRepository.save(itemReceta);
        }

        pricingService.recalcularPrecioProducto(prodGuardado);
        return productoRepository.save(prodGuardado);
    }

    @Transactional
    public Producto modificarProducto(Long id, Producto datosNuevos) {
        if(id == null){
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        validarDatosConfigurables(datosNuevos);

        Producto producto = buscarPorId(id);

        producto.setTiempoDesarrollo(datosNuevos.getTiempoDesarrollo());
        producto.setMargenGanancia(datosNuevos.getMargenGanancia());
        pricingService.recalcularPrecioProducto(producto);

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto modificarReceta(Long id, List<ProductoIngrediente> nuevaReceta) {
        if(id == null){
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }

        if(nuevaReceta == null || nuevaReceta.isEmpty()){
            throw new IllegalArgumentException("La nueva receta no puede estar vacia.");
        }

        Producto producto = buscarPorId(id);
        productoIngredienteRepository.deleteByProductoId(id);
        for(ProductoIngrediente itemReceta : nuevaReceta){
            if(itemReceta == null) {
                throw new IllegalArgumentException("La receta contiene elementos invalidos.");
            }
            if(itemReceta.getIngrediente() == null){
                throw new IllegalArgumentException("Cada item debe contener un ingrediente.");
            }
            if(itemReceta.getCantidadNecesaria().compareTo(BigDecimal.ZERO) <= 0){
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }

            itemReceta.setProducto(producto);
            productoIngredienteRepository.save(itemReceta);
        }
        pricingService.recalcularPrecioProducto(producto);

        return productoRepository.save(producto);
    }

    @Transactional
    public void bajaLogicaProducto(Long id) {
        Producto producto = buscarPorId(id);

        if(!producto.isActivo()){
            throw new IllegalArgumentException("El producto ya se encuentra inactivo.");
        }

        producto.marcarComoInactivo();
        productoRepository.save(producto);
    }

    @Transactional
    public void reactivarProducto(Long id) {
        Producto producto = buscarPorId(id);

        if(producto.isActivo()){
            throw new IllegalArgumentException("El producto ya se encuentra activo.");
        }

        producto.setActivo(true);
        productoRepository.save(producto);
    }

    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        if(id == null){
            throw new IllegalArgumentException("El ID no puede ser nulo.");
        }
        return productoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }


    private void validarDatosCreacion(Producto producto) {
        if(producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if(producto.getNombre() == null || producto.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if(producto.getTiempoDesarrollo() <= 0) {
            throw new IllegalArgumentException("El tiempo de desarrollo debe ser mayor a cero.");
        }

        if(producto.getMargenGanancia().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El margen de ganancia no puede ser negativo.");
        }
    }

    private void validarDatosConfigurables(Producto producto) {
        if(producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if(producto.getTiempoDesarrollo() <= 0) {
            throw new IllegalArgumentException("El tiempo debe ser mayor a cero.");
        }

        if(producto.getMargenGanancia().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El margen ganancia no puede ser negativo.");
        }
    }
}
