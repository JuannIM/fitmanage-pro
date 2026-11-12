package com.clubdeportivo.model;

/**
 * Excepción de regla de negocio lanzada cuando una clase grupal alcanza su capacidad máxima.
 *
 * @author Merlina
 * @version 1.0.0
 */
public class CupoAgotadoException extends Exception {

    private final String idClase;
    private final int cupoMaximo;

    public CupoAgotadoException(String idClase, String nombreClase, int cupoMaximo) {
        super(String.format("No es posible inscribir socios en '%s' (ID: %s). Se alcanzó el cupo máximo de %d alumnos.",
                nombreClase, idClase, cupoMaximo));
        this.idClase = idClase;
        this.cupoMaximo = cupoMaximo;
    }

    public String getIdClase() {
        return idClase;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }
}
