package com.clubdeportivo.service;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para administración y auditoría del padrón de socios.
 *
 * @author Juan
 * @author Alfonsina
 * @version 1.0.0
 */
public class SocioService {

    private final SocioRepository socioRepository;

    public SocioService(SocioRepository socioRepository) {
        this.socioRepository = socioRepository;
    }

    public Socio registrarSocio(String dni, String nombre, String apellido,
                                String email, String telefono, LocalDate fechaNacimiento,
                                TipoMembresia tipoMembresia) {
        if (!ValidationUtils.esDniValido(dni)) {
            throw new IllegalArgumentException("DNI inválido. Debe contener entre 7 y 8 dígitos numéricos.");
        }
        if (!ValidationUtils.esEmailValido(email)) {
            throw new IllegalArgumentException("Formato de correo electrónico inválido: " + email);
        }

        String dniSanitizado = ValidationUtils.sanitizarDni(dni);
        if (socioRepository.buscarPorDni(dniSanitizado).isPresent()) {
            throw new IllegalStateException("Ya existe un socio registrado con el DNI " + dniSanitizado);
        }

        String id = IdGenerator.generarIdSocio();
        String numSocio = id; // o formato personalizado
        LocalDate hoy = LocalDate.now();

        Membresia membresia = new Membresia(
                "MEM-" + id,
                tipoMembresia != null ? tipoMembresia : TipoMembresia.MUSCULACION,
                tipoMembresia != null ? tipoMembresia.getPrecioBase() : TipoMembresia.MUSCULACION.getPrecioBase(),
                hoy,
                hoy.plusMonths(1),
                true
        );

        Socio nuevoSocio = new Socio(
                id, dniSanitizado, nombre, apellido, email, telefono,
                fechaNacimiento, hoy, numSocio, EstadoCuota.AL_DIA, membresia
        );

        return socioRepository.guardar(nuevoSocio);
    }

    public Optional<Socio> buscarPorId(String id) {
        return socioRepository.buscarPorId(id);
    }

    public Optional<Socio> buscarPorDni(String dni) {
        return socioRepository.buscarPorDni(ValidationUtils.sanitizarDni(dni));
    }

    public Optional<Socio> buscarPorNumeroSocio(String numSocio) {
        return socioRepository.buscarPorNumeroSocio(numSocio);
    }

    public List<Socio> listarTodos() {
        return socioRepository.listarTodos();
    }

    public List<Socio> filtrarSocios(String query) {
        if (query == null || query.isBlank()) {
            return listarTodos();
        }
        String q = query.trim().toLowerCase();
        return socioRepository.listarTodos().stream()
                .filter(s -> s.getNombreCompleto().toLowerCase().contains(q)
                        || s.getDni().contains(q)
                        || s.getNumeroSocio().toLowerCase().contains(q)
                        || s.getEmail().toLowerCase().contains(q))
                .toList();
    }

    public void actualizarEstadoCuota(String socioId, EstadoCuota nuevoEstado) {
        socioRepository.buscarPorId(socioId).ifPresent(s -> {
            s.setEstadoCuota(nuevoEstado);
            socioRepository.guardar(s);
        });
    }

    public boolean eliminarSocio(String socioId) {
        return socioRepository.eliminarPorId(socioId);
    }

    public void guardar(Socio socio) {
        socioRepository.guardar(socio);
    }
}
