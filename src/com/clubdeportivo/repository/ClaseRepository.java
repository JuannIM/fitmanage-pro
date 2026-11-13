package com.clubdeportivo.repository;

import com.clubdeportivo.model.*;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

/**
 * Repositorio de Clases Grupales y control de cupos con persistencia en data/clases.json.
 *
 * @author Joel
 * @author Merlina
 * @version 1.0.0
 */
public class ClaseRepository implements IRepository<ClaseGrupal, String> {

    private final Object lock = new Object();
    private final Path archivoDatos;
    private final Map<String, ClaseGrupal> cacheEnMemoria;
    private SocioRepository socioRepo;
    private InstructorRepository instructorRepo;

    public ClaseRepository() {
        this(Paths.get("data", "clases.json"), null, null);
    }

    public ClaseRepository(Path archivoDatos, SocioRepository socioRepo, InstructorRepository instructorRepo) {
        this.archivoDatos = Objects.requireNonNull(archivoDatos, "La ruta del archivo de clases no puede ser nula.");
        this.cacheEnMemoria = new LinkedHashMap<>();
        this.socioRepo = socioRepo;
        this.instructorRepo = instructorRepo;
        cargarInicialDesdeDisco();
    }

    public void setDependencias(SocioRepository socioRepo, InstructorRepository instructorRepo) {
        synchronized (lock) {
            this.socioRepo = socioRepo;
            this.instructorRepo = instructorRepo;
            cargarInicialDesdeDisco();
        }
    }

    @Override
    public ClaseGrupal guardar(ClaseGrupal clase) {
        if (clase == null || clase.getId() == null || clase.getId().isBlank()) {
            throw new IllegalArgumentException("La clase o su ID no pueden ser nulos ni vacíos.");
        }
        synchronized (lock) {
            ClaseGrupal previo = cacheEnMemoria.get(clase.getId());
            cacheEnMemoria.put(clase.getId(), clase);
            try {
                guardarEnDisco();
            } catch (PersistenceException e) {
                if (previo != null) {
                    cacheEnMemoria.put(clase.getId(), previo);
                } else {
                    cacheEnMemoria.remove(clase.getId());
                }
                throw e;
            }
            return clase;
        }
    }

    @Override
    public Optional<ClaseGrupal> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        synchronized (lock) {
            return Optional.ofNullable(cacheEnMemoria.get(id));
        }
    }

    @Override
    public List<ClaseGrupal> listarTodos() {
        synchronized (lock) {
            List<ClaseGrupal> lista = new ArrayList<>(cacheEnMemoria.values());
            lista.sort(Comparator.comparing(ClaseGrupal::getDiaSemana).thenComparing(ClaseGrupal::getHoraInicio));
            return lista;
        }
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        synchronized (lock) {
            ClaseGrupal previo = cacheEnMemoria.remove(id);
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
            Map<String, ClaseGrupal> nuevosDatos = new LinkedHashMap<>();
            int maxClase = 0;

            try {
                Object json = JsonPersistenceEngine.readFromFile(archivoDatos);
                if (json != null) {
                    if (!(json instanceof List<?> lista)) {
                        throw new PersistenceException("Formato JSON inválido en " + archivoDatos + ": se esperaba una lista de clases.");
                    }
                    for (int i = 0; i < lista.size(); i++) {
                        Object item = lista.get(i);
                        if (!(item instanceof Map<?, ?> map)) {
                            throw new PersistenceException("Registro corrupto en " + archivoDatos + " (índice " + i + "): se esperaba un objeto JSON.");
                        }
                        ClaseGrupal clase;
                        try {
                            clase = deserializarClase((Map<String, Object>) map);
                        } catch (RuntimeException e) {
                            throw new PersistenceException("Error deserializando clase en " + archivoDatos + " (índice " + i + "): " + e.getMessage(), e);
                        }
                        if (clase == null || clase.getId() == null || clase.getId().isBlank()) {
                            throw new PersistenceException("Registro de clase inválido o sin ID en " + archivoDatos + " (índice " + i + ").");
                        }
                        nuevosDatos.put(clase.getId(), clase);
                        maxClase = Math.max(maxClase, IdGenerator.extraerSufijoNumerico(clase.getId(), "CLS-"));
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
            IdGenerator.actualizarSecuenciaClase(maxClase);
        }
    }

    @Override
    public void guardarEnDisco() {
        synchronized (lock) {
            try {
                List<Map<String, Object>> listaSerializada = new ArrayList<>();
                for (ClaseGrupal c : cacheEnMemoria.values()) {
                    listaSerializada.add(serializarClase(c));
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

    private ClaseGrupal deserializarClase(Map<String, Object> map) {
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

        Object sociosRaw = map.get("sociosInscriptosIds");
        if (sociosRaw != null) {
            if (!(sociosRaw instanceof List<?> idsList)) {
                throw new IllegalArgumentException("El campo 'sociosInscriptosIds' debe ser una lista.");
            }
            for (Object rawId : idsList) {
                if (!(rawId instanceof String sId)) {
                    throw new IllegalArgumentException("Elemento inválido en 'sociosInscriptosIds'.");
                }
                if (socioRepo != null) {
                    socioRepo.buscarPorId(sId).ifPresent(clase::restaurarInscripcion);
                }
            }
        }

        return clase;
    }
}
