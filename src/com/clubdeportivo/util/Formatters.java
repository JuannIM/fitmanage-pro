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
 * @author Alfonsina
 * @author Sara
 * @version 1.0.0
 */
public final class Formatters {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final DecimalFormat CURRENCY_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("es", "AR"));
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

    public static String formatearPesoKg(double kg) {
        return String.format(Locale.US, "%.1f kg", kg);
    }
}
