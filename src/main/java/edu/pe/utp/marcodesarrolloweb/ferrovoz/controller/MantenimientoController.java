package edu.pe.utp.marcodesarrolloweb.ferrovoz.controller;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Categoria;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Usuario;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.dto.UsuarioRegistroDTO;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.CategoriaService;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.service.UsuarioService;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mantenimiento")
public class MantenimientoController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${metalmax.nombre}")
    private String nombreTienda;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("nuevaCategoria", new Categoria());
        model.addAttribute("nuevoUsuario", new UsuarioRegistroDTO());
        model.addAttribute("usuarios", usuarioRepository.findAll());
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

    @PostMapping("/usuarios/registrar")
    public String registrarUsuario(@Valid @ModelAttribute("nuevoUsuario") UsuarioRegistroDTO dto,
                                   BindingResult result,
                                   RedirectAttributes ra) {
        if (result.hasErrors()) {
            String errores = result.getFieldErrors().stream()
                .map(e -> e.getDefaultMessage())
                .collect(Collectors.joining(". "));
            ra.addFlashAttribute("errorMsg", errores);
            return "redirect:/mantenimiento";
        }
        try {
            usuarioService.registrar(dto);
            ra.addFlashAttribute("successMsg", "Usuario \"" + dto.getCorreo() + "\" registrado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/mantenimiento";
    }

    @PostMapping("/usuarios/eliminar")
    public String eliminarUsuario(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            usuarioRepository.deleteById(id);
            ra.addFlashAttribute("successMsg", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/mantenimiento";
    }

    @PostMapping("/usuarios/editar")
    public String editarUsuario(@RequestParam("id") Integer id,
                                @RequestParam("rol") String rol,
                                @RequestParam("nombre") String nombre,
                                @RequestParam("apellido") String apellido,
                                @RequestParam("dni") String dni,
                                @RequestParam("telefono") String telefono,
                                @RequestParam(value = "password", required = false) String password,
                                RedirectAttributes ra) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            usuario.setRol(rol);
            usuario.setNombre(nombre.trim());
            usuario.setApellido(apellido.trim());
            usuario.setDni(dni.trim());
            usuario.setTelefono(telefono.trim());

            if (password != null && !password.isBlank()) {
                usuario.setPassword(passwordEncoder.encode(password));
            }
            usuarioRepository.save(usuario);
            ra.addFlashAttribute("successMsg", "Usuario \"" + usuario.getCorreo() + "\" actualizado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Error al actualizar usuario: " + e.getMessage());
        }
        return "redirect:/mantenimiento";
    }
}
