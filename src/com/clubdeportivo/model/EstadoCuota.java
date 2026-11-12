package com.clubdeportivo.model;

/**
 * Estados posibles de la cuota mensual de un socio del club.
 *
 * @author Juan
 * @author Alfonsina
 * @version 1.0.0
 */
public enum EstadoCuota {
    AL_DIA("Al Día"),
    PENDIENTE("Pendiente"),
    VENCIDO("Vencido"),
    INACTIVO("Inactivo");

    private final String etiqueta;

    EstadoCuota(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
