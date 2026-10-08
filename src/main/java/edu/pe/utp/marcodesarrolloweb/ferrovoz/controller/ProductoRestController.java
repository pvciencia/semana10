package edu.pe.utp.marcodesarrolloweb.ferrovoz.controller;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.dto.ProductoDTO;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<ProductoDTO> listar() {
        return productoService.listarTodos();
    }
}
