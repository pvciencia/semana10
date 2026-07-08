package edu.pe.utp.marcodesarrolloweb.ferrovoz.service;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.dto.ClienteDTO;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.exception.RecursoNoEncontradoException;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.exception.ReglaNegocioException;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.mapper.FerrovozMapper;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClienteService {

    @Autowired private ClienteRepository repository;
    @Autowired private FerrovozMapper    mapper;

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarTodos() {
        return mapper.toDTOListClientes(repository.findAll());
    }

    @Transactional(readOnly = true)
    public ClienteDTO buscarPorId(Long id) {
        return mapper.toDTO(repository.findById(id.intValue())
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + id)));
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> buscar(String termino) {
        if (termino == null || termino.isBlank()) return listarTodos();
        return mapper.toDTOListClientes(
            repository.findByNombreContainingIgnoreCaseOrDniContaining(termino, termino));
    }

    @Transactional
    public void registrar(ClienteDTO dto) {
        if (repository.existsByDni(dto.getDni())) {
            throw new ReglaNegocioException("Ya existe un cliente con DNI: " + dto.getDni());
        }
        repository.save(mapper.toEntity(dto));
    }

    @Transactional
    public void actualizar(ClienteDTO dto) {
        if (dto.getId() != null && repository.existsByDniAndIdNot(dto.getDni(), dto.getId().intValue())) {
            throw new ReglaNegocioException("El DNI " + dto.getDni() + " ya está registrado en otro cliente.");
        }
        repository.save(mapper.toEntity(dto));
    }

    @Transactional
    public void cambiarEstado(Long id, String estado) {
        var cliente = repository.findById(id.intValue())
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + id));
        cliente.setEstado(estado);
        repository.save(cliente);
    }

    @Transactional
    public void eliminar(Long id) {
        repository.findById(id.intValue())
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + id));
        repository.deleteById(id.intValue());
    }
}
