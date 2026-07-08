package edu.pe.utp.marcodesarrolloweb.ferrovoz.mapper;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.dto.*;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.*;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FerrovozMapper {

    // ── Cliente ─────────────────────────────────────────────

    public ClienteDTO toDTO(Cliente c) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(c.getId() != null ? c.getId().longValue() : null);
        dto.setNombre(c.getNombre());
        dto.setDni(c.getDni());
        dto.setEmail(c.getCorreo());
        dto.setTelefono(c.getTelefono());
        dto.setDireccion(c.getDireccion());
        dto.setEstado(c.getEstado());
        return dto;
    }

    public Cliente toEntity(ClienteDTO dto) {
        Cliente c = new Cliente();
        if (dto.getId() != null) c.setId(dto.getId().intValue());
        c.setNombre(dto.getNombre());
        c.setDni(dto.getDni());
        c.setCorreo(dto.getEmail());
        c.setTelefono(dto.getTelefono());
        c.setDireccion(dto.getDireccion());
        c.setEstado(dto.getEstado() != null ? dto.getEstado() : "Activo");
        return c;
    }

    public List<ClienteDTO> toDTOListClientes(List<Cliente> list) {
        return list.stream().map(this::toDTO).toList();
    }

    // Mapeos de Pedido eliminados — usar Venta y DetalleVenta

    // ── Venta ───────────────────────────────────────────────

    public VentaDTO toDTO(Venta v) {
        VentaDTO dto = new VentaDTO();
        dto.setId(v.getId());
        if (v.getCliente() != null) {
            dto.setClienteId(v.getCliente().getId());
            dto.setClienteNombre(v.getCliente().getNombre());
            dto.setClienteDni(v.getCliente().getDni());
        }
        if (v.getUsuario() != null) dto.setUsuarioId(v.getUsuario().getId());
        dto.setFecha(v.getFecha());
        dto.setTotal(v.getTotal());
        dto.setEstado(v.getEstado());
        dto.setMetodoPago(v.getMetodoPago());
        return dto;
    }

    public List<VentaDTO> toDTOListVentas(List<Venta> list) {
        return list.stream().map(this::toDTO).toList();
    }
}
