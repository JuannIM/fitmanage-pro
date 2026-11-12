package com.clubdeportivo.model;

/**
 * Puntos de control de ingreso al predio deportivo.
 *
 * @author Lucas Rodríguez (Integrante 3 - Logística & Clases)
 * @author Camila Díaz (Integrante 6 - Persistencia & QA)
 * @version 1.0.0
 */
public enum TipoAcceso {
    GENERAL("Acceso General al Predio / Musculación"),
    CLASE_GRUPAL("Acceso a Sala de Clase Grupal");

    private final String descripcion;

    TipoAcceso(String descripcion) {
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
