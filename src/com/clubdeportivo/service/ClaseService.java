package com.clubdeportivo.service;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.ClaseRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.util.IdGenerator;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de coordinación de cronograma de actividades grupales y control de aforo.
 *
 * @author Lucas Rodríguez (Integrante 3 - Gestor de Logística, Clases & Horarios)
 * @author María Gomez (Integrante 2 - Entrenamiento & Dominio Deportivo)
 * @version 1.0.0
 */
public class ClaseService {

    private final ClaseRepository claseRepository;
    private final SocioRepository socioRepository;

    public ClaseService(ClaseRepository claseRepository, SocioRepository socioRepository) {
        this.claseRepository = claseRepository;
        this.socioRepository = socioRepository;
    }

    public ClaseGrupal crearClase(String nombre, Disciplina disciplina, DayOfWeek diaSemana,
                                  LocalTime horaInicio, int duracionMinutos, String sala,
                                  int cupoMaximo, Instructor instructor) {
        String id = IdGenerator.generarIdClase();
        ClaseGrupal clase = new ClaseGrupal(
                id, nombre, disciplina, diaSemana, horaInicio,
                duracionMinutos, sala, cupoMaximo, instructor
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
        return claseRepository.eliminarPorId(id);
    }

    public void guardar(ClaseGrupal clase) {
        claseRepository.guardar(clase);
    }
}
