package edu.pe.utp.marcodesarrolloweb.ferrovoz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.CategoriaService;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.UsuarioRepository;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Usuario;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class FerrovozApplication {
    public static void main(String[] args) {
        SpringApplication.run(FerrovozApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(CategoriaService categoriaService, 
                                  JdbcTemplate jdbcTemplate, 
                                  UsuarioRepository usuarioRepository) {
        return args -> {
            // 1. Asegurar esquema de base de datos físico
            try {
                // Producto
                jdbcTemplate.execute("ALTER TABLE producto MODIFY COLUMN imagen LONGTEXT");
                
                // Usuario (columnas de perfil solicitadas)
                try { jdbcTemplate.execute("ALTER TABLE usuario ADD COLUMN nombre VARCHAR(100)"); } catch (Exception e) {}
                try { jdbcTemplate.execute("ALTER TABLE usuario ADD COLUMN apellido VARCHAR(100)"); } catch (Exception e) {}
                try { jdbcTemplate.execute("ALTER TABLE usuario ADD COLUMN dni VARCHAR(8)"); } catch (Exception e) {}
                try { jdbcTemplate.execute("ALTER TABLE usuario ADD COLUMN telefono VARCHAR(15)"); } catch (Exception e) {}
                try { jdbcTemplate.execute("ALTER TABLE usuario MODIFY COLUMN username VARCHAR(100)"); } catch (Exception e) {}
            } catch (Exception e) {
                System.err.println("Advertencia al actualizar el esquema físico de base de datos: " + e.getMessage());
            }

            // 2. Poblar categorías iniciales
            categoriaService.poblarCategoriasIniciales();

            // 3. Poblar cuentas de prueba iniciales con contraseñas encriptadas (BCrypt)
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            
            // Administrador
            if (usuarioRepository.findByUsername("admin@ferrovoz.pe").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setCorreo("admin@ferrovoz.pe");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRol("ADMIN");
                admin.setNombre("Administrador");
                admin.setApellido("General");
                admin.setDni("00000000");
                admin.setTelefono("999999999");
                usuarioRepository.save(admin);
            }
            
            // Vendedor
            if (usuarioRepository.findByUsername("vendedor@ferrovoz.pe").isEmpty()) {
                Usuario vendedor = new Usuario();
                vendedor.setCorreo("vendedor@ferrovoz.pe");
                vendedor.setPassword(encoder.encode("vendedor123"));
                vendedor.setRol("VENDEDOR");
                vendedor.setNombre("Juan");
                vendedor.setApellido("Vargas");
                vendedor.setDni("88888888");
                vendedor.setTelefono("988888888");
                usuarioRepository.save(vendedor);
            }
            
            // Almacenero
            if (usuarioRepository.findByUsername("almacenero@ferrovoz.pe").isEmpty()) {
                Usuario almacenero = new Usuario();
                almacenero.setCorreo("almacenero@ferrovoz.pe");
                almacenero.setPassword(encoder.encode("almacen123"));
                almacenero.setRol("ALMACENERO");
                almacenero.setNombre("Pedro");
                almacenero.setApellido("Martínez");
                almacenero.setDni("77777777");
                almacenero.setTelefono("977777777");
                usuarioRepository.save(almacenero);
            }
        };
    }
}
