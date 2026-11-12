package com.clubdeportivo.repository;

import com.clubdeportivo.model.*;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio de Socios con persistencia local en data/socios.json.
 *
 * @author Camila Díaz (Integrante 6 - Persistencia, QA & Auditoría)
 * @author Juan Pérez (Integrante 1 - Arquitectura Core)
 * @version 1.0.0
 */
public class SocioRepository implements IRepository<Socio, String> {

    private final Path archivoDatos;
    private final Map<String, Socio> cacheEnMemoria;

    public SocioRepository() {
        this(Paths.get("data", "socios.json"));
    }

    public SocioRepository(Path archivoDatos) {
        this.archivoDatos = archivoDatos;
        this.cacheEnMemoria = new ConcurrentHashMap<>();
        cargarDesdeDisco();
    }

    @Override
    public Socio guardar(Socio socio) {
        if (socio == null || socio.getId() == null) {
            throw new IllegalArgumentException("El socio o su identificador no pueden ser nulos.");
        }
        cacheEnMemoria.put(socio.getId(), socio);
        guardarEnDisco();
        return socio;
    }

    @Override
    public Optional<Socio> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cacheEnMemoria.get(id));
    }

    public Optional<Socio> buscarPorDni(String dni) {
        if (dni == null) return Optional.empty();
        String limpio = dni.trim().replace(".", "");
        return cacheEnMemoria.values().stream()
                .filter(s -> s.getDni().equalsIgnoreCase(limpio))
                .findFirst();
    }

    public Optional<Socio> buscarPorNumeroSocio(String numeroSocio) {
        if (numeroSocio == null) return Optional.empty();
        return cacheEnMemoria.values().stream()
                .filter(s -> s.getNumeroSocio().equalsIgnoreCase(numeroSocio.trim()))
                .findFirst();
    }

    @Override
    public List<Socio> listarTodos() {
        List<Socio> lista = new ArrayList<>(cacheEnMemoria.values());
        lista.sort(Comparator.comparing(Socio::getNumeroSocio));
        return lista;
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        boolean eliminado = cacheEnMemoria.remove(id) != null;
        if (eliminado) {
            guardarEnDisco();
        }
        return eliminado;
    }

    @Override
    public int contar() {
        return cacheEnMemoria.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void cargarDesdeDisco() {
        cacheEnMemoria.clear();
        try {
            Object json = JsonPersistenceEngine.readFromFile(archivoDatos);
            if (json instanceof List<?> lista) {
                for (Object item : lista) {
                    if (item instanceof Map<?, ?> map) {
                        Socio socio = deserializarSocio((Map<String, Object>) map);
                        if (socio != null) {
                            cacheEnMemoria.put(socio.getId(), socio);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(">> [WARN] No se pudo leer " + archivoDatos + ": " + e.getMessage());
        }
    }

    @Override
    public void guardarEnDisco() {
        try {
            List<Map<String, Object>> listaSerializada = new ArrayList<>();
            for (Socio s : cacheEnMemoria.values()) {
                listaSerializada.add(serializarSocio(s));
            }
            JsonPersistenceEngine.saveToFile(archivoDatos, listaSerializada);
        } catch (IOException e) {
            System.err.println(">> [ERROR] Fallo al escribir en " + archivoDatos + ": " + e.getMessage());
        }
    }

    private Map<String, Object> serializarSocio(Socio s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", s.getId());
        map.put("dni", s.getDni());
        map.put("nombre", s.getNombre());
        map.put("apellido", s.getApellido());
        map.put("email", s.getEmail());
        map.put("telefono", s.getTelefono());
        map.put("fechaNacimiento", s.getFechaNacimiento() != null ? s.getFechaNacimiento().toString() : null);
        map.put("fechaRegistro", s.getFechaRegistro() != null ? s.getFechaRegistro().toString() : null);
        map.put("numeroSocio", s.getNumeroSocio());
        map.put("estadoCuota", s.getEstadoCuota().name());
        map.put("porcentajeDescuentoManual", s.getPorcentajeDescuentoManual());

        if (s.getMembresiaActual() != null) {
            Membresia m = s.getMembresiaActual();
            Map<String, Object> memMap = new LinkedHashMap<>();
            memMap.put("id", m.getId());
            memMap.put("tipo", m.getTipo().name());
            memMap.put("precioBase", m.getPrecioBase());
            memMap.put("fechaInicio", m.getFechaInicio() != null ? m.getFechaInicio().toString() : null);
            memMap.put("fechaVencimiento", m.getFechaVencimiento() != null ? m.getFechaVencimiento().toString() : null);
            memMap.put("renovacionAutomatica", m.isRenovacionAutomatica());
            map.put("membresia", memMap);
        } else {
            map.put("membresia", null);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private Socio deserializarSocio(Map<String, Object> map) {
        try {
            String id = (String) map.get("id");
            String dni = (String) map.get("dni");
            String nombre = (String) map.get("nombre");
            String apellido = (String) map.get("apellido");
            String email = (String) map.get("email");
            String telefono = (String) map.get("telefono");
            LocalDate fechaNac = map.get("fechaNacimiento") != null ? LocalDate.parse((String) map.get("fechaNacimiento")) : null;
            LocalDate fechaReg = map.get("fechaRegistro") != null ? LocalDate.parse((String) map.get("fechaRegistro")) : null;
            String numSocio = (String) map.get("numeroSocio");
            EstadoCuota estado = EstadoCuota.valueOf((String) map.get("estadoCuota"));

            Membresia mem = null;
            if (map.get("membresia") instanceof Map<?, ?> memMapRaw) {
                Map<String, Object> memMap = (Map<String, Object>) memMapRaw;
                String memId = (String) memMap.get("id");
                TipoMembresia tipo = TipoMembresia.valueOf((String) memMap.get("tipo"));
                double precioBase = ((Number) memMap.get("precioBase")).doubleValue();
                LocalDate inicio = memMap.get("fechaInicio") != null ? LocalDate.parse((String) memMap.get("fechaInicio")) : null;
                LocalDate venc = memMap.get("fechaVencimiento") != null ? LocalDate.parse((String) memMap.get("fechaVencimiento")) : null;
                boolean reno = Boolean.TRUE.equals(memMap.get("renovacionAutomatica"));
                mem = new Membresia(memId, tipo, precioBase, inicio, venc, reno);
            }

            Socio socio = new Socio(id, dni, nombre, apellido, email, telefono, fechaNac, fechaReg, numSocio, estado, mem);
            if (map.get("porcentajeDescuentoManual") instanceof Number n) {
                socio.aplicarDescuento(n.doubleValue());
            }
            return socio;
        } catch (Exception e) {
            System.err.println(">> [ERROR] Deserializando socio: " + e.getMessage());
            return null;
        }
    }
}
