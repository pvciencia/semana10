package edu.pe.utp.marcodesarrolloweb.ferrovoz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.CategoriaService;

@SpringBootApplication
public class FerrovozApplication {
    public static void main(String[] args) {
        SpringApplication.run(FerrovozApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(CategoriaService categoriaService) {
        return args -> {
            categoriaService.poblarCategoriasIniciales();
        };
    }
}
