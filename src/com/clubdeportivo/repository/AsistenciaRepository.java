package com.clubdeportivo.repository;

import com.clubdeportivo.model.EstadoAcceso;
import com.clubdeportivo.model.TipoAcceso;
import com.clubdeportivo.model.Asistencia;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio de Asistencias y registros de molinete con persistencia en data/asistencias.json.
 *
 * @author Camila Díaz (Integrante 6 - Persistencia, QA & Auditoría)
 * @author Lucas Rodríguez (Integrante 3 - Logística & Clases)
 * @version 1.0.0
 */
public class AsistenciaRepository implements IRepository<Asistencia, String> {

    private final Path archivoDatos;
    private final Map<String, Asistencia> cacheEnMemoria;

    public AsistenciaRepository() {
        this(Paths.get("data", "asistencias.json"));
    }

    public AsistenciaRepository(Path archivoDatos) {
        this.archivoDatos = archivoDatos;
        this.cacheEnMemoria = new ConcurrentHashMap<>();
        cargarDesdeDisco();
    }

    @Override
    public Asistencia guardar(Asistencia asistencia) {
        if (asistencia == null || asistencia.getId() == null) {
            throw new IllegalArgumentException("La asistencia o su ID no pueden ser nulos.");
        }
        cacheEnMemoria.put(asistencia.getId(), asistencia);
        guardarEnDisco();
        return asistencia;
    }

    @Override
    public Optional<Asistencia> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cacheEnMemoria.get(id));
    }

    public List<Asistencia> buscarPorSocioId(String socioId) {
        if (socioId == null) return Collections.emptyList();
        return cacheEnMemoria.values().stream()
                .filter(a -> a.getSocioId().equalsIgnoreCase(socioId))
                .sorted(Comparator.comparing(Asistencia::getFechaHora).reversed())
                .toList();
    }

    public List<Asistencia> listarAsistenciasDeHoy() {
        LocalDate hoy = LocalDate.now();
        return cacheEnMemoria.values().stream()
                .filter(a -> a.getFechaHora().toLocalDate().isEqual(hoy) && a.esAccesoValido())
                .sorted(Comparator.comparing(Asistencia::getFechaHora).reversed())
                .toList();
    }

    @Override
    public List<Asistencia> listarTodos() {
        List<Asistencia> lista = new ArrayList<>(cacheEnMemoria.values());
        lista.sort(Comparator.comparing(Asistencia::getFechaHora).reversed());
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
                        Asistencia asistencia = deserializarAsistencia((Map<String, Object>) map);
                        if (asistencia != null) {
                            cacheEnMemoria.put(asistencia.getId(), asistencia);
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
            for (Asistencia a : cacheEnMemoria.values()) {
                listaSerializada.add(serializarAsistencia(a));
            }
            JsonPersistenceEngine.saveToFile(archivoDatos, listaSerializada);
        } catch (IOException e) {
            System.err.println(">> [ERROR] Fallo al escribir en " + archivoDatos + ": " + e.getMessage());
        }
    }

    private Map<String, Object> serializarAsistencia(Asistencia a) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", a.getId());
        map.put("socioId", a.getSocioId());
        map.put("fechaHora", a.getFechaHora().toString());
        map.put("tipoAcceso", a.getTipoAcceso().name());
        map.put("claseId", a.getClaseId());
        map.put("estadoAcceso", a.getEstadoAcceso().name());
        map.put("observacion", a.getObservacion());
        return map;
    }

    private Asistencia deserializarAsistencia(Map<String, Object> map) {
        try {
            String id = (String) map.get("id");
            String socioId = (String) map.get("socioId");
            LocalDateTime fechaHora = LocalDateTime.parse((String) map.get("fechaHora"));
            TipoAcceso tipo = TipoAcceso.valueOf((String) map.get("tipoAcceso"));
            String claseId = (String) map.get("claseId");
            EstadoAcceso estado = EstadoAcceso.valueOf((String) map.get("estadoAcceso"));
            String obs = (String) map.get("observacion");

            return new Asistencia(id, socioId, fechaHora, tipo, claseId, estado, obs);
        } catch (Exception e) {
            System.err.println(">> [ERROR] Deserializando asistencia: " + e.getMessage());
            return null;
        }
    }
}
