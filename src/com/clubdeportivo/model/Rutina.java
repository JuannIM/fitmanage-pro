package com.clubdeportivo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Contenedor de planificación de entrenamiento físico.
 * Implementa una relación de COMPOSICIÓN FUERTE con Ejercicio:
 * La rutina crea, administra y destruye su propia colección de ejercicios.
 *
 * @author Rocio
 * @author Juan
 * @version 1.0.0
 */
public class Rutina {

    private String id;
    private String nombre;
    private String objetivo;
    private String nivelDificultad;
    private LocalDate fechaCreacion;
    private LocalDate fechaRevision;
    private String instructorCreadorId;
    // Composición fuerte: la rutina posee la propiedad exclusiva del ciclo de vida de los ejercicios
    private final List<Ejercicio> ejercicios;

    public Rutina(String id, String nombre, String objetivo, String nivelDificultad,
                  LocalDate fechaCreacion, LocalDate fechaRevision, String instructorCreadorId) {
        this.id = id;
        this.nombre = (nombre != null) ? nombre.trim() : "Rutina General";
        this.objetivo = (objetivo != null) ? objetivo.trim() : "Acondicionamiento General";
        this.nivelDificultad = (nivelDificultad != null) ? nivelDificultad.trim() : "Intermedio";
        this.fechaCreacion = (fechaCreacion != null) ? fechaCreacion : LocalDate.now();
        this.fechaRevision = (fechaRevision != null) ? fechaRevision : LocalDate.now().plusMonths(2);
        this.instructorCreadorId = instructorCreadorId;
        this.ejercicios = new ArrayList<>();
    }

    // =========================================================================
    // MÉTODOS DE COMPOSICIÓN Y GESTIÓN DE EJERCICIOS
    // =========================================================================

    public void agregarEjercicio(Ejercicio ejercicio) {
        if (ejercicio == null) return;
        if (ejercicio.getId() != null) {
            for (Ejercicio existente : ejercicios) {
                if (ejercicio.getId().equals(existente.getId())) {
                    return;
                }
            }
        } else if (ejercicios.contains(ejercicio)) {
            return;
        }
        this.ejercicios.add(ejercicio);
    }

    public boolean eliminarEjercicio(String ejercicioId) {
        if (ejercicioId == null) return false;
        return this.ejercicios.removeIf(e -> ejercicioId.equals(e.getId()));
    }

    public void vaciarEjercicios() {
        this.ejercicios.clear();
    }

    public List<Ejercicio> getEjercicios() {
        return Collections.unmodifiableList(ejercicios);
    }

    public int getCantidadEjercicios() {
        return ejercicios.size();
    }

    /**
     * Calcula la suma agregada del tonelaje o volumen de carga de toda la rutina.
     * sum(series * reps * pesoKg).
     */
    public double calcularVolumenGlobal() {
        double total = 0.0;
        for (Ejercicio e : ejercicios) {
            total += e.calcularVolumenTotal();
        }
        return total;
    }

    public String mostrarFichaRutina() {
        Locale locale = Locale.of("es", "AR");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(locale, "=== RUTINA: %s (ID: %s) ===\n", nombre, id));
        sb.append(String.format(locale, "Objetivo: %s | Nivel: %s\n", objetivo, nivelDificultad));
        sb.append(String.format(locale, "Ejercicios (%d) - Volumen Total: %.1f kg acumulados\n",
                ejercicios.size(), calcularVolumenGlobal()));
        for (int i = 0; i < ejercicios.size(); i++) {
            sb.append(String.format(locale, "  [%d] %s\n", i + 1, ejercicios.get(i).mostrarResumen()));
        }
        return sb.toString();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID de la rutina no puede ser nulo ni vacío.");
        }
        this.id = id.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la rutina no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        if (objetivo == null || objetivo.isBlank()) {
            throw new IllegalArgumentException("El objetivo de la rutina no puede ser nulo ni vacío.");
        }
        this.objetivo = objetivo.trim();
    }

    public String getNivelDificultad() {
        return nivelDificultad;
    }

    public void setNivelDificultad(String nivelDificultad) {
        if (nivelDificultad == null || nivelDificultad.isBlank()) {
            throw new IllegalArgumentException("El nivel de dificultad no puede ser nulo ni vacío.");
        }
        this.nivelDificultad = nivelDificultad.trim();
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        if (fechaCreacion == null) {
            throw new IllegalArgumentException("La fecha de creación no puede ser nula.");
        }
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaRevision() {
        return fechaRevision;
    }

    public void setFechaRevision(LocalDate fechaRevision) {
        if (fechaRevision == null) {
            throw new IllegalArgumentException("La fecha de revisión no puede ser nula.");
        }
        this.fechaRevision = fechaRevision;
    }

    public String getInstructorCreadorId() {
        return instructorCreadorId;
    }

    public void setInstructorCreadorId(String instructorCreadorId) {
        if (instructorCreadorId == null || instructorCreadorId.isBlank()) {
            throw new IllegalArgumentException("El ID del instructor creador no puede ser nulo ni vacío.");
        }
        this.instructorCreadorId = instructorCreadorId.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rutina rutina)) return false;
        return Objects.equals(id, rutina.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %d ejer.)", nombre, nivelDificultad, ejercicios.size());
    }
}
