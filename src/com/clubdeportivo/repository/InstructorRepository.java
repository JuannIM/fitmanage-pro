package com.clubdeportivo.repository;

import com.clubdeportivo.model.Instructor;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.JsonPersistenceEngine;
import com.clubdeportivo.util.ValidationUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

/**
 * Repositorio de Instructores con persistencia local en data/instructores.json.
 *
 * @author Joel
 * @author Merlina
 * @version 1.0.0
 */
public class InstructorRepository implements IRepository<Instructor, String> {

    private final Object lock = new Object();
    private final Path archivoDatos;
    private final Map<String, Instructor> cacheEnMemoria;

    public InstructorRepository() {
        this(Paths.get("data", "instructores.json"));
    }

    public InstructorRepository(Path archivoDatos) {
        this.archivoDatos = Objects.requireNonNull(archivoDatos, "La ruta del archivo de instructores no puede ser nula.");
        this.cacheEnMemoria = new LinkedHashMap<>();
        cargarInicialDesdeDisco();
    }

    @Override
    public Instructor guardar(Instructor instructor) {
        if (instructor == null || instructor.getId() == null || instructor.getId().isBlank()) {
            throw new IllegalArgumentException("El instructor o su ID no pueden ser nulos ni vacíos.");
        }
        synchronized (lock) {
            String dniNuevo = ValidationUtils.sanitizarDni(instructor.getDni());
            String legajoNuevo = instructor.getLegajo() != null ? instructor.getLegajo().trim() : "";

            for (Instructor otro : cacheEnMemoria.values()) {
                if (otro.getId().equals(instructor.getId())) {
                    continue;
                }
                if (!dniNuevo.isEmpty() && dniNuevo.equals(ValidationUtils.sanitizarDni(otro.getDni()))) {
                    throw new IllegalStateException("Ya existe otro instructor registrado con el DNI: " + instructor.getDni());
                }
                if (!legajoNuevo.isEmpty() && otro.getLegajo() != null
                        && legajoNuevo.equalsIgnoreCase(otro.getLegajo().trim())) {
                    throw new IllegalStateException("Ya existe otro instructor registrado con el legajo: " + instructor.getLegajo());
                }
            }

            Instructor previo = cacheEnMemoria.get(instructor.getId());
            cacheEnMemoria.put(instructor.getId(), instructor);
            try {
                guardarEnDisco();
            } catch (PersistenceException e) {
                if (previo != null) {
                    cacheEnMemoria.put(instructor.getId(), previo);
                } else {
                    cacheEnMemoria.remove(instructor.getId());
                }
                throw e;
            }
            return instructor;
        }
    }

