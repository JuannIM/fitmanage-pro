package com.clubdeportivo.model;

/**
 * Disciplinas deportivas y de acondicionamiento físico ofrecidas en el club.
 *
 * @author Merlina
 * @author Rocio
 * @version 1.0.0
 */
public enum Disciplina {
    SPINNING("Spinning Indoor"),
    CROSSFIT("CrossFit WOD"),
    YOGA("Yoga Flow & Flex"),
    FUNCIONAL("Entrenamiento Funcional"),
    BOXEO("Power Boxeo"),
    PILATES("Pilates Mat");

    private final String nombreVisible;

    Disciplina(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    @Override
    public String toString() {
        return nombreVisible;
    }
}
