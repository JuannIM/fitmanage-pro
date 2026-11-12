package com.clubdeportivo.model;

/**
 * Medios de pago autorizados en la administración contable del club.
 *
 * @author Alfonsina
 * @version 1.0.0
 */
public enum MetodoPago {
    EFECTIVO("Efectivo en Caja"),
    TARJETA_DEBITO("Tarjeta de Débito"),
    TARJETA_CREDITO("Tarjeta de Crédito"),
    TRANSFERENCIA("Transferencia Bancaria");

    private final String descripcion;

    MetodoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
