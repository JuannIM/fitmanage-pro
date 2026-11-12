package com.clubdeportivo.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generador centralizado de identificadores alfanuméricos legibles y únicos.
 *
 * @author Juan Pérez (Integrante 1 - Arquitectura Core)
 * @author Camila Díaz (Integrante 6 - Persistencia & QA)
 * @version 1.0.0
 */
public final class IdGenerator {

    private static final AtomicInteger SOCIO_SEQ = new AtomicInteger(100);
    private static final AtomicInteger INSTRUCTOR_SEQ = new AtomicInteger(10);
    private static final AtomicInteger CLASE_SEQ = new AtomicInteger(100);
    private static final AtomicInteger RUTINA_SEQ = new AtomicInteger(100);
    private static final AtomicInteger EJERCICIO_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger PAGO_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger ASISTENCIA_SEQ = new AtomicInteger(10000);

    private IdGenerator() {
        // Constructor privado para clase utilitaria
    }

    public static String generarIdSocio() {
        return "SOC-" + SOCIO_SEQ.incrementAndGet();
    }

    public static String generarIdInstructor() {
        return "INS-" + INSTRUCTOR_SEQ.incrementAndGet();
    }

    public static String generarIdClase() {
        return "CLS-" + CLASE_SEQ.incrementAndGet();
    }

    public static String generarIdRutina() {
        return "RUT-" + RUTINA_SEQ.incrementAndGet();
    }

    public static String generarIdEjercicio() {
        return "EJE-" + EJERCICIO_SEQ.incrementAndGet();
    }

    public static String generarNumeroComprobante() {
        return "REC-2026-" + PAGO_SEQ.incrementAndGet();
    }

    public static String generarIdAsistencia() {
        return "ASI-" + ASISTENCIA_SEQ.incrementAndGet();
    }

    public static void actualizarSecuenciaSocio(int maxExistente) {
        if (maxExistente > SOCIO_SEQ.get()) {
            SOCIO_SEQ.set(maxExistente);
        }
    }
}
