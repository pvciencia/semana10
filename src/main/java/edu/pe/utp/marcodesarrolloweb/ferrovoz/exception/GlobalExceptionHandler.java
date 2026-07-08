package edu.pe.utp.marcodesarrolloweb.ferrovoz.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String handleNoEncontrado(RecursoNoEncontradoException ex, Model model) {
        model.addAttribute("errorMsg", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public String handleReglaNegocio(ReglaNegocioException ex, Model model) {
        model.addAttribute("errorMsg", ex.getMessage());
        return "redirect:/ventas/historial";
    }
}
