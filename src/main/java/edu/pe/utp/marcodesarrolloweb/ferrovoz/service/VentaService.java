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
        v.setEstado("Pendiente");
        v.setMetodoPago(dto.getMetodoPago());
        ventaRepo.save(v);
    }

    @Transactional
    public void registrarFromForm(Integer clienteId, java.util.List<Integer> productoIds, java.util.List<Integer> cantidades, String metodo, String observaciones) {
        if (productoIds == null || productoIds.isEmpty()) throw new IllegalArgumentException("Debe indicar al menos un producto.");
        if (cantidades == null || productoIds.size() != cantidades.size()) throw new IllegalArgumentException("Productos y cantidades no coinciden.");
        Cliente cliente = clienteRepo.findById(clienteId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado"));
        Venta v = new Venta();
        v.setCliente(cliente);
        v.setFecha(LocalDateTime.now());
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        v.setEstado("Pendiente");
        v.setMetodoPago(metodo);
        java.util.List<DetalleVenta> detalles = new java.util.ArrayList<>();
        for (int i = 0; i < productoIds.size(); i++) {
            Integer pid = productoIds.get(i);
            int cant = (cantidades.get(i) == null) ? 0 : cantidades.get(i);
            Producto producto = productoRepo.findById(pid)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + pid));
            DetalleVenta dv = new DetalleVenta();
            dv.setProducto(producto);
            dv.setCantidad(cant);
            dv.setPrecio(producto.getPrecio());
            dv.setVenta(v);
            detalles.add(dv);
            java.math.BigDecimal line = (producto.getPrecio() != null ? producto.getPrecio() : java.math.BigDecimal.ZERO).multiply(java.math.BigDecimal.valueOf(cant));
            total = total.add(line);
            Integer stockActual = producto.getStock() != null ? producto.getStock() : 0;
            producto.setStock(Math.max(0, stockActual - cant));
            productoRepo.save(producto);
        }
        v.setTotal(total);
        v.setDetalles(detalles);
        ventaRepo.save(v);
    }

    @Transactional
    public void confirmar(Integer id) {
        Venta v = buscarPorId(id);
        if ("Cancelado".equals(v.getEstado()))
            throw new IllegalStateException("No se puede confirmar una venta cancelada.");
        v.setEstado("Confirmado");
        ventaRepo.save(v);
    }

    @Transactional
    public void cancelar(Integer id) {
        Venta v = buscarPorId(id);
        if ("Confirmado".equals(v.getEstado()))
            throw new IllegalStateException("No se puede cancelar una venta ya confirmada.");
        v.setEstado("Cancelado");
        ventaRepo.save(v);
    }

    @Transactional
    public void actualizarEstado(Integer id, String estado) {
        Venta v = buscarPorId(id);
        v.setEstado(estado);
        ventaRepo.save(v);
    }

    @Transactional
    public void eliminar(Integer id) {
        ventaRepo.findById(id)
            .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada: " + id));
        ventaRepo.deleteById(id);
    }

    public long contarPendientes()  { return ventaRepo.countByEstado("Pendiente"); }
    public long contarConfirmados() { return ventaRepo.countByEstado("Confirmado"); }
    public long contarCancelados()  { return ventaRepo.countByEstado("Cancelado"); }
}
