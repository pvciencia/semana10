package edu.pe.utp.marcodesarrolloweb.ferrovoz.controller;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Venta;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.VentaService;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.ClienteRepository;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.ProductoRepository;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Producto;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired private VentaService     ventaService;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private ProductoRepository productoRepo;

    @Value("${metalmax.nombre}") private String nombreTienda;

    // ── GET /ventas/historial ──────────────────────────────
    @GetMapping("/historial")
    public String historial(@RequestParam(value = "q", required = false, defaultValue = "") String q,
                            Model model) {
        List<Venta> ventas;
        if (!q.isBlank()) {
            ventas = ventaService.buscar(q);
        } else {
            ventas = ventaService.listarTodos();
        }

        model.addAttribute("ventas",       ventas);
        model.addAttribute("q",            q);
        model.addAttribute("nombreTienda", nombreTienda);
        return "historial-ventas";
    }

    // ── GET /ventas/nuevo ─────────────────────────────────
    @GetMapping("/nuevo")
    public String nuevoFormulario(Model model) {
        model.addAttribute("clientes", clienteRepo.findAll());
        model.addAttribute("productos", productoRepo.findByStockGreaterThan(0));
        model.addAttribute("nombreTienda", nombreTienda);
        return "nuevo-venta"; // plantilla corregida
    }

    // ── POST /ventas/registrar (formulario simple de una línea) ──
    @PostMapping("/registrar")
    public String registrarVenta(@RequestParam(value = "clienteNombre", required = false) String clienteNombre,
                                 @RequestParam(value = "clienteDni", required = false) String clienteDni,
                                 @RequestParam("productoId") List<Integer> productoId,
                                 @RequestParam("cantidad") List<Integer> cantidad,
                                 @RequestParam(value = "metodo", required = false) String metodo,
                                 @RequestParam(value = "observaciones", required = false) String observaciones,
                                 Model model) {
        try {
            if (clienteNombre == null || clienteNombre.isBlank() || clienteDni == null || clienteDni.isBlank()) {
                throw new IllegalArgumentException("Debe indicar el nombre y el DNI de 8 dígitos del cliente.");
            }
            String trimDni = clienteDni.trim();
            if (!trimDni.matches("\\d{8}")) {
                throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos.");
            }

            Optional<Cliente> optCliente = clienteRepo.findByDni(trimDni);
            Cliente cliente;
            if (optCliente.isPresent()) {
                cliente = optCliente.get();
                if (!cliente.getNombre().equalsIgnoreCase(clienteNombre.trim())) {
                    cliente.setNombre(clienteNombre.trim());
                    clienteRepo.save(cliente);
                }
            } else {
                cliente = new Cliente();
                cliente.setNombre(clienteNombre.trim());
                cliente.setDni(trimDni);
                clienteRepo.save(cliente);
            }

            Venta ventaRegistrada = ventaService.registrarFromForm(cliente.getId(), productoId, cantidad, metodo, observaciones);
            model.addAttribute("v", ventaRegistrada);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("clientes", clienteRepo.findAll());
            model.addAttribute("productos", productoRepo.findByStockGreaterThan(0));
            model.addAttribute("nombreTienda", nombreTienda);
            return "nuevo-venta";
        }

        model.addAttribute("nombreTienda", nombreTienda);
        return "confirmacion-venta";
    }

    // ── POST /ventas/{id}/eliminar ────────────────────────
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            ventaService.eliminar(id);
            ra.addFlashAttribute("successMsg", "Venta #" + id + " eliminada.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ventas/historial";
    }
}
