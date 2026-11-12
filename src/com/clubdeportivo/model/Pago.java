package com.clubdeportivo.model;

import com.clubdeportivo.util.Formatters;
import java.time.LocalDateTime;

/**
 * Transacción económica formal emitida por cobro de membresías o servicios.
 *
 * @author Sofia Fernández (Integrante 4 - Finanzas & Facturación)
 * @version 1.0.0
 */
public class Pago {

    private String id;
    private String numeroComprobante;
    private String socioId;
    private double monto;
    private LocalDateTime fechaHoraPago;
    private MetodoPago metodoPago;
    private String concepto;

    public Pago(String id, String numeroComprobante, String socioId,
                double monto, LocalDateTime fechaHoraPago,
                MetodoPago metodoPago, String concepto) {
        this.id = id;
        this.numeroComprobante = numeroComprobante;
        this.socioId = socioId;
        this.monto = Math.max(0.0, monto);
        this.fechaHoraPago = (fechaHoraPago != null) ? fechaHoraPago : LocalDateTime.now();
        this.metodoPago = (metodoPago != null) ? metodoPago : MetodoPago.EFECTIVO;
        this.concepto = (concepto != null) ? concepto : "Cuota Social Mensual";
    }

    public String generarReciboImprimible() {
        return String.format("""
                ====================================================
                              FITMANAGE PRO - RECIBO OFICIAL
                ====================================================
                Comprobante N°: %s
                Fecha / Hora  : %s
                Socio Titular : %s
                Concepto      : %s
                Medio de Pago : %s
                ----------------------------------------------------
                MONTO TOTAL   : %s
                ====================================================
                """,
                numeroComprobante,
                Formatters.formatearFechaHora(fechaHoraPago),
                socioId,
                concepto,
                metodoPago.getDescripcion(),
                Formatters.formatearMoneda(monto));
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public String getSocioId() {
        return socioId;
    }

    public void setSocioId(String socioId) {
        this.socioId = socioId;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = Math.max(0.0, monto);
    }

    public LocalDateTime getFechaHoraPago() {
        return fechaHoraPago;
    }

    public void setFechaHoraPago(LocalDateTime fechaHoraPago) {
        this.fechaHoraPago = fechaHoraPago;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", numeroComprobante, Formatters.formatearMoneda(monto), concepto);
    }
}
