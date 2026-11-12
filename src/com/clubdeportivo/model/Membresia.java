package com.clubdeportivo.model;

import java.time.LocalDate;

/**
 * Representa el contrato y plan de afiliación de un socio al club.
 *
 * @author Alfonsina
 * @version 1.0.0
 */
public class Membresia {

    private String id;
    private TipoMembresia tipo;
    private double precioBase;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private boolean renovacionAutomatica;

    public Membresia(String id, TipoMembresia tipo, double precioBase,
                     LocalDate fechaInicio, LocalDate fechaVencimiento, boolean renovacionAutomatica) {
        this.id = id;
        this.tipo = (tipo != null) ? tipo : TipoMembresia.MUSCULACION;
        this.precioBase = (precioBase > 0) ? precioBase : this.tipo.getPrecioBase();
        this.fechaInicio = (fechaInicio != null) ? fechaInicio : LocalDate.now();
        this.fechaVencimiento = (fechaVencimiento != null) ? fechaVencimiento : this.fechaInicio.plusMonths(1);
        this.renovacionAutomatica = renovacionAutomatica;
    }

    public boolean estaVencida() {
        if (fechaVencimiento == null) return false;
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    public void extenderPeriodo(int dias) {
        if (fechaVencimiento == null) {
            fechaVencimiento = LocalDate.now().plusDays(dias);
        } else if (estaVencida()) {
            fechaVencimiento = LocalDate.now().plusDays(dias);
        } else {
            fechaVencimiento = fechaVencimiento.plusDays(dias);
        }
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TipoMembresia getTipo() {
        return tipo;
    }

    public void setTipo(TipoMembresia tipo) {
        this.tipo = tipo;
        if (tipo != null) {
            this.precioBase = tipo.getPrecioBase();
        }
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public boolean isRenovacionAutomatica() {
        return renovacionAutomatica;
    }

    public void setRenovacionAutomatica(boolean renovacionAutomatica) {
        this.renovacionAutomatica = renovacionAutomatica;
    }

    @Override
    public String toString() {
        return String.format("%s (Vence: %s)", tipo.getNombre(), fechaVencimiento);
    }
}
