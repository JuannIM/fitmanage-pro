package com.clubdeportivo.repository;

import com.clubdeportivo.model.Asistencia;
import com.clubdeportivo.model.EstadoAcceso;
import com.clubdeportivo.model.TipoAcceso;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Repositorio de Asistencias y registros de molinete con persistencia en data/asistencias.json.
 *
 * @author Joel
 * @author Merlina
 * @version 1.0.0
 */
public class AsistenciaRepository implements IRepository<Asistencia, String> {

    private final Object lock = new Object();
    private final Path archivoDatos;
    private final Map<String, Asistencia> cacheEnMemoria;

    public AsistenciaRepository() {
        this(Paths.get("data", "asistencias.json"));
    }

    public AsistenciaRepository(Path archivoDatos) {
        this.archivoDatos = Objects.requireNonNull(archivoDatos, "La ruta del archivo de asistencias no puede ser nula.");
        this.cacheEnMemoria = new LinkedHashMap<>();
        cargarInicialDesdeDisco();
    }

    @Override
    public Asistencia guardar(Asistencia asistencia) {
        if (asistencia == null || asistencia.getId() == null || asistencia.getId().isBlank()) {
            throw new IllegalArgumentException("La asistencia o su ID no pueden ser nulos ni vacíos.");
        }
        synchronized (lock) {
            Asistencia previo = cacheEnMemoria.get(asistencia.getId());
            cacheEnMemoria.put(asistencia.getId(), asistencia);
            try {
                guardarEnDisco();
            } catch (PersistenceException e) {
                if (previo != null) {
                    cacheEnMemoria.put(asistencia.getId(), previo);
                } else {
                    cacheEnMemoria.remove(asistencia.getId());
                }
                throw e;
            }
            return asistencia;
        }
    }

    @Override
    public Optional<Asistencia> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        synchronized (lock) {
            return Optional.ofNullable(cacheEnMemoria.get(id));
        }
    }

    public List<Asistencia> buscarPorSocioId(String socioId) {
        if (socioId == null) return Collections.emptyList();
        synchronized (lock) {
            return cacheEnMemoria.values().stream()
                    .filter(a -> a.getSocioId() != null && a.getSocioId().equalsIgnoreCase(socioId))
                    .sorted(Comparator.comparing(Asistencia::getFechaHora).reversed())
                    .toList();
        }
    }

    public List<Asistencia> listarAsistenciasDeHoy() {
        LocalDate hoy = LocalDate.now();
        synchronized (lock) {
            return cacheEnMemoria.values().stream()
                    .filter(a -> a.getFechaHora().toLocalDate().isEqual(hoy) && a.esAccesoValido())
                    .sorted(Comparator.comparing(Asistencia::getFechaHora).reversed())
                    .toList();
        }
    }

    @Override
    public List<Asistencia> listarTodos() {
        synchronized (lock) {
            List<Asistencia> lista = new ArrayList<>(cacheEnMemoria.values());
            lista.sort(Comparator.comparing(Asistencia::getFechaHora).reversed());
            return lista;
        }
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        synchronized (lock) {
            Asistencia previo = cacheEnMemoria.remove(id);
            if (previo == null) {
                return false;
            }
            try {
                guardarEnDisco();
            } catch (PersistenceException e) {
                cacheEnMemoria.put(id, previo);
                throw e;
            }
            return true;
        }
    }

    @Override
    public int contar() {
        synchronized (lock) {
            return cacheEnMemoria.size();
        }
    }

    @Override
    public void cargarDesdeDisco() {
        cargarInicialDesdeDisco();
    }

    @SuppressWarnings("unchecked")
    private void cargarInicialDesdeDisco() {
        synchronized (lock) {
            Map<String, Asistencia> nuevosDatos = new LinkedHashMap<>();
            int maxAsistencia = 0;

            try {
                Object json = JsonPersistenceEngine.readFromFile(archivoDatos);
                if (json != null) {
                    if (!(json instanceof List<?> lista)) {
                        throw new PersistenceException("Formato JSON inválido en " + archivoDatos + ": se esperaba una lista de asistencias.");
                    }
                    for (int i = 0; i < lista.size(); i++) {
                        Object item = lista.get(i);
                        if (!(item instanceof Map<?, ?> map)) {
                            throw new PersistenceException("Registro corrupto en " + archivoDatos + " (índice " + i + "): se esperaba un objeto JSON.");
                        }
                        Asistencia asistencia;
                        try {
                            asistencia = deserializarAsistencia((Map<String, Object>) map);
                        } catch (RuntimeException e) {
                            throw new PersistenceException("Error deserializando asistencia en " + archivoDatos + " (índice " + i + "): " + e.getMessage(), e);
                        }
                        if (asistencia == null || asistencia.getId() == null || asistencia.getId().isBlank()) {
                            throw new PersistenceException("Registro de asistencia inválido o sin ID en " + archivoDatos + " (índice " + i + ").");
                        }
                        nuevosDatos.put(asistencia.getId(), asistencia);
                        maxAsistencia = Math.max(maxAsistencia, IdGenerator.extraerSufijoNumerico(asistencia.getId(), "ASI-"));
                    }
                }
            } catch (IOException | RuntimeException e) {
                if (e instanceof PersistenceException pe) {
                    throw pe;
                }
                throw new PersistenceException("No se pudo leer " + archivoDatos + ": " + e.getMessage(), e);
            }

            cacheEnMemoria.clear();
            cacheEnMemoria.putAll(nuevosDatos);
            IdGenerator.actualizarSecuenciaAsistencia(maxAsistencia);
        }
    }

    @Override
    public void guardarEnDisco() {
        synchronized (lock) {
            try {
                List<Map<String, Object>> listaSerializada = new ArrayList<>();
                for (Asistencia a : cacheEnMemoria.values()) {
                    listaSerializada.add(serializarAsistencia(a));
                }
                JsonPersistenceEngine.saveToFile(archivoDatos, listaSerializada);
            } catch (IOException | RuntimeException e) {
                if (e instanceof PersistenceException pe) {
                    throw pe;
                }
                throw new PersistenceException("Fallo al escribir en " + archivoDatos + ": " + e.getMessage(), e);
            }
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
        String id = (String) map.get("id");
        String socioId = (String) map.get("socioId");
        LocalDateTime fechaHora = LocalDateTime.parse((String) map.get("fechaHora"));
        TipoAcceso tipo = TipoAcceso.valueOf((String) map.get("tipoAcceso"));
        String claseId = (String) map.get("claseId");
        EstadoAcceso estado = EstadoAcceso.valueOf((String) map.get("estadoAcceso"));
        String obs = (String) map.get("observacion");

        return new Asistencia(id, socioId, fechaHora, tipo, claseId, estado, obs);
    }
}
