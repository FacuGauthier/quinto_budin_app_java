package org.example.models;

import jakarta.persistence.*;

@Entity
@Table(name = "producto_ingrediente")
public class ProductoIngrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Column(name = "cantidad_necesaria", nullable = false)
    private Integer cantidadNecesaria;

    public ProductoIngrediente() {
    }
    public ProductoIngrediente(Producto producto, Ingrediente ingrediente, Integer cantidadNecesaria) {
        this.producto = producto;
        this.ingrediente = ingrediente;
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    public Ingrediente getIngrediente() {
        return ingrediente;
    }
    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }
    public Integer getCantidadNecesaria() {
        return cantidadNecesaria;
    }
    public void setCantidadNecesaria(Integer cantidadNecesaria) {
        this.cantidadNecesaria = cantidadNecesaria;
    }
}
