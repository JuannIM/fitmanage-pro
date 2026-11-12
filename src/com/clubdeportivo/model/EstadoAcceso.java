package com.clubdeportivo.model;

/**
 * Dictamen emitido por el sistema de molinete / recepción tras validar las credenciales.
 *
 * @author Joel
 * @author Juan
 * @version 1.0.0
 */
public enum EstadoAcceso {
    PERMITIDO("Acceso Permitido", true),
    DENEGADO_CUOTA_VENCIDA("Acceso Denegado: Cuota Vencida", false),
    DENEGADO_SIN_CUPO("Acceso Denegado: Sala sin Cupo Disponible", false),
    DENEGADO_MEMBRESIA_INSUFICIENTE("Acceso Denegado: Membresía no autoriza esta actividad", false),
    DENEGADO_NO_ENCONTRADO("Acceso Denegado: Socio no registrado", false);

    private final String mensaje;
    private final boolean aprobado;

    EstadoAcceso(String mensaje, boolean aprobado) {
        this.mensaje = mensaje;
        this.aprobado = aprobado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean esAprobado() {
        return aprobado;
    }

    @Override
    public String toString() {
        return mensaje;
    }
}
