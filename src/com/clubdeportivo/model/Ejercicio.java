package com.clubdeportivo.model;

import java.util.Locale;
import java.util.Objects;

/**
 * Representa un ejercicio físico individual con parámetros de carga y volumen.
 * Es un componente que vive en composición estricta dentro de una Rutina.
 *
 * @author Rocio
 * @version 1.0.0
 */
public class Ejercicio {

    private String id;
    private String nombre;
    private GrupoMuscular grupoMuscular;
    private int series;
    private int repeticiones;
    private double pesoSugeridoKg;
    private int descansoSegundos;
    private String notasTecnicas;

    public Ejercicio(String id, String nombre, GrupoMuscular grupoMuscular,
                     int series, int repeticiones, double pesoSugeridoKg,
                     int descansoSegundos, String notasTecnicas) {
        this.id = id;
        this.nombre = (nombre != null) ? nombre.trim() : "Ejercicio Genérico";
        this.grupoMuscular = (grupoMuscular != null) ? grupoMuscular : GrupoMuscular.CORE;
        this.series = Math.max(1, series);
        this.repeticiones = Math.max(1, repeticiones);
        this.pesoSugeridoKg = Math.max(0.0, pesoSugeridoKg);
        this.descansoSegundos = Math.max(15, descansoSegundos);
        this.notasTecnicas = (notasTecnicas != null) ? notasTecnicas.trim() : "Técnica estándar";
    }

    /**
     * Calcula el tonelaje o volumen total movilizado en este ejercicio.
     * Fórmula deportiva: Series * Repeticiones * Carga (Kg).
     */
    public double calcularVolumenTotal() {
        return (double) series * repeticiones * pesoSugeridoKg;
    }

    public String mostrarResumen() {
        return String.format(Locale.of("es", "AR"), "%s (%s): %d series x %d reps @ %.1f kg [Pausa: %ds]",
                nombre, grupoMuscular.getDescripcion(), series, repeticiones, pesoSugeridoKg, descansoSegundos);
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID del ejercicio no puede ser nulo ni vacío.");
        }
        this.id = id.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del ejercicio no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
    }

    public GrupoMuscular getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(GrupoMuscular grupoMuscular) {
        if (grupoMuscular == null) {
            throw new IllegalArgumentException("El grupo muscular no puede ser nulo.");
        }
        this.grupoMuscular = grupoMuscular;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        if (series <= 0) {
            throw new IllegalArgumentException("Las series deben ser mayores a 0.");
        }
        this.series = series;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        if (repeticiones <= 0) {
            throw new IllegalArgumentException("Las repeticiones deben ser mayores a 0.");
        }
        this.repeticiones = repeticiones;
    }

    public double getPesoSugeridoKg() {
        return pesoSugeridoKg;
    }

    public void setPesoSugeridoKg(double pesoSugeridoKg) {
        if (!Double.isFinite(pesoSugeridoKg) || pesoSugeridoKg < 0.0) {
            throw new IllegalArgumentException("El peso sugerido debe ser un número finito mayor o igual a 0.");
        }
        this.pesoSugeridoKg = pesoSugeridoKg;
    }

    public int getDescansoSegundos() {
        return descansoSegundos;
    }

    public void setDescansoSegundos(int descansoSegundos) {
        if (descansoSegundos < 0) {
            throw new IllegalArgumentException("El descanso en segundos no puede ser negativo.");
        }
        this.descansoSegundos = descansoSegundos;
    }

    public String getNotasTecnicas() {
        return notasTecnicas;
    }

    public void setNotasTecnicas(String notasTecnicas) {
        if (notasTecnicas == null) {
            throw new IllegalArgumentException("Las notas técnicas no pueden ser nulas.");
        }
        this.notasTecnicas = notasTecnicas.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ejercicio ejercicio)) return false;
        return Objects.equals(id, ejercicio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return mostrarResumen();
    }
}
