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
                            @RequestParam(value = "estado", required = false, defaultValue = "") String estado,
                            Model model) {
        List<Venta> ventas;
        if (!q.isBlank()) {
            ventas = ventaService.buscar(q);
        } else if (!estado.isBlank()) {
            ventas = ventaService.buscar(estado);
        } else {
            ventas = ventaService.listarTodos();
        }

        model.addAttribute("ventas",          ventas);
        model.addAttribute("q",               q);
        model.addAttribute("estadoFiltro",    estado);
        model.addAttribute("totalPendientes", ventaService.contarPendientes());
        model.addAttribute("totalConfirm",    ventaService.contarConfirmados());
        model.addAttribute("totalCancel",     ventaService.contarCancelados());
        model.addAttribute("nombreTienda",    nombreTienda);
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
    public String registrarVenta(@RequestParam(value = "clienteId", required = false) Integer clienteId,
                                 @RequestParam(value = "clienteNombre", required = false) String clienteNombre,
                                 @RequestParam("productoId") List<Integer> productoId,
                                 @RequestParam("cantidad") List<Integer> cantidad,
                                 @RequestParam(value = "metodo", required = false) String metodo,
                                 @RequestParam(value = "observaciones", required = false) String observaciones,
                                 Model model) {
        Integer usadoClienteId = clienteId;
        try {
            if (usadoClienteId == null) {
                if (clienteNombre == null || clienteNombre.isBlank()) {
                    throw new IllegalArgumentException("Debe indicar el cliente (nombre o seleccionar uno existente).");
                }
                var encontrados = clienteRepo.findByNombreContainingIgnoreCaseOrDniContaining(clienteNombre, clienteNombre);
                if (!encontrados.isEmpty()) {
                    usadoClienteId = encontrados.get(0).getId();
                } else {
                    Cliente nuevo = new Cliente();
                    nuevo.setNombre(clienteNombre);
                    String dni = String.valueOf(System.currentTimeMillis());
                    if (dni.length() > 8) dni = dni.substring(dni.length() - 8);
                    while (clienteRepo.existsByDni(dni)) {
                        dni = String.valueOf(System.currentTimeMillis() % 100000000);
                        if (dni.length() > 8) dni = dni.substring(dni.length() - 8);
                    }
                    nuevo.setDni(dni);
                    clienteRepo.save(nuevo);
                    usadoClienteId = nuevo.getId();
                }
            }
            ventaService.registrarFromForm(usadoClienteId, productoId, cantidad, metodo, observaciones);
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            model.addAttribute("clientes", clienteRepo.findAll());
            model.addAttribute("productos", productoRepo.findByStockGreaterThan(0));
            model.addAttribute("nombreTienda", nombreTienda);
            return "nuevo-venta";
        }

        // Para la vista de confirmación mostramos los datos enviados
        var cliente = clienteRepo.findById(usadoClienteId).orElse(null);
        List<String> prodNombres = new java.util.ArrayList<>();
        for (int i = 0; i < productoId.size(); i++) {
            var p = productoRepo.findById(productoId.get(i)).orElse(null);
            prodNombres.add(p != null ? p.getNombre() : "-");
        }
        String productosStr = String.join(", ", prodNombres);
        Map<String,Object> venta = new HashMap<>();
        venta.put("cliente", cliente != null ? cliente.getNombre() : "-" );
        venta.put("producto", productosStr);
        venta.put("cantidad", cantidad);
        venta.put("metodo", metodo);
        venta.put("observaciones", observaciones);
        model.addAttribute("venta", venta);
        model.addAttribute("nombreTienda", nombreTienda);
        return "confirmacion-venta";
    }

    // ── POST /ventas/{id}/confirmar ───────────────────────
    @PostMapping("/{id}/confirmar")
    public String confirmar(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            ventaService.confirmar(id);
            ra.addFlashAttribute("successMsg", "Venta #" + id + " confirmada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ventas/historial";
    }

    // ── POST /ventas/{id}/cancelar ────────────────────────
    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            ventaService.cancelar(id);
            ra.addFlashAttribute("successMsg", "Venta #" + id + " cancelada.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ventas/historial";
    }

    // ── POST /ventas/{id}/estado ──────────────────────────
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable("id") Integer id,
                                @RequestParam("estado") String estado,
                                RedirectAttributes ra) {
        try {
            ventaService.actualizarEstado(id, estado);
            ra.addFlashAttribute("successMsg", "Estado de venta #" + id + " actualizado a " + estado + ".");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ventas/historial";
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
