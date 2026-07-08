package edu.pe.utp.marcodesarrolloweb.ferrovoz.repository;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByStockGreaterThan(int stock);
    List<Producto> findByCategoriaIgnoreCase(String categoria);
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
}
