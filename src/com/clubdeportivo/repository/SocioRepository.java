package com.clubdeportivo.repository;

import com.clubdeportivo.model.*;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.JsonPersistenceEngine;
import com.clubdeportivo.util.ValidationUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

/**
 * Repositorio de Socios con persistencia local en data/socios.json.
 *
 * @author Joel
 * @author Juan
 * @version 1.0.0
 */
public class SocioRepository implements IRepository<Socio, String> {

    private final Object lock = new Object();
    private final Path archivoDatos;
    private final Map<String, Socio> cacheEnMemoria;
    private final Map<String, List<String>> rutinasPendientesPorSocio;
    private RutinaRepository rutinaRepo;

    public SocioRepository() {
        this(Paths.get("data", "socios.json"));
    }

    public SocioRepository(Path archivoDatos) {
        this.archivoDatos = Objects.requireNonNull(archivoDatos, "La ruta del archivo de socios no puede ser nula.");
        this.cacheEnMemoria = new LinkedHashMap<>();
        this.rutinasPendientesPorSocio = new LinkedHashMap<>();
        cargarInicialDesdeDisco();
    }

    public void setRutinaRepository(RutinaRepository rutinaRepo) {
        synchronized (lock) {
            this.rutinaRepo = rutinaRepo;
            rehidratarRutinas();
        }
    }

    private void rehidratarRutinas() {
        if (rutinaRepo == null) {
            return;
        }
        for (Socio socio : cacheEnMemoria.values()) {
            List<String> ids = rutinasPendientesPorSocio.get(socio.getId());
            if (ids != null) {
                for (String rutinaId : ids) {
                    rutinaRepo.buscarPorId(rutinaId).ifPresent(socio::asignarRutina);
                }
            }
        }
    }

    @Override
    public Socio guardar(Socio socio) {
        if (socio == null || socio.getId() == null || socio.getId().isBlank()) {
            throw new IllegalArgumentException("El socio o su identificador no pueden ser nulos ni vacíos.");
        }
        synchronized (lock) {
            String dniNuevo = ValidationUtils.sanitizarDni(socio.getDni());
            String numSocioNuevo = socio.getNumeroSocio() != null ? socio.getNumeroSocio().trim() : "";

            for (Socio otro : cacheEnMemoria.values()) {
                if (otro.getId().equals(socio.getId())) {
                    continue;
                }
                if (!dniNuevo.isEmpty() && dniNuevo.equals(ValidationUtils.sanitizarDni(otro.getDni()))) {
                    throw new IllegalStateException("Ya existe otro socio registrado con el DNI: " + socio.getDni());
                }
                if (!numSocioNuevo.isEmpty() && otro.getNumeroSocio() != null
                        && numSocioNuevo.equalsIgnoreCase(otro.getNumeroSocio().trim())) {
                    throw new IllegalStateException("Ya existe otro socio registrado con el número: " + socio.getNumeroSocio());
                }
            }

            Socio previo = cacheEnMemoria.get(socio.getId());
            List<String> rutinasPrevias = rutinasPendientesPorSocio.get(socio.getId());

            cacheEnMemoria.put(socio.getId(), socio);
            rutinasPendientesPorSocio.put(socio.getId(), extraerIdsRutinas(socio));
            try {
                guardarEnDisco();
            } catch (PersistenceException e) {
                if (previo != null) {
                    cacheEnMemoria.put(socio.getId(), previo);
                } else {
                    cacheEnMemoria.remove(socio.getId());
                }
                if (rutinasPrevias != null) {
                    rutinasPendientesPorSocio.put(socio.getId(), rutinasPrevias);
                } else {
                    rutinasPendientesPorSocio.remove(socio.getId());
                }
                throw e;
            }
            return socio;
        }
    }

    private List<String> extraerIdsRutinas(Socio socio) {
        List<String> ids = new ArrayList<>();
        for (Rutina r : socio.getRutinasAsignadas()) {
            if (r != null && r.getId() != null && !r.getId().isBlank()) {
                ids.add(r.getId());
            }
        }
        return ids;
    }

