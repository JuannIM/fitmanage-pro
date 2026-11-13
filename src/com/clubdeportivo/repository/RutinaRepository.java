package com.clubdeportivo.repository;

import com.clubdeportivo.model.Ejercicio;
import com.clubdeportivo.model.GrupoMuscular;
import com.clubdeportivo.model.Rutina;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

/**
 * Repositorio de Rutinas de entrenamiento y sus Ejercicios compuestos en data/rutinas.json.
 *
 * @author Joel
 * @author Rocio
 * @version 1.0.0
 */
public class RutinaRepository implements IRepository<Rutina, String> {

    private final Object lock = new Object();
    private final Path archivoDatos;
    private final Map<String, Rutina> cacheEnMemoria;

    public RutinaRepository() {
        this(Paths.get("data", "rutinas.json"));
    }

    public RutinaRepository(Path archivoDatos) {
        this.archivoDatos = Objects.requireNonNull(archivoDatos, "La ruta del archivo de rutinas no puede ser nula.");
        this.cacheEnMemoria = new LinkedHashMap<>();
        cargarInicialDesdeDisco();
    }

    @Override
    public Rutina guardar(Rutina rutina) {
        if (rutina == null || rutina.getId() == null || rutina.getId().isBlank()) {
            throw new IllegalArgumentException("La rutina o su ID no pueden ser nulos ni vacíos.");
        }
        synchronized (lock) {
            Rutina previo = cacheEnMemoria.get(rutina.getId());
            cacheEnMemoria.put(rutina.getId(), rutina);
            try {
                guardarEnDisco();
            } catch (PersistenceException e) {
                if (previo != null) {
                    cacheEnMemoria.put(rutina.getId(), previo);
                } else {
                    cacheEnMemoria.remove(rutina.getId());
                }
                throw e;
            }
            return rutina;
        }
    }

