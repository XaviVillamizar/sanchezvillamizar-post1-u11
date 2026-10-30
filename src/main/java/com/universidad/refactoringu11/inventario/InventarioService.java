package com.universidad.refactoringu11.inventario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventarioService {
    private final List<Producto> productos = new ArrayList<>();
    private final List<String> movimientos = new ArrayList<>();
    
    private NotificadorBajoStock notificador; // Depende de la abstracción, sin importar reportes

    @Autowired(required = false)
    public void setNotificador(NotificadorBajoStock notificador) {
        this.notificador = notificador;
    }

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public String actualizarInventario(Long productoId, int cantidad, String tipoMovimiento) {
        if (esEntradaInvalida(productoId, cantidad, tipoMovimiento)) return "ERROR_DATOS_INVALIDOS";

        Producto producto = buscarProducto(productoId);
        if (producto == null) return "ERROR_PRODUCTO_NO_EXISTE";

        int nuevoStock = calcularNuevoStock(producto.getStock(), cantidad, tipoMovimiento);
        if (nuevoStock < 0) return "ERROR_STOCK_INSUFICIENTE";

        producto.setStock(nuevoStock);
        registrarMovimiento(producto, cantidad, tipoMovimiento, nuevoStock);

        return procesarPosibleReorden(producto, nuevoStock);
    }

    private boolean esEntradaInvalida(Long productoId, int cantidad, String tipoMovimiento) {
        return productoId == null || cantidad <= 0
                || (!tipoMovimiento.equals("ENTRADA") && !tipoMovimiento.equals("SALIDA"));
    }

    private Producto buscarProducto(Long productoId) {
        return productos.stream()
                .filter(p -> p.getId().equals(productoId))
                .findFirst()
                .orElse(null);
    }

    private int calcularNuevoStock(int stockActual, int cantidad, String tipoMovimiento) {
        return tipoMovimiento.equals("ENTRADA") ? stockActual + cantidad : stockActual - cantidad;
    }

    private void registrarMovimiento(Producto producto, int cantidad, String tipoMovimiento, int nuevoStock) {
        String registro = tipoMovimiento + " de " + cantidad + " unidades de "
                + producto.getNombre() + " -> stock final: " + nuevoStock;
        movimientos.add(registro);
        System.out.println(registro);
    }

    private String procesarPosibleReorden(Producto producto, int nuevoStock) {
        if (nuevoStock <= producto.getStockMinimo()) {
            int cantidadReorden = calcularCantidadReorden(producto.getStockMinimo(), nuevoStock);
            
            // Delegamos en la interfaz
            if (notificador != null) {
                notificador.notificar(producto, cantidadReorden);
            }

            System.out.println("ALERTA: " + producto.getNombre()
                    + " bajo stock minimo. Reordenar " + cantidadReorden + " unidades.");
            return "OK_BAJO_STOCK_REORDEN_" + cantidadReorden;
        }
        return "OK_" + nuevoStock;
    }

    private int calcularCantidadReorden(int stockMinimo, int nuevoStock) {
        return (stockMinimo * 3) - nuevoStock;
    }
}