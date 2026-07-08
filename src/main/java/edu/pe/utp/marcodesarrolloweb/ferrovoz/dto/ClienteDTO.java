package edu.pe.utp.marcodesarrolloweb.ferrovoz.dto;

import jakarta.validation.constraints.*;

public class ClienteDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    private String dni;

    private String email;      // mapea a correo en la entidad
    private String telefono;
    private String direccion;
    private String estado;     // campo virtual (no está en la tabla cliente nueva)

    public ClienteDTO() {}

    public String getEstadoBadgeClass() {
        return switch (estado == null ? "Activo" : estado) {
            case "Activo"    -> "bg-success";
            case "Pendiente" -> "bg-warning text-dark";
            default          -> "bg-secondary";
        };
    }

    public Long   getId()                        { return id; }
    public void   setId(Long id)                 { this.id = id; }
    public String getNombre()                    { return nombre; }
    public void   setNombre(String n)            { this.nombre = n; }
    public String getDni()                       { return dni; }
    public void   setDni(String d)               { this.dni = d; }
    public String getEmail()                     { return email; }
    public void   setEmail(String e)             { this.email = e; }
    public String getTelefono()                  { return telefono; }
    public void   setTelefono(String t)          { this.telefono = t; }
    public String getDireccion()                 { return direccion; }
    public void   setDireccion(String d)         { this.direccion = d; }
    public String getEstado()                    { return estado != null ? estado : "Activo"; }
    public void   setEstado(String e)            { this.estado = e; }
}
