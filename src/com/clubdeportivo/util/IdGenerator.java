package com.clubdeportivo.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generador centralizado de identificadores alfanuméricos legibles y únicos.
 *
 * @author Juan
 * @author Joel
 * @version 1.0.0
 */
public final class IdGenerator {

    private static final AtomicInteger SOCIO_SEQ = new AtomicInteger(100);
    private static final AtomicInteger INSTRUCTOR_SEQ = new AtomicInteger(10);
    private static final AtomicInteger CLASE_SEQ = new AtomicInteger(100);
    private static final AtomicInteger RUTINA_SEQ = new AtomicInteger(100);
    private static final AtomicInteger EJERCICIO_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger PAGO_SEQ = new AtomicInteger(1000);
    private static final AtomicInteger PAGO_ID_SEQ = new AtomicInteger(1000);
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

    public static String generarIdPago() {
        return "PAG-" + PAGO_ID_SEQ.incrementAndGet();
    }

    public static String generarIdAsistencia() {
        return "ASI-" + ASISTENCIA_SEQ.incrementAndGet();
    }

    public static void actualizarSecuenciaSocio(int maxExistente) {
        if (maxExistente > 0) {
            SOCIO_SEQ.accumulateAndGet(maxExistente, Math::max);
        }
    }

    public static void actualizarSecuenciaInstructor(int maxExistente) {
        if (maxExistente > 0) {
            INSTRUCTOR_SEQ.accumulateAndGet(maxExistente, Math::max);
        }
    }

    public static void actualizarSecuenciaClase(int maxExistente) {
        if (maxExistente > 0) {
            CLASE_SEQ.accumulateAndGet(maxExistente, Math::max);
        }
    }

    public static void actualizarSecuenciaRutina(int maxExistente) {
        if (maxExistente > 0) {
            RUTINA_SEQ.accumulateAndGet(maxExistente, Math::max);
        }
    }

    public static void actualizarSecuenciaEjercicio(int maxExistente) {
        if (maxExistente > 0) {
            EJERCICIO_SEQ.accumulateAndGet(maxExistente, Math::max);
        }
    }

    public static void actualizarSecuenciaPago(int maxExistente) {
        if (maxExistente > 0) {
            PAGO_SEQ.accumulateAndGet(maxExistente, Math::max);
        }
    }

    public static void actualizarSecuenciaIdPago(int maxExistente) {
        if (maxExistente > 0) {
            PAGO_ID_SEQ.accumulateAndGet(maxExistente, Math::max);
        }
    }

    public static void actualizarSecuenciaAsistencia(int maxExistente) {
        if (maxExistente > 0) {
            ASISTENCIA_SEQ.accumulateAndGet(maxExistente, Math::max);
        }
    }

    public static int extraerSufijoNumerico(String id, String prefijo) {
        if (id == null || prefijo == null || !id.startsWith(prefijo)) {
            return -1;
        }
        String sufijo = id.substring(prefijo.length());
        if (sufijo.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < sufijo.length(); i++) {
            char c = sufijo.charAt(i);
            if (c < '0' || c > '9') {
                return -1;
            }
        }
        try {
            return Integer.parseInt(sufijo);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
