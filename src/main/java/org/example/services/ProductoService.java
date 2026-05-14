package org.example.services;

import org.example.models.Producto;
import org.example.models.ProductoIngrediente;
import org.example.repositories.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final ProductoIngredienteRepository productoIngredienteRepository;

    public ProductoService(ProductoRepository productoRepository, ProductoIngredienteRepository productoIngredienteRepository) {
        this.productoRepository = productoRepository;
        this.productoIngredienteRepository = productoIngredienteRepository;
    }

    public Producto crearProductoConReceta(Producto producto, List<ProductoIngrediente> receta) {
        if(producto == null){
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }

        if(receta == null || receta.isEmpty()){
            throw new IllegalArgumentException("El receta debe contener al menos un ingrediente.");
        }

        producto.setId(null);
        Producto prodGuardado = productoRepository.save(producto);

        for(ProductoIngrediente itemReceta : receta){
            if(itemReceta == null) {
                throw new IllegalArgumentException("La receta contiene un item invalido.");
            }
            if(itemReceta.getIngrediente() == null){
                throw new IllegalArgumentException("Cada item debe contener un ingrediente.");
            }
            if(itemReceta.getCantidadNecesaria() <= 0){
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            itemReceta.setProducto(prodGuardado);
            productoIngredienteRepository.save(itemReceta);
        }

        return prodGuardado;
    }
}
