package com.clubdeportivo.model;

import java.time.LocalDateTime;

/**
 * Registro inmutable de intento de ingreso por molinete o recepción.
 *
 * @author Joel
 * @author Merlina
 * @version 1.0.0
 */
public class Asistencia {

    private String id;
    private String socioId;
    private LocalDateTime fechaHora;
    private TipoAcceso tipoAcceso;
    private String claseId;
    private EstadoAcceso estadoAcceso;
    private String observacion;

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

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSocioId() {
        return socioId;
    }

    public void setSocioId(String socioId) {
        this.socioId = socioId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public TipoAcceso getTipoAcceso() {
        return tipoAcceso;
    }

    public void setTipoAcceso(TipoAcceso tipoAcceso) {
        this.tipoAcceso = tipoAcceso;
    }

    public String getClaseId() {
        return claseId;
    }

    public void setClaseId(String claseId) {
        this.claseId = claseId;
    }

    public EstadoAcceso getEstadoAcceso() {
        return estadoAcceso;
    }

    public void setEstadoAcceso(EstadoAcceso estadoAcceso) {
        this.estadoAcceso = estadoAcceso;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Override
    public String toString() {
        return String.format("[%s] Socio: %s | %s (%s)",
                estadoAcceso.esAprobado() ? "OK" : "DENEGADO", socioId, tipoAcceso, fechaHora);
    }
}
