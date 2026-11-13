package com.clubdeportivo.model;

import com.clubdeportivo.util.ValidationUtils;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Clase base abstracta que encapsula los atributos y comportamientos comunes
 * de cualquier individuo vinculado al club deportivo (Socios, Instructores, Personal).
 *
 * @author Juan
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
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
        this.id = id;
        this.dni = ValidationUtils.sanitizarDni(dni);
        this.nombre = (nombre != null) ? nombre.trim() : "";
        this.apellido = (apellido != null) ? apellido.trim() : "";
        this.email = (email != null) ? email.trim() : "";
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
        if (fechaNacimiento == null || fechaNacimiento.isAfter(LocalDate.now())) return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    // =========================================================================
    // GETTERS Y SETTERS CON VALIDACIÓN
    // =========================================================================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID no puede ser nulo ni vacío.");
        }
        this.id = id.trim();
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        if (dni == null) {
            throw new IllegalArgumentException("El DNI no puede ser nulo.");
        }
        this.dni = ValidationUtils.sanitizarDni(dni);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre no puede ser nulo.");
        }
        this.nombre = nombre.trim();
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        if (apellido == null) {
            throw new IllegalArgumentException("El apellido no puede ser nulo.");
        }
        this.apellido = apellido.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("El email no puede ser nulo.");
        }
        this.email = email.trim();
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
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
        this.fechaNacimiento = fechaNacimiento;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return Objects.equals(id, persona.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (DNI: %s)", getRol(), getNombreCompleto(), dni);
    }
}
