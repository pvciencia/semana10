package edu.pe.utp.marcodesarrolloweb.ferrovoz.controller;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Categoria;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/mantenimiento")
public class MantenimientoController {

    @Autowired
    private CategoriaService categoriaService;

    @Value("${metalmax.nombre}")
    private String nombreTienda;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("nuevaCategoria", new Categoria());
        model.addAttribute("nombreTienda", nombreTienda);
        return "mantenimiento";
    }

    @PostMapping("/categorias/registrar")
    public String registrar(@ModelAttribute Categoria categoria, RedirectAttributes ra) {
        try {
            categoriaService.registrar(categoria);
            ra.addFlashAttribute("successMsg", "Categoría '" + categoria.getNombre() + "' registrada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/mantenimiento";
    }

    @PostMapping("/categorias/eliminar")
    public String eliminar(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            categoriaService.eliminar(id);
            ra.addFlashAttribute("successMsg", "Categoría eliminada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/mantenimiento";
    }
}
