package com.clubdeportivo.repository;

import com.clubdeportivo.model.MetodoPago;
import com.clubdeportivo.model.Pago;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Repositorio de Pagos y comprobantes de facturación con persistencia en data/pagos.json.
 *
 * @author Joel
 * @author Alfonsina
 * @version 1.0.0
 */
public class PagoRepository implements IRepository<Pago, String> {

    private final Object lock = new Object();
    private final Path archivoDatos;
    private final Map<String, Pago> cacheEnMemoria;

    public PagoRepository() {
        this(Paths.get("data", "pagos.json"));
    }

    public PagoRepository(Path archivoDatos) {
        this.archivoDatos = Objects.requireNonNull(archivoDatos, "La ruta del archivo de pagos no puede ser nula.");
        this.cacheEnMemoria = new LinkedHashMap<>();
        cargarInicialDesdeDisco();
    }

    @Override
    public Pago guardar(Pago pago) {
        if (pago == null || pago.getId() == null || pago.getId().isBlank()) {
            throw new IllegalArgumentException("El pago o su ID no pueden ser nulos ni vacíos.");
        }
        synchronized (lock) {
            String comprobanteNuevo = pago.getNumeroComprobante() != null ? pago.getNumeroComprobante().trim() : "";
            for (Pago otro : cacheEnMemoria.values()) {
                if (otro.getId().equals(pago.getId())) {
                    continue;
                }
                if (!comprobanteNuevo.isEmpty() && otro.getNumeroComprobante() != null
                        && comprobanteNuevo.equalsIgnoreCase(otro.getNumeroComprobante().trim())) {
                    throw new IllegalStateException("Ya existe otro pago registrado con el comprobante: " + pago.getNumeroComprobante());
                }
            }

            Pago previo = cacheEnMemoria.get(pago.getId());
            cacheEnMemoria.put(pago.getId(), pago);
            try {
                guardarEnDisco();
            } catch (PersistenceException e) {
                if (previo != null) {
                    cacheEnMemoria.put(pago.getId(), previo);
                } else {
                    cacheEnMemoria.remove(pago.getId());
                }
                throw e;
            }
            return pago;
        }
    }

    @Override
    public Optional<Pago> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        synchronized (lock) {
            return Optional.ofNullable(cacheEnMemoria.get(id));
        }
    }

    public List<Pago> buscarPorSocioId(String socioId) {
        if (socioId == null) return Collections.emptyList();
        synchronized (lock) {
            return cacheEnMemoria.values().stream()
                    .filter(p -> p.getSocioId() != null && p.getSocioId().equalsIgnoreCase(socioId))
                    .sorted(Comparator.comparing(Pago::getFechaHoraPago).reversed())
                    .toList();
        }
    }

    @Override
    public List<Pago> listarTodos() {
        synchronized (lock) {
            List<Pago> lista = new ArrayList<>(cacheEnMemoria.values());
            lista.sort(Comparator.comparing(Pago::getFechaHoraPago).reversed());
            return lista;
        }
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        synchronized (lock) {
            Pago previo = cacheEnMemoria.remove(id);
            if (previo == null) {
                return false;
            }
            try {
                guardarEnDisco();
            } catch (PersistenceException e) {
                cacheEnMemoria.put(id, previo);
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
            Map<String, Pago> nuevosDatos = new LinkedHashMap<>();
            int maxRec = 0;
            int maxPag = 0;

            try {
                Object json = JsonPersistenceEngine.readFromFile(archivoDatos);
                if (json != null) {
                    if (!(json instanceof List<?> lista)) {
                        throw new PersistenceException("Formato JSON inválido en " + archivoDatos + ": se esperaba una lista de pagos.");
                    }
                    for (int i = 0; i < lista.size(); i++) {
                        Object item = lista.get(i);
                        if (!(item instanceof Map<?, ?> map)) {
                            throw new PersistenceException("Registro corrupto en " + archivoDatos + " (índice " + i + "): se esperaba un objeto JSON.");
                        }
                        Pago pago;
                        try {
                            pago = deserializarPago((Map<String, Object>) map);
                        } catch (RuntimeException e) {
                            throw new PersistenceException("Error deserializando pago en " + archivoDatos + " (índice " + i + "): " + e.getMessage(), e);
                        }
                        if (pago == null || pago.getId() == null || pago.getId().isBlank()) {
                            throw new PersistenceException("Registro de pago inválido o sin ID en " + archivoDatos + " (índice " + i + ").");
                        }
                        nuevosDatos.put(pago.getId(), pago);
                        maxRec = Math.max(maxRec, IdGenerator.extraerSufijoNumerico(pago.getNumeroComprobante(), "REC-2026-"));
                        maxPag = Math.max(maxPag, IdGenerator.extraerSufijoNumerico(pago.getId(), "PAG-"));
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
            IdGenerator.actualizarSecuenciaPago(maxRec);
            IdGenerator.actualizarSecuenciaIdPago(maxPag);
        }
    }

    @Override
    public void guardarEnDisco() {
        synchronized (lock) {
            try {
                List<Map<String, Object>> listaSerializada = new ArrayList<>();
                for (Pago p : cacheEnMemoria.values()) {
                    listaSerializada.add(serializarPago(p));
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

    private Map<String, Object> serializarPago(Pago p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", p.getId());
        map.put("numeroComprobante", p.getNumeroComprobante());
        map.put("socioId", p.getSocioId());
        map.put("monto", p.getMonto());
        map.put("fechaHoraPago", p.getFechaHoraPago().toString());
        map.put("metodoPago", p.getMetodoPago().name());
        map.put("concepto", p.getConcepto());
        return map;
    }

    private Pago deserializarPago(Map<String, Object> map) {
        String id = (String) map.get("id");
        String comprobante = (String) map.get("numeroComprobante");
        String socioId = (String) map.get("socioId");
        double monto = ((Number) map.get("monto")).doubleValue();
        LocalDateTime fechaHora = LocalDateTime.parse((String) map.get("fechaHoraPago"));
        MetodoPago metodo = MetodoPago.valueOf((String) map.get("metodoPago"));
        String concepto = (String) map.get("concepto");

        return new Pago(id, comprobante, socioId, monto, fechaHora, metodo, concepto);
    }
}
