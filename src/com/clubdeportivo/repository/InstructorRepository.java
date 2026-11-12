package com.clubdeportivo.repository;

import com.clubdeportivo.model.Instructor;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio de Instructores con persistencia local en data/instructores.json.
 *
 * @author Joel
 * @author Merlina
 * @version 1.0.0
 */
public class InstructorRepository implements IRepository<Instructor, String> {

    private final Path archivoDatos;
    private final Map<String, Instructor> cacheEnMemoria;

    public InstructorRepository() {
        this(Paths.get("data", "instructores.json"));
    }

    public InstructorRepository(Path archivoDatos) {
        this.archivoDatos = archivoDatos;
        this.cacheEnMemoria = new ConcurrentHashMap<>();
        cargarDesdeDisco();
    }

    @Override
    public Instructor guardar(Instructor instructor) {
        if (instructor == null || instructor.getId() == null) {
            throw new IllegalArgumentException("El instructor o su ID no pueden ser nulos.");
        }
        cacheEnMemoria.put(instructor.getId(), instructor);
        guardarEnDisco();
        return instructor;
    }

    @Override
    public Optional<Instructor> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cacheEnMemoria.get(id));
    }

    public Optional<Instructor> buscarPorLegajo(String legajo) {
        if (legajo == null) return Optional.empty();
        return cacheEnMemoria.values().stream()
                .filter(i -> i.getLegajo().equalsIgnoreCase(legajo.trim()))
                .findFirst();
    }

    @Override
    public List<Instructor> listarTodos() {
        List<Instructor> lista = new ArrayList<>(cacheEnMemoria.values());
        lista.sort(Comparator.comparing(Instructor::getNombreCompleto));
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
                        Instructor instructor = deserializarInstructor((Map<String, Object>) map);
                        if (instructor != null) {
                            cacheEnMemoria.put(instructor.getId(), instructor);
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
            for (Instructor inst : cacheEnMemoria.values()) {
                listaSerializada.add(serializarInstructor(inst));
            }
            JsonPersistenceEngine.saveToFile(archivoDatos, listaSerializada);
        } catch (IOException e) {
            System.err.println(">> [ERROR] Fallo al escribir en " + archivoDatos + ": " + e.getMessage());
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
        try {
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
        } catch (Exception e) {
            System.err.println(">> [ERROR] Deserializando instructor: " + e.getMessage());
            return null;
        }
    }
}
