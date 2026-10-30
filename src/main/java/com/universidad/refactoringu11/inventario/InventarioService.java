package com.universidad.refactoringu11.inventario;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventarioService {
    private final List<Producto> productos = new ArrayList<>();
    private final List<String> movimientos = new ArrayList<>();

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    // El método principal ahora actúa como un orquestador limpio
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

    // 1. Responsabilidad: Validación
    private boolean esEntradaInvalida(Long productoId, int cantidad, String tipoMovimiento) {
        return productoId == null || cantidad <= 0
                || (!tipoMovimiento.equals("ENTRADA") && !tipoMovimiento.equals("SALIDA"));
    }

    // 2. Responsabilidad: Búsqueda
    private Producto buscarProducto(Long productoId) {
        return productos.stream()
                .filter(p -> p.getId().equals(productoId))
                .findFirst()
                .orElse(null);
    }

    // 3. Responsabilidad: Regla de actualización
    private int calcularNuevoStock(int stockActual, int cantidad, String tipoMovimiento) {
        return tipoMovimiento.equals("ENTRADA") ? stockActual + cantidad : stockActual - cantidad;
    }

    // 4. Responsabilidad: Auditoría
    private void registrarMovimiento(Producto producto, int cantidad, String tipoMovimiento, int nuevoStock) {
        String registro = tipoMovimiento + " de " + cantidad + " unidades de "
                + producto.getNombre() + " -> stock final: " + nuevoStock;
        movimientos.add(registro);
        System.out.println(registro);
    }

    // 5. Responsabilidad: Alertas
    private String procesarPosibleReorden(Producto producto, int nuevoStock) {
        if (nuevoStock <= producto.getStockMinimo()) {
            int cantidadReorden = calcularCantidadReorden(producto.getStockMinimo(), nuevoStock);
            System.out.println("ALERTA: " + producto.getNombre()
                    + " bajo stock minimo. Reordenar " + cantidadReorden + " unidades.");
            return "OK_BAJO_STOCK_REORDEN_" + cantidadReorden;
        }
        return "OK_" + nuevoStock;
    }

    // 6. Responsabilidad: Cálculo de reorden (Decisión de Diseño 1)
    private int calcularCantidadReorden(int stockMinimo, int nuevoStock) {
        return (stockMinimo * 3) - nuevoStock;
    }
}