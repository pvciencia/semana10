package edu.pe.utp.marcodesarrolloweb.ferrovoz.controller;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.security.JwtUtil;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.UsuarioDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private UsuarioDetailsService usuarioDetailsService;
    @Autowired private JwtUtil               jwtUtil;

    public record LoginRequest(String username, String password) {}
    public record LoginResponse(String token, String username, List<String> roles) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(), request.password()));
            UserDetails userDetails =
                usuarioDetailsService.loadUserByUsername(request.username());
            String token = jwtUtil.generarToken(userDetails);
            return ResponseEntity.ok(new LoginResponse(
                token, userDetails.getUsername(),
                userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).toList()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Credenciales incorrectas"));
        }
    }
}
