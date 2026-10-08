package edu.pe.utp.marcodesarrolloweb.ferrovoz.controller;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.dto.ProductoDTO;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    @Value("${metalmax.nombre}")
    private String nombreTienda;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // ── GET /productos  ─────────────────────────────────────────────────────
    @GetMapping
    public String listar(@RequestParam(value = "categoria", required = false) String categoria,
                         @RequestParam(value = "q", required = false) String q,
                         Model model) {

        List<ProductoDTO> lista;
        String categoriaActiva = "Todos";

        if (categoria != null && !categoria.isBlank()) {
            lista = productoService.listarPorCategoria(categoria);
            categoriaActiva = categoria;
        } else if (q != null && !q.isBlank()) {
            lista = productoService.buscarPorNombre(q);
            categoriaActiva = "Búsqueda: " + q;
        } else {
            lista = productoService.listarTodos();
        }

        model.addAttribute("productos",      lista);
        model.addAttribute("totalProductos", productoService.contarTotal());
        model.addAttribute("stockBajo",      productoService.contarStockBajo());
        model.addAttribute("categoriaActiva", categoriaActiva);
        model.addAttribute("nombreTienda",   nombreTienda);
        model.addAttribute("categorias",     productoService.listarCategorias());
        return "productos";
    }

    // ── GET /productos/{id}  ─────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String detalle(@PathVariable("id") int id,
                          Model model,
                          RedirectAttributes ra) {
        Optional<ProductoDTO> opt = productoService.buscarPorId(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Producto con ID " + id + " no encontrado.");
            return "redirect:/productos";
        }
        model.addAttribute("producto",     opt.get());
        model.addAttribute("nombreTienda", nombreTienda);
        return "detalle-producto";
    }

    // ── POST /productos/registrar  ───────────────────────────────────────────
    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute ProductoDTO producto,
                            BindingResult result,
                            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                            RedirectAttributes ra) {

        // Segunda capa: Spring Validator
        if (result.hasErrors()) {
            String errores = result.getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(". "));
            ra.addFlashAttribute("errorMsg", errores);
            return "redirect:/productos";
        }

        // Procesar imagen si se subió
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String base64 = java.util.Base64.getEncoder().encodeToString(imagenFile.getBytes());
                String dataUrl = "data:" + imagenFile.getContentType() + ";base64," + base64;
                producto.setImagen(dataUrl);
            } catch (IOException e) {
                ra.addFlashAttribute("errorMsg", "Error al procesar la imagen: " + e.getMessage());
                return "redirect:/productos";
            }
        }

        productoService.registrar(producto);
        ra.addFlashAttribute("successMsg",
                "Producto \"" + producto.getNombre() + "\" registrado correctamente.");
        return "redirect:/productos";
    }

    // ── POST /productos/{id}/actualizar  ────────────────────────────────────
    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable("id") int id,
                             @Valid @ModelAttribute ProductoDTO producto,
                             BindingResult result,
                             @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
                             RedirectAttributes ra) {

        // Segunda capa: Spring Validator
        if (result.hasErrors()) {
            String errores = result.getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(". "));
            ra.addFlashAttribute("errorMsg", errores);
            return "redirect:/productos";
        }

        // Procesar imagen si se subió
        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String base64 = java.util.Base64.getEncoder().encodeToString(imagenFile.getBytes());
                String dataUrl = "data:" + imagenFile.getContentType() + ";base64," + base64;
                producto.setImagen(dataUrl);
            } catch (IOException e) {
                ra.addFlashAttribute("errorMsg", "Error al procesar la imagen: " + e.getMessage());
                return "redirect:/productos";
            }
        }

        boolean actualizado = productoService.actualizar(id, producto).isPresent();
        if (actualizado) {
            ra.addFlashAttribute("successMsg",
                    "Producto \"" + producto.getNombre() + "\" actualizado correctamente.");
        } else {
            ra.addFlashAttribute("errorMsg", "No se encontró el producto con ID " + id + ".");
        }
        return "redirect:/productos";
    }

    // ── POST /productos/{id}/eliminar  ──────────────────────────────────────
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes ra) {
        try {
            boolean eliminado = productoService.eliminar(id);
            if (eliminado) {
                ra.addFlashAttribute("successMsg", "Producto eliminado correctamente.");
            } else {
                ra.addFlashAttribute("errorMsg", "No se encontró el producto con ID " + id + ".");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "No se puede eliminar el producto porque tiene ventas registradas asociadas.");
        }
        return "redirect:/productos";
    }

    // ── POST /productos/{id}/agregar-stock  ─────────────────────────────────
    @PostMapping("/{id}/agregar-stock")
    public String agregarStock(@PathVariable("id") int id,
                               @RequestParam("cantidadStock") int cantidadStock,
                               RedirectAttributes ra) {
        if (cantidadStock <= 0) {
            ra.addFlashAttribute("errorMsg", "La cantidad a sumar debe ser mayor que cero.");
            return "redirect:/productos";
        }
        Optional<ProductoDTO> opt = productoService.buscarPorId(id);
        if (opt.isPresent()) {
            ProductoDTO p = opt.get();
            int nuevoStock = (p.getStock() != null ? p.getStock() : 0) + cantidadStock;
            p.setStock(nuevoStock);
            productoService.actualizar(id, p);
            ra.addFlashAttribute("successMsg", "Se agregaron " + cantidadStock + " unidades al stock de \"" + p.getNombre() + "\".");
        } else {
            ra.addFlashAttribute("errorMsg", "No se encontró el producto con ID " + id + ".");
        }
        return "redirect:/productos";
    }
}
