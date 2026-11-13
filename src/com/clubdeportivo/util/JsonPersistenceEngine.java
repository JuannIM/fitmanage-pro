package com.clubdeportivo.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Motor de persistencia JSON autónomo y de alto rendimiento sin librerías externas.
 * Implementa serialización estructurada con formato legible (pretty-print),
 * parseo recursivo y escritura atómica sobre el sistema de archivos.
 *
 * @author Joel
 * @author Juan
 * @version 1.0.0
 */
public final class JsonPersistenceEngine {

    private static final ConcurrentHashMap<Path, ReentrantReadWriteLock> FILE_LOCKS = new ConcurrentHashMap<>();

    private JsonPersistenceEngine() {
    }

    private static ReentrantReadWriteLock lockFor(Path path) {
        return FILE_LOCKS.computeIfAbsent(path.toAbsolutePath().normalize(), p -> new ReentrantReadWriteLock());
    }

    /**
     * Serializa un objeto (Map, List, tipos primitivos) a una cadena JSON formateada.
     */
    public static String toJson(Object value) {
        StringBuilder sb = new StringBuilder();
        serialize(value, sb, 0);
        return sb.toString();
    }

    private static void serialize(Object value, StringBuilder sb, int indent) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String s) {
            sb.append('"').append(escapeString(s)).append('"');
        } else if (value instanceof Double d) {
            if (!Double.isFinite(d)) {
                throw new IllegalArgumentException("No se puede serializar Double no finito: " + d);
            }
            sb.append(d);
        } else if (value instanceof Float f) {
            if (!Float.isFinite(f)) {
                throw new IllegalArgumentException("No se puede serializar Float no finito: " + f);
            }
            sb.append(f);
        } else if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
        } else if (value instanceof Map<?, ?> map) {
            serializeMap(map, sb, indent);
        } else if (value instanceof Collection<?> coll) {
            serializeCollection(coll, sb, indent);
        } else if (value.getClass().isArray()) {
            serializeCollection(Arrays.asList((Object[]) value), sb, indent);
        } else {
            // Representación de cadena por defecto
            sb.append('"').append(escapeString(value.toString())).append('"');
        }
    }

    private static void serializeMap(Map<?, ?> map, StringBuilder sb, int indent) {
        if (map.isEmpty()) {
            sb.append("{}");
            return;
        }
        sb.append("{\n");
        int nextIndent = indent + 2;
        String indentStr = " ".repeat(nextIndent);
        int i = 0;
        int size = map.size();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            sb.append(indentStr);
            sb.append('"').append(escapeString(String.valueOf(entry.getKey()))).append("\": ");
            serialize(entry.getValue(), sb, nextIndent);
            if (++i < size) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(" ".repeat(indent)).append("}");
    }

    private static void serializeCollection(Collection<?> coll, StringBuilder sb, int indent) {
        if (coll.isEmpty()) {
            sb.append("[]");
            return;
        }
        sb.append("[\n");
        int nextIndent = indent + 2;
        String indentStr = " ".repeat(nextIndent);
        int i = 0;
        int size = coll.size();
        for (Object item : coll) {
            sb.append(indentStr);
            serialize(item, sb, nextIndent);
            if (++i < size) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(" ".repeat(indent)).append("]");
    }

    private static String escapeString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * Parsea una cadena JSON retornando Map, List, String, Number, Boolean o null.
     */
    public static Object parseJson(String json) {
        if (json == null) return null;
        JsonParser parser = new JsonParser(json);
        return parser.parse();
    }

    /**
     * Guarda atómicamente un objeto serializado a disco.
     */
    public static void saveToFile(Path path, Object data) throws IOException {
        Path normalized = path.toAbsolutePath().normalize();
        ReentrantReadWriteLock.WriteLock writeLock = lockFor(normalized).writeLock();
        writeLock.lock();
        try {
            Path parent = normalized.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            String jsonContent = toJson(data);
            Path tempDir = (parent != null) ? parent : Paths.get(".").toAbsolutePath().normalize();
            Path tempPath = Files.createTempFile(tempDir, normalized.getFileName().toString() + ".", ".tmp");
            try {
                Files.writeString(tempPath, jsonContent, StandardCharsets.UTF_8,
                        StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                try {
                    Files.move(tempPath, normalized, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(tempPath, normalized, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(tempPath);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Lee un archivo JSON y lo deserializa.
     */
    public static Object readFromFile(Path path) throws IOException {
        Path normalized = path.toAbsolutePath().normalize();
        ReentrantReadWriteLock.ReadLock readLock = lockFor(normalized).readLock();
        readLock.lock();
        try {
            if (!Files.exists(normalized) || Files.size(normalized) == 0) {
                return null;
            }
            String content = Files.readString(normalized, StandardCharsets.UTF_8);
            return parseJson(content);
        } finally {
            readLock.unlock();
        }
    }

    // =========================================================================
    // PARSER DESCENDENTE RECURSIVO
    // =========================================================================

    private static class JsonParser {
        private final String src;
        private int pos = 0;
        private final int len;

        JsonParser(String src) {
            this.src = src;
            this.len = src.length();
        }

        Object parse() {
            skipWhitespace();
            if (pos >= len) {
                return null;
            }
            Object result = parseValue();
            skipWhitespace();
            if (pos < len) {
                throw new IllegalArgumentException("Caracteres inesperados tras el fin del JSON en pos " + pos);
            }
            return result;
        }

        private void skipWhitespace() {
            while (pos < len) {
                char c = src.charAt(pos);
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    pos++;
                } else {
                    break;
                }
            }
        }

        private Object parseValue() {
            skipWhitespace();
            if (pos >= len) {
                throw new IllegalArgumentException("Se esperaba un valor JSON en pos " + pos);
            }
            char c = src.charAt(pos);
            return switch (c) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't', 'f' -> parseBoolean();
                case 'n' -> parseNull();
                default -> parseNumber();
            };
        }

        private Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            pos++; // Consumir '{'
            skipWhitespace();
            if (pos < len && src.charAt(pos) == '}') {
                pos++;
                return map;
            }
            while (true) {
                skipWhitespace();
                if (pos >= len || src.charAt(pos) != '"') {
                    throw new IllegalArgumentException("Se esperaba clave entre comillas en pos " + pos);
                }
                String key = parseString();
                skipWhitespace();
                if (pos >= len || src.charAt(pos) != ':') {
                    throw new IllegalArgumentException("Se esperaba ':' tras clave en pos " + pos);
                }
                pos++; // Consumir ':'
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                if (pos >= len) {
                    throw new IllegalArgumentException("Objeto JSON sin cerrar en pos " + pos);
                }
                char next = src.charAt(pos);
                if (next == ',') {
                    pos++;
                    skipWhitespace();
                    if (pos >= len || src.charAt(pos) == '}') {
                        throw new IllegalArgumentException("Coma final inesperada en objeto JSON en pos " + pos);
                    }
                } else if (next == '}') {
                    pos++;
                    return map;
                } else {
                    throw new IllegalArgumentException("Se esperaba ',' o '}' en pos " + pos);
                }
            }
        }

        private List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            pos++; // Consumir '['
            skipWhitespace();
            if (pos < len && src.charAt(pos) == ']') {
                pos++;
                return list;
            }
            while (true) {
                Object item = parseValue();
                list.add(item);
                skipWhitespace();
                if (pos >= len) {
                    throw new IllegalArgumentException("Arreglo JSON sin cerrar en pos " + pos);
                }
                char next = src.charAt(pos);
                if (next == ',') {
                    pos++;
                    skipWhitespace();
                    if (pos >= len || src.charAt(pos) == ']') {
                        throw new IllegalArgumentException("Coma final inesperada en arreglo JSON en pos " + pos);
                    }
                } else if (next == ']') {
                    pos++;
                    return list;
                } else {
                    throw new IllegalArgumentException("Se esperaba ',' o ']' en pos " + pos);
                }
            }
        }

        private String parseString() {
            pos++; // Consumir '"' inicial
            StringBuilder sb = new StringBuilder();
            while (pos < len) {
                char c = src.charAt(pos++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c == '\\') {
                    if (pos >= len) {
                        throw new IllegalArgumentException("Escape incompleto al final del JSON en pos " + pos);
                    }
                    char esc = src.charAt(pos++);
                    switch (esc) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/' -> sb.append('/');
                        case 'b' -> sb.append('\b');
                        case 'f' -> sb.append('\f');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        case 'u' -> {
                            if (pos + 4 > len) {
                                throw new IllegalArgumentException("Escape unicode incompleto en pos " + pos);
                            }
                            for (int i = 0; i < 4; i++) {
                                if (!isHexDigit(src.charAt(pos + i))) {
                                    throw new IllegalArgumentException("Dígito hexadecimal inválido en escape unicode en pos " + (pos + i));
                                }
                            }
                            String hex = src.substring(pos, pos + 4);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                        }
                        default -> throw new IllegalArgumentException("Escape inválido '\\" + esc + "' en pos " + (pos - 1));
                    }
                } else {
                    sb.append(c);
                }
            }
            throw new IllegalArgumentException("Cadena JSON sin cerrar al final del contenido en pos " + pos);
        }

        private static boolean isHexDigit(char c) {
            return (c >= '0' && c <= '9')
                    || (c >= 'a' && c <= 'f')
                    || (c >= 'A' && c <= 'F');
        }

        private Boolean parseBoolean() {
            if (src.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            } else if (src.startsWith("false", pos)) {
                pos += 5;
                return Boolean.FALSE;
            }
            throw new IllegalArgumentException("Token booleano inválido en pos " + pos);
        }

        private Object parseNull() {
            if (src.startsWith("null", pos)) {
                pos += 4;
                return null;
            }
            throw new IllegalArgumentException("Token nulo inválido en pos " + pos);
        }

        private Number parseNumber() {
            int start = pos;
            if (pos < len && src.charAt(pos) == '-') {
                pos++;
            }
            if (pos >= len) {
                throw new IllegalArgumentException("Número JSON inválido en pos " + start);
            }
            char firstDigit = src.charAt(pos);
            if (firstDigit == '0') {
                pos++;
                if (pos < len && isAsciiDigit(src.charAt(pos))) {
                    throw new IllegalArgumentException("Cero inicial inválido en número JSON en pos " + start);
                }
            } else if (firstDigit >= '1' && firstDigit <= '9') {
                pos++;
                while (pos < len && isAsciiDigit(src.charAt(pos))) {
                    pos++;
                }
            } else {
                throw new IllegalArgumentException("Token numérico inválido en pos " + start);
            }

            boolean isFloating = false;
            if (pos < len && src.charAt(pos) == '.') {
                isFloating = true;
                pos++;
                if (pos >= len || !isAsciiDigit(src.charAt(pos))) {
                    throw new IllegalArgumentException("Punto decimal sin dígitos en pos " + pos);
                }
                while (pos < len && isAsciiDigit(src.charAt(pos))) {
                    pos++;
                }
            }

            if (pos < len && (src.charAt(pos) == 'e' || src.charAt(pos) == 'E')) {
                isFloating = true;
                pos++;
                if (pos < len && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) {
                    pos++;
                }
                if (pos >= len || !isAsciiDigit(src.charAt(pos))) {
                    throw new IllegalArgumentException("Exponente numérico inválido en pos " + pos);
                }
                while (pos < len && isAsciiDigit(src.charAt(pos))) {
                    pos++;
                }
            }

            String numStr = src.substring(start, pos);
            if (isFloating) {
                double d = Double.parseDouble(numStr);
                if (!Double.isFinite(d)) {
                    throw new IllegalArgumentException("Número JSON fuera de rango en pos " + start);
                }
                return d;
            }
            try {
                long val = Long.parseLong(numStr);
                if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
                    return (int) val;
                }
                return val;
            } catch (NumberFormatException e) {
                double d = Double.parseDouble(numStr);
                if (!Double.isFinite(d)) {
                    throw new IllegalArgumentException("Número JSON fuera de rango en pos " + start);
                }
                return d;
            }
        }

        private static boolean isAsciiDigit(char c) {
            return c >= '0' && c <= '9';
        }
    }
}
