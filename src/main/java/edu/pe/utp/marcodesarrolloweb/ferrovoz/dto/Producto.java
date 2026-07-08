package edu.pe.utp.marcodesarrolloweb.ferrovoz.dto;

public class Producto {

    private int id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private double precio;
    private int stock;
    private String unidad;
    private String imagen;   // ruta relativa: /img/productos/nombre.jpg

    public Producto() {}

    public Producto(int id, String nombre, String descripcion,
                    String categoria, double precio, int stock,
                    String unidad, String imagen) {
        this.id          = id;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.categoria   = categoria;
        this.precio      = precio;
        this.stock       = stock;
        this.unidad      = unidad;
        this.imagen      = imagen;
    }

    // ── Getters ──
    public int    getId()          { return id; }
    public String getNombre()      { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria()   { return categoria; }
    public double getPrecio()      { return precio; }
    public int    getStock()       { return stock; }
    public String getUnidad()      { return unidad; }
    public String getImagen()      { return imagen; }

    // ── Setters ──
    public void setId(int id)                   { this.id = id; }
    public void setNombre(String nombre)         { this.nombre = nombre; }
    public void setDescripcion(String desc)      { this.descripcion = desc; }
    public void setCategoria(String categoria)   { this.categoria = categoria; }
    public void setPrecio(double precio)         { this.precio = precio; }
    public void setStock(int stock)              { this.stock = stock; }
    public void setUnidad(String unidad)         { this.unidad = unidad; }
    public void setImagen(String imagen)         { this.imagen = imagen; }

    // ── Helper para Thymeleaf ──
    public String getEstadoStock() {
        if (stock == 0)   return "sin-stock";
        if (stock <= 5)   return "stock-low";
        return "stock-ok";
    }

    public String getEtiquetaStock() {
        if (stock == 0)   return "Sin stock";
        if (stock <= 5)   return "⚠ Stock: " + stock;
        return "Stock: " + stock;
    }
}