    @Override
    public Optional<Instructor> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        synchronized (lock) {
            return Optional.ofNullable(cacheEnMemoria.get(id));
        }
    }

    public Optional<Instructor> buscarPorDni(String dni) {
        if (dni == null) return Optional.empty();
        String limpio = ValidationUtils.sanitizarDni(dni);
        if (limpio.isEmpty()) return Optional.empty();
        synchronized (lock) {
            return cacheEnMemoria.values().stream()
                    .filter(i -> ValidationUtils.sanitizarDni(i.getDni()).equals(limpio))
                    .findFirst();
        }
    }

    public Optional<Instructor> buscarPorLegajo(String legajo) {
        if (legajo == null || legajo.isBlank()) return Optional.empty();
        String buscado = legajo.trim();
        synchronized (lock) {
            return cacheEnMemoria.values().stream()
                    .filter(i -> i.getLegajo() != null && i.getLegajo().trim().equalsIgnoreCase(buscado))
                    .findFirst();
        }
    }

    @Override
    public List<Instructor> listarTodos() {
        synchronized (lock) {
            List<Instructor> lista = new ArrayList<>(cacheEnMemoria.values());
            lista.sort(Comparator.comparing(Instructor::getNombreCompleto));
            return lista;
        }
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        synchronized (lock) {
            Instructor previo = cacheEnMemoria.remove(id);
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
            Map<String, Instructor> nuevosDatos = new LinkedHashMap<>();
            int maxInstructor = 0;

            try {
                Object json = JsonPersistenceEngine.readFromFile(archivoDatos);
                if (json != null) {
                    if (!(json instanceof List<?> lista)) {
                        throw new PersistenceException("Formato JSON inválido en " + archivoDatos + ": se esperaba una lista de instructores.");
                    }
                    for (int i = 0; i < lista.size(); i++) {
                        Object item = lista.get(i);
                        if (!(item instanceof Map<?, ?> map)) {
                            throw new PersistenceException("Registro corrupto en " + archivoDatos + " (índice " + i + "): se esperaba un objeto JSON.");
                        }
                        Instructor instructor;
                        try {
                            instructor = deserializarInstructor((Map<String, Object>) map);
                        } catch (RuntimeException e) {
                            throw new PersistenceException("Error deserializando instructor en " + archivoDatos + " (índice " + i + "): " + e.getMessage(), e);
                        }
                        if (instructor == null || instructor.getId() == null || instructor.getId().isBlank()) {
                            throw new PersistenceException("Registro de instructor inválido o sin ID en " + archivoDatos + " (índice " + i + ").");
                        }
                        nuevosDatos.put(instructor.getId(), instructor);
                        maxInstructor = Math.max(maxInstructor, IdGenerator.extraerSufijoNumerico(instructor.getId(), "INS-"));
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
            IdGenerator.actualizarSecuenciaInstructor(maxInstructor);
        }
    }

    @Override
    public void guardarEnDisco() {
        synchronized (lock) {
            try {
                List<Map<String, Object>> listaSerializada = new ArrayList<>();
                for (Instructor inst : cacheEnMemoria.values()) {
                    listaSerializada.add(serializarInstructor(inst));
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

    private Map<String, Object> serializarInstructor(Instructor i) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", i.getId());
        map.put("dni", i.getDni());
        map.put("nombre", i.getNombre());
        map.put("apellido", i.getApellido());
        map.put("email", i.getEmail());
        map.put("telefono", i.getTelefono());
        map.put("fechaNacimiento", i.getFechaNacimiento() != null ? i.getFechaNacimiento().toString() : null);
        map.put("fechaRegistro", i.getFechaRegistro() != null ? i.getFechaRegistro().toString() : null);
        map.put("legajo", i.getLegajo());
        map.put("especialidad", i.getEspecialidad());
        map.put("sueldoBase", i.getSueldoBase());
        map.put("honorarioPorClase", i.getHonorarioPorClase());
        map.put("turnoDisponibilidad", i.getTurnoDisponibilidad());
        return map;
    }

    private Instructor deserializarInstructor(Map<String, Object> map) {
        String id = (String) map.get("id");
        String dni = (String) map.get("dni");
        String nombre = (String) map.get("nombre");
        String apellido = (String) map.get("apellido");
        String email = (String) map.get("email");
        String telefono = (String) map.get("telefono");
        LocalDate fechaNac = map.get("fechaNacimiento") != null ? LocalDate.parse((String) map.get("fechaNacimiento")) : null;
        LocalDate fechaReg = map.get("fechaRegistro") != null ? LocalDate.parse((String) map.get("fechaRegistro")) : null;
        String legajo = (String) map.get("legajo");
        String especialidad = (String) map.get("especialidad");
        double sueldoBase = ((Number) map.get("sueldoBase")).doubleValue();
        double honorario = ((Number) map.get("honorarioPorClase")).doubleValue();
        String turno = (String) map.get("turnoDisponibilidad");

        return new Instructor(id, dni, nombre, apellido, email, telefono, fechaNac, fechaReg,
                legajo, especialidad, sueldoBase, honorario, turno);
    }
}
