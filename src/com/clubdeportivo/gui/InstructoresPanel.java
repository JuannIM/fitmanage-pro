package com.clubdeportivo.gui;

import com.clubdeportivo.gui.components.ModernButton;
import com.clubdeportivo.gui.components.SearchTextField;
import com.clubdeportivo.gui.theme.ThemeConfig;
import com.clubdeportivo.model.Instructor;
import com.clubdeportivo.service.InstructorService;
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
 * Panel de administración docente y liquidación mensual de instructores.
 *
 * @author Merlina
 * @author Alfonsina
 * @version 1.0.0
 */
public class InstructoresPanel extends JPanel {

    private final InstructorService instructorService;
    private JTable tablaInstructores;
    private DefaultTableModel modeloInstructores;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private SearchTextField txtBuscar;

    public InstructoresPanel(InstructorService instructorService) {
        this.instructorService = instructorService;

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

        JLabel lblTitle = new JLabel("Cuerpo de Instructores & Liquidaciones");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JLabel lblSub = new JLabel("Planificación del staff técnico, especialidades deportivas y honorarios");
        lblSub.setFont(ThemeConfig.FONT_REGULAR);
        lblSub.setForeground(ThemeConfig.TEXT_MUTED);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setOpaque(false);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        ModernButton btnNuevo = ModernButton.createPrimary("+ Nuevo Instructor");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevoInstructor());

