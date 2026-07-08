package edu.pe.utp.marcodesarrolloweb.ferrovoz.service;

import edu.pe.utp.marcodesarrolloweb.ferrovoz.dto.VentaDTO;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.exception.RecursoNoEncontradoException;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.model.*;
import edu.pe.utp.marcodesarrolloweb.ferrovoz.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    @Autowired private VentaRepository    ventaRepo;
    @Autowired private ClienteRepository  clienteRepo;
    @Autowired private ProductoRepository productoRepo;

    @Transactional(readOnly = true)
    public List<Venta> listarTodos() {
        return ventaRepo.findAll();
    }

    @Transactional(readOnly = true)
    public List<Venta> buscar(String q) {
        if (q == null || q.isBlank()) return ventaRepo.findAll();
        return ventaRepo.buscar(q.trim());
    }

    @Transactional(readOnly = true)
    public Venta buscarPorId(Integer id) {
        return ventaRepo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada: " + id));
    }

    @Transactional
    public void registrar(VentaDTO dto) {
        Cliente cliente = clienteRepo.findById(dto.getClienteId())
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        Venta v = new Venta();
        v.setCliente(cliente);
        v.setFecha(LocalDateTime.now());
        v.setTotal(dto.getTotal() != null ? dto.getTotal() : BigDecimal.ZERO);
        v.setEstado("Confirmado");
        v.setMetodoPago(dto.getMetodoPago());
        ventaRepo.save(v);
    }

    @Transactional
    public Venta registrarFromForm(Integer clienteId, java.util.List<Integer> productoIds, java.util.List<Integer> cantidades, String metodo, String observaciones) {
        if (productoIds == null || productoIds.isEmpty()) throw new IllegalArgumentException("Debe indicar al menos un producto.");
        if (cantidades == null || productoIds.size() != cantidades.size()) throw new IllegalArgumentException("Productos y cantidades no coinciden.");
        
        Cliente cliente = clienteRepo.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));

        // Consolidar cantidades por producto id para evitar procesar duplicados por separado
        java.util.Map<Integer, Integer> consolidados = new java.util.HashMap<>();
        for (int i = 0; i < productoIds.size(); i++) {
            Integer pid = productoIds.get(i);
            if (pid == null) continue;
            int cant = (cantidades.get(i) == null) ? 0 : cantidades.get(i);
            if (cant <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
            }
            consolidados.put(pid, consolidados.getOrDefault(pid, 0) + cant);
        }

        if (consolidados.isEmpty()) throw new IllegalArgumentException("Debe indicar al menos un producto válido.");

        Venta v = new Venta();
        v.setCliente(cliente);
        v.setFecha(LocalDateTime.now());
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        v.setEstado("Confirmado");
        v.setMetodoPago(metodo);
        java.util.List<DetalleVenta> detalles = new java.util.ArrayList<>();

        for (java.util.Map.Entry<Integer, Integer> entry : consolidados.entrySet()) {
            Integer pid = entry.getKey();
            int cant = entry.getValue();

            Producto producto = productoRepo.findById(pid)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + pid));

            Integer stockActual = producto.getStock() != null ? producto.getStock() : 0;
            if (cant > stockActual) {
                throw new edu.pe.utp.marcodesarrolloweb.ferrovoz.exception.ReglaNegocioException(
                    "Stock insuficiente para el producto \"" + producto.getNombre() + "\". Stock disponible: " + stockActual + ", cantidad solicitada: " + cant
                );
            }

            DetalleVenta dv = new DetalleVenta();
            dv.setProducto(producto);
            dv.setCantidad(cant);
            dv.setPrecio(producto.getPrecio());
            dv.setVenta(v);
            detalles.add(dv);

            java.math.BigDecimal line = (producto.getPrecio() != null ? producto.getPrecio() : java.math.BigDecimal.ZERO)
                .multiply(java.math.BigDecimal.valueOf(cant));
            total = total.add(line);

            producto.setStock(stockActual - cant);
            productoRepo.save(producto);
        }

        v.setTotal(total);
        v.setDetalles(detalles);
        return ventaRepo.save(v);
    }

    @Transactional
    public void eliminar(Integer id) {
        Venta v = buscarPorId(id);
        // Siempre devolvemos el stock correspondiente al eliminarla, ya que toda venta registrada es confirmada
        for (DetalleVenta dv : v.getDetalles()) {
            Producto p = dv.getProducto();
            p.setStock((p.getStock() != null ? p.getStock() : 0) + dv.getCantidad());
            productoRepo.save(p);
        }
        ventaRepo.delete(v);
    }
}