    @Override
    public Optional<Socio> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        synchronized (lock) {
            return Optional.ofNullable(cacheEnMemoria.get(id));
        }
    }

    public Optional<Socio> buscarPorDni(String dni) {
        if (dni == null) return Optional.empty();
        String limpio = ValidationUtils.sanitizarDni(dni);
        if (limpio.isEmpty()) return Optional.empty();
        synchronized (lock) {
            return cacheEnMemoria.values().stream()
                    .filter(s -> ValidationUtils.sanitizarDni(s.getDni()).equals(limpio))
                    .findFirst();
        }
    }

    public Optional<Socio> buscarPorNumeroSocio(String numeroSocio) {
        if (numeroSocio == null || numeroSocio.isBlank()) return Optional.empty();
        String buscado = numeroSocio.trim();
        synchronized (lock) {
            return cacheEnMemoria.values().stream()
                    .filter(s -> s.getNumeroSocio() != null && s.getNumeroSocio().trim().equalsIgnoreCase(buscado))
                    .findFirst();
        }
    }

    @Override
    public List<Socio> listarTodos() {
        synchronized (lock) {
            List<Socio> lista = new ArrayList<>(cacheEnMemoria.values());
            lista.sort(Comparator.comparing(Socio::getNumeroSocio));
            return lista;
        }
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        synchronized (lock) {
            Socio previo = cacheEnMemoria.remove(id);
            if (previo == null) {
                return false;
            }
            List<String> rutinasPrevias = rutinasPendientesPorSocio.remove(id);
            try {
                guardarEnDisco();
            } catch (PersistenceException e) {
                cacheEnMemoria.put(id, previo);
                if (rutinasPrevias != null) {
                    rutinasPendientesPorSocio.put(id, rutinasPrevias);
                }
                throw e;
            }
            return true;
        }
    }

    @Override
    public int contar() {
        synchronized (lock) {
            return cacheEnMemoria.size();
        }
    }

    @Override
    public void cargarDesdeDisco() {
        cargarInicialDesdeDisco();
    }

    @SuppressWarnings("unchecked")
    private void cargarInicialDesdeDisco() {
        synchronized (lock) {
            Map<String, Socio> nuevosDatos = new LinkedHashMap<>();
            Map<String, List<String>> nuevasRutinas = new LinkedHashMap<>();
            int maxSocio = 0;

            try {
                Object json = JsonPersistenceEngine.readFromFile(archivoDatos);
                if (json != null) {
                    if (!(json instanceof List<?> lista)) {
                        throw new PersistenceException("Formato JSON inválido en " + archivoDatos + ": se esperaba una lista de socios.");
                    }
                    for (int i = 0; i < lista.size(); i++) {
                        Object item = lista.get(i);
                        if (!(item instanceof Map<?, ?> map)) {
                            throw new PersistenceException("Registro corrupto en " + archivoDatos + " (índice " + i + "): se esperaba un objeto JSON.");
                        }
                        Map<String, Object> socioMap = (Map<String, Object>) map;
                        Socio socio;
                        try {
                            socio = deserializarSocio(socioMap);
                        } catch (RuntimeException e) {
                            throw new PersistenceException("Error deserializando socio en " + archivoDatos + " (índice " + i + "): " + e.getMessage(), e);
                        }
                        if (socio == null || socio.getId() == null || socio.getId().isBlank()) {
                            throw new PersistenceException("Registro de socio inválido o sin ID en " + archivoDatos + " (índice " + i + ").");
                        }
                        nuevosDatos.put(socio.getId(), socio);
                        nuevasRutinas.put(socio.getId(), extraerRutinasIdsDeMapa(socioMap));

                        maxSocio = Math.max(maxSocio, IdGenerator.extraerSufijoNumerico(socio.getId(), "SOC-"));
                        maxSocio = Math.max(maxSocio, IdGenerator.extraerSufijoNumerico(socio.getNumeroSocio(), "SOC-"));
                    }
                }
            } catch (IOException | RuntimeException e) {
                if (e instanceof PersistenceException pe) {
                    throw pe;
                }
                throw new PersistenceException("No se pudo leer " + archivoDatos + ": " + e.getMessage(), e);
            }

            cacheEnMemoria.clear();
            cacheEnMemoria.putAll(nuevosDatos);
            rutinasPendientesPorSocio.clear();
            rutinasPendientesPorSocio.putAll(nuevasRutinas);
            rehidratarRutinas();
            IdGenerator.actualizarSecuenciaSocio(maxSocio);
        }
    }

    @Override
    public void guardarEnDisco() {
        synchronized (lock) {
            try {
                List<Map<String, Object>> listaSerializada = new ArrayList<>();
                for (Socio s : cacheEnMemoria.values()) {
                    listaSerializada.add(serializarSocio(s));
                }
                JsonPersistenceEngine.saveToFile(archivoDatos, listaSerializada);
            } catch (IOException | RuntimeException e) {
                if (e instanceof PersistenceException pe) {
                    throw pe;
                }
                throw new PersistenceException("Fallo al escribir en " + archivoDatos + ": " + e.getMessage(), e);
            }
        }
    }

    private Map<String, Object> serializarSocio(Socio s) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", s.getId());
        map.put("dni", s.getDni());
        map.put("nombre", s.getNombre());
        map.put("apellido", s.getApellido());
        map.put("email", s.getEmail());
        map.put("telefono", s.getTelefono());
        map.put("fechaNacimiento", s.getFechaNacimiento() != null ? s.getFechaNacimiento().toString() : null);
        map.put("fechaRegistro", s.getFechaRegistro() != null ? s.getFechaRegistro().toString() : null);
        map.put("numeroSocio", s.getNumeroSocio());
        map.put("estadoCuota", s.getEstadoCuota().name());
        map.put("porcentajeDescuentoManual", s.getPorcentajeDescuentoManual());

        if (s.getMembresiaActual() != null) {
            Membresia m = s.getMembresiaActual();
            Map<String, Object> memMap = new LinkedHashMap<>();
            memMap.put("id", m.getId());
            memMap.put("tipo", m.getTipo().name());
            memMap.put("precioBase", m.getPrecioBase());
            memMap.put("fechaInicio", m.getFechaInicio() != null ? m.getFechaInicio().toString() : null);
            memMap.put("fechaVencimiento", m.getFechaVencimiento() != null ? m.getFechaVencimiento().toString() : null);
            memMap.put("renovacionAutomatica", m.isRenovacionAutomatica());
            map.put("membresia", memMap);
        } else {
            map.put("membresia", null);
        }

        map.put("rutinasAsignadasIds", extraerIdsRutinas(s));
        return map;
    }

    private List<String> extraerRutinasIdsDeMapa(Map<String, Object> map) {
        List<String> ids = new ArrayList<>();
        Object raw = map.get("rutinasAsignadasIds");
        if (raw == null) {
            return ids;
        }
        if (!(raw instanceof List<?> lista)) {
            throw new IllegalArgumentException("El campo 'rutinasAsignadasIds' debe ser una lista.");
        }
        for (Object item : lista) {
            if (item instanceof String rId && !rId.isBlank()) {
                ids.add(rId);
            } else {
                throw new IllegalArgumentException("Elemento inválido en 'rutinasAsignadasIds'.");
            }
        }
        return ids;
    }

    @SuppressWarnings("unchecked")
    private Socio deserializarSocio(Map<String, Object> map) {
        String id = (String) map.get("id");
        String dni = (String) map.get("dni");
        String nombre = (String) map.get("nombre");
        String apellido = (String) map.get("apellido");
        String email = (String) map.get("email");
        String telefono = (String) map.get("telefono");
        LocalDate fechaNac = map.get("fechaNacimiento") != null ? LocalDate.parse((String) map.get("fechaNacimiento")) : null;
        LocalDate fechaReg = map.get("fechaRegistro") != null ? LocalDate.parse((String) map.get("fechaRegistro")) : null;
        String numSocio = (String) map.get("numeroSocio");
        EstadoCuota estado = EstadoCuota.valueOf((String) map.get("estadoCuota"));

        Membresia mem = null;
        if (map.get("membresia") != null) {
            if (!(map.get("membresia") instanceof Map<?, ?> memMapRaw)) {
                throw new IllegalArgumentException("El campo 'membresia' debe ser un objeto JSON.");
            }
            Map<String, Object> memMap = (Map<String, Object>) memMapRaw;
            String memId = (String) memMap.get("id");
            TipoMembresia tipo = TipoMembresia.valueOf((String) memMap.get("tipo"));
            double precioBase = ((Number) memMap.get("precioBase")).doubleValue();
            LocalDate inicio = memMap.get("fechaInicio") != null ? LocalDate.parse((String) memMap.get("fechaInicio")) : null;
            LocalDate venc = memMap.get("fechaVencimiento") != null ? LocalDate.parse((String) memMap.get("fechaVencimiento")) : null;
            boolean reno = Boolean.TRUE.equals(memMap.get("renovacionAutomatica"));
            mem = new Membresia(memId, tipo, precioBase, inicio, venc, reno);
        }

        Socio socio = new Socio(id, dni, nombre, apellido, email, telefono, fechaNac, fechaReg, numSocio, estado, mem);
        if (map.get("porcentajeDescuentoManual") instanceof Number n) {
            socio.aplicarDescuento(n.doubleValue());
        }
        return socio;
    }
}