        pnlActions.add(btnNuevo);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlActions, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // Centro: Tabla y Toolbar
        JPanel pnlContenedor = new JPanel(new BorderLayout(10, 10));
        pnlContenedor.setBackground(ThemeConfig.BG_CARD);
        pnlContenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConfig.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);

        txtBuscar = new SearchTextField("Buscar por Legajo, Especialidad o Nombre...");
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

        ModernButton btnLiquidacion = ModernButton.createSecondary("Ver Detalle Liquidación");
        btnLiquidacion.addActionListener(e -> verLiquidacionSeleccionada());

        ModernButton btnEliminar = ModernButton.createDanger("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        pnlBotonesTabla.add(btnLiquidacion);
        pnlBotonesTabla.add(btnEliminar);

        pnlToolbar.add(txtBuscar, BorderLayout.WEST);
        pnlToolbar.add(pnlBotonesTabla, BorderLayout.EAST);
        pnlContenedor.add(pnlToolbar, BorderLayout.NORTH);

        String[] columnas = {"ID", "Legajo", "Profesor", "DNI", "Especialidad", "Turno", "Sueldo Base", "Honorario/Clase", "Clases Activas", "Liquidación Mensual"};
        modeloInstructores = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaInstructores = new JTable(modeloInstructores);
        tablaInstructores.setRowHeight(34);
        tablaInstructores.setFont(ThemeConfig.FONT_REGULAR);
        tablaInstructores.getTableHeader().setFont(ThemeConfig.FONT_BOLD);
        tablaInstructores.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaInstructores.setGridColor(ThemeConfig.BORDER_COLOR);
        tablaInstructores.setSelectionBackground(ThemeConfig.PRIMARY_LIGHT);
        tablaInstructores.setSelectionForeground(ThemeConfig.TEXT_MAIN);

        rowSorter = new TableRowSorter<>(modeloInstructores);
        tablaInstructores.setRowSorter(rowSorter);

        JScrollPane scroll = new JScrollPane(tablaInstructores);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        pnlContenedor.add(scroll, BorderLayout.CENTER);

        add(pnlContenedor, BorderLayout.CENTER);
    }

    public void cargarDatos() {
        modeloInstructores.setRowCount(0);
        List<Instructor> lista = instructorService.listarTodos();
        for (Instructor i : lista) {
            modeloInstructores.addRow(new Object[]{
                    i.getId(),
                    i.getLegajo(),
                    i.getNombreCompleto(),
                    i.getDni(),
                    i.getEspecialidad(),
                    i.getTurnoDisponibilidad(),
                    Formatters.formatearMoneda(i.getSueldoBase()),
                    Formatters.formatearMoneda(i.getHonorarioPorClase()),
                    i.getClasesAsignadas().size(),
                    Formatters.formatearMoneda(i.calcularLiquidacionMensual())
            });
        }
    }

    private Instructor getInstructorSeleccionado() {
        int selected = tablaInstructores.getSelectedRow();
        if (selected < 0) return null;
        int modelRow = tablaInstructores.convertRowIndexToModel(selected);
        String id = (String) modeloInstructores.getValueAt(modelRow, 0);
        return instructorService.buscarPorId(id).orElse(null);
    }

    private void verLiquidacionSeleccionada() {
        Instructor inst = getInstructorSeleccionado();
        if (inst == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un instructor de la nómina.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reporte = String.format("""
                ====================================================
                     FICHA DOCENTE Y LIQUIDACIÓN SALARIAL MENSUAL
                ====================================================
                Legajo           : %s
                Profesor         : %s (DNI: %s)
                Especialidad     : %s
                Turno            : %s
                ----------------------------------------------------
                Sueldo Base      : %s
                Honorario / Clase: %s
                Clases Semanales : %d clases asignadas
                Clases Mensuales : %d clases estimadas
                Variable Clases  : %s
                Bono Multi-Disc. : %s
                ----------------------------------------------------
                TOTAL A LIQUIDAR : %s
                ====================================================
                """,
                inst.getLegajo(), inst.getNombreCompleto(), inst.getDni(),
                inst.getEspecialidad(), inst.getTurnoDisponibilidad(),
                Formatters.formatearMoneda(inst.getSueldoBase()),
                Formatters.formatearMoneda(inst.getHonorarioPorClase()),
                inst.getClasesAsignadas().size(),
                inst.getClasesAsignadas().size() * 4,
                Formatters.formatearMoneda(inst.getClasesAsignadas().size() * 4 * inst.getHonorarioPorClase()),
                (inst.getClasesAsignadas().size() >= 3 ? "$ 15.000,00" : "$ 0,00"),
                Formatters.formatearMoneda(inst.calcularLiquidacionMensual())
        );

        JTextArea txt = new JTextArea(reporte);
        txt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txt.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(txt), "Liquidación Docente - " + inst.getNombreCompleto(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarSeleccionado() {
        Instructor inst = getInstructorSeleccionado();
        if (inst == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un instructor a remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Desea dar de baja al instructor " + inst.getNombreCompleto() + " (" + inst.getLegajo() + ")?",
                "Confirmar Baja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            instructorService.eliminarInstructor(inst.getId());
            cargarDatos();
        }
    }

    private void mostrarDialogoNuevoInstructor() {
        JTextField txtDni = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtEmail = new JTextField();
        JTextField txtTelefono = new JTextField();
        JTextField txtEsp = new JTextField();
        JTextField txtSueldo = new JTextField("450000");
        JTextField txtHonorario = new JTextField("6500");
        JComboBox<String> cbTurno = new JComboBox<>(new String[]{"Turno Mañana", "Turno Tarde", "Turno Noche", "Jornada Completa"});

        JPanel panel = new JPanel(new GridLayout(9, 2, 6, 6));
        panel.add(new JLabel("DNI:")); panel.add(txtDni);
        panel.add(new JLabel("Nombre:")); panel.add(txtNombre);
        panel.add(new JLabel("Apellido:")); panel.add(txtApellido);
        panel.add(new JLabel("Email:")); panel.add(txtEmail);
        panel.add(new JLabel("Teléfono:")); panel.add(txtTelefono);
        panel.add(new JLabel("Especialidad:")); panel.add(txtEsp);
        panel.add(new JLabel("Sueldo Base ($):")); panel.add(txtSueldo);
        panel.add(new JLabel("Honorario por Clase ($):")); panel.add(txtHonorario);
        panel.add(new JLabel("Turno:")); panel.add(cbTurno);

        int res = JOptionPane.showConfirmDialog(this, panel, "Registrar Nuevo Instructor", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                double sueldo = Double.parseDouble(txtSueldo.getText().trim());
                double honorario = Double.parseDouble(txtHonorario.getText().trim());

                instructorService.registrarInstructor(
                        txtDni.getText(), txtNombre.getText(), txtApellido.getText(),
                        txtEmail.getText(), txtTelefono.getText(), LocalDate.now().minusYears(30),
                        txtEsp.getText(), sueldo, honorario, (String) cbTurno.getSelectedItem()
                );
                cargarDatos();
                JOptionPane.showMessageDialog(this, "Instructor registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error al Registrar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
