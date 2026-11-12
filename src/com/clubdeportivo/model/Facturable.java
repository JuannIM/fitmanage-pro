package com.clubdeportivo.model;

/**
 * Contrato de negocio para entidades sujetas a liquidación contable,
 * cálculo de aranceles y emisión de comprobantes fiscales.
 *
 * @author Sofia Fernández (Integrante 4 - Finanzas & Facturación)
 * @author Juan Pérez (Integrante 1 - Arquitectura Core)
 * @version 1.0.0
 */
public interface Facturable {

    /**
     * Calcula el monto monetario total a abonar considerando tarifas y bonificaciones.
     */
    double calcularMontoTotal();

    /**
     * Emite la constancia o comprobante formal de la transacción en texto legible.
     */
    String generarComprobante();

    /**
     * Determina si la cuenta se encuentra regularizada administrativamente.
     */
    boolean estaAlDia();

    /**
     * Aplica un porcentaje de descuento extraordinario sobre el valor base.
     *
     * @param porcentaje valor entre 0.0 y 100.0
     */
    void aplicarDescuento(double porcentaje);
}
