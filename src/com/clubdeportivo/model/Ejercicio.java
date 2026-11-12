package com.clubdeportivo.model;

import com.clubdeportivo.util.Formatters;

/**
 * Representa un ejercicio físico individual con parámetros de carga y volumen.
 * Es un componente que vive en composición estricta dentro de una Rutina.
 *
 * @author María Gomez (Integrante 2 - Especialista en Entrenamiento & Dominio Deportivo)
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
        return String.format("%s (%s): %d series x %d reps @ %.1f kg [Pausa: %ds]",
                nombre, grupoMuscular.getDescripcion(), series, repeticiones, pesoSugeridoKg, descansoSegundos);
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

    public GrupoMuscular getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(GrupoMuscular grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = Math.max(1, series);
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = Math.max(1, repeticiones);
    }

    public double getPesoSugeridoKg() {
        return pesoSugeridoKg;
    }

    public void setPesoSugeridoKg(double pesoSugeridoKg) {
        this.pesoSugeridoKg = Math.max(0.0, pesoSugeridoKg);
    }

    public int getDescansoSegundos() {
        return descansoSegundos;
    }

    public void setDescansoSegundos(int descansoSegundos) {
        this.descansoSegundos = Math.max(15, descansoSegundos);
    }

    public String getNotasTecnicas() {
        return notasTecnicas;
    }

    public void setNotasTecnicas(String notasTecnicas) {
        this.notasTecnicas = notasTecnicas;
    }

    @Override
    public String toString() {
        return mostrarResumen();
    }
}
