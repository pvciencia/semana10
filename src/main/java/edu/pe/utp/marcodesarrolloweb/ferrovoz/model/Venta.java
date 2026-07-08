package edu.pe.utp.marcodesarrolloweb.ferrovoz.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(length = 20)
    private String estado;  // "Pendiente" | "Confirmado" | "Cancelado"

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;  // "Efectivo" | "Tarjeta" | "Transferencia"

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {}

    // Helper Thymeleaf
    public String getEstadoBadgeClass() {
        return switch (estado == null ? "" : estado) {
            case "Confirmado" -> "bg-success";
            case "Pendiente"  -> "bg-warning text-dark";
            case "Cancelado"  -> "bg-danger";
            default           -> "bg-secondary";
        };
    }

    public Integer       getId()                        { return id; }
    public void          setId(Integer id)              { this.id = id; }
    public Cliente       getCliente()                   { return cliente; }
    public void          setCliente(Cliente c)          { this.cliente = c; }
    public Usuario       getUsuario()                   { return usuario; }
    public void          setUsuario(Usuario u)          { this.usuario = u; }
    public LocalDateTime getFecha()                     { return fecha; }
    public void          setFecha(LocalDateTime f)      { this.fecha = f; }
    public BigDecimal    getTotal()                     { return total; }
    public void          setTotal(BigDecimal t)         { this.total = t; }
    public String        getEstado()                    { return estado; }
    public void          setEstado(String e)            { this.estado = e; }
    public String        getMetodoPago()                { return metodoPago; }
    public void          setMetodoPago(String m)        { this.metodoPago = m; }
    public List<DetalleVenta> getDetalles()             { return detalles; }
    public void          setDetalles(List<DetalleVenta> d) { this.detalles = d; }
}
