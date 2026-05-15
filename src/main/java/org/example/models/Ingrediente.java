package org.example.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ingredientes")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String marca;

    @Column(name = "stock_actual", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockActual;

    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida;

    @Column(name = "costo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoUnitario;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDateTime fechaModificacion;

    public Ingrediente() {
    }
    public Ingrediente(String nombre, String marca, BigDecimal stockActual, String unidadMedida, BigDecimal costoUnitario, boolean activo) {
        this.nombre = nombre;
        this.marca = marca;
        this.stockActual = stockActual;
        this.unidadMedida = unidadMedida;
        this.costoUnitario = costoUnitario;
        this.activo = activo;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    public void marcarComoInactivo(){
        this.activo = false;
    }

    public void sumarStock(BigDecimal cantidad){
        this.stockActual = stockActual.add(cantidad);
    }
    public void restarStock(BigDecimal cantidad){
        if(this.stockActual.subtract(cantidad).compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalStateException("Stock insuficiente.");
        }
        this.stockActual = stockActual.subtract(cantidad);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public BigDecimal getStockActual() {
        return stockActual;
    }
    public void setStockActual(BigDecimal stockActual) {
        this.stockActual = stockActual;
    }
    public String getUnidadMedida() {
        return unidadMedida;
    }
    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }
    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }
}
