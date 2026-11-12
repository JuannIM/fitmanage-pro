package com.clubdeportivo.repository;

import com.clubdeportivo.model.MetodoPago;
import com.clubdeportivo.model.Pago;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio de Pagos y comprobantes de facturación con persistencia en data/pagos.json.
 *
 * @author Joel
 * @author Alfonsina
 * @version 1.0.0
 */
public class PagoRepository implements IRepository<Pago, String> {

    private final Path archivoDatos;
    private final Map<String, Pago> cacheEnMemoria;

    public PagoRepository() {
        this(Paths.get("data", "pagos.json"));
    }

    public PagoRepository(Path archivoDatos) {
        this.archivoDatos = archivoDatos;
        this.cacheEnMemoria = new ConcurrentHashMap<>();
        cargarDesdeDisco();
    }

    @Override
    public Pago guardar(Pago pago) {
        if (pago == null || pago.getId() == null) {
            throw new IllegalArgumentException("El pago o su ID no pueden ser nulos.");
        }
        cacheEnMemoria.put(pago.getId(), pago);
        guardarEnDisco();
        return pago;
    }

    @Override
    public Optional<Pago> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(cacheEnMemoria.get(id));
    }

    public List<Pago> buscarPorSocioId(String socioId) {
        if (socioId == null) return Collections.emptyList();
        return cacheEnMemoria.values().stream()
                .filter(p -> p.getSocioId().equalsIgnoreCase(socioId))
                .sorted(Comparator.comparing(Pago::getFechaHoraPago).reversed())
                .toList();
    }

    @Override
    public List<Pago> listarTodos() {
        List<Pago> lista = new ArrayList<>(cacheEnMemoria.values());
        lista.sort(Comparator.comparing(Pago::getFechaHoraPago).reversed());
        return lista;
    }

    @Override
    public boolean eliminarPorId(String id) {
        if (id == null) return false;
        boolean eliminado = cacheEnMemoria.remove(id) != null;
        if (eliminado) {
            guardarEnDisco();
        }
        return eliminado;
    }

    @Override
    public int contar() {
        return cacheEnMemoria.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void cargarDesdeDisco() {
        cacheEnMemoria.clear();
        try {
            Object json = JsonPersistenceEngine.readFromFile(archivoDatos);
            if (json instanceof List<?> lista) {
                for (Object item : lista) {
                    if (item instanceof Map<?, ?> map) {
                        Pago pago = deserializarPago((Map<String, Object>) map);
                        if (pago != null) {
                            cacheEnMemoria.put(pago.getId(), pago);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(">> [WARN] No se pudo leer " + archivoDatos + ": " + e.getMessage());
        }
    }

    @Override
    public void guardarEnDisco() {
        try {
            List<Map<String, Object>> listaSerializada = new ArrayList<>();
            for (Pago p : cacheEnMemoria.values()) {
                listaSerializada.add(serializarPago(p));
            }
            JsonPersistenceEngine.saveToFile(archivoDatos, listaSerializada);
        } catch (IOException e) {
            System.err.println(">> [ERROR] Fallo al escribir en " + archivoDatos + ": " + e.getMessage());
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
        try {
            String id = (String) map.get("id");
            String comprobante = (String) map.get("numeroComprobante");
            String socioId = (String) map.get("socioId");
            double monto = ((Number) map.get("monto")).doubleValue();
            LocalDateTime fechaHora = LocalDateTime.parse((String) map.get("fechaHoraPago"));
            MetodoPago metodo = MetodoPago.valueOf((String) map.get("metodoPago"));
            String concepto = (String) map.get("concepto");

            return new Pago(id, comprobante, socioId, monto, fechaHora, metodo, concepto);
        } catch (Exception e) {
            System.err.println(">> [ERROR] Deserializando pago: " + e.getMessage());
            return null;
        }
    }
}
