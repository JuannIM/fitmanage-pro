package com.clubdeportivo.service;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.AsistenciaRepository;
import com.clubdeportivo.repository.ClaseRepository;
import com.clubdeportivo.repository.InstructorRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Servicio de control de acceso en molinete y recepción en tiempo real.
 * Realiza la validación cruzada entre estado de cuotas, cupos y membresías.
 *
 * @author Joel
 * @author Merlina
 * @version 1.0.0
 */
public class AccesoService {

    private final SocioRepository socioRepository;
    private final InstructorRepository instructorRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final ClaseRepository claseRepository;

    public AccesoService(SocioRepository socioRepository,
                         AsistenciaRepository asistenciaRepository,
                         ClaseRepository claseRepository) {
        this(socioRepository, null, asistenciaRepository, claseRepository);
    }

    public AccesoService(SocioRepository socioRepository,
                         InstructorRepository instructorRepository,
                         AsistenciaRepository asistenciaRepository,
                         ClaseRepository claseRepository) {
        this.socioRepository = Objects.requireNonNull(socioRepository, "El repositorio de socios es obligatorio.");
        this.instructorRepository = instructorRepository;
        this.asistenciaRepository = Objects.requireNonNull(asistenciaRepository, "El repositorio de asistencias es obligatorio.");
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

        String credTrim = credencial.trim();
        String limpio = ValidationUtils.sanitizarDni(credTrim);
        Optional<Socio> optSocio = limpio.isEmpty() ? Optional.empty() : socioRepository.buscarPorDni(limpio);
        if (optSocio.isEmpty()) {
            optSocio = socioRepository.buscarPorNumeroSocio(credTrim);
        }
        if (optSocio.isEmpty()) {
            optSocio = socioRepository.buscarPorId(credTrim);
        }

        if (optSocio.isEmpty() && instructorRepository != null) {
            Optional<Instructor> optInstructor = instructorRepository.buscarPorId(credTrim);
            if (optInstructor.isEmpty() && !limpio.isEmpty()) {
                optInstructor = instructorRepository.buscarPorDni(limpio);
            }
            if (optInstructor.isEmpty()) {
                optInstructor = instructorRepository.buscarPorLegajo(credTrim);
            }
            if (optInstructor.isPresent()) {
                Instructor instructor = optInstructor.get();
                EstadoAcceso estadoInst = instructor.validarIngreso(tipo)
                        ? EstadoAcceso.PERMITIDO
                        : EstadoAcceso.DENEGADO_NO_ENCONTRADO;
                String obsInst = estadoInst.esAprobado()
                        ? "Acceso personal docente / instructor"
                        : "Acceso de instructor denegado.";
                Asistencia asistenciaInst = new Asistencia(
                        idAsistencia, instructor.getId(), ahora, tipo, claseId, estadoInst, obsInst);
                return asistenciaRepository.guardar(asistenciaInst);
            }
        }

        if (optSocio.isEmpty()) {
            Asistencia denegada = new Asistencia(idAsistencia, credTrim, ahora, tipo, claseId,
                    EstadoAcceso.DENEGADO_NO_ENCONTRADO, "No se encontró ningún socio con la credencial ingresada");
            return asistenciaRepository.guardar(denegada);
        }

        Socio socio = optSocio.get();
        EstadoAcceso dictamen;
        String observacion;

        Membresia membresia = socio.getMembresiaActual();
        EstadoCuota estadoCuota = socio.getEstadoCuota();

        // Validación 1: Estado de cuota y vigencia de membresía
        if (membresia == null) {
            dictamen = EstadoAcceso.DENEGADO_CUOTA_VENCIDA;
            observacion = "El socio no posee una membresía activa configurada.";
        } else if (estadoCuota == EstadoCuota.VENCIDO) {
            dictamen = EstadoAcceso.DENEGADO_CUOTA_VENCIDA;
            observacion = "Cuota social vencida. Debe regularizar en administración.";
        } else if (estadoCuota == EstadoCuota.INACTIVO) {
            dictamen = EstadoAcceso.DENEGADO_CUOTA_VENCIDA;
            observacion = "Socio en estado inactivo / dado de baja.";
        } else if (estadoCuota == EstadoCuota.AL_DIA && membresia.estaVencida()) {
            dictamen = EstadoAcceso.DENEGADO_CUOTA_VENCIDA;
            observacion = "La membresía del socio se encuentra vencida.";
        } else if (estadoCuota == EstadoCuota.PENDIENTE
                && membresia.estaVencida()
                && !membresia.estaEnPeriodoGracia(Socio.DIAS_GRACIA_CUOTA)) {
            dictamen = EstadoAcceso.DENEGADO_CUOTA_VENCIDA;
            observacion = "Cuota pendiente con período de gracia vencido.";
        } else if (tipo == TipoAcceso.CLASE_GRUPAL) {
            // Validación 2: Membresía habilita clases grupales
            if (!membresia.getTipo().permiteClases()) {
                dictamen = EstadoAcceso.DENEGADO_MEMBRESIA_INSUFICIENTE;
                observacion = "El plan actual no autoriza el acceso a clases grupales dirigidas.";
            } else if (claseId == null || claseId.isBlank() || claseRepository == null) {
                dictamen = EstadoAcceso.DENEGADO_MEMBRESIA_INSUFICIENTE;
                observacion = "Debe seleccionar una clase grupal válida.";
            } else {
                Optional<ClaseGrupal> optClase = claseRepository.buscarPorId(claseId);
                if (optClase.isEmpty()) {
                    dictamen = EstadoAcceso.DENEGADO_MEMBRESIA_INSUFICIENTE;
                    observacion = "Debe seleccionar una clase grupal válida.";
                } else {
                    ClaseGrupal c = optClase.get();
                    boolean yaInscripto = c.getSociosInscriptos().stream()
                            .anyMatch(s -> Objects.equals(s.getId(), socio.getId()));
                    if (!yaInscripto) {
                        dictamen = EstadoAcceso.DENEGADO_MEMBRESIA_INSUFICIENTE;
                        observacion = "El socio no está inscripto en la clase " + c.getNombre();
                    } else {
                        dictamen = EstadoAcceso.PERMITIDO;
                        observacion = "Ingreso concedido a clase: " + c.getNombre();
                    }
                }
            }
        } else {
            dictamen = EstadoAcceso.PERMITIDO;
            observacion = (estadoCuota == EstadoCuota.PENDIENTE)
                    ? "Ingreso concedido (Aviso: Cuota en período de gracia)."
                    : "Ingreso concedido. Bienvenido/a!";
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
        return (int) asistenciaRepository.listarAsistenciasDeHoy().stream()
                .map(Asistencia::getSocioId)
                .distinct()
                .count();
    }

    public List<Asistencia> listarTodas() {
        return asistenciaRepository.listarTodos();
    }
}
