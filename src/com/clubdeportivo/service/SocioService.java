package com.clubdeportivo.service;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.AsistenciaRepository;
import com.clubdeportivo.repository.ClaseRepository;
import com.clubdeportivo.repository.PagoRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
    private final ClaseRepository claseRepository;
    private final PagoRepository pagoRepository;
    private final AsistenciaRepository asistenciaRepository;

    public SocioService(SocioRepository socioRepository) {
        this(socioRepository, null, null, null);
    }

    public SocioService(SocioRepository socioRepository,
                        ClaseRepository claseRepository,
                        PagoRepository pagoRepository,
                        AsistenciaRepository asistenciaRepository) {
        this.socioRepository = Objects.requireNonNull(socioRepository, "El repositorio de socios es obligatorio.");
        this.claseRepository = claseRepository;
        this.pagoRepository = pagoRepository;
        this.asistenciaRepository = asistenciaRepository;
    }

    public Socio registrarSocio(String dni, String nombre, String apellido,
                                String email, String telefono, LocalDate fechaNacimiento,
                                TipoMembresia tipoMembresia) {
        validarDatosSocio(dni, nombre, apellido, email, telefono, fechaNacimiento);

        String dniSanitizado = ValidationUtils.sanitizarDni(dni);
        if (socioRepository.buscarPorDni(dniSanitizado).isPresent()) {
            throw new IllegalStateException("Ya existe un socio registrado con el DNI " + dniSanitizado);
        }

        String emailTrimmed = email.trim();
        if (existeOtroSocioConEmail(emailTrimmed, null)) {
            throw new IllegalStateException("Ya existe un socio registrado con el correo electrónico " + emailTrimmed);
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
                id, dniSanitizado, nombre.trim(), apellido.trim(), emailTrimmed,
                telefono != null ? telefono.trim() : "",
                fechaNacimiento, hoy, numSocio, EstadoCuota.AL_DIA, membresia
        );

        return socioRepository.guardar(nuevoSocio);
    }

    public Socio actualizarSocio(Socio socio) {
        if (socio == null) {
            throw new IllegalArgumentException("El socio a actualizar no puede ser nulo.");
        }
        if (socio.getId() == null || socio.getId().isBlank()) {
            throw new IllegalArgumentException("El ID del socio es obligatorio para actualizar.");
        }
        socioRepository.buscarPorId(socio.getId())
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado: " + socio.getId()));

        validarDatosSocio(socio.getDni(), socio.getNombre(), socio.getApellido(),
                socio.getEmail(), socio.getTelefono(), socio.getFechaNacimiento());

        String dniSanitizado = ValidationUtils.sanitizarDni(socio.getDni());
        socio.setDni(dniSanitizado);

        Optional<Socio> porDni = socioRepository.buscarPorDni(dniSanitizado);
        if (porDni.isPresent() && !porDni.get().getId().equals(socio.getId())) {
            throw new IllegalStateException("Ya existe otro socio registrado con el DNI " + dniSanitizado);
        }

        String emailTrimmed = socio.getEmail().trim();
        if (existeOtroSocioConEmail(emailTrimmed, socio.getId())) {
            throw new IllegalStateException("Ya existe otro socio registrado con el correo electrónico " + emailTrimmed);
        }

        return socioRepository.guardar(socio);
    }

    private void validarDatosSocio(String dni, String nombre, String apellido,
                                   String email, String telefono, LocalDate fechaNacimiento) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del socio no puede ser nulo ni estar en blanco.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido del socio no puede ser nulo ni estar en blanco.");
        }
        if (!ValidationUtils.esDniValido(dni)) {
            throw new IllegalArgumentException("DNI inválido. Debe contener entre 7 y 8 dígitos numéricos.");
        }
        if (!ValidationUtils.esEmailValido(email)) {
            throw new IllegalArgumentException("Formato de correo electrónico inválido: " + email);
        }
        if (fechaNacimiento != null && fechaNacimiento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura.");
        }
        if (telefono != null && !telefono.isBlank() && !ValidationUtils.esTelefonoValido(telefono)) {
            throw new IllegalArgumentException("Formato de teléfono inválido: " + telefono);
        }
    }

    private boolean existeOtroSocioConEmail(String emailTrimmed, String excluirSocioId) {
        return socioRepository.listarTodos().stream()
                .anyMatch(s -> (excluirSocioId == null || !excluirSocioId.equals(s.getId()))
                        && s.getEmail() != null
                        && s.getEmail().trim().equalsIgnoreCase(emailTrimmed));
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
        if (socioId == null || socioId.isBlank()) {
            throw new IllegalArgumentException("El ID del socio no puede ser nulo ni estar en blanco.");
        }
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado de cuota no puede ser nulo.");
        }
        Socio socio = socioRepository.buscarPorId(socioId)
                .orElseThrow(() -> new IllegalArgumentException("Socio no encontrado: " + socioId));
        socio.setEstadoCuota(nuevoEstado);
        socioRepository.guardar(socio);
    }

    public boolean eliminarSocio(String socioId) {
        if (socioId == null || socioId.isBlank()) {
            return false;
        }
        if (pagoRepository != null && !pagoRepository.buscarPorSocioId(socioId).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el socio porque registra pagos históricos.");
        }
        if (asistenciaRepository != null && !asistenciaRepository.buscarPorSocioId(socioId).isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el socio porque registra historial de asistencias.");
        }
        Optional<Socio> optSocio = socioRepository.buscarPorId(socioId);
        if (optSocio.isEmpty()) {
            return false;
        }
        if (claseRepository != null) {
            String numeroSocio = optSocio.get().getNumeroSocio();
            for (ClaseGrupal clase : claseRepository.listarTodos()) {
                boolean removido = clase.desinscribirSocio(socioId);
                if (!removido && numeroSocio != null && !numeroSocio.isBlank()) {
                    removido = clase.desinscribirSocio(numeroSocio);
                }
                if (removido) {
                    claseRepository.guardar(clase);
                }
            }
        }
        return socioRepository.eliminarPorId(socioId);
    }

    public void guardar(Socio socio) {
        socioRepository.guardar(socio);
    }
}
