package com.clubdeportivo.util;

import java.util.regex.Pattern;

/**
 * Validaciones exhaustivas de cadenas, identificadores fiscales y formatos de contacto.
 *
 * @author Juan
 * @author Joel
 * @version 1.0.0
 */
public final class ValidationUtils {

    private static final Pattern DNI_PATTERN = Pattern.compile("^\\d{7,8}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^[0-9+()\\-\\s]{6,20}$");

    private ValidationUtils() {
    }

    public static boolean esDniValido(String dni) {
        if (dni == null) return false;
        String limpio = dni.trim().replace(".", "");
        return DNI_PATTERN.matcher(limpio).matches();
    }

    public static boolean esEmailValido(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null) return false;
        return TELEFONO_PATTERN.matcher(telefono.trim()).matches();
    }

    public static String sanitizarDni(String dni) {
        if (dni == null) return "";
        return dni.trim().replace(".", "").replace("-", "");
    }
}
