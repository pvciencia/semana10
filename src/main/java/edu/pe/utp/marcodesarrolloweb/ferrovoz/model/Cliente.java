package edu.pe.utp.marcodesarrolloweb.ferrovoz.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(length = 15)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Column(length = 150)
    private String direccion;

    @Column(length = 20)
    private String estado = "Activo";

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<Venta> ventas = new ArrayList<>();

    public Cliente() {}

    // Helpers Thymeleaf
    public String getIniciales() {
        if (nombre != null && nombre.contains(",")) {
            String[] parts = nombre.split(",", 2);
            String apellido     = parts[0].trim();
            String primerNombre = parts[1].trim();
            if (!primerNombre.isEmpty() && !apellido.isEmpty()) {
                return String.valueOf(primerNombre.charAt(0)).toUpperCase()
                     + String.valueOf(apellido.charAt(0)).toUpperCase();
            }
        }
        return nombre != null
            ? nombre.substring(0, Math.min(2, nombre.length())).toUpperCase()
            : "?";
    }

    // getters/setters
    public Integer getId()                   { return id; }
    public void    setId(Integer id)         { this.id = id; }
    public String  getNombre()               { return nombre; }
    public void    setNombre(String n)       { this.nombre = n; }
    public String  getDni()                  { return dni; }
    public void    setDni(String d)          { this.dni = d; }
    public String  getTelefono()             { return telefono; }
    public void    setTelefono(String t)     { this.telefono = t; }
    public String  getCorreo()               { return correo; }
    public void    setCorreo(String c)       { this.correo = c; }
    public String  getDireccion()            { return direccion; }
    public void    setDireccion(String d)    { this.direccion = d; }
    public String  getEstado()               { return estado != null ? estado : "Activo"; }
    public void    setEstado(String e)       { this.estado = e; }
    public List<Venta> getVentas()           { return ventas; }
    public void    setVentas(List<Venta> v)  { this.ventas = v; }
}
