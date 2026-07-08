package edu.pe.utp.marcodesarrolloweb.ferrovoz.controller;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.dto.ClienteDTO;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired private ClienteService clienteService;
    @Value("${metalmax.nombre}") private String nombreTienda;

    @GetMapping
    public String listar(@RequestParam(value = "q", required = false, defaultValue = "") String q, Model model) {
        List<ClienteDTO> clientes = q.isBlank()
            ? clienteService.listarTodos()
            : clienteService.buscar(q);
        model.addAttribute("clientes",     clientes);
        model.addAttribute("busqueda",     q);
        model.addAttribute("nombreTienda", nombreTienda);
        return "clientes";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente",      new ClienteDTO());
        model.addAttribute("nombreTienda", nombreTienda);
        model.addAttribute("accion",       "nuevo");
        return "clientes";
    }

    @PostMapping("/registrar")
    public String registrar(@ModelAttribute ClienteDTO dto, RedirectAttributes ra) {
        try {
            clienteService.registrar(dto);
            ra.addFlashAttribute("successMsg", "Cliente registrado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/clientes";
    }

    @PostMapping("/editar")
    public String editar(@ModelAttribute ClienteDTO dto, RedirectAttributes ra) {
        try {
            clienteService.actualizar(dto);
            ra.addFlashAttribute("successMsg", "Cliente actualizado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/clientes";
    }

    @PostMapping("/estado")
    public String cambiarEstado(@RequestParam("id") Long id, @RequestParam("estado") String estado, RedirectAttributes ra) {
        try {
            clienteService.cambiarEstado(id, estado);
            ra.addFlashAttribute("successMsg", "Estado del cliente actualizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/clientes";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam("id") Long id, RedirectAttributes ra) {
        try {
            clienteService.eliminar(id);
            ra.addFlashAttribute("successMsg", "Cliente eliminado.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/clientes";
    }
}
