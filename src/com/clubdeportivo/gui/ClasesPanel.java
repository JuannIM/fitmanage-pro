package com.clubdeportivo.gui;

import com.clubdeportivo.gui.components.ModernButton;
import com.clubdeportivo.gui.components.SearchTextField;
import com.clubdeportivo.gui.components.StatusBadgeCellRenderer;
import com.clubdeportivo.gui.theme.ThemeConfig;
import com.clubdeportivo.model.*;
import com.clubdeportivo.service.ClaseService;
import com.clubdeportivo.service.InstructorService;
import com.clubdeportivo.service.SocioService;
import com.clubdeportivo.util.Formatters;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Panel de cronograma de clases grupales y gestión rigurosa de aforo.
 * Demuestra control de excepciones de negocio (CupoAgotadoException).
 *
 * @author Lucas Rodríguez (Integrante 3 - Gestor de Logística, Clases & Horarios)
 * @author María Gomez (Integrante 2 - Entrenamiento & Dominio Deportivo)
 * @version 1.0.0
 */
public class ClasesPanel extends JPanel {

    private final ClaseService claseService;
    private final SocioService socioService;
    private final InstructorService instructorService;

    private JTable tablaClases;
    private DefaultTableModel modeloClases;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private SearchTextField txtBuscar;

    public ClasesPanel(ClaseService claseService, SocioService socioService, InstructorService instructorService) {
        this.claseService = claseService;
        this.socioService = socioService;
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

        JLabel lblTitle = new JLabel("Cronograma Semanal de Clases Grupales");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JLabel lblSub = new JLabel("Coordinación de salas, disciplinas, instructores titulares y control estricto de aforo");
        lblSub.setFont(ThemeConfig.FONT_REGULAR);
        lblSub.setForeground(ThemeConfig.TEXT_MUTED);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setOpaque(false);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        ModernButton btnNueva = ModernButton.createPrimary("+ Nueva Clase");
        btnNueva.addActionListener(e -> mostrarDialogoNuevaClase());

        pnlActions.add(btnNueva);
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

        txtBuscar = new SearchTextField("Buscar por Clase, Disciplina o Sala...");
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

        ModernButton btnInscribir = ModernButton.createSuccess("+ Inscribir Socio");
        btnInscribir.addActionListener(e -> inscribirSocioSeleccionado());

        ModernButton btnVerAlumnos = ModernButton.createSecondary("Ver Inscriptos");
        btnVerAlumnos.addActionListener(e -> verAlumnosSeleccionados());

        ModernButton btnEliminar = ModernButton.createDanger("Eliminar");
        btnEliminar.addActionListener(e -> eliminarClaseSeleccionada());

        pnlBotonesTabla.add(btnInscribir);
        pnlBotonesTabla.add(btnVerAlumnos);
        pnlBotonesTabla.add(btnEliminar);

        pnlToolbar.add(txtBuscar, BorderLayout.WEST);
        pnlToolbar.add(pnlBotonesTabla, BorderLayout.EAST);
        pnlContenedor.add(pnlToolbar, BorderLayout.NORTH);

        String[] columnas = {"ID", "Actividad", "Disciplina", "Día", "Horario", "Duración", "Sala", "Ocupación", "Profesor", "Estado Cupo"};
        modeloClases = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClases = new JTable(modeloClases);
        tablaClases.setRowHeight(34);
        tablaClases.setFont(ThemeConfig.FONT_REGULAR);
        tablaClases.getTableHeader().setFont(ThemeConfig.FONT_BOLD);
        tablaClases.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaClases.setGridColor(ThemeConfig.BORDER_COLOR);
        tablaClases.setSelectionBackground(ThemeConfig.PRIMARY_LIGHT);
        tablaClases.setSelectionForeground(ThemeConfig.TEXT_MAIN);

        rowSorter = new TableRowSorter<>(modeloClases);
        tablaClases.setRowSorter(rowSorter);

        tablaClases.getColumnModel().getColumn(9).setCellRenderer(new StatusBadgeCellRenderer());

        JScrollPane scroll = new JScrollPane(tablaClases);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        pnlContenedor.add(scroll, BorderLayout.CENTER);

        add(pnlContenedor, BorderLayout.CENTER);
    }

    public void cargarDatos() {
        modeloClases.setRowCount(0);
        List<ClaseGrupal> lista = claseService.listarTodas();
        for (ClaseGrupal c : lista) {
            String diaStr = c.getDiaSemana().getDisplayName(TextStyle.FULL, new Locale("es", "AR")).toUpperCase();
            String ocupacion = c.getSociosInscriptos().size() + " / " + c.getCupoMaximo();
            String prof = (c.getInstructor() != null) ? c.getInstructor().getNombreCompleto() : "A confirmar";
            String estadoCupo = c.tieneCupoDisponible() ? "DISPONIBLE" : "COMPLETO";

            modeloClases.addRow(new Object[]{
                    c.getId(),
                    c.getNombre(),
                    c.getDisciplina().getNombreVisible(),
                    diaStr,
                    Formatters.formatearHora(c.getHoraInicio()),
                    c.getDuracionMinutos() + " min",
                    c.getSala(),
                    ocupacion,
                    prof,
                    estadoCupo
            });
        }
    }

    private ClaseGrupal getClaseSeleccionada() {
        int selected = tablaClases.getSelectedRow();
        if (selected < 0) return null;
        int modelRow = tablaClases.convertRowIndexToModel(selected);
        String id = (String) modeloClases.getValueAt(modelRow, 0);
        return claseService.buscarPorId(id).orElse(null);
    }

