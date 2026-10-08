package edu.pe.utp.marcodesarrolloweb.ferrovoz.service;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.UsuarioRepository;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + usuario.getRol())
        );

        return new CustomUserDetails(
            usuario.getUsername(),
            usuario.getPassword(),
            true,
            authorities,
            usuario.getNombreCompleto(),
            usuario.getRol()
        );
    }
}
