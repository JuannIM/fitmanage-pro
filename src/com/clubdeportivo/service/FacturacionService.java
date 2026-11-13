package com.clubdeportivo.service;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.PagoRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de administración contable, liquidación de cuotas y emisión de recibos.
 *
 * @author Alfonsina
 * @author Juan
 * @version 1.0.0
 */
public class FacturacionService {

    private final PagoRepository pagoRepository;
    private final SocioRepository socioRepository;

    public FacturacionService(PagoRepository pagoRepository, SocioRepository socioRepository) {
        this.pagoRepository = pagoRepository;
        this.socioRepository = socioRepository;
    }

    /**
     * Procesa el cobro formal de la cuota de un socio, computando descuentos y renovando vigencia.
     */
    public Pago cobrarCuota(String socioId, MetodoPago metodoPago, String concepto) {
        if (socioId == null || socioId.isBlank()) {
            throw new IllegalArgumentException("El ID del socio es obligatorio.");
        }
        if (metodoPago == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }

        Socio socio = socioRepository.buscarPorId(socioId)
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado: " + socioId));

        if (socio.getMembresiaActual() == null) {
            throw new IllegalStateException("El socio no posee una membresía activa configurada.");
        }

        double montoTotal = socio.calcularMontoTotal();
        if (!Double.isFinite(montoTotal) || montoTotal <= 0.0) {
            throw new IllegalStateException("El monto a cobrar debe ser un valor positivo mayor a cero.");
        }

        String comprobanteNum = IdGenerator.generarNumeroComprobante();
        String id = IdGenerator.generarIdPago();
        String conc = (concepto != null && !concepto.isBlank()) ? concepto.trim() :
                "Pago Cuota Mensual - " + socio.getMembresiaActual().getTipo().getNombre();

        Pago nuevoPago = new Pago(
                id, comprobanteNum, socio.getId(), montoTotal,
                LocalDateTime.now(), metodoPago, conc
        );

        pagoRepository.guardar(nuevoPago);

        // Actualizar estado del socio a AL_DIA y renovar vigencia por 30 días
        socio.getMembresiaActual().extenderPeriodo(30);
        socio.setEstadoCuota(EstadoCuota.AL_DIA);
        socioRepository.guardar(socio);

        return nuevoPago;
    }

    public double calcularTotalRecaudado() {
        return pagoRepository.listarTodos().stream()
                .mapToDouble(Pago::getMonto)
                .sum();
    }

    public List<Pago> listarTodos() {
        return pagoRepository.listarTodos();
    }

    public List<Pago> listarPagosPorSocio(String socioId) {
        return pagoRepository.buscarPorSocioId(socioId);
    }

    public Optional<Pago> buscarPorId(String id) {
        return pagoRepository.buscarPorId(id);
    }
}
