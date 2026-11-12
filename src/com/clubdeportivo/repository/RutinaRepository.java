package com.clubdeportivo.repository;

import com.clubdeportivo.model.Ejercicio;
import com.clubdeportivo.model.GrupoMuscular;
import com.clubdeportivo.model.Rutina;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio de Rutinas de entrenamiento y sus Ejercicios compuestos en data/rutinas.json.
 *
 * @author Joel
 * @author Rocio
 * @version 1.0.0
 */
public class RutinaRepository implements IRepository<Rutina, String> {

    private final Path archivoDatos;
    private final Map<String, Rutina> cacheEnMemoria;

    public RutinaRepository() {
        this(Paths.get("data", "rutinas.json"));
    }

    public RutinaRepository(Path archivoDatos) {
        this.archivoDatos = archivoDatos;
        this.cacheEnMemoria = new ConcurrentHashMap<>();
        cargarDesdeDisco();
    }

    @Override
    public Rutina guardar(Rutina rutina) {
        if (rutina == null || rutina.getId() == null) {
            throw new IllegalArgumentException("La rutina o su ID no pueden ser nulos.");
        }
        cacheEnMemoria.put(rutina.getId(), rutina);
        guardarEnDisco();
        return rutina;
    }

    @Override
    public Optional<Rutina> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cacheEnMemoria.get(id));
    }

    @Override
    public List<Rutina> listarTodos() {
        List<Rutina> lista = new ArrayList<>(cacheEnMemoria.values());
        lista.sort(Comparator.comparing(Rutina::getNombre));
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
                        Rutina r = deserializarRutina((Map<String, Object>) map);
                        if (r != null) {
                            cacheEnMemoria.put(r.getId(), r);
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
            for (Rutina r : cacheEnMemoria.values()) {
                listaSerializada.add(serializarRutina(r));
            }
            JsonPersistenceEngine.saveToFile(archivoDatos, listaSerializada);
        } catch (IOException e) {
            System.err.println(">> [ERROR] Fallo al escribir en " + archivoDatos + ": " + e.getMessage());
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
        try {
            String id = (String) map.get("id");
            String nombre = (String) map.get("nombre");
            String objetivo = (String) map.get("objetivo");
            String nivel = (String) map.get("nivelDificultad");
            LocalDate fechaCreacion = map.get("fechaCreacion") != null ? LocalDate.parse((String) map.get("fechaCreacion")) : null;
            LocalDate fechaRevision = map.get("fechaRevision") != null ? LocalDate.parse((String) map.get("fechaRevision")) : null;
            String instId = (String) map.get("instructorCreadorId");

            Rutina r = new Rutina(id, nombre, objetivo, nivel, fechaCreacion, fechaRevision, instId);

            if (map.get("ejercicios") instanceof List<?> ejRaw) {
                for (Object item : ejRaw) {
                    if (item instanceof Map<?, ?> ejMapRaw) {
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
            }
            return r;
        } catch (Exception e) {
            System.err.println(">> [ERROR] Deserializando rutina: " + e.getMessage());
            return null;
        }
    }
}
