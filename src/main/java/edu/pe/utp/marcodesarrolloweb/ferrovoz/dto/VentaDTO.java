package edu.pe.utp.marcodesarrolloweb.ferrovoz.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class VentaDTO {

    private Integer id;
    private Integer clienteId;
    private String  clienteNombre;
    private String  clienteDni;
    private Integer usuarioId;
    private LocalDateTime fecha;
    private BigDecimal total;
    private String  estado;
    private String  metodoPago;
    private List<DetalleVentaDTO> detalles;

    public VentaDTO() {}

    public String getEstadoBadgeClass() {
        return switch (estado == null ? "" : estado) {
            case "Confirmado" -> "bg-success";
            case "Pendiente"  -> "bg-warning text-dark";
            case "Cancelado"  -> "bg-danger";
            default           -> "bg-secondary";
        };
    }

    public Integer       getId()                             { return id; }
    public void          setId(Integer id)                   { this.id = id; }
    public Integer       getClienteId()                      { return clienteId; }
    public void          setClienteId(Integer c)             { this.clienteId = c; }
    public String        getClienteNombre()                  { return clienteNombre; }
    public void          setClienteNombre(String c)          { this.clienteNombre = c; }
    public String        getClienteDni()                     { return clienteDni; }
    public void          setClienteDni(String d)             { this.clienteDni = d; }
    public Integer       getUsuarioId()                      { return usuarioId; }
    public void          setUsuarioId(Integer u)             { this.usuarioId = u; }
    public LocalDateTime getFecha()                          { return fecha; }
    public void          setFecha(LocalDateTime f)           { this.fecha = f; }
    public BigDecimal    getTotal()                          { return total; }
    public void          setTotal(BigDecimal t)              { this.total = t; }
    public String        getEstado()                         { return estado; }
    public void          setEstado(String e)                 { this.estado = e; }
    public String        getMetodoPago()                     { return metodoPago; }
    public void          setMetodoPago(String m)             { this.metodoPago = m; }
    public List<DetalleVentaDTO> getDetalles()               { return detalles; }
    public void          setDetalles(List<DetalleVentaDTO> d){ this.detalles = d; }
}
