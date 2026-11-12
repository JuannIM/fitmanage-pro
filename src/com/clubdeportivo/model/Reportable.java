package com.clubdeportivo.model;

import java.util.Map;

/**
 * Contrato de auditoría e inteligencia de negocio para exportación de métricas.
 *
 * @author Juan Pérez (Integrante 1 - Arquitectura Core)
 * @author Nicolás Martínez (Integrante 5 - UI/UX & Presentación)
 * @version 1.0.0
 */
public interface Reportable {

    /**
     * Genera un informe textual estructurado apto para consola o impresión.
     */
    String generarReporteTexto();

    /**
     * Exporta los indicadores cuantitativos en un mapa de claves y valores.
     */
    Map<String, Object> exportarMetricas();
}
