package com.clubdeportivo.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registro inmutable de intento de ingreso por molinete o recepción.
 *
 * @author Joel
 * @author Merlina
 * @version 1.0.0
 */
public final class Asistencia {

    private final String id;
    private final String socioId;
    private final LocalDateTime fechaHora;
    private final TipoAcceso tipoAcceso;
    private final String claseId;
    private final EstadoAcceso estadoAcceso;
    private final String observacion;

    public Asistencia(String id, String socioId, LocalDateTime fechaHora,
                      TipoAcceso tipoAcceso, String claseId,
                      EstadoAcceso estadoAcceso, String observacion) {
        this.id = id;
        this.socioId = socioId;
        this.fechaHora = (fechaHora != null) ? fechaHora : LocalDateTime.now();
        this.tipoAcceso = (tipoAcceso != null) ? tipoAcceso : TipoAcceso.GENERAL;
        this.claseId = claseId;
        this.estadoAcceso = (estadoAcceso != null) ? estadoAcceso : EstadoAcceso.PERMITIDO;
        this.observacion = (observacion != null) ? observacion : "";
    }

    public boolean esAccesoValido() {
        return estadoAcceso == EstadoAcceso.PERMITIDO;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getSocioId() {
        return socioId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public TipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public String getClaseId() {
        return claseId;
    }

    public EstadoAcceso getEstadoAcceso() {
        return estadoAcceso;
    }

    public String getObservacion() {
        return observacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asistencia that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] Socio: %s | %s (%s)",
                estadoAcceso.esAprobado() ? "OK" : "DENEGADO", socioId, tipoAcceso, fechaHora);
    }
}
