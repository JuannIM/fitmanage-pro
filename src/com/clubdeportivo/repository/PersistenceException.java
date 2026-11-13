package com.clubdeportivo.repository;

/**
 * Excepción de tiempo de ejecución lanzada ante fallos de lectura, escritura
 * o deserialización en la capa de persistencia.
 */
public class PersistenceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
