package edu.pe.utp.marcodesarrolloweb.ferrovoz.security;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.UsuarioDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_PREFIX      = "/api/";
    private static final String API_AUTH_PREFIX = "/api/auth/";

    @Autowired private JwtUtil               jwtUtil;
    @Autowired private UsuarioDetailsService usuarioDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith(API_PREFIX) || path.startsWith(API_AUTH_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            enviarError(response, request, "Token no proporcionado");
            return;
        }

        final String token = authHeader.substring(7);
        final String username;
        try {
            username = jwtUtil.extraerUsername(token);
        } catch (ExpiredJwtException e) {
            enviarError(response, request, "Token expirado");
            return;
        } catch (JwtException | IllegalArgumentException e) {
            enviarError(response, request, "Token invalido");
            return;
        }

        final UserDetails userDetails;
        try {
            userDetails = usuarioDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            enviarError(response, request, "Usuario no valido");
            return;
        }

        if (!jwtUtil.esValido(token, userDetails)) {
            enviarError(response, request, "Token invalido");
            return;
        }

        var authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    private void enviarError(HttpServletResponse response,
            HttpServletRequest request, String mensaje) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = """
            {"timestamp":"%s","status":%d,"error":"Forbidden","message":"%s","path":"%s"}"""
            .formatted(LocalDateTime.now(), HttpServletResponse.SC_FORBIDDEN,
                       mensaje, request.getRequestURI());
        response.getWriter().write(json);
    }
}
