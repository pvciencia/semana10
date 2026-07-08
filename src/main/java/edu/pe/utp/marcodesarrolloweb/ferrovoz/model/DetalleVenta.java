package edu.pe.utp.marcodesarrolloweb.ferrovoz.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    public DetalleVenta() {}

    public Integer    getId()                    { return id; }
    public void       setId(Integer id)          { this.id = id; }
    public Venta      getVenta()                 { return venta; }
    public void       setVenta(Venta v)          { this.venta = v; }
    public Producto   getProducto()              { return producto; }
    public void       setProducto(Producto p)    { this.producto = p; }
    public Integer    getCantidad()              { return cantidad; }
    public void       setCantidad(Integer c)     { this.cantidad = c; }
    public BigDecimal getPrecio()                { return precio; }
    public void       setPrecio(BigDecimal p)    { this.precio = p; }
}
