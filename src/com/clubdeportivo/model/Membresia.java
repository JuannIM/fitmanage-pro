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

    public boolean estaEnPeriodoGracia(int diasGracia) {
        if (fechaVencimiento == null) return false;
        LocalDate hoy = LocalDate.now();
        return estaVencida() && !hoy.isAfter(fechaVencimiento.plusDays(Math.max(0, diasGracia)));
    }

    public void extenderPeriodo(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días de extensión deben ser mayores a 0.");
        }
        if (fechaVencimiento == null || estaVencida()) {
            LocalDate hoy = LocalDate.now();
            this.fechaInicio = hoy;
            this.fechaVencimiento = hoy.plusDays(dias);
        } else {
            this.fechaVencimiento = fechaVencimiento.plusDays(dias);
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
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de membresía no puede ser nulo.");
        }
        this.tipo = tipo;
        this.precioBase = tipo.getPrecioBase();
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        if (!Double.isFinite(precioBase) || precioBase <= 0.0) {
            throw new IllegalArgumentException("El precio base debe ser un número finito mayor a 0.");
        }
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
