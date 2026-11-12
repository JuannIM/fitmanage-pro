package com.clubdeportivo.service;

import com.clubdeportivo.model.Ejercicio;
import com.clubdeportivo.model.GrupoMuscular;
import com.clubdeportivo.model.Rutina;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.repository.RutinaRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.util.IdGenerator;

import java.time.LocalDate;
import java.util.List;
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

    public RutinaService(RutinaRepository rutinaRepository, SocioRepository socioRepository) {
        this.rutinaRepository = rutinaRepository;
        this.socioRepository = socioRepository;
    }

    public Rutina crearRutina(String nombre, String objetivo, String nivelDificultad,
                              String instructorCreadorId) {
        String id = IdGenerator.generarIdRutina();
        LocalDate hoy = LocalDate.now();
        Rutina rutina = new Rutina(id, nombre, objetivo, nivelDificultad, hoy, hoy.plusMonths(2), instructorCreadorId);
        return rutinaRepository.guardar(rutina);
    }

    public Ejercicio agregarEjercicioARutina(String rutinaId, String nombreEjercicio,
                                            GrupoMuscular grupo, int series, int reps,
                                            double pesoKg, int descansoSegundos, String notas) {
        Rutina rutina = rutinaRepository.buscarPorId(rutinaId)
                .orElseThrow(() -> new IllegalArgumentException("Rutina no encontrada: " + rutinaId));

        String ejId = IdGenerator.generarIdEjercicio();
        Ejercicio ejercicio = new Ejercicio(ejId, nombreEjercicio, grupo, series, reps, pesoKg, descansoSegundos, notas);

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
        return rutinaRepository.eliminarPorId(id);
    }

    public void guardar(Rutina rutina) {
        rutinaRepository.guardar(rutina);
    }
}
