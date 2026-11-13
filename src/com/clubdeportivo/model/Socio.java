package com.clubdeportivo.model;

import com.clubdeportivo.util.Formatters;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * Entidad central que representa a un socio activo o inactivo del club.
 * Implementa múltiples interfaces de negocio: Facturable, Autenticable y Reportable.
 * Demuestra herencia profunda, sobreescritura polimórfica y reglas de descuento.
 *
 * @author Juan
 * @author Alfonsina
 * @version 1.0.0
 */
public class Socio extends Persona implements Facturable, Autenticable, Reportable {

    public static final int DIAS_GRACIA_CUOTA = 5;

    private String numeroSocio;
    private EstadoCuota estadoCuota;
    private Membresia membresiaActual;
    private final List<Rutina> rutinasAsignadas;
    private final List<Asistencia> historialAsistencias;
    private double porcentajeDescuentoManual;

    public Socio(String id, String dni, String nombre, String apellido,
                 String email, String telefono, LocalDate fechaNacimiento,
                 LocalDate fechaRegistro, String numeroSocio,
                 EstadoCuota estadoCuota, Membresia membresiaActual) {
        super(id, dni, nombre, apellido, email, telefono, fechaNacimiento, fechaRegistro);
        this.numeroSocio = (numeroSocio != null) ? numeroSocio.trim() : id;
        this.estadoCuota = (estadoCuota != null) ? estadoCuota : EstadoCuota.PENDIENTE;
        this.membresiaActual = membresiaActual;
        this.rutinasAsignadas = new ArrayList<>();
        this.historialAsistencias = new ArrayList<>();
        this.porcentajeDescuentoManual = 0.0;
    }

    // =========================================================================
    // SOBREESCRITURA POLIMÓRFICA DE PERSONA
    // =========================================================================

    @Override
    public String getRol() {
        return "Socio";
    }

    @Override
    public String mostrarDetalle() {
        String plan = (membresiaActual != null) ? membresiaActual.getTipo().getNombre() : "Sin Membresía";
        return String.format("Socio [%s] %s | DNI: %s | Plan: %s | Cuota: %s | Antigüedad: %d meses",
                numeroSocio, getNombreCompleto(), dni, plan, estadoCuota.getEtiqueta(), calcularMesesAntiguedad());
    }

