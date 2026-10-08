package edu.pe.utp.marcodesarrolloweb.ferrovoz.service;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Usuario;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.dto.UsuarioRegistroDTO;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registrar(UsuarioRegistroDTO dto) {
        if (usuarioRepository.findByUsername(dto.getCorreo()).isPresent()) {
            throw new IllegalArgumentException("El correo ya está registrado por otro usuario.");
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getCorreo().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setRol(dto.getRol());
        usuario.setNombre(dto.getNombre().trim());
        usuario.setApellido(dto.getApellido().trim());
        usuario.setDni(dto.getDni().trim());
        usuario.setTelefono(dto.getTelefono().trim());

        usuarioRepository.save(usuario);
    }
}
