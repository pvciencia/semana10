package edu.pe.utp.marcodesarrolloweb.ferrovoz.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class CustomUserDetails extends User {

    private final String nombreCompleto;
    private final String rol;

    public CustomUserDetails(String username, String password, boolean enabled,
                             Collection<? extends GrantedAuthority> authorities,
                             String nombreCompleto, String rol) {
        super(username, password, enabled, true, true, true, authorities);
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getRol() {
        return rol;
    }
}
