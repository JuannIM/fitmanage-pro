package com.clubdeportivo.service;

import com.clubdeportivo.model.ClaseGrupal;
import com.clubdeportivo.model.Instructor;
import com.clubdeportivo.model.Rutina;
import com.clubdeportivo.repository.ClaseRepository;
import com.clubdeportivo.repository.InstructorRepository;
import com.clubdeportivo.repository.RutinaRepository;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
    private final ClaseRepository claseRepository;
    private final RutinaRepository rutinaRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this(instructorRepository, null, null);
    }

    public InstructorService(InstructorRepository instructorRepository,
                             ClaseRepository claseRepository,
                             RutinaRepository rutinaRepository) {
        this.instructorRepository = Objects.requireNonNull(instructorRepository, "El repositorio de instructores es obligatorio.");
        this.claseRepository = claseRepository;
        this.rutinaRepository = rutinaRepository;
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
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del instructor no puede ser nulo ni estar en blanco.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido del instructor no puede ser nulo ni estar en blanco.");
        }
        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("La especialidad del instructor no puede ser nula ni estar en blanco.");
        }
        if (turnoDisponibilidad == null || turnoDisponibilidad.isBlank()) {
            throw new IllegalArgumentException("El turno de disponibilidad no puede ser nulo ni estar en blanco.");
        }
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
        if (telefono != null && !telefono.isBlank() && !ValidationUtils.esTelefonoValido(telefono)) {
            throw new IllegalArgumentException("Formato de teléfono inválido: " + telefono);
        }
        if (!Double.isFinite(sueldoBase) || sueldoBase < 0.0) {
            throw new IllegalArgumentException("El sueldo base debe ser un valor finito mayor o igual a 0.");
        }
        if (!Double.isFinite(honorarioPorClase) || honorarioPorClase < 0.0) {
            throw new IllegalArgumentException("El honorario por clase debe ser un valor finito mayor o igual a 0.");
        }

        String dniSanitizado = ValidationUtils.sanitizarDni(dni);
        if (instructorRepository.buscarPorDni(dniSanitizado).isPresent()) {
            throw new IllegalStateException("Ya existe un instructor registrado con el DNI " + dniSanitizado);
        }
        String emailTrimmed = email.trim();
        boolean emailDuplicado = instructorRepository.listarTodos().stream()
                .anyMatch(i -> i.getEmail() != null && i.getEmail().trim().equalsIgnoreCase(emailTrimmed));
        if (emailDuplicado) {
            throw new IllegalStateException("Ya existe un instructor registrado con el correo electrónico " + emailTrimmed);
        }

        String id = IdGenerator.generarIdInstructor();
        String legajo = "LEG-" + id.substring(id.indexOf('-') + 1);
        if (instructorRepository.buscarPorLegajo(legajo).isPresent()) {
            throw new IllegalStateException("Ya existe un instructor registrado con el legajo " + legajo);
        }

        Instructor instructor = new Instructor(
                id, dniSanitizado, nombre.trim(), apellido.trim(), emailTrimmed,
                telefono != null ? telefono.trim() : "",
                fechaNacimiento, LocalDate.now(), legajo, especialidad.trim(),
                sueldoBase, honorarioPorClase, turnoDisponibilidad.trim()
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
        if (id == null || id.isBlank()) {
            return false;
        }
        if (claseRepository != null) {
            for (ClaseGrupal c : claseRepository.listarTodos()) {
                if (c.getInstructor() != null && id.equals(c.getInstructor().getId())) {
                    throw new IllegalStateException("No se puede eliminar el instructor porque tiene clases grupales asignadas.");
                }
            }
        }
        if (rutinaRepository != null) {
            for (Rutina r : rutinaRepository.listarTodos()) {
                if (r.getInstructorCreadorId() != null && id.equals(r.getInstructorCreadorId())) {
                    throw new IllegalStateException("No se puede eliminar el instructor porque tiene rutinas creadas asociadas.");
                }
            }
        }
        return instructorRepository.eliminarPorId(id);
    }

    public void guardar(Instructor instructor) {
        instructorRepository.guardar(instructor);
    }
}
