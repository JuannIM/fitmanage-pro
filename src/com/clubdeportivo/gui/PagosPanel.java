package com.clubdeportivo.gui;

import com.clubdeportivo.gui.components.ModernButton;
import com.clubdeportivo.gui.components.SearchTextField;
import com.clubdeportivo.gui.theme.ThemeConfig;
import com.clubdeportivo.model.MetodoPago;
import com.clubdeportivo.model.Pago;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.service.FacturacionService;
import com.clubdeportivo.service.SocioService;
import com.clubdeportivo.util.Formatters;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

/**
 * Panel contable para la administración de cobranzas, balance de caja y recibos oficiales.
 *
 * @author Sofia Fernández (Integrante 4 - Ingeniera de Finanzas & Facturación)
 * @author Nicolás Martínez (Integrante 5 - UI/UX & Especialista Swing)
 * @version 1.0.0
 */
public class PagosPanel extends JPanel {

    private final FacturacionService facturacionService;
    private final SocioService socioService;

    private JTable tablaPagos;
    private DefaultTableModel modeloPagos;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private SearchTextField txtBuscar;
    private JLabel lblTotalCaja;

    public PagosPanel(FacturacionService facturacionService, SocioService socioService) {
        this.facturacionService = facturacionService;
        this.socioService = socioService;

        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        cargarDatos();
    }

    private void initUI() {
        // Encabezado
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Caja y Liquidación de Cuotas");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JLabel lblSub = new JLabel("Libro diario de ingresos, cobro de membresías y comprobantes correlativos");
        lblSub.setFont(ThemeConfig.FONT_REGULAR);
        lblSub.setForeground(ThemeConfig.TEXT_MUTED);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setOpaque(false);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        ModernButton btnCobrar = ModernButton.createPrimary("+ Cobrar Cuota");
        btnCobrar.addActionListener(e -> mostrarDialogoCobro());

        pnlActions.add(btnCobrar);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlActions, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // Centro: Contenedor Blanco
        JPanel pnlContenedor = new JPanel(new BorderLayout(10, 10));
        pnlContenedor.setBackground(ThemeConfig.BG_CARD);
        pnlContenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConfig.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Toolbar
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);

