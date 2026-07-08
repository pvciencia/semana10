package edu.pe.utp.marcodesarrolloweb.ferrovoz.repository;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByEstado(String estado);
    long countByEstado(String estado);

    @Query("SELECT v FROM Venta v JOIN v.cliente c WHERE " +
           "LOWER(c.nombre) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(c.dni) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(v.estado) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(v.metodoPago) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Venta> buscar(@Param("q") String q);
}
