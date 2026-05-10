package org.example.models;

public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "precio_venta",nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "tiempo_desarrollo",nullable = false)
    private Integer tiempoDesarrollo;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion",nullable = false)
    private LocalDateTime fechaModificacion;

    public Producto() {
    }
    public Producto(String nombre, BigDecimal precioVenta, Integer tiempoDesarrollo, boolean activo) {
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.tiempoDesarrollo = tiempoDesarrollo;
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
    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }
    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }
    public Integer getTiempoDesarrollo() {
        return tiempoDesarrollo;
    }
    public void setTiempoDesarrollo(Integer tiempoDesarrollo) {
        this.tiempoDesarrollo = tiempoDesarrollo;
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
