package com.clubdeportivo.model;

import com.clubdeportivo.util.Formatters;
import java.time.LocalDate;
import java.util.*;

/**
 * Representa al personal técnico y docente a cargo del dictado de clases y prescripción de rutinas.
 * Implementa Reportable y demuestra polimorfismo sobre Persona.
 *
 * @author Juan
 * @author Merlina
 * @version 1.0.0
 */
public class Instructor extends Persona implements Reportable {

    private String legajo;
    private String especialidad;
    private double sueldoBase;
    private double honorarioPorClase;
    private final List<ClaseGrupal> clasesAsignadas;
    private String turnoDisponibilidad;

    public Instructor(String id, String dni, String nombre, String apellido,
                      String email, String telefono, LocalDate fechaNacimiento,
                      LocalDate fechaRegistro, String legajo, String especialidad,
                      double sueldoBase, double honorarioPorClase, String turnoDisponibilidad) {
        super(id, dni, nombre, apellido, email, telefono, fechaNacimiento, fechaRegistro);
        this.legajo = (legajo != null) ? legajo.trim() : id;
        this.especialidad = (especialidad != null) ? especialidad.trim() : "Acondicionamiento Físico";
        this.sueldoBase = Math.max(0.0, sueldoBase);
        this.honorarioPorClase = Math.max(0.0, honorarioPorClase);
        this.clasesAsignadas = new ArrayList<>();
        this.turnoDisponibilidad = (turnoDisponibilidad != null) ? turnoDisponibilidad.trim() : "Turno Completo";
    }

    // =========================================================================
    // SOBREESCRITURA POLIMÓRFICA DE PERSONA
    // =========================================================================

    @Override
    public String getRol() {
        return "Instructor";
    }

    @Override
    public String mostrarDetalle() {
        return String.format("Prof. [%s] %s | Esp: %s | Turno: %s | Clases Asignadas: %d | Base: %s",
                legajo, getNombreCompleto(), especialidad, turnoDisponibilidad,
                clasesAsignadas.size(), Formatters.formatearMoneda(sueldoBase));
    }

    @Override
    public boolean validarIngreso(TipoAcceso tipo) {
        // Un instructor siempre tiene acceso habilitado al predio deportivo
        return true;
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO Y LIQUIDACIÓN (Alfonsina & Merlina)
    // =========================================================================

    /**
     * Calcula la liquidación salarial estimada en base a su sueldo base y
     * el total de horas de clases grupales que tiene a su cargo mensualmente.
     */
    public double calcularLiquidacionMensual() {
        // 4 semanas de clases mensuales estimadas
        int clasesMensuales = clasesAsignadas.size() * 4;
        double variableClases = clasesMensuales * honorarioPorClase;
        // Bono por especialidad si dicta más de 3 disciplinas distintas
        long disciplinasDistintas = clasesAsignadas.stream()
                .map(ClaseGrupal::getDisciplina)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        double bono = (disciplinasDistintas > 3) ? 15000.0 : 0.0;
        return sueldoBase + variableClases + bono;
    }

    public void asignarClase(ClaseGrupal clase) {
        if (clase != null && !clasesAsignadas.contains(clase)) {
            clasesAsignadas.add(clase);
        }
    }

    public void removerClase(ClaseGrupal clase) {
        if (clase != null) {
            clasesAsignadas.remove(clase);
        }
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE REPORTABLE
    // =========================================================================

    @Override
    public String generarReporteTexto() {
        return String.format("Instructor %s (%s): %d clases semanales asignadas. Liquidación estimada: %s",
                getNombreCompleto(), legajo, clasesAsignadas.size(),
                Formatters.formatearMoneda(calcularLiquidacionMensual()));
    }

    @Override
    public Map<String, Object> exportarMetricas() {
        Map<String, Object> metricas = new LinkedHashMap<>();
        metricas.put("legajo", legajo);
        metricas.put("nombreCompleto", getNombreCompleto());
        metricas.put("especialidad", especialidad);
        metricas.put("clasesSemanales", clasesAsignadas.size());
        metricas.put("sueldoBase", sueldoBase);
        metricas.put("liquidacionEstimada", calcularLiquidacionMensual());
        return metricas;
    }

    // Getters y Setters
    public String getLegajo() {
        return legajo;
    }

    public void setLegajo(String legajo) {
        if (legajo == null || legajo.isBlank()) {
            throw new IllegalArgumentException("El legajo no puede ser nulo ni vacío.");
        }
        this.legajo = legajo.trim();
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("La especialidad no puede ser nula ni vacía.");
        }
        this.especialidad = especialidad.trim();
    }

    public double getSueldoBase() {
        return sueldoBase;
    }

    public void setSueldoBase(double sueldoBase) {
        this.sueldoBase = Math.max(0.0, sueldoBase);
    }

    public double getHonorarioPorClase() {
        return honorarioPorClase;
    }

    public void setHonorarioPorClase(double honorarioPorClase) {
        this.honorarioPorClase = Math.max(0.0, honorarioPorClase);
    }

    public List<ClaseGrupal> getClasesAsignadas() {
        return Collections.unmodifiableList(clasesAsignadas);
    }

    public String getTurnoDisponibilidad() {
        return turnoDisponibilidad;
    }

    public void setTurnoDisponibilidad(String turnoDisponibilidad) {
        if (turnoDisponibilidad == null || turnoDisponibilidad.isBlank()) {
            throw new IllegalArgumentException("El turno de disponibilidad no puede ser nulo ni vacío.");
        }
        this.turnoDisponibilidad = turnoDisponibilidad.trim();
    }
}
