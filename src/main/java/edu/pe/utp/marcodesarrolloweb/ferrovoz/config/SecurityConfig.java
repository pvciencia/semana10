package edu.pe.utp.marcodesarrolloweb.ferrovoz.config;

import org.springframework.http.HttpMethod;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/", "/nosotros", "/contacto", "/login", "/acceso-denegado", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/img/**", "/webjars/**").permitAll()

                // Rutas de Inventario públicas (lectura)
                .requestMatchers(HttpMethod.GET, "/productos", "/productos/*").permitAll()

                // API JWT públicas
                .requestMatchers("/api/auth/**").permitAll()

                // API REST protegidas (requieren JWT de ADMIN)
                .requestMatchers("/api/**").hasRole("ADMIN")

                // Mantenimiento (solo ADMIN)
                .requestMatchers("/mantenimiento/**").hasRole("ADMIN")

                // Ventas (ADMIN y VENDEDOR)
                .requestMatchers("/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")

                // Inventario / Productos para operaciones de edición (ADMIN, VENDEDOR, ALMACENERO)
                .requestMatchers("/productos/**").hasAnyRole("ADMIN", "VENDEDOR", "ALMACENERO")

                // Cualquier otra requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/acceso-denegado")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
