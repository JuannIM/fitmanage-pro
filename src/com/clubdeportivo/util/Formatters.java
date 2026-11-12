package com.clubdeportivo.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilitario para el formateo homogéneo de fechas, horas y montos monetarios.
 *
 * @author Sofia Fernández (Integrante 4 - Finanzas & Facturación)
 * @author Nicolás Martínez (Integrante 5 - UI/UX & Presentación)
 * @version 1.0.0
 */
public final class Formatters {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final DecimalFormat CURRENCY_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "AR"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("$ #,##0.00", symbols);
    }

    private Formatters() {
    }

    public static String formatearFecha(LocalDate fecha) {
        if (fecha == null) return "-";
        return fecha.format(DATE_FORMATTER);
    }

    public static String formatearHora(LocalTime hora) {
        if (hora == null) return "-";
        return hora.format(TIME_FORMATTER);
    }

    public static String formatearFechaHora(LocalDateTime fechaHora) {
        if (fechaHora == null) return "-";
        return fechaHora.format(DATE_TIME_FORMATTER);
    }

    public static String formatearMoneda(double monto) {
        synchronized (CURRENCY_FORMAT) {
            return CURRENCY_FORMAT.format(monto);
        }
    }
}
