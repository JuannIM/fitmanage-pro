package com.clubdeportivo.model;

import com.clubdeportivo.util.Formatters;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Representa una sesión grupal guiada con horario, sala y límite riguroso de cupos.
 * Implementa una relación de AGREGACIÓN con Instructor y Socio:
 * Si la clase se cancela o elimina, ni los socios ni el profesor desaparecen del club.
 *
 * @author Lucas Rodríguez (Integrante 3 - Gestor de Logística, Clases & Horarios)
 * @author María Gomez (Integrante 2 - Entrenamiento & Dominio Deportivo)
 * @version 1.0.0
 */
public class ClaseGrupal {

    private String id;
    private String nombre;
    private Disciplina disciplina;
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private int duracionMinutos;
    private String sala;
    private int cupoMaximo;
    // Agregación débil: el ciclo de vida del Instructor es independiente de la clase
    private Instructor instructor;
    // Agregación múltiple: los socios inscriptos
    private final List<Socio> sociosInscriptos;

    public ClaseGrupal(String id, String nombre, Disciplina disciplina,
                       DayOfWeek diaSemana, LocalTime horaInicio,
                       int duracionMinutos, String sala, int cupoMaximo,
                       Instructor instructor) {
        this.id = id;
        this.nombre = (nombre != null) ? nombre.trim() : "Clase Sin Nombre";
        this.disciplina = (disciplina != null) ? disciplina : Disciplina.FUNCIONAL;
        this.diaSemana = (diaSemana != null) ? diaSemana : DayOfWeek.MONDAY;
        this.horaInicio = (horaInicio != null) ? horaInicio : LocalTime.of(18, 0);
        this.duracionMinutos = Math.max(30, duracionMinutos);
        this.sala = (sala != null) ? sala.trim() : "Sala Principal";
        this.cupoMaximo = Math.max(1, cupoMaximo);
        this.instructor = instructor;
        this.sociosInscriptos = new ArrayList<>();

        if (instructor != null) {
            instructor.asignarClase(this);
        }
    }

    // =========================================================================
    // CONTROL ESTRICTO DE CUPOS E INSCRIPCIÓN (Lucas Rodríguez)
    // =========================================================================

    public boolean tieneCupoDisponible() {
        return sociosInscriptos.size() < cupoMaximo;
    }

    public int getCuposRestantes() {
        return Math.max(0, cupoMaximo - sociosInscriptos.size());
    }

    /**
     * Inscribe a un socio validando capacidad máxima, membresía y estado de cuota.
     * Arroja CupoAgotadoException si no hay plazas libres.
     */
    public boolean inscribirSocio(Socio socio) throws CupoAgotadoException {
        if (socio == null) return false;

        // Regla de Negocio: No duplicidad en la misma clase
        for (Socio s : sociosInscriptos) {
            if (s.getId().equals(socio.getId()) || s.getNumeroSocio().equals(socio.getNumeroSocio())) {
                return false; // Ya inscripto
            }
        }

        // Regla de Negocio: Control estricto de cupo
        if (!tieneCupoDisponible()) {
            throw new CupoAgotadoException(id, nombre, cupoMaximo);
        }

        // Regla de Negocio: Membresía y cuota al día
        if (!socio.validarIngreso(TipoAcceso.CLASE_GRUPAL)) {
            return false;
        }

        return sociosInscriptos.add(socio);
    }

    public boolean desinscribirSocio(String numeroSocio) {
        if (numeroSocio == null) return false;
        return sociosInscriptos.removeIf(s -> numeroSocio.equalsIgnoreCase(s.getNumeroSocio())
                || numeroSocio.equalsIgnoreCase(s.getId()));
    }

    public String mostrarFichaClase() {
        String diaStr = diaSemana.getDisplayName(TextStyle.FULL, new Locale("es", "AR"));
        String profStr = (instructor != null) ? instructor.getNombreCompleto() : "A confirmar";
        return String.format("[%s] %s (%s) | %s %s (%d min) | Sala: %s | Ocupación: %d/%d (Libres: %d) | Prof: %s",
                id, nombre, disciplina.getNombreVisible(), diaStr,
                Formatters.formatearHora(horaInicio), duracionMinutos, sala,
                sociosInscriptos.size(), cupoMaximo, getCuposRestantes(), profStr);
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public DayOfWeek getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DayOfWeek diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = Math.max(30, duracionMinutos);
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = Math.max(1, cupoMaximo);
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor nuevoInstructor) {
        if (this.instructor != null && this.instructor != nuevoInstructor) {
            this.instructor.removerClase(this);
        }
        this.instructor = nuevoInstructor;
        if (nuevoInstructor != null) {
            nuevoInstructor.asignarClase(this);
        }
    }

    public List<Socio> getSociosInscriptos() {
        return Collections.unmodifiableList(sociosInscriptos);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d/%d)", nombre, disciplina.getNombreVisible(), sociosInscriptos.size(), cupoMaximo);
    }
}
