package edu.pe.utp.marcodesarrolloweb.ferrovoz.controller;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.ProductoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class HomeController {

    private final ProductoService productoService;

    @Value("${metalmax.nombre}")
    private String nombreTienda;

    @Value("${metalmax.contacto}")
    private String contacto;

    @Value("${metalmax.telefono}")
    private String telefono;

    @Value("${metalmax.direccion}")
    private String direccion;

    public HomeController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /** Datos comunes a varias vistas que no provienen del modelo de negocio */
    private void agregarDatosTienda(Model model) {
        model.addAttribute("nombreTienda", nombreTienda);
        model.addAttribute("contacto",    contacto);
        model.addAttribute("telefono",    telefono);
        model.addAttribute("direccion",   direccion);
        model.addAttribute("anio",        LocalDate.now().getYear());
    }

    @GetMapping("/")
    public String index(Model model) {
        agregarDatosTienda(model);
        model.addAttribute("totalProductos", productoService.contarTotal());
        model.addAttribute("stockBajo",      productoService.contarStockBajo());
        model.addAttribute("sinStock",       productoService.contarSinStock());
        return "index";
    }

    @GetMapping("/contacto")
    public String contacto(Model model) {
        agregarDatosTienda(model);
        return "contacto";
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        agregarDatosTienda(model);
        return "nosotros";
    }

    @GetMapping("/login")
    public String login(Model model) {
        agregarDatosTienda(model);
        return "login";
    }
}
