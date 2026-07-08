package edu.pe.utp.marcodesarrolloweb.ferrovoz.service;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Producto;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.ProductoRepository;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Categoria;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private CategoriaRepository categoriaRepo;

    public List<Producto> listarTodos() {
        return productoRepo.findAll();
    }

    public List<Producto> listarDisponibles() {
        return productoRepo.findByStockGreaterThan(0);
    }

    public List<Producto> listarPorCategoria(String categoria) {
        return productoRepo.findByCategoriaIgnoreCase(categoria);
    }

    public List<Producto> buscarPorNombre(String texto) {
        return productoRepo.findByNombreContainingIgnoreCase(texto);
    }

    public List<String> listarCategorias() {
        return categoriaRepo.findAll().stream()
                .map(Categoria::getNombre)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<Producto> buscarPorId(int id) {
        return productoRepo.findById(id);
    }

    public long contarTotal() {
        return productoRepo.count();
    }

    public long contarStockBajo() {
        return productoRepo.findAll().stream().filter(p -> p.getStock() != null && p.getStock() > 0 && p.getStock() <= 5).count();
    }

    public long contarSinStock() {
        return productoRepo.findAll().stream().filter(p -> p.getStock() != null && p.getStock() == 0).count();
    }

    @Transactional
    public Producto registrar(Producto p) {
        if (p.getImagen() == null || p.getImagen().isBlank()) {
            p.setImagen("/img/productos/sin-imagen.jpg");
        }
        if (p.getPrecio() == null) p.setPrecio(BigDecimal.ZERO);
        return productoRepo.save(p);
    }

    @Transactional
    public Optional<Producto> actualizar(int id, Producto datos) {
        return productoRepo.findById(id).map(p -> {
            p.setNombre(datos.getNombre());
            p.setDescripcion(datos.getDescripcion());
            p.setCategoria(datos.getCategoria());
            p.setPrecio(datos.getPrecio());
            p.setStock(datos.getStock());
            p.setUnidad(datos.getUnidad());
            if (datos.getImagen() != null && !datos.getImagen().isBlank()) {
                p.setImagen(datos.getImagen());
            }
            return productoRepo.save(p);
        });
    }

    @Transactional
    public boolean eliminar(int id) {
        if (productoRepo.existsById(id)) {
            productoRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