    @Override
    public boolean validarIngreso(TipoAcceso tipo) {
        if (tipo == null || membresiaActual == null) {
            return false;
        }
        if (estadoCuota == EstadoCuota.VENCIDO || estadoCuota == EstadoCuota.INACTIVO) {
            return false;
        }
        if (estadoCuota == EstadoCuota.AL_DIA && membresiaActual.estaVencida()) {
            return false;
        }
        if (estadoCuota == EstadoCuota.PENDIENTE
                && membresiaActual.estaVencida()
                && !membresiaActual.estaEnPeriodoGracia(DIAS_GRACIA_CUOTA)) {
            return false;
        }
        if (tipo == TipoAcceso.CLASE_GRUPAL) {
            return membresiaActual.getTipo().permiteClases();
        }
        return true;
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE FACTURABLE (Alfonsina)
    // =========================================================================

    public int calcularMesesAntiguedad() {
        if (fechaRegistro == null) return 0;
        Period p = Period.between(fechaRegistro, LocalDate.now());
        return p.getYears() * 12 + p.getMonths();
    }

    public boolean tieneDescuentoAntiguedad() {
        // Regla de Negocio: Más de 12 meses de antigüedad otorgan 10% de descuento automático
        return calcularMesesAntiguedad() >= 12;
    }

    @Override
    public double calcularMontoTotal() {
        if (membresiaActual == null) return 0.0;
        double base = membresiaActual.getPrecioBase();

        // [COLABORACIÓN - Alfonsina & Juan]:
        // Se aplica el 10% por antigüedad si supera el año en el club
        if (tieneDescuentoAntiguedad()) {
            base = base * 0.90;
        }

        // Descuento manual adicional si se hubiera configurado
        if (porcentajeDescuentoManual > 0.0) {
            base = base * (1.0 - (porcentajeDescuentoManual / 100.0));
        }

        return Math.max(0.0, base);
    }

    @Override
    public String generarComprobante() {
        double subtotal = (membresiaActual != null) ? membresiaActual.getPrecioBase() : 0.0;
        double total = calcularMontoTotal();
        double ahorro = subtotal - total;

        return String.format("""
                ====================================================
                        LIQUIDACIÓN DE CUOTA SOCIAL - FITMANAGE
                ====================================================
                Socio       : %s (%s)
                DNI         : %s
                Membresía   : %s
                Antigüedad  : %d meses %s
                ----------------------------------------------------
                Tarifa Base : %s
                Bonificación: -%s
                TOTAL A PAGAR: %s
                ====================================================
                """,
                getNombreCompleto(), numeroSocio, dni,
                (membresiaActual != null ? membresiaActual.getTipo().getNombre() : "-"),
                calcularMesesAntiguedad(),
                (tieneDescuentoAntiguedad() ? "[10% Antigüedad Aplicado]" : ""),
                Formatters.formatearMoneda(subtotal),
                Formatters.formatearMoneda(ahorro),
                Formatters.formatearMoneda(total));
    }

    @Override
    public boolean estaAlDia() {
        return estadoCuota == EstadoCuota.AL_DIA;
    }

    @Override
    public void aplicarDescuento(double porcentaje) {
        if (porcentaje >= 0.0 && porcentaje <= 100.0) {
            this.porcentajeDescuentoManual = porcentaje;
        }
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE AUTENTICABLE (Juan & Joel)
    // =========================================================================

    @Override
    public boolean autenticar(String credencial) {
        if (credencial == null || credencial.isBlank()) return false;
        String c = credencial.trim();
        return (dni != null && !dni.isBlank() && c.equalsIgnoreCase(dni))
                || (numeroSocio != null && !numeroSocio.isBlank() && c.equalsIgnoreCase(numeroSocio))
                || (email != null && !email.isBlank() && c.equalsIgnoreCase(email));
    }

    @Override
    public boolean validarAcceso(String puntoControl) {
        TipoAcceso tipo = "CLASES".equalsIgnoreCase(puntoControl) ? TipoAcceso.CLASE_GRUPAL : TipoAcceso.GENERAL;
        return validarIngreso(tipo);
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE REPORTABLE (Sara & Juan)
    // =========================================================================

    @Override
    public String generarReporteTexto() {
        return String.format("Reporte Socio: %s | Asistencias Totales: %d | Rutinas: %d",
                getNombreCompleto(), historialAsistencias.size(), rutinasAsignadas.size());
    }

    @Override
    public Map<String, Object> exportarMetricas() {
        Map<String, Object> metricas = new LinkedHashMap<>();
        metricas.put("numeroSocio", numeroSocio);
        metricas.put("nombreCompleto", getNombreCompleto());
        metricas.put("estadoCuota", estadoCuota.name());
        metricas.put("mesesAntiguedad", calcularMesesAntiguedad());
        metricas.put("asistenciasRegistradas", historialAsistencias.size());
        metricas.put("montoMensual", calcularMontoTotal());
        return metricas;
    }

    // =========================================================================
    // GESTIÓN DE RELACIONES (Rutinas y Asistencias)
    // =========================================================================

    public void asignarRutina(Rutina rutina) {
        if (rutina != null && !rutinasAsignadas.contains(rutina)) {
            this.rutinasAsignadas.add(rutina);
        }
    }

    public void removerRutina(String rutinaId) {
        if (rutinaId != null) {
            this.rutinasAsignadas.removeIf(r -> rutinaId.equals(r.getId()));
        }
    }

    public void registrarAsistencia(Asistencia asistencia) {
        if (asistencia != null) {
            this.historialAsistencias.add(asistencia);
        }
    }

    // Getters y Setters
    public String getNumeroSocio() {
        return numeroSocio;
    }

    public void setNumeroSocio(String numeroSocio) {
        if (numeroSocio == null || numeroSocio.isBlank()) {
            throw new IllegalArgumentException("El número de socio no puede ser nulo ni vacío.");
        }
        this.numeroSocio = numeroSocio.trim();
    }

    public EstadoCuota getEstadoCuota() {
        return estadoCuota;
    }

    public void setEstadoCuota(EstadoCuota estadoCuota) {
        if (estadoCuota == null) {
            throw new IllegalArgumentException("El estado de cuota no puede ser nulo.");
        }
        this.estadoCuota = estadoCuota;
    }

    public Membresia getMembresiaActual() {
        return membresiaActual;
    }

    public void setMembresiaActual(Membresia membresiaActual) {
        this.membresiaActual = membresiaActual;
    }

    public List<Rutina> getRutinasAsignadas() {
        return Collections.unmodifiableList(rutinasAsignadas);
    }

    public List<Asistencia> getHistorialAsistencias() {
        return Collections.unmodifiableList(historialAsistencias);
    }

    public double getPorcentajeDescuentoManual() {
        return porcentajeDescuentoManual;
    }
}
