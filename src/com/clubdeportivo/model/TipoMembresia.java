package com.clubdeportivo.model;

/**
 * Categorías de planes y membresías disponibles en FitManage Pro.
 * Define las tarifas base mensuales y el acceso a clases dirigidas.
 *
 * @author Alfonsina
 * @author Merlina
 * @version 1.0.0
 */
public enum TipoMembresia {
    PASE_LIBRE("Pase Libre Total", 18500.0, true),
    MUSCULACION("Solo Musculación", 12000.0, false),
    CLASES_GRUPALES("Clases Grupales", 14500.0, true),
    VIP_TOTAL("VIP Total + Locker", 25000.0, true);

    private final String nombre;
    private final double precioBase;
    private final boolean permiteClases;

    TipoMembresia(String nombre, double precioBase, boolean permiteClases) {
        this.nombre = nombre;
        this.precioBase = precioBase;
        this.permiteClases = permiteClases;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public boolean permiteClases() {
        return permiteClases;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
