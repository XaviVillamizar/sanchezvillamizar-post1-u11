package com.universidad.refactoringu11.inventario;

public class ClasificadorProducto {
    public double calcularRecargoEnvio(Producto producto) {
        // El condicional desaparece; se delega polimórficamente al tipo
        return producto.getTipo().calcularRecargoEnvio(producto.getPrecio());
    }
}