    @Override
    public Optional<Rutina> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        synchronized (lock) {
            return Optional.ofNullable(cacheEnMemoria.get(id));
        }
    }

    @Override
    public List<Rutina> listarTodos() {
        synchronized (lock) {
            List<Rutina> lista = new ArrayList<>(cacheEnMemoria.values());
            lista.sort(Comparator.comparing(Rutina::getNombre));
            return lista;
        }
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        synchronized (lock) {
            Rutina previo = cacheEnMemoria.remove(id);
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
            Map<String, Rutina> nuevosDatos = new LinkedHashMap<>();
            int maxRutina = 0;
            int maxEjercicio = 0;

            try {
                Object json = JsonPersistenceEngine.readFromFile(archivoDatos);
                if (json != null) {
                    if (!(json instanceof List<?> lista)) {
                        throw new PersistenceException("Formato JSON inválido en " + archivoDatos + ": se esperaba una lista de rutinas.");
                    }
                    for (int i = 0; i < lista.size(); i++) {
                        Object item = lista.get(i);
                        if (!(item instanceof Map<?, ?> map)) {
                            throw new PersistenceException("Registro corrupto en " + archivoDatos + " (índice " + i + "): se esperaba un objeto JSON.");
                        }
                        Rutina r;
                        try {
                            r = deserializarRutina((Map<String, Object>) map);
                        } catch (RuntimeException e) {
                            throw new PersistenceException("Error deserializando rutina en " + archivoDatos + " (índice " + i + "): " + e.getMessage(), e);
                        }
                        if (r == null || r.getId() == null || r.getId().isBlank()) {
                            throw new PersistenceException("Registro de rutina inválido o sin ID en " + archivoDatos + " (índice " + i + ").");
                        }
                        nuevosDatos.put(r.getId(), r);
                        maxRutina = Math.max(maxRutina, IdGenerator.extraerSufijoNumerico(r.getId(), "RUT-"));
                        for (Ejercicio ej : r.getEjercicios()) {
                            maxEjercicio = Math.max(maxEjercicio, IdGenerator.extraerSufijoNumerico(ej.getId(), "EJE-"));
                        }
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
            IdGenerator.actualizarSecuenciaRutina(maxRutina);
            IdGenerator.actualizarSecuenciaEjercicio(maxEjercicio);
        }
    }

    @Override
    public void guardarEnDisco() {
        synchronized (lock) {
            try {
                List<Map<String, Object>> listaSerializada = new ArrayList<>();
                for (Rutina r : cacheEnMemoria.values()) {
                    listaSerializada.add(serializarRutina(r));
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

    private Map<String, Object> serializarRutina(Rutina r) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", r.getId());
        map.put("nombre", r.getNombre());
        map.put("objetivo", r.getObjetivo());
        map.put("nivelDificultad", r.getNivelDificultad());
        map.put("fechaCreacion", r.getFechaCreacion() != null ? r.getFechaCreacion().toString() : null);
        map.put("fechaRevision", r.getFechaRevision() != null ? r.getFechaRevision().toString() : null);
        map.put("instructorCreadorId", r.getInstructorCreadorId());

        List<Map<String, Object>> ejList = new ArrayList<>();
        for (Ejercicio e : r.getEjercicios()) {
            Map<String, Object> ejMap = new LinkedHashMap<>();
            ejMap.put("id", e.getId());
            ejMap.put("nombre", e.getNombre());
            ejMap.put("grupoMuscular", e.getGrupoMuscular().name());
            ejMap.put("series", e.getSeries());
            ejMap.put("repeticiones", e.getRepeticiones());
            ejMap.put("pesoSugeridoKg", e.getPesoSugeridoKg());
            ejMap.put("descansoSegundos", e.getDescansoSegundos());
            ejMap.put("notasTecnicas", e.getNotasTecnicas());
            ejList.add(ejMap);
        }
        map.put("ejercicios", ejList);
        return map;
    }

    @SuppressWarnings("unchecked")
    private Rutina deserializarRutina(Map<String, Object> map) {
        String id = (String) map.get("id");
        String nombre = (String) map.get("nombre");
        String objetivo = (String) map.get("objetivo");
        String nivel = (String) map.get("nivelDificultad");
        LocalDate fechaCreacion = map.get("fechaCreacion") != null ? LocalDate.parse((String) map.get("fechaCreacion")) : null;
        LocalDate fechaRevision = map.get("fechaRevision") != null ? LocalDate.parse((String) map.get("fechaRevision")) : null;
        String instId = (String) map.get("instructorCreadorId");

        Rutina r = new Rutina(id, nombre, objetivo, nivel, fechaCreacion, fechaRevision, instId);

        Object ejerciciosRaw = map.get("ejercicios");
        if (ejerciciosRaw != null) {
            if (!(ejerciciosRaw instanceof List<?> ejRaw)) {
                throw new IllegalArgumentException("El campo 'ejercicios' debe ser una lista.");
            }
            for (Object item : ejRaw) {
                if (!(item instanceof Map<?, ?> ejMapRaw)) {
                    throw new IllegalArgumentException("Elemento corrupto en 'ejercicios': se esperaba un objeto JSON.");
                }
                Map<String, Object> ejMap = (Map<String, Object>) ejMapRaw;
                String ejId = (String) ejMap.get("id");
                String ejNombre = (String) ejMap.get("nombre");
                GrupoMuscular grupo = GrupoMuscular.valueOf((String) ejMap.get("grupoMuscular"));
                int series = ((Number) ejMap.get("series")).intValue();
                int reps = ((Number) ejMap.get("repeticiones")).intValue();
                double peso = ((Number) ejMap.get("pesoSugeridoKg")).doubleValue();
                int descanso = ((Number) ejMap.get("descansoSegundos")).intValue();
                String notas = (String) ejMap.get("notasTecnicas");

                Ejercicio ej = new Ejercicio(ejId, ejNombre, grupo, series, reps, peso, descanso, notas);
                r.agregarEjercicio(ej);
            }
        }
        return r;
    }
}
