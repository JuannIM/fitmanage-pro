package com.clubdeportivo.service;

import com.clubdeportivo.model.Ejercicio;
import com.clubdeportivo.model.GrupoMuscular;
import com.clubdeportivo.model.Rutina;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.repository.InstructorRepository;
import com.clubdeportivo.repository.RutinaRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Servicio de diseño deportivo, cálculo de volumen global y prescripción de rutinas.
 *
 * @author Rocio
 * @author Juan
 * @version 1.0.0
 */
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final SocioRepository socioRepository;
    private final InstructorRepository instructorRepository;

    public RutinaService(RutinaRepository rutinaRepository, SocioRepository socioRepository) {
        this(rutinaRepository, socioRepository, null);
    }

    public RutinaService(RutinaRepository rutinaRepository,
                         SocioRepository socioRepository,
                         InstructorRepository instructorRepository) {
        this.rutinaRepository = Objects.requireNonNull(rutinaRepository, "El repositorio de rutinas es obligatorio.");
        this.socioRepository = Objects.requireNonNull(socioRepository, "El repositorio de socios es obligatorio.");
        this.instructorRepository = instructorRepository;
    }

    public Rutina crearRutina(String nombre, String objetivo, String nivelDificultad,
                              String instructorCreadorId) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la rutina no puede ser nulo ni estar en blanco.");
        }
        if (objetivo == null || objetivo.isBlank()) {
            throw new IllegalArgumentException("El objetivo de la rutina no puede ser nulo ni estar en blanco.");
        }
        if (nivelDificultad == null || nivelDificultad.isBlank()) {
            throw new IllegalArgumentException("El nivel de dificultad no puede ser nulo ni estar en blanco.");
        }
        if (instructorCreadorId == null || instructorCreadorId.isBlank()) {
            throw new IllegalArgumentException("El ID del instructor creador es obligatorio.");
        }
        String instIdTrimmed = instructorCreadorId.trim();
        if (instructorRepository != null && instructorRepository.buscarPorId(instIdTrimmed).isEmpty()) {
            throw new IllegalArgumentException("Instructor no encontrado: " + instIdTrimmed);
        }

        String id = IdGenerator.generarIdRutina();
        LocalDate hoy = LocalDate.now();
        Rutina rutina = new Rutina(id, nombre.trim(), objetivo.trim(), nivelDificultad.trim(),
                hoy, hoy.plusMonths(2), instIdTrimmed);
        return rutinaRepository.guardar(rutina);
    }

    public Ejercicio agregarEjercicioARutina(String rutinaId, String nombreEjercicio,
                                            GrupoMuscular grupo, int series, int reps,
                                            double pesoKg, int descansoSegundos, String notas) {
        if (rutinaId == null || rutinaId.isBlank()) {
            throw new IllegalArgumentException("El ID de la rutina es obligatorio.");
        }
        if (nombreEjercicio == null || nombreEjercicio.isBlank()) {
            throw new IllegalArgumentException("El nombre del ejercicio no puede ser nulo ni estar en blanco.");
        }
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo muscular es obligatorio.");
        }
        if (series <= 0) {
            throw new IllegalArgumentException("La cantidad de series debe ser mayor a 0.");
        }
        if (reps <= 0) {
            throw new IllegalArgumentException("La cantidad de repeticiones debe ser mayor a 0.");
        }
        if (!Double.isFinite(pesoKg) || pesoKg < 0.0) {
            throw new IllegalArgumentException("El peso en kg debe ser un valor finito mayor o igual a 0.");
        }
        if (descansoSegundos < 0) {
            throw new IllegalArgumentException("El descanso en segundos no puede ser negativo.");
        }

        Rutina rutina = rutinaRepository.buscarPorId(rutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + rutinaId));

        String ejId = IdGenerator.generarIdEjercicio();
        Ejercicio ejercicio = new Ejercicio(ejId, nombreEjercicio.trim(), grupo, series, reps, pesoKg, descansoSegundos, notas);

        // Relación de composición: la rutina agrega el ejercicio
        rutina.agregarEjercicio(ejercicio);
        rutinaRepository.guardar(rutina);
        return ejercicio;
    }

    public boolean eliminarEjercicioDeRutina(String rutinaId, String ejercicioId) {
        Optional<Rutina> optRutina = rutinaRepository.buscarPorId(rutinaId);
        if (optRutina.isPresent()) {
            Rutina r = optRutina.get();
            boolean removido = r.eliminarEjercicio(ejercicioId);
            if (removido) {
                rutinaRepository.guardar(r);
            }
            return removido;
        }
        return false;
    }

    public void asignarRutinaASocio(String rutinaId, String socioId) {
        Rutina rutina = rutinaRepository.buscarPorId(rutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + rutinaId));
        Socio socio = socioRepository.buscarPorId(socioId)
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado: " + socioId));

        socio.asignarRutina(rutina);
        socioRepository.guardar(socio);
    }

    public Optional<Rutina> buscarPorId(String id) {
        return rutinaRepository.buscarPorId(id);
    }

    public List<Rutina> listarTodas() {
        return rutinaRepository.listarTodos();
    }

    public boolean eliminarRutina(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        boolean eliminada = rutinaRepository.eliminarPorId(id);
        if (eliminada) {
            for (Socio s : socioRepository.listarTodos()) {
                boolean teniaRutina = s.getRutinasAsignadas().stream()
                        .anyMatch(r -> id.equals(r.getId()));
                if (teniaRutina) {
                    s.removerRutina(id);
                    socioRepository.guardar(s);
                }
            }
        }
        return eliminada;
    }

    public void guardar(Rutina rutina) {
        rutinaRepository.guardar(rutina);
    }
}
