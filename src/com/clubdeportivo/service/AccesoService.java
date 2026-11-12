package com.clubdeportivo.service;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.AsistenciaRepository;
import com.clubdeportivo.repository.ClaseRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de control de acceso en molinete y recepción en tiempo real.
 * Realiza la validación cruzada entre estado de cuotas, cupos y membresías.
 *
 * @author Camila Díaz (Integrante 6 - Persistencia, QA & Auditoría)
 * @author Lucas Rodríguez (Integrante 3 - Logística & Clases)
 * @version 1.0.0
 */
public class AccesoService {

    private final SocioRepository socioRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final ClaseRepository claseRepository;

    public AccesoService(SocioRepository socioRepository,
                         AsistenciaRepository asistenciaRepository,
                         ClaseRepository claseRepository) {
        this.socioRepository = socioRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.claseRepository = claseRepository;
    }

    /**
     * Valida y procesa un intento de ingreso físico por molinete.
     *
     * @param credencial DNI, N° de Socio o ID
     * @param tipoAcceso General o Clase Grupal
     * @param claseId    Identificador de clase opcional si es acceso a clase
     * @return Registro de Asistencia con el dictamen de acceso
     */
    public Asistencia registrarCheckIn(String credencial, TipoAcceso tipoAcceso, String claseId) {
        String idAsistencia = IdGenerator.generarIdAsistencia();
        LocalDateTime ahora = LocalDateTime.now();
        TipoAcceso tipo = (tipoAcceso != null) ? tipoAcceso : TipoAcceso.GENERAL;

        if (credencial == null || credencial.isBlank()) {
            Asistencia a = new Asistencia(idAsistencia, "DESCONOCIDO", ahora, tipo, claseId,
                    EstadoAcceso.DENEGADO_NO_ENCONTRADO, "Credencial en blanco o no provista");
            return asistenciaRepository.guardar(a);
        }

        String limpio = ValidationUtils.sanitizarDni(credencial);
        Optional<Socio> optSocio = socioRepository.buscarPorDni(limpio);
        if (optSocio.isEmpty()) {
            optSocio = socioRepository.buscarPorNumeroSocio(credencial.trim());
        }
        if (optSocio.isEmpty()) {
            optSocio = socioRepository.buscarPorId(credencial.trim());
        }

        if (optSocio.isEmpty()) {
            Asistencia denegada = new Asistencia(idAsistencia, credencial, ahora, tipo, claseId,
                    EstadoAcceso.DENEGADO_NO_ENCONTRADO, "No se encontró ningún socio con la credencial ingresada");
            return asistenciaRepository.guardar(denegada);
        }

        Socio socio = optSocio.get();
        EstadoAcceso dictamen;
        String observacion = "";

        // Validación 1: Estado de cuota
        if (socio.getEstadoCuota() == EstadoCuota.VENCIDO) {
            dictamen = EstadoAcceso.DENEGADO_CUOTA_VENCIDA;
            observacion = "Cuota social vencida. Debe regularizar en administración.";
        } else if (socio.getEstadoCuota() == EstadoCuota.INACTIVO) {
            dictamen = EstadoAcceso.DENEGADO_CUOTA_VENCIDA;
            observacion = "Socio en estado inactivo / dado de baja.";
        } else {
            // Cuota AL_DIA o PENDIENTE (período de gracia)
            if (tipo == TipoAcceso.CLASE_GRUPAL) {
                // Validación 2: Membresía habilita clases grupales
                if (socio.getMembresiaActual() == null || !socio.getMembresiaActual().getTipo().permiteClases()) {
                    dictamen = EstadoAcceso.DENEGADO_MEMBRESIA_INSUFICIENTE;
                    observacion = "El plan actual no autoriza el acceso a clases grupales dirigidas.";
                } else if (claseId != null && !claseId.isBlank()) {
                    // Validación 3: Cupo disponible en la clase
                    Optional<ClaseGrupal> optClase = claseRepository.buscarPorId(claseId);
                    if (optClase.isPresent()) {
                        ClaseGrupal c = optClase.get();
                        boolean yaInscripto = c.getSociosInscriptos().stream()
                                .anyMatch(s -> s.getId().equals(socio.getId()));
                        if (!yaInscripto && !c.tieneCupoDisponible()) {
                            dictamen = EstadoAcceso.DENEGADO_SIN_CUPO;
                            observacion = "La clase seleccionada no tiene cupo disponible.";
                        } else {
                            dictamen = EstadoAcceso.PERMITIDO;
                            observacion = "Ingreso concedido a clase: " + c.getNombre();
                        }
                    } else {
                        dictamen = EstadoAcceso.PERMITIDO;
                        observacion = "Ingreso general autorizado.";
                    }
                } else {
                    dictamen = EstadoAcceso.PERMITIDO;
                    observacion = "Ingreso concedido.";
                }
            } else {
                dictamen = EstadoAcceso.PERMITIDO;
                observacion = (socio.getEstadoCuota() == EstadoCuota.PENDIENTE) ?
                        "Ingreso concedido (Aviso: Cuota en período de gracia)." : "Ingreso concedido. Bienvenido/a!";
            }
        }

        Asistencia asistencia = new Asistencia(idAsistencia, socio.getId(), ahora, tipo, claseId, dictamen, observacion);
        asistenciaRepository.guardar(asistencia);

        if (dictamen.esAprobado()) {
            socio.registrarAsistencia(asistencia);
            socioRepository.guardar(socio);
        }

        return asistencia;
    }

    public List<Asistencia> listarAsistenciasHoy() {
        return asistenciaRepository.listarAsistenciasDeHoy();
    }

    public int contarPresentesHoy() {
        return asistenciaRepository.listarAsistenciasDeHoy().size();
    }

    public List<Asistencia> listarTodas() {
        return asistenciaRepository.listarTodos();
    }
}
