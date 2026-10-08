package edu.pe.utp.marcodesarrolloweb.ferrovoz.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductoDTO {

    private Integer id;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 3, message = "El nombre debe tener al menos 3 caracteres")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotBlank(message = "Debes seleccionar una categoría")
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "El stock inicial es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    private String unidad;
    private String imagen;

    public ProductoDTO() {}

    public ProductoDTO(Integer id, String nombre, String descripcion,
                       String categoria, BigDecimal precio, Integer stock,
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

    // Helper para Thymeleaf (estado de stock)
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

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
}
