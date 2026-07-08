package edu.pe.utp.marcodesarrolloweb.ferrovoz.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 50)
    private String categoria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    // ruta imagen (guardada como ruta relativa: /img/productos/archivo.ext)
    @Column(length = 255)
    private String imagen;

    @Transient
    private String unidad;

    public Producto() {}

    // helpers Thymeleaf
    public String getEstadoStock() {
        if (stock == null || stock == 0) return "sin-stock";
        if (stock <= 5) return "stock-low";
        return "stock-ok";
    }
    public String getEtiquetaStock() {
        if (stock == null || stock == 0) return "Sin stock";
        if (stock <= 5) return "⚠ Stock: " + stock;
        return "Stock: " + stock;
    }

    public Integer    getId()                      { return id; }
    public void       setId(Integer id)            { this.id = id; }
    public String     getNombre()                  { return nombre; }
    public void       setNombre(String n)          { this.nombre = n; }
    public String     getCategoria()               { return categoria; }
    public void       setCategoria(String c)       { this.categoria = c; }
    public BigDecimal getPrecio()                  { return precio; }
    public void       setPrecio(BigDecimal p)      { this.precio = p; }
    public Integer    getStock()                   { return stock; }
    public void       setStock(Integer s)          { this.stock = s; }
    public String     getDescripcion()             { return descripcion; }
    public void       setDescripcion(String d)     { this.descripcion = d; }
    public String     getImagen()                  { return imagen; }
    public void       setImagen(String i)          { this.imagen = i; }
    public String     getUnidad()                  { return unidad; }
    public void       setUnidad(String u)          { this.unidad = u; }
}
