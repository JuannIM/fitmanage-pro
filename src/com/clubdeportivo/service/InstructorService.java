package com.clubdeportivo.service;

import com.clubdeportivo.model.ClaseGrupal;
import com.clubdeportivo.model.Instructor;
import com.clubdeportivo.repository.InstructorRepository;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de administración del cuerpo técnico y liquidación salarial docente.
 *
 * @author Merlina
 * @author Juan
 * @version 1.0.0
 */
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public Instructor registrarInstructor(String dni, String nombre, String apellido,
                                          String email, String telefono, LocalDate fechaNacimiento,
                                          String especialidad, double sueldoBase,
                                          double honorarioPorClase, String turnoDisponibilidad) {
        if (!ValidationUtils.esDniValido(dni)) {
            throw new IllegalArgumentException("DNI de instructor inválido: " + dni);
        }
        if (!ValidationUtils.esEmailValido(email)) {
            throw new IllegalArgumentException("Correo electrónico inválido: " + email);
        }

        String dniSanitizado = ValidationUtils.sanitizarDni(dni);
        String id = IdGenerator.generarIdInstructor();
        String legajo = "LEG-" + id.substring(id.indexOf('-') + 1);

        Instructor instructor = new Instructor(
                id, dniSanitizado, nombre, apellido, email, telefono,
                fechaNacimiento, LocalDate.now(), legajo, especialidad,
                sueldoBase, honorarioPorClase, turnoDisponibilidad
        );

        return instructorRepository.guardar(instructor);
    }

    public Optional<Instructor> buscarPorId(String id) {
        return instructorRepository.buscarPorId(id);
    }

    public Optional<Instructor> buscarPorLegajo(String legajo) {
        return instructorRepository.buscarPorLegajo(legajo);
    }

    public List<Instructor> listarTodos() {
        return instructorRepository.listarTodos();
    }

    public List<Instructor> filtrarInstructores(String query) {
        if (query == null || query.isBlank()) {
            return listarTodos();
        }
        String q = query.trim().toLowerCase();
        return instructorRepository.listarTodos().stream()
                .filter(i -> i.getNombreCompleto().toLowerCase().contains(q)
                        || i.getEspecialidad().toLowerCase().contains(q)
                        || i.getLegajo().toLowerCase().contains(q)
                        || i.getDni().contains(q))
                .toList();
    }

    public void asignarClaseAInstructor(String instructorId, ClaseGrupal clase) {
        instructorRepository.buscarPorId(instructorId).ifPresent(i -> {
            i.asignarClase(clase);
            instructorRepository.guardar(i);
        });
    }

    public boolean eliminarInstructor(String id) {
        return instructorRepository.eliminarPorId(id);
    }

    public void guardar(Instructor instructor) {
        instructorRepository.guardar(instructor);
    }
}
