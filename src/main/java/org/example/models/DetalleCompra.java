package org.example.models;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "detalle_compra")
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "compra_id", nullable = false)
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Column(name = "cantidad_comprada", nullable = false)
    private Integer cantidadComprada;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    public DetalleCompra() {
    }
    public DetalleCompra(Compra compra, Ingrediente ingrediente, Integer cantidadComprada, BigDecimal precioUnitario) {
        this.compra = compra;
        this.ingrediente = ingrediente;
        this.cantidadComprada = cantidadComprada;
        this.precioUnitario = precioUnitario;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Compra getCompra() {
        return compra;
    }
    public void setCompra(Compra compra) {
        this.compra = compra;
    }
    public Ingrediente getIngrediente() {
        return ingrediente;
    }
    public void setIngrediente(Ingrediente ingrediente) {
        this.ingrediente = ingrediente;
    }
    public Integer getCantidadComprada() {
        return cantidadComprada;
    }
    public void setCantidadComprada(Integer cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
    }
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
