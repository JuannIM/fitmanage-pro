package com.clubdeportivo.gui.components;

import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

/**
 * Utilidad para aplicar filtros de texto seguros a tablas Swing.
 */
public final class TableFilterHelper {

    private TableFilterHelper() {
    }

    public static void aplicarFiltroTexto(TableRowSorter<DefaultTableModel> sorter, String query) {
        if (sorter == null) {
            return;
        }
        String limpio = (query != null) ? query.trim() : "";
        if (limpio.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(limpio)));
        }
    }
}
