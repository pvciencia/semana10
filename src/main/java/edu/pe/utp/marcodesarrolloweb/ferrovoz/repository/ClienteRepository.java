package edu.pe.utp.marcodesarrolloweb.ferrovoz.repository;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByDni(String dni);
    boolean existsByDni(String dni);
    boolean existsByDniAndIdNot(String dni, Integer id);
    List<Cliente> findByNombreContainingIgnoreCaseOrDniContaining(String nombre, String dni);
}
