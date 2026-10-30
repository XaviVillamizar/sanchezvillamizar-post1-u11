package com.universidad.refactoringu11.reportes;

import com.universidad.refactoringu11.inventario.InventarioService;
import com.universidad.refactoringu11.inventario.NotificadorBajoStock;
import com.universidad.refactoringu11.inventario.Producto;
import org.springframework.stereotype.Service;

@Service
public class ReporteBajoStockService implements NotificadorBajoStock {
    private final InventarioService inventarioService;

    public ReporteBajoStockService(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @Override
    public void notificar(Producto producto, int cantidadReorden) {
        // Registra la alerta silenciosamente para incluirla en el reporte luego
        System.out.println("Reportes [INTERNO]: Registrando alerta de " + producto.getNombre());
    }

    public String generarReporte() {
        return "Reporte de bajo stock pendiente de implementar";
    }
}