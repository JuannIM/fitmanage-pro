package com.clubdeportivo.model;

import com.clubdeportivo.util.Formatters;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Transacción económica formal emitida por cobro de membresías o servicios.
 *
 * @author Alfonsina
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
        if (!Double.isFinite(monto) || monto <= 0.0) {
            throw new IllegalArgumentException("El monto del pago debe ser un número finito mayor a 0.");
        }
        this.id = id;
        this.numeroComprobante = numeroComprobante;
        this.socioId = socioId;
        this.monto = monto;
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
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del pago no puede ser nulo ni vacío.");
        }
        this.id = id.trim();
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        if (numeroComprobante == null || numeroComprobante.isBlank()) {
            throw new IllegalArgumentException("El número de comprobante no puede ser nulo ni vacío.");
        }
        this.numeroComprobante = numeroComprobante.trim();
    }

    public String getSocioId() {
        return socioId;
    }

    public void setSocioId(String socioId) {
        if (socioId == null || socioId.isBlank()) {
            throw new IllegalArgumentException("El ID del socio no puede ser nulo ni vacío.");
        }
        this.socioId = socioId.trim();
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        if (!Double.isFinite(monto) || monto <= 0.0) {
            throw new IllegalArgumentException("El monto del pago debe ser un número finito mayor a 0.");
        }
        this.monto = monto;
    }

    public LocalDateTime getFechaHoraPago() {
        return fechaHoraPago;
    }

    public void setFechaHoraPago(LocalDateTime fechaHoraPago) {
        if (fechaHoraPago == null) {
            throw new IllegalArgumentException("La fecha y hora de pago no pueden ser nulas.");
        }
        this.fechaHoraPago = fechaHoraPago;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        if (metodoPago == null) {
            throw new IllegalArgumentException("El método de pago no puede ser nulo.");
        }
        this.metodoPago = metodoPago;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        if (concepto == null || concepto.isBlank()) {
            throw new IllegalArgumentException("El concepto del pago no puede ser nulo ni vacío.");
        }
        this.concepto = concepto.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago pago)) return false;
        return Objects.equals(id, pago.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", numeroComprobante, Formatters.formatearMoneda(monto), concepto);
    }
}
