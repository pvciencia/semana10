package edu.pe.utp.marcodesarrolloweb.ferrovoz.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 20)
    private String rol; // "ADMIN", "VENDEDOR"

    public Usuario() {}

    public Integer getId()            { return id; }
    public void    setId(Integer id)  { this.id = id; }
    public String  getUsername()                  { return username; }
    public void    setUsername(String username)    { this.username = username; }
    public String  getPassword()                  { return password; }
    public void    setPassword(String password)   { this.password = password; }
    public String  getRol()                       { return rol; }
    public void    setRol(String rol)             { this.rol = rol; }
}
