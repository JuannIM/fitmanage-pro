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
 * @author Sofia Fernández (Integrante 4 - Ingeniera de Finanzas & Facturación)
 * @author Juan Pérez (Integrante 1 - Arquitectura Core)
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
        Socio socio = socioRepository.buscarPorId(socioId)
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado: " + socioId));

        double montoTotal = socio.calcularMontoTotal();
        String comprobanteNum = IdGenerator.generarNumeroComprobante();
        String id = "PAG-" + System.currentTimeMillis();
        String conc = (concepto != null && !concepto.isBlank()) ? concepto :
                "Pago Cuota Mensual - " + (socio.getMembresiaActual() != null ? socio.getMembresiaActual().getTipo().getNombre() : "General");

        Pago nuevoPago = new Pago(
                id, comprobanteNum, socio.getId(), montoTotal,
                LocalDateTime.now(), metodoPago, conc
        );

        pagoRepository.guardar(nuevoPago);

        // Actualizar estado del socio a AL_DIA y renovar vigencia por 30 días
        socio.setEstadoCuota(EstadoCuota.AL_DIA);
        if (socio.getMembresiaActual() != null) {
            socio.getMembresiaActual().extenderPeriodo(30);
        }
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
