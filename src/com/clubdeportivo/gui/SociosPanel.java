package com.clubdeportivo.gui;

import com.clubdeportivo.gui.components.ModernButton;
import com.clubdeportivo.gui.components.SearchTextField;
import com.clubdeportivo.gui.components.StatusBadgeCellRenderer;
import com.clubdeportivo.gui.theme.ThemeConfig;
import com.clubdeportivo.model.*;
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
import java.time.LocalDate;
import java.util.List;

/**
 * Panel de administración de Socios con búsqueda en tiempo real,
 * altas, edición de membresías y liquidación de cuotas.
 *
 * @author Juan
 * @author Alfonsina
 * @version 1.0.0
 */
public class SociosPanel extends JPanel {

    private final SocioService socioService;
    private final FacturacionService facturacionService;

    private JTable tablaSocios;
    private DefaultTableModel modeloSocios;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private SearchTextField txtBuscar;

    public SociosPanel(SocioService socioService, FacturacionService facturacionService) {
        this.socioService = socioService;
        this.facturacionService = facturacionService;

        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        cargarDatos();
    }

    private void initUI() {
        // ENCABEZADO
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Padrón de Socios");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JLabel lblSub = new JLabel("Administración de afiliaciones, control de mora y fichas de entrenamiento");
        lblSub.setFont(ThemeConfig.FONT_REGULAR);
        lblSub.setForeground(ThemeConfig.TEXT_MUTED);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setOpaque(false);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        ModernButton btnNuevo = ModernButton.createPrimary("+ Nuevo Socio");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevoSocio());

