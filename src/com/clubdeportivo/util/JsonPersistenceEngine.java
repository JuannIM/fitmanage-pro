package com.clubdeportivo.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Motor de persistencia JSON autónomo y de alto rendimiento sin librerías externas.
 * Implementa serialización estructurada con formato legible (pretty-print),
 * parseo recursivo y escritura atómica sobre el sistema de archivos.
 *
 * @author Camila Díaz (Integrante 6 - Persistencia & QA)
 * @author Juan Pérez (Integrante 1 - Arquitectura Core)
 * @version 1.0.0
 */
public final class JsonPersistenceEngine {

    private JsonPersistenceEngine() {
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
        Path parent = path.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        String jsonContent = toJson(data);
        Path tempPath = path.resolveSibling(path.getFileName().toString() + ".tmp");
        Files.writeString(tempPath, jsonContent, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        try {
            Files.move(tempPath, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Lee un archivo JSON y lo deserializa.
     */
    public static Object readFromFile(Path path) throws IOException {
        if (!Files.exists(path) || Files.size(path) == 0) {
            return null;
        }
        String content = Files.readString(path, StandardCharsets.UTF_8);
        return parseJson(content);
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
            Object result = parseValue();
            skipWhitespace();
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
            if (pos >= len) return null;
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
            while (pos < len) {
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
                if (pos < len && src.charAt(pos) == ',') {
                    pos++;
                } else if (pos < len && src.charAt(pos) == '}') {
                    pos++;
                    break;
                } else {
                    throw new IllegalArgumentException("Se esperaba ',' o '}' en pos " + pos);
                }
            }
            return map;
        }

        private List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            pos++; // Consumir '['
            skipWhitespace();
            if (pos < len && src.charAt(pos) == ']') {
                pos++;
                return list;
            }
            while (pos < len) {
                Object item = parseValue();
                list.add(item);
                skipWhitespace();
                if (pos < len && src.charAt(pos) == ',') {
                    pos++;
                } else if (pos < len && src.charAt(pos) == ']') {
                    pos++;
                    break;
                } else {
                    throw new IllegalArgumentException("Se esperaba ',' o ']' en pos " + pos);
                }
            }
            return list;
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
                    if (pos >= len) break;
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
                            if (pos + 4 <= len) {
                                String hex = src.substring(pos, pos + 4);
                                sb.append((char) Integer.parseInt(hex, 16));
                                pos += 4;
                            }
                        }
                        default -> sb.append(esc);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
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
            if (pos < len && (src.charAt(pos) == '-' || src.charAt(pos) == '+')) {
                pos++;
            }
            boolean isFloating = false;
            while (pos < len) {
                char c = src.charAt(pos);
                if (Character.isDigit(c)) {
                    pos++;
                } else if (c == '.' || c == 'e' || c == 'E') {
                    isFloating = true;
                    pos++;
                } else {
                    break;
                }
            }
            String numStr = src.substring(start, pos);
            if (isFloating) {
                return Double.parseDouble(numStr);
            }
            try {
                long val = Long.parseLong(numStr);
                if (val >= Integer.MIN_VALUE && val <= Integer.MAX_VALUE) {
                    return (int) val;
                }
                return val;
            } catch (NumberFormatException e) {
                return Double.parseDouble(numStr);
            }
        }
    }
}