    private void inscribirSocioSeleccionado() {
        ClaseGrupal clase = getClaseSeleccionada();
        if (clase == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Socio> socios = socioService.listarTodos();
        JComboBox<SocioItem> cbSocios = new JComboBox<>();
        for (Socio s : socios) {
            cbSocios.addItem(new SocioItem(s));
        }

        JPanel panel = new JPanel(new GridLayout(2, 1, 6, 6));
        panel.add(new JLabel("Seleccione el socio a inscribir en " + clase.getNombre() + ":"));
        panel.add(cbSocios);

        int res = JOptionPane.showConfirmDialog(this, panel, "Inscribir Alumno a Clase", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION && cbSocios.getSelectedItem() instanceof SocioItem item) {
            try {
                boolean ok = claseService.inscribirSocioEnClase(clase.getId(), item.socio.getId());
                cargarDatos();
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Socio inscripto exitosamente en " + clase.getNombre(), "Inscripción Exitosa", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo inscribir. Verifique que la membresía del socio permita clases o que no esté ya inscripto.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (CupoAgotadoException ex) {
                // Captura formal de la excepción de cupo agotado
                JOptionPane.showMessageDialog(this,
                        "¡CUPO AGOTADO!\n" + ex.getMessage(),
                        "Regla de Negocio: Aforo Máximo Excedido", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verAlumnosSeleccionados() {
        ClaseGrupal clase = getClaseSeleccionada();
        if (clase == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(clase.mostrarFichaClase()).append("\n\n");
        sb.append("NÓMINA DE SOCIOS INSCRIPTOS (").append(clase.getSociosInscriptos().size()).append("):\n");
        sb.append("----------------------------------------------------\n");
        int idx = 1;
        for (Socio s : clase.getSociosInscriptos()) {
            sb.append(String.format(" %2d. [%s] %s | DNI: %s | Cuota: %s\n",
                    idx++, s.getNumeroSocio(), s.getNombreCompleto(), s.getDni(), s.getEstadoCuota().getEtiqueta()));
        }
        if (clase.getSociosInscriptos().isEmpty()) {
            sb.append(" (Sin socios inscriptos aún)\n");
        }

        JTextArea txt = new JTextArea(sb.toString());
        txt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txt.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(txt), "Alumnos - " + clase.getNombre(), JOptionPane.INFORMATION_MESSAGE);
    }

    private void eliminarClaseSeleccionada() {
        ClaseGrupal clase = getClaseSeleccionada();
        if (clase == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una clase a eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Desea cancelar y eliminar la clase " + clase.getNombre() + "?",
                "Confirmar Cancelación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            claseService.eliminarClase(clase.getId());
            cargarDatos();
        }
    }

    private void mostrarDialogoNuevaClase() {
        JTextField txtNombre = new JTextField();
        JComboBox<Disciplina> cbDisciplina = new JComboBox<>(Disciplina.values());
        JComboBox<DayOfWeek> cbDia = new JComboBox<>(new DayOfWeek[]{
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
        });
        JTextField txtHora = new JTextField("18:00");
        JTextField txtDuracion = new JTextField("60");
        JTextField txtSala = new JTextField("Sala Principal");
        JTextField txtCupo = new JTextField("15");

        List<Instructor> instructores = instructorService.listarTodos();
        JComboBox<InstructorItem> cbProf = new JComboBox<>();
        for (Instructor i : instructores) {
            cbProf.addItem(new InstructorItem(i));
        }

        JPanel panel = new JPanel(new GridLayout(8, 2, 6, 6));
        panel.add(new JLabel("Nombre de la Clase:")); panel.add(txtNombre);
        panel.add(new JLabel("Disciplina:")); panel.add(cbDisciplina);
        panel.add(new JLabel("Día de la Semana:")); panel.add(cbDia);
        panel.add(new JLabel("Hora Inicio (HH:mm):")); panel.add(txtHora);
        panel.add(new JLabel("Duración (minutos):")); panel.add(txtDuracion);
        panel.add(new JLabel("Sala:")); panel.add(txtSala);
        panel.add(new JLabel("Cupo Máximo:")); panel.add(txtCupo);
        panel.add(new JLabel("Instructor Titular:")); panel.add(cbProf);

        int res = JOptionPane.showConfirmDialog(this, panel, "Programar Nueva Clase Grupal", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                LocalTime hora = LocalTime.parse(txtHora.getText().trim());
                int dur = Integer.parseInt(txtDuracion.getText().trim());
                int cupo = Integer.parseInt(txtCupo.getText().trim());
                Instructor inst = (cbProf.getSelectedItem() instanceof InstructorItem item) ? item.inst : null;

                claseService.crearClase(
                        txtNombre.getText(), (Disciplina) cbDisciplina.getSelectedItem(),
                        (DayOfWeek) cbDia.getSelectedItem(), hora, dur, txtSala.getText(), cupo, inst
                );
                cargarDatos();
                JOptionPane.showMessageDialog(this, "Clase programada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error al Crear Clase", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class SocioItem {
        final Socio socio;
        SocioItem(Socio socio) { this.socio = socio; }
        @Override
        public String toString() {
            return String.format("[%s] %s (Plan: %s)", socio.getNumeroSocio(),
                    socio.getNombreCompleto(),
                    (socio.getMembresiaActual() != null ? socio.getMembresiaActual().getTipo().getNombre() : "-"));
        }
    }

    private static class InstructorItem {
        final Instructor inst;
        InstructorItem(Instructor inst) { this.inst = inst; }
        @Override
        public String toString() {
            return inst.getNombreCompleto() + " (" + inst.getEspecialidad() + ")";
        }
    }
}
