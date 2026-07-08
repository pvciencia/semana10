package edu.pe.utp.marcodesarrolloweb.ferrovoz.service;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Categoria;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Producto;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.CategoriaRepository;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepo;

    @Autowired
    private ProductoRepository productoRepo;

    public List<Categoria> listarTodas() {
        return categoriaRepo.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
    }

    @Transactional
    public Categoria registrar(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío.");
        }
        String nombreLimpio = categoria.getNombre().trim();
        if (categoriaRepo.existsByNombreIgnoreCase(nombreLimpio)) {
            throw new IllegalArgumentException("La categoría '" + nombreLimpio + "' ya existe.");
        }
        categoria.setNombre(nombreLimpio);
        return categoriaRepo.save(categoria);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!categoriaRepo.existsById(id)) {
            throw new IllegalArgumentException("La categoría con ID " + id + " no existe.");
        }
        categoriaRepo.deleteById(id);
    }

    @Transactional
    public void poblarCategoriasIniciales() {
        if (categoriaRepo.count() == 0) {
            Set<String> nombresCategorias = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

            // 1. Intentar cargar categorías de los productos existentes en base de datos
            List<Producto> productos = productoRepo.findAll();
            if (!productos.isEmpty()) {
                productos.stream()
                        .map(Producto::getCategoria)
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(c -> !c.isEmpty())
                        .forEach(nombresCategorias::add);
            }

            // 2. Si no hay categorías de productos, agregar categorías por defecto
            if (nombresCategorias.isEmpty()) {
                nombresCategorias.add("Herramientas");
                nombresCategorias.add("Eléctrico");
                nombresCategorias.add("Plomería");
                nombresCategorias.add("Pinturas");
                nombresCategorias.add("Fijaciones");
                nombresCategorias.add("Construcción");
            }

            // 3. Guardar todas las categorías en la base de datos
            for (String nombre : nombresCategorias) {
                categoriaRepo.save(new Categoria(nombre));
            }
        }
    }
}
