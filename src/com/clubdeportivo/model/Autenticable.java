package com.clubdeportivo.model;

/**
 * Contrato de seguridad y control de acceso físico a las instalaciones del club.
 *
 * @author Juan
 * @author Joel
 * @version 1.0.0
 */
public interface Autenticable {

    /**
     * Valida la identidad mediante una credencial (DNI, código QR o N° de Socio).
     */
    boolean autenticar(String credencial);

    /**
     * Comprueba los permisos de ingreso a un sector físico determinado.
     */
    boolean validarAcceso(String puntoControl);

    /**
     * Actualiza la clave o credencial asignada.
     */
    default void actualizarClave(String nuevaClave) {
        // Implementación por defecto extensible
    }
}
