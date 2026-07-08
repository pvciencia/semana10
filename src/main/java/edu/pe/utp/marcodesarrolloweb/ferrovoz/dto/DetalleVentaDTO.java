package edu.pe.utp.marcodesarrolloweb.ferrovoz.dto;

import java.math.BigDecimal;

public class DetalleVentaDTO {
    private Integer   id;
    private Integer   productoId;
    private String    productoNombre;
    private Integer   cantidad;
    private BigDecimal precio;

    public DetalleVentaDTO() {}

    public Integer    getId()                        { return id; }
    public void       setId(Integer id)              { this.id = id; }
    public Integer    getProductoId()                { return productoId; }
    public void       setProductoId(Integer p)       { this.productoId = p; }
    public String     getProductoNombre()            { return productoNombre; }
    public void       setProductoNombre(String n)    { this.productoNombre = n; }
    public Integer    getCantidad()                  { return cantidad; }
    public void       setCantidad(Integer c)         { this.cantidad = c; }
    public BigDecimal getPrecio()                    { return precio; }
    public void       setPrecio(BigDecimal p)        { this.precio = p; }
}
