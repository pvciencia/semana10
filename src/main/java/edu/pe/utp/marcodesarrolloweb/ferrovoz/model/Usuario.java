package edu.pe.utp.marcodesarrolloweb.ferrovoz.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100, unique = true)
    private String username; // se almacena el correo electrónico

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 20)
    private String rol; // "ADMIN", "VENDEDOR", "ALMACENERO"

    @Column(length = 100)
    private String nombre;

    @Column(length = 100)
    private String apellido;

    @Column(length = 8)
    private String dni;

    @Column(length = 15)
    private String telefono;

    public Usuario() {}

    // Alias para el correo
    public String getCorreo() { return username; }
    public void setCorreo(String correo) { this.username = correo; }

    // Helpers
    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "");
    }

    public Integer getId()            { return id; }
    public void    setId(Integer id)  { this.id = id; }
    public String  getUsername()                  { return username; }
    public void    setUsername(String username)    { this.username = username; }
    public String  getPassword()                  { return password; }
    public void    setPassword(String password)   { this.password = password; }
    public String  getRol()                       { return rol; }
    public void    setRol(String rol)             { this.rol = rol; }
    public String  getNombre()                    { return nombre; }
    public void    setNombre(String nombre)       { this.nombre = nombre; }
    public String  getApellido()                  { return apellido; }
    public void    setApellido(String apellido)   { this.apellido = apellido; }
    public String  getDni()                       { return dni; }
    public void    setDni(String dni)             { this.dni = dni; }
    public String  getTelefono()                  { return telefono; }
    public void    setTelefono(String telefono)   { this.telefono = telefono; }
}
