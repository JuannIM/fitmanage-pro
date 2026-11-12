package com.clubdeportivo.repository;

import com.clubdeportivo.model.*;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio de Clases Grupales y control de cupos con persistencia en data/clases.json.
 *
 * @author Camila Díaz (Integrante 6 - Persistencia, QA & Auditoría)
 * @author Lucas Rodríguez (Integrante 3 - Logística & Clases)
 * @version 1.0.0
 */
public class ClaseRepository implements IRepository<ClaseGrupal, String> {

    private final Path archivoDatos;
    private final Map<String, ClaseGrupal> cacheEnMemoria;
    private SocioRepository socioRepo;
    private InstructorRepository instructorRepo;

    public ClaseRepository() {
        this(Paths.get("data", "clases.json"), null, null);
    }

    public ClaseRepository(Path archivoDatos, SocioRepository socioRepo, InstructorRepository instructorRepo) {
        this.archivoDatos = archivoDatos;
        this.cacheEnMemoria = new ConcurrentHashMap<>();
        this.socioRepo = socioRepo;
        this.instructorRepo = instructorRepo;
        cargarDesdeDisco();
    }

    public void setDependencias(SocioRepository socioRepo, InstructorRepository instructorRepo) {
        this.socioRepo = socioRepo;
        this.instructorRepo = instructorRepo;
        cargarDesdeDisco();
    }

    @Override
    public ClaseGrupal guardar(ClaseGrupal clase) {
        if (clase == null || clase.getId() == null) {
            throw new IllegalArgumentException("La clase o su ID no pueden ser nulos.");
        }
        cacheEnMemoria.put(clase.getId(), clase);
        guardarEnDisco();
        return clase;
    }

    @Override
    public Optional<ClaseGrupal> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cacheEnMemoria.get(id));
    }

    @Override
    public List<ClaseGrupal> listarTodos() {
        List<ClaseGrupal> lista = new ArrayList<>(cacheEnMemoria.values());
        lista.sort(Comparator.comparing(ClaseGrupal::getDiaSemana).thenComparing(ClaseGrupal::getHoraInicio));
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
                        ClaseGrupal clase = deserializarClase((Map<String, Object>) map);
                        if (clase != null) {
                            cacheEnMemoria.put(clase.getId(), clase);
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
            for (ClaseGrupal c : cacheEnMemoria.values()) {
                listaSerializada.add(serializarClase(c));
            }
            JsonPersistenceEngine.saveToFile(archivoDatos, listaSerializada);
        } catch (IOException e) {
            System.err.println(">> [ERROR] Fallo al escribir en " + archivoDatos + ": " + e.getMessage());
        }
    }

    private Map<String, Object> serializarClase(ClaseGrupal c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", c.getId());
        map.put("nombre", c.getNombre());
        map.put("disciplina", c.getDisciplina().name());
        map.put("diaSemana", c.getDiaSemana().name());
        map.put("horaInicio", c.getHoraInicio().toString());
        map.put("duracionMinutos", c.getDuracionMinutos());
        map.put("sala", c.getSala());
        map.put("cupoMaximo", c.getCupoMaximo());
        map.put("instructorId", c.getInstructor() != null ? c.getInstructor().getId() : null);

        List<String> sociosIds = new ArrayList<>();
        for (Socio s : c.getSociosInscriptos()) {
            sociosIds.add(s.getId());
        }
        map.put("sociosInscriptosIds", sociosIds);
        return map;
    }

    @SuppressWarnings("unchecked")
    private ClaseGrupal deserializarClase(Map<String, Object> map) {
        try {
            String id = (String) map.get("id");
            String nombre = (String) map.get("nombre");
            Disciplina disciplina = Disciplina.valueOf((String) map.get("disciplina"));
            DayOfWeek dia = DayOfWeek.valueOf((String) map.get("diaSemana"));
            LocalTime hora = LocalTime.parse((String) map.get("horaInicio"));
            int duracion = ((Number) map.get("duracionMinutos")).intValue();
            String sala = (String) map.get("sala");
            int cupo = ((Number) map.get("cupoMaximo")).intValue();

            Instructor instructor = null;
            String instId = (String) map.get("instructorId");
            if (instId != null && instructorRepo != null) {
                instructor = instructorRepo.buscarPorId(instId).orElse(null);
            }

            ClaseGrupal clase = new ClaseGrupal(id, nombre, disciplina, dia, hora, duracion, sala, cupo, instructor);

            if (map.get("sociosInscriptosIds") instanceof List<?> idsList && socioRepo != null) {
                for (Object rawId : idsList) {
                    if (rawId instanceof String sId) {
                        socioRepo.buscarPorId(sId).ifPresent(s -> {
                            try {
                                clase.inscribirSocio(s);
                            } catch (CupoAgotadoException ignored) {
                            }
                        });
                    }
                }
            }

            return clase;
        } catch (Exception e) {
            System.err.println(">> [ERROR] Deserializando clase: " + e.getMessage());
            return null;
        }
    }
}
