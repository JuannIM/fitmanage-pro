package com.clubdeportivo.model;

/**
 * Grupos musculares anatómicos para clasificación de ejercicios y dosificación de cargas.
 *
 * @author María Gomez (Integrante 2 - Entrenamiento & Dominio Deportivo)
 * @version 1.0.0
 */
public enum GrupoMuscular {
    PECHO("Pecho / Pectorales"),
    ESPALDA("Espalda / Dorsales"),
    PIERNAS("Piernas / Cuádriceps e Isquios"),
    HOMBROS("Hombros / Deltoides"),
    BRAZOS("Brazos / Bíceps y Tríceps"),
    CORE("Core / Zona Media y Abdominales");

    private final String descripcion;

    GrupoMuscular(String descripcion) {
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
