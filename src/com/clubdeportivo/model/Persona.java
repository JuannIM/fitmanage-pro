package com.clubdeportivo.model;

import com.clubdeportivo.util.ValidationUtils;
import java.time.LocalDate;
import java.time.Period;

/**
 * Clase base abstracta que encapsula los atributos y comportamientos comunes
 * de cualquier individuo vinculado al club deportivo (Socios, Instructores, Personal).
 *
 * @author Juan Pérez (Integrante 1 - Líder Técnico & Arquitecto Core POO)
 * @version 1.0.0
 */
public abstract class Persona {

    protected String id;
    protected String dni;
    protected String nombre;
    protected String apellido;
    protected String email;
    protected String telefono;
    protected LocalDate fechaNacimiento;
    protected LocalDate fechaRegistro;

    public Persona(String id, String dni, String nombre, String apellido,
                   String email, String telefono, LocalDate fechaNacimiento, LocalDate fechaRegistro) {
        this.id = id;
        setDni(dni);
        this.nombre = (nombre != null) ? nombre.trim() : "";
        this.apellido = (apellido != null) ? apellido.trim() : "";
        setEmail(email);
        this.telefono = (telefono != null) ? telefono.trim() : "";
        this.fechaNacimiento = (fechaNacimiento != null) ? fechaNacimiento : LocalDate.now().minusYears(18);
        this.fechaRegistro = (fechaRegistro != null) ? fechaRegistro : LocalDate.now();
    }

    // =========================================================================
    // MÉTODOS ABSTRACTOS POLIMÓRFICOS
    // =========================================================================

    /**
     * Retorna la representación formal y detallada de la ficha de la persona.
     */
    public abstract String mostrarDetalle();

    /**
     * Retorna el rol operativo que desempeña la persona en la institución.
     */
    public abstract String getRol();

    /**
     * Evalúa si la persona se encuentra en condiciones reglamentarias de ingresar.
     */
    public abstract boolean validarIngreso(TipoAcceso tipo);

    // =========================================================================
    // COMPORTAMIENTOS CONCRETOS
    // =========================================================================

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public int calcularEdad() {
        if (fechaNacimiento == null) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    // =========================================================================
    // GETTERS Y SETTERS CON VALIDACIÓN
    // =========================================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = ValidationUtils.sanitizarDni(dni);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null) ? nombre.trim() : "";
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = (apellido != null) ? apellido.trim() : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = (email != null) ? email.trim() : "";
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = (telefono != null) ? telefono.trim() : "";
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (DNI: %s)", getRol(), getNombreCompleto(), dni);
    }
}