        txtBuscar = new SearchTextField("Buscar por Comprobante, Socio o Concepto...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }

            private void filtrar() {
                String text = txtBuscar.getText().trim();
                if (text.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JPanel pnlBotonesTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBotonesTabla.setOpaque(false);

        lblTotalCaja = new JLabel("Total Recaudado: $ 0,00");
        lblTotalCaja.setFont(ThemeConfig.FONT_SUBTITLE);
        lblTotalCaja.setForeground(ThemeConfig.STATUS_GREEN);

        ModernButton btnVerRecibo = ModernButton.createSecondary("Ver Recibo Oficial");
        btnVerRecibo.addActionListener(e -> verReciboSeleccionado());

        pnlBotonesTabla.add(lblTotalCaja);
        pnlBotonesTabla.add(Box.createHorizontalStrut(15));
        pnlBotonesTabla.add(btnVerRecibo);

        pnlToolbar.add(txtBuscar, BorderLayout.WEST);
        pnlToolbar.add(pnlBotonesTabla, BorderLayout.EAST);
        pnlContenedor.add(pnlToolbar, BorderLayout.NORTH);

        String[] columnas = {"ID", "N° Comprobante", "Socio Titular", "DNI", "Fecha / Hora", "Monto Abonado", "Medio de Pago", "Concepto"};
        modeloPagos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPagos = new JTable(modeloPagos);
        tablaPagos.setRowHeight(34);
        tablaPagos.setFont(ThemeConfig.FONT_REGULAR);
        tablaPagos.getTableHeader().setFont(ThemeConfig.FONT_BOLD);
        tablaPagos.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaPagos.setGridColor(ThemeConfig.BORDER_COLOR);
        tablaPagos.setSelectionBackground(ThemeConfig.PRIMARY_LIGHT);
        tablaPagos.setSelectionForeground(ThemeConfig.TEXT_MAIN);

        rowSorter = new TableRowSorter<>(modeloPagos);
        tablaPagos.setRowSorter(rowSorter);

        JScrollPane scroll = new JScrollPane(tablaPagos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        pnlContenedor.add(scroll, BorderLayout.CENTER);

        add(pnlContenedor, BorderLayout.CENTER);
    }

    public void cargarDatos() {
        modeloPagos.setRowCount(0);
        List<Pago> lista = facturacionService.listarTodos();
        for (Pago p : lista) {
            String nombreSocio = p.getSocioId();
            String dni = "-";
            var optSocio = socioService.buscarPorId(p.getSocioId());
            if (optSocio.isPresent()) {
                nombreSocio = optSocio.get().getNombreCompleto();
                dni = optSocio.get().getDni();
            }

            modeloPagos.addRow(new Object[]{
                    p.getId(),
                    p.getNumeroComprobante(),
                    nombreSocio,
                    dni,
                    Formatters.formatearFechaHora(p.getFechaHoraPago()),
                    Formatters.formatearMoneda(p.getMonto()),
                    p.getMetodoPago().getDescripcion(),
                    p.getConcepto()
            });
        }

        double total = facturacionService.calcularTotalRecaudado();
        lblTotalCaja.setText("Total en Caja: " + Formatters.formatearMoneda(total));
    }

    private Pago getPagoSeleccionado() {
        int row = tablaPagos.getSelectedRow();
        if (row < 0) return null;
        int modelRow = tablaPagos.convertRowIndexToModel(row);
        String id = (String) modeloPagos.getValueAt(modelRow, 0);
        return facturacionService.buscarPorId(id).orElse(null);
    }

    private void verReciboSeleccionado() {
        Pago p = getPagoSeleccionado();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un comprobante de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextArea txt = new JTextArea(p.generarReciboImprimible());
        txt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txt.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(txt), "Recibo Oficial de Pago - " + p.getNumeroComprobante(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDialogoCobro() {
        List<Socio> socios = socioService.listarTodos();
        JComboBox<SocioItem> cbSocios = new JComboBox<>();
        for (Socio s : socios) {
            cbSocios.addItem(new SocioItem(s));
        }

        JComboBox<MetodoPago> cbMetodos = new JComboBox<>(MetodoPago.values());
        JTextField txtConcepto = new JTextField("Cobro Cuota Mensual");

        JLabel lblMontoPreview = new JLabel("Monto: -");
        lblMontoPreview.setFont(ThemeConfig.FONT_BOLD);
        lblMontoPreview.setForeground(ThemeConfig.PRIMARY);

        Runnable updateMonto = () -> {
            if (cbSocios.getSelectedItem() instanceof SocioItem item) {
                double monto = item.socio.calcularMontoTotal();
                boolean desc = item.socio.tieneDescuentoAntiguedad();
                lblMontoPreview.setText(String.format("Monto a Cobrar: %s %s",
                        Formatters.formatearMoneda(monto),
                        (desc ? "(Incluye 10% Bonificación por Antigüedad)" : "")));
            }
        };

        cbSocios.addActionListener(e -> updateMonto.run());
        updateMonto.run();

        JPanel panel = new JPanel(new GridLayout(4, 2, 6, 6));
        panel.add(new JLabel("Socio a Cobrar:")); panel.add(cbSocios);
        panel.add(new JLabel("Medio de Pago:")); panel.add(cbMetodos);
        panel.add(new JLabel("Concepto:")); panel.add(txtConcepto);
        panel.add(new JLabel("Cálculo Automático:")); panel.add(lblMontoPreview);

        int res = JOptionPane.showConfirmDialog(this, panel, "Cobro de Cuota Social", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION && cbSocios.getSelectedItem() instanceof SocioItem item) {
            Pago pago = facturacionService.cobrarCuota(
                    item.socio.getId(),
                    (MetodoPago) cbMetodos.getSelectedItem(),
                    txtConcepto.getText().trim()
            );
            cargarDatos();
            JOptionPane.showMessageDialog(this,
                    "Cobro registrado con éxito!\n\n" + pago.generarReciboImprimible(),
                    "Comprobante Generado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private static class SocioItem {
        final Socio socio;
        SocioItem(Socio socio) { this.socio = socio; }
        @Override
        public String toString() {
            return String.format("[%s] %s (DNI: %s)", socio.getNumeroSocio(), socio.getNombreCompleto(), socio.getDni());
        }
    }
}
