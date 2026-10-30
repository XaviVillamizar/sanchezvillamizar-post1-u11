package com.universidad.refactoringu11.inventario;

public class EtiquetadoProducto {
    public String generarEtiqueta(Producto producto) {
        // El condicional desaparece; se delega polimórficamente al tipo
        return producto.getTipo().generarEtiqueta(producto.getNombre());
    }
}