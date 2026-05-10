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

    @Column(nullable = false)
    private Integer cantidad_necesaria;

    public ProductoIngrediente() {
    }
    public ProductoIngrediente(Producto producto, Ingrediente ingrediente, Integer cantidad_necesaria) {
        this.producto = producto;
        this.ingrediente = ingrediente;
        this.cantidad_necesaria = cantidad_necesaria;
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
    public Integer getCantidad_necesaria() {
        return cantidad_necesaria;
    }
    public void setCantidad_necesaria(Integer cantidad_necesaria) {
        this.cantidad_necesaria = cantidad_necesaria;
    }
}
