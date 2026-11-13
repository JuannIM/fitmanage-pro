package com.clubdeportivo.service;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.ClaseRepository;
import com.clubdeportivo.repository.InstructorRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.util.IdGenerator;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Servicio de coordinación de cronograma de actividades grupales y control de aforo.
 *
 * @author Merlina
 * @author Rocio
 * @version 1.0.0
 */
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final SocioRepository socioRepository;
    private final InstructorRepository instructorRepository;

    public ClaseService(ClaseRepository claseRepository, SocioRepository socioRepository) {
        this(claseRepository, socioRepository, null);
    }

    public ClaseService(ClaseRepository claseRepository,
                        SocioRepository socioRepository,
                        InstructorRepository instructorRepository) {
        this.claseRepository = Objects.requireNonNull(claseRepository, "El repositorio de clases es obligatorio.");
        this.socioRepository = Objects.requireNonNull(socioRepository, "El repositorio de socios es obligatorio.");
        this.instructorRepository = instructorRepository;
    }

    public ClaseGrupal crearClase(String nombre, Disciplina disciplina, DayOfWeek diaSemana,
                                  LocalTime horaInicio, int duracionMinutos, String sala,
                                  int cupoMaximo, Instructor instructor) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la clase no puede ser nulo ni estar en blanco.");
        }
        if (sala == null || sala.isBlank()) {
            throw new IllegalArgumentException("La sala de la clase no puede ser nula ni estar en blanco.");
        }
        if (disciplina == null) {
            throw new IllegalArgumentException("La disciplina de la clase es obligatoria.");
        }
        if (diaSemana == null) {
            throw new IllegalArgumentException("El día de la semana es obligatorio.");
        }
        if (horaInicio == null) {
            throw new IllegalArgumentException("La hora de inicio es obligatoria.");
        }
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duración en minutos debe ser mayor a 0.");
        }
        if (cupoMaximo <= 0) {
            throw new IllegalArgumentException("El cupo máximo debe ser mayor a 0.");
        }
        if (instructor == null) {
            throw new IllegalArgumentException("El instructor de la clase es obligatorio.");
        }

        String salaTrimmed = sala.trim();
        int inicioA = horaInicio.toSecondOfDay();
        int finA = inicioA + duracionMinutos * 60;

        for (ClaseGrupal existente : claseRepository.listarTodos()) {
            if (existente.getDiaSemana() != diaSemana || existente.getHoraInicio() == null) {
                continue;
            }
            int inicioB = existente.getHoraInicio().toSecondOfDay();
            int finB = inicioB + existente.getDuracionMinutos() * 60;
            boolean seSolapa = inicioA < finB && inicioB < finA;
            if (!seSolapa) {
                continue;
            }

            if (existente.getSala() != null && existente.getSala().trim().equalsIgnoreCase(salaTrimmed)) {
                throw new IllegalStateException("Conflicto de horario en la sala " + salaTrimmed
                        + " con la clase " + existente.getNombre());
            }
            if (existente.getInstructor() != null
                    && instructor.getId() != null
                    && instructor.getId().equals(existente.getInstructor().getId())) {
                throw new IllegalStateException("El instructor ya tiene otra clase asignada en ese horario.");
            }
        }

        String id = IdGenerator.generarIdClase();
        ClaseGrupal clase = new ClaseGrupal(
                id, nombre.trim(), disciplina, diaSemana, horaInicio,
                duracionMinutos, salaTrimmed, cupoMaximo, instructor
        );
        return claseRepository.guardar(clase);
    }

    public boolean inscribirSocioEnClase(String claseId, String socioId) throws CupoAgotadoException {
        ClaseGrupal clase = claseRepository.buscarPorId(claseId)
                .orElseThrow(() -> new IllegalArgumentException("Clase no encontrada: " + claseId));

        Socio socio = socioRepository.buscarPorId(socioId)
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado: " + socioId));

        boolean inscripto = clase.inscribirSocio(socio);
        if (inscripto) {
            claseRepository.guardar(clase);
        }
        return inscripto;
    }

    public String obtenerMotivoRechazoInscripcion(String claseId, String socioId) {
        if (claseId == null || claseId.isBlank()) {
            return "Clase no encontrada.";
        }
        Optional<ClaseGrupal> optClase = claseRepository.buscarPorId(claseId);
        if (optClase.isEmpty()) {
            return "Clase no encontrada.";
        }
        if (socioId == null || socioId.isBlank()) {
            return "Socio no encontrado.";
        }
        Optional<Socio> optSocio = socioRepository.buscarPorId(socioId);
        if (optSocio.isEmpty()) {
            return "Socio no encontrado.";
        }

        ClaseGrupal clase = optClase.get();
        Socio socio = optSocio.get();

        boolean yaInscripto = clase.getSociosInscriptos().stream()
                .anyMatch(s -> Objects.equals(s.getId(), socio.getId())
                        || Objects.equals(s.getNumeroSocio(), socio.getNumeroSocio()));
        if (yaInscripto) {
            return "El socio ya está inscripto en la clase.";
        }
        if (!clase.tieneCupoDisponible()) {
            return "La clase no tiene cupos disponibles.";
        }

        Membresia membresia = socio.getMembresiaActual();
        EstadoCuota estado = socio.getEstadoCuota();
        if (membresia == null
                || estado == EstadoCuota.VENCIDO
                || estado == EstadoCuota.INACTIVO
                || (estado == EstadoCuota.AL_DIA && membresia.estaVencida())
                || (estado == EstadoCuota.PENDIENTE && membresia.estaVencida()
                    && !membresia.estaEnPeriodoGracia(Socio.DIAS_GRACIA_CUOTA))) {
            return "La cuota del socio está vencida o inactiva.";
        }
        if (!membresia.getTipo().permiteClases()) {
            return "La membresía del socio no habilita clases grupales.";
        }
        return null;
    }

    public boolean desinscribirSocioDeClase(String claseId, String numeroSocio) {
        Optional<ClaseGrupal> optClase = claseRepository.buscarPorId(claseId);
        if (optClase.isPresent()) {
            ClaseGrupal clase = optClase.get();
            boolean removido = clase.desinscribirSocio(numeroSocio);
            if (removido) {
                claseRepository.guardar(clase);
            }
            return removido;
        }
        return false;
    }

    public Optional<ClaseGrupal> buscarPorId(String id) {
        return claseRepository.buscarPorId(id);
    }

    public List<ClaseGrupal> listarTodas() {
        return claseRepository.listarTodos();
    }

    public List<ClaseGrupal> listarPorDisciplina(Disciplina disciplina) {
        return claseRepository.listarTodos().stream()
                .filter(c -> c.getDisciplina() == disciplina)
                .toList();
    }

    public List<ClaseGrupal> listarPorDia(DayOfWeek dia) {
        return claseRepository.listarTodos().stream()
                .filter(c -> c.getDiaSemana() == dia)
                .toList();
    }

    public boolean eliminarClase(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        Optional<ClaseGrupal> optClase = claseRepository.buscarPorId(id);
        if (optClase.isEmpty()) {
            return false;
        }
        ClaseGrupal clase = optClase.get();
        Instructor instructor = clase.getInstructor();
        if (instructor != null) {
            instructor.removerClase(clase);
            if (instructorRepository != null && instructor.getId() != null && !instructor.getId().isBlank()) {
                instructorRepository.buscarPorId(instructor.getId()).ifPresent(repoInst -> {
                    repoInst.removerClase(clase);
                    instructorRepository.guardar(repoInst);
                });
            }
        }
        return claseRepository.eliminarPorId(id);
    }

    public void guardar(ClaseGrupal clase) {
        claseRepository.guardar(clase);
    }
}