        pnlActions.add(btnNuevo);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlActions, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // CENTRO: Buscador + Tabla
        JPanel pnlContenedor = new JPanel(new BorderLayout(10, 10));
        pnlContenedor.setBackground(ThemeConfig.BG_CARD);
        pnlContenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConfig.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Barra de búsqueda y botones contextuales
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);

        txtBuscar = new SearchTextField("Buscar por DNI, Nombre o N° Socio...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrar();
            }

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

        ModernButton btnVerFicha = ModernButton.createSecondary("Ver Ficha");
        btnVerFicha.addActionListener(e -> verFichaSocioSeleccionado());

        ModernButton btnCobrar = ModernButton.createSuccess("Cobrar Cuota");
        btnCobrar.addActionListener(e -> cobrarSocioSeleccionado());

        ModernButton btnEditar = ModernButton.createSecondary("Modificar Estado");
        btnEditar.addActionListener(e -> modificarEstadoSeleccionado());

        ModernButton btnEliminar = ModernButton.createDanger("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSocioSeleccionado());

        pnlBotonesTabla.add(btnVerFicha);
        pnlBotonesTabla.add(btnCobrar);
        pnlBotonesTabla.add(btnEditar);
        pnlBotonesTabla.add(btnEliminar);

        pnlToolbar.add(txtBuscar, BorderLayout.WEST);
        pnlToolbar.add(pnlBotonesTabla, BorderLayout.EAST);
        pnlContenedor.add(pnlToolbar, BorderLayout.NORTH);

        // Definición de Tabla
        String[] columnas = {"ID", "N° Socio", "Nombre Completo", "DNI", "Teléfono", "Membresía", "Cuota Mensual", "Antigüedad", "Estado Cuota"};
        modeloSocios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaSocios = new JTable(modeloSocios);
        tablaSocios.setRowHeight(34);
        tablaSocios.setFont(ThemeConfig.FONT_REGULAR);
        tablaSocios.getTableHeader().setFont(ThemeConfig.FONT_BOLD);
        tablaSocios.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaSocios.setGridColor(ThemeConfig.BORDER_COLOR);
        tablaSocios.setSelectionBackground(ThemeConfig.PRIMARY_LIGHT);
        tablaSocios.setSelectionForeground(ThemeConfig.TEXT_MAIN);

        rowSorter = new TableRowSorter<>(modeloSocios);
        tablaSocios.setRowSorter(rowSorter);

        tablaSocios.getColumnModel().getColumn(8).setCellRenderer(new StatusBadgeCellRenderer());

        JScrollPane scroll = new JScrollPane(tablaSocios);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        pnlContenedor.add(scroll, BorderLayout.CENTER);

        add(pnlContenedor, BorderLayout.CENTER);
    }

    public void cargarDatos() {
        modeloSocios.setRowCount(0);
        List<Socio> lista = socioService.listarTodos();
        for (Socio s : lista) {
            String plan = (s.getMembresiaActual() != null) ? s.getMembresiaActual().getTipo().getNombre() : "-";
            String antiguedad = s.calcularMesesAntiguedad() + " meses" + (s.tieneDescuentoAntiguedad() ? " (-10%)" : "");
            modeloSocios.addRow(new Object[]{
                    s.getId(),
                    s.getNumeroSocio(),
                    s.getNombreCompleto(),
                    s.getDni(),
                    s.getTelefono(),
                    plan,
                    Formatters.formatearMoneda(s.calcularMontoTotal()),
                    antiguedad,
                    s.getEstadoCuota().getEtiqueta()
            });
        }
    }

    private Socio getSocioSeleccionado() {
        int selectedRow = tablaSocios.getSelectedRow();
        if (selectedRow < 0) return null;
        int modelRow = tablaSocios.convertRowIndexToModel(selectedRow);
        String id = (String) modeloSocios.getValueAt(modelRow, 0);
        return socioService.buscarPorId(id).orElse(null);
    }

    private void verFichaSocioSeleccionado() {
        Socio s = getSocioSeleccionado();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un socio de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ficha = s.mostrarDetalle() + "\n\n" + s.generarComprobante();
        JTextArea area = new JTextArea(ficha);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setCaretPosition(0);

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(520, 320));
        JOptionPane.showMessageDialog(this, sp, "Ficha Técnica y Liquidación - " + s.getNombreCompleto(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void cobrarSocioSeleccionado() {
        Socio s = getSocioSeleccionado();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un socio para cobrar la cuota.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MetodoPago[] metodos = MetodoPago.values();
        MetodoPago metodo = (MetodoPago) JOptionPane.showInputDialog(
                this,
                String.format("Cobro para %s\nMonto a pagar: %s\nSeleccione Medio de Pago:",
                        s.getNombreCompleto(), Formatters.formatearMoneda(s.calcularMontoTotal())),
                "Caja y Facturación",
                JOptionPane.QUESTION_MESSAGE,
                null,
                metodos,
                metodos[0]
        );

        if (metodo != null) {
            Pago pago = facturacionService.cobrarCuota(s.getId(), metodo, "Pago Cuota Mensual");
            cargarDatos();
            JOptionPane.showMessageDialog(this,
                    "Pago procesado con éxito!\n\n" + pago.generarReciboImprimible(),
                    "Comprobante Emitido", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void modificarEstadoSeleccionado() {
        Socio s = getSocioSeleccionado();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un socio para modificar su estado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EstadoCuota[] estados = EstadoCuota.values();
        EstadoCuota nuevo = (EstadoCuota) JOptionPane.showInputDialog(
                this,
                "Seleccione nuevo estado de cuota para " + s.getNombreCompleto() + ":",
                "Actualizar Estado",
                JOptionPane.QUESTION_MESSAGE,
                null,
                estados,
                s.getEstadoCuota()
        );

        if (nuevo != null) {
            socioService.actualizarEstadoCuota(s.getId(), nuevo);
            cargarDatos();
        }
    }

    private void eliminarSocioSeleccionado() {
        Socio s = getSocioSeleccionado();
        if (s == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un socio a eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al socio " + s.getNombreCompleto() + " (" + s.getNumeroSocio() + ")?",
                "Confirmar Baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            socioService.eliminarSocio(s.getId());
            cargarDatos();
        }
    }

    private void mostrarDialogoNuevoSocio() {
        JTextField txtDni = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtTelefono = new JTextField();
        JComboBox<TipoMembresia> cbPlan = new JComboBox<>(TipoMembresia.values());

        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.add(new JLabel("DNI (7-8 dígitos):"));
        panel.add(txtDni);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);
        panel.add(new JLabel("Correo Electrónico:"));
        panel.add(txtEmail);
        panel.add(new JLabel("Teléfono:"));
        panel.add(txtTelefono);
        panel.add(new JLabel("Plan / Membresía:"));
        panel.add(cbPlan);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Registrar Nuevo Socio", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                socioService.registrarSocio(
                        txtDni.getText(),
                        txtNombre.getText(),
                        txtApellido.getText(),
                        txtEmail.getText(),
                        txtTelefono.getText(),
                        LocalDate.now().minusYears(25),
                        (TipoMembresia) cbPlan.getSelectedItem()
                );
                cargarDatos();
                JOptionPane.showMessageDialog(this, "Socio registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error al Registrar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
