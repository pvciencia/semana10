package edu.pe.utp.marcodesarrolloweb.ferrovoz;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapear /img/productos/** a la carpeta uploads/img/productos del directorio de
        // trabajo
        Path uploadDir = Path.of(System.getProperty("user.dir"), "uploads", "img", "productos");
        String uploadPath = uploadDir.toUri().toString();
        registry.addResourceHandler("/img/productoss/**")
                .addResourceLocations(uploadPath);
    }
}
