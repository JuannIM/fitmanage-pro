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
        String limpio = sanitizarDni(dni);
        return DNI_PATTERN.matcher(limpio).matches();
    }

    public static boolean esEmailValido(String email) {
        if (email == null) return false;
        String trimmed = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmed).matches() || trimmed.contains("..")) {
            return false;
        }
        int atIdx = trimmed.indexOf('@');
        String local = trimmed.substring(0, atIdx);
        String domain = trimmed.substring(atIdx + 1);
        return !local.startsWith(".") && !local.endsWith(".")
                && !domain.startsWith(".") && !domain.endsWith(".");
    }

    public static boolean esTelefonoValido(String telefono) {
        if (telefono == null) return false;
        String trimmed = telefono.trim();
        if (!TELEFONO_PATTERN.matcher(trimmed).matches()) {
            return false;
        }
        int digits = 0;
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            if (c >= '0' && c <= '9') {
                digits++;
            }
        }
        return digits >= 6 && digits <= 15;
    }

    public static String sanitizarDni(String dni) {
        if (dni == null) return "";
        return dni.trim().replace(".", "").replace("-", "");
    }
}
