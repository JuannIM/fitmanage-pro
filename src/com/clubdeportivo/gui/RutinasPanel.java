package com.clubdeportivo.gui;

import com.clubdeportivo.gui.components.ModernButton;
import com.clubdeportivo.gui.theme.ThemeConfig;
import com.clubdeportivo.model.Ejercicio;
import com.clubdeportivo.model.GrupoMuscular;
import com.clubdeportivo.model.Instructor;
import com.clubdeportivo.model.Rutina;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.service.InstructorService;
import com.clubdeportivo.service.RutinaService;
import com.clubdeportivo.service.SocioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de diseño de entrenamientos y dosificación de cargas.
 * Demuestra la relación de COMPOSICIÓN FUERTE (Rutina *-- Ejercicio)
 * y el cálculo dinámico del tonelaje global de entrenamiento.
 *
 * @author Rocio
 * @author Sara
 * @version 1.0.0
 */
public final class RutinasPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient RutinaService rutinaService;
    private final transient SocioService socioService;
    private final transient InstructorService instructorService;

    private JTable tablaRutinas;
    private DefaultTableModel modeloRutinas;
    private JTable tablaEjercicios;
    private DefaultTableModel modeloEjercicios;

    private JLabel lblDetalleVolumen;

    public RutinasPanel(RutinaService rutinaService, SocioService socioService, InstructorService instructorService) {
        this.rutinaService = rutinaService;
        this.socioService = socioService;
        this.instructorService = instructorService;

        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        cargarRutinas();
    }

    public RutinasPanel(RutinaService rutinaService, SocioService socioService) {
        this(rutinaService, socioService, null);
    }

    private void initUI() {
        // Encabezado
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Diseñador de Rutinas y Prescripción de Ejercicios");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JLabel lblSub = new JLabel("Composición anatómica de ejercicios, tonelaje acumulado y asignación personalizada a socios");
        lblSub.setFont(ThemeConfig.FONT_REGULAR);
        lblSub.setForeground(ThemeConfig.TEXT_MUTED);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setOpaque(false);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlActions.setOpaque(false);

        ModernButton btnNueva = ModernButton.createPrimary("+ Nueva Rutina");
        btnNueva.addActionListener(e -> mostrarDialogoNuevaRutina());

        pnlActions.add(btnNueva);
        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlActions, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // Centro: SplitPane (Maestro / Detalle)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.45);
        splitPane.setBorder(null);

        // PANEL SUPERIOR: Lista de Rutinas
        JPanel pnlRutinas = new JPanel(new BorderLayout(8, 8));
        pnlRutinas.setBackground(ThemeConfig.BG_CARD);
        pnlRutinas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConfig.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel pnlToolbarRutinas = new JPanel(new BorderLayout());
        pnlToolbarRutinas.setOpaque(false);
        JLabel lblRutinasTitulo = new JLabel("Rutinas Disponibles");
        lblRutinasTitulo.setFont(ThemeConfig.FONT_SUBTITLE);
        lblRutinasTitulo.setForeground(ThemeConfig.TEXT_MAIN);

        JPanel pnlBotonesRutinas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlBotonesRutinas.setOpaque(false);

        ModernButton btnAsignar = ModernButton.createSuccess("Asignar a Socio");
        btnAsignar.addActionListener(e -> asignarRutinaASocio());

        ModernButton btnFicha = ModernButton.createSecondary("Ver Ficha");
        btnFicha.addActionListener(e -> verFichaRutina());

        ModernButton btnEliminarRutina = ModernButton.createDanger("Eliminar Rutina");
        btnEliminarRutina.addActionListener(e -> eliminarRutinaSeleccionada());

        pnlBotonesRutinas.add(btnAsignar);
        pnlBotonesRutinas.add(btnFicha);
        pnlBotonesRutinas.add(btnEliminarRutina);

        pnlToolbarRutinas.add(lblRutinasTitulo, BorderLayout.WEST);
        pnlToolbarRutinas.add(pnlBotonesRutinas, BorderLayout.EAST);
        pnlRutinas.add(pnlToolbarRutinas, BorderLayout.NORTH);

        String[] colsRutinas = {"ID", "Nombre de la Rutina", "Objetivo", "Nivel Dificultad", "Ejercicios", "Volumen Global (kg)"};
        modeloRutinas = new DefaultTableModel(colsRutinas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRutinas = new JTable(modeloRutinas);
        tablaRutinas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaRutinas.setRowHeight(30);
        tablaRutinas.setFont(ThemeConfig.FONT_REGULAR);
        tablaRutinas.getTableHeader().setFont(ThemeConfig.FONT_BOLD);
        tablaRutinas.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaRutinas.setSelectionBackground(ThemeConfig.PRIMARY_LIGHT);
        tablaRutinas.setSelectionForeground(ThemeConfig.TEXT_MAIN);

        tablaRutinas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarEjerciciosDeRutina();
            }
        });

        JScrollPane scrollRutinas = new JScrollPane(tablaRutinas);
        scrollRutinas.getViewport().setBackground(Color.WHITE);
        pnlRutinas.add(scrollRutinas, BorderLayout.CENTER);
        splitPane.setTopComponent(pnlRutinas);

        // PANEL INFERIOR: Ejercicios de la Rutina (Composición)
        JPanel pnlEjercicios = new JPanel(new BorderLayout(8, 8));
        pnlEjercicios.setBackground(ThemeConfig.BG_CARD);
        pnlEjercicios.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConfig.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel pnlToolbarEjercicios = new JPanel(new BorderLayout());
        pnlToolbarEjercicios.setOpaque(false);

        lblDetalleVolumen = new JLabel("Seleccione una rutina para ver y editar sus ejercicios compuestos.");
        lblDetalleVolumen.setFont(ThemeConfig.FONT_BOLD);
        lblDetalleVolumen.setForeground(ThemeConfig.PRIMARY);

        JPanel pnlBotonesEjercicios = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        pnlBotonesEjercicios.setOpaque(false);

        ModernButton btnAgregarEjercicio = ModernButton.createPrimary("+ Agregar Ejercicio");
        btnAgregarEjercicio.addActionListener(e -> mostrarDialogoAgregarEjercicio());

        ModernButton btnEliminarEjercicio = ModernButton.createDanger("Eliminar Ejercicio");
        btnEliminarEjercicio.addActionListener(e -> eliminarEjercicioSeleccionado());

        pnlBotonesEjercicios.add(btnAgregarEjercicio);
        pnlBotonesEjercicios.add(btnEliminarEjercicio);

        pnlToolbarEjercicios.add(lblDetalleVolumen, BorderLayout.WEST);
        pnlToolbarEjercicios.add(pnlBotonesEjercicios, BorderLayout.EAST);
        pnlEjercicios.add(pnlToolbarEjercicios, BorderLayout.NORTH);

        String[] colsEjercicios = {"ID", "Ejercicio", "Grupo Muscular", "Series", "Reps", "Carga (kg)", "Pausa (s)", "Volumen Ítem (kg)", "Notas Técnicas"};
        modeloEjercicios = new DefaultTableModel(colsEjercicios, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaEjercicios = new JTable(modeloEjercicios);
        tablaEjercicios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEjercicios.setRowHeight(28);
        tablaEjercicios.setFont(ThemeConfig.FONT_REGULAR);
        tablaEjercicios.getTableHeader().setFont(ThemeConfig.FONT_BOLD);
        tablaEjercicios.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaEjercicios.setSelectionBackground(ThemeConfig.PRIMARY_LIGHT);
        tablaEjercicios.setSelectionForeground(ThemeConfig.TEXT_MAIN);

        JScrollPane scrollEjercicios = new JScrollPane(tablaEjercicios);
        scrollEjercicios.getViewport().setBackground(Color.WHITE);
        pnlEjercicios.add(scrollEjercicios, BorderLayout.CENTER);
        splitPane.setBottomComponent(pnlEjercicios);

        add(splitPane, BorderLayout.CENTER);
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.45));
    }

    public void cargarRutinas() {
        modeloRutinas.setRowCount(0);
        List<Rutina> lista = rutinaService.listarTodas();
        for (Rutina r : lista) {
            modeloRutinas.addRow(new Object[]{
                    r.getId(),
                    r.getNombre(),
                    r.getObjetivo(),
                    r.getNivelDificultad(),
                    r.getCantidadEjercicios() + " ejercicios",
                    String.format("%.1f kg", r.calcularVolumenGlobal())
            });
        }
        if (modeloRutinas.getRowCount() > 0) {
            tablaRutinas.setRowSelectionInterval(0, 0);
            actualizarEjerciciosDeRutina();
        } else {
            modeloEjercicios.setRowCount(0);
            lblDetalleVolumen.setText("No hay rutinas creadas.");
        }
    }

    private Rutina getRutinaSeleccionada() {
        int row = tablaRutinas.getSelectedRow();
        if (row < 0) return null;
        int modelRow = tablaRutinas.convertRowIndexToModel(row);
        String id = (String) modeloRutinas.getValueAt(modelRow, 0);
        return rutinaService.buscarPorId(id).orElse(null);
    }

    private void actualizarEjerciciosDeRutina() {
        Rutina r = getRutinaSeleccionada();
        modeloEjercicios.setRowCount(0);
        if (r == null) {
            lblDetalleVolumen.setText("Seleccione una rutina para ver sus ejercicios.");
            return;
        }

        lblDetalleVolumen.setText(String.format("Ejercicios de '%s' | Volumen Acumulado: %.1f kg (%d ejercicios)",
                r.getNombre(), r.calcularVolumenGlobal(), r.getCantidadEjercicios()));

        for (Ejercicio e : r.getEjercicios()) {
            modeloEjercicios.addRow(new Object[]{
                    e.getId(),
                    e.getNombre(),
                    e.getGrupoMuscular().getDescripcion(),
                    e.getSeries(),
                    e.getRepeticiones(),
                    e.getPesoSugeridoKg(),
                    e.getDescansoSegundos() + "s",
                    String.format("%.1f kg", e.calcularVolumenTotal()),
                    e.getNotasTecnicas()
            });
        }
    }

    private void mostrarDialogoNuevaRutina() {
        JComboBox<InstructorItem> cbInstructor = null;
        JTextField txtInstructorId = null;

        if (instructorService != null) {
            List<Instructor> instructores = instructorService.listarTodos();
            if (instructores.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay instructores registrados. Registre un instructor antes de crear una rutina.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            cbInstructor = new JComboBox<>();
            for (Instructor inst : instructores) {
                cbInstructor.addItem(new InstructorItem(inst));
            }
        } else {
            txtInstructorId = new JTextField();
        }

        JTextField txtNombre = new JTextField();
        JTextField txtObjetivo = new JTextField("Hipertrofia Muscular");
        JComboBox<String> cbNivel = new JComboBox<>(new String[]{"Principiante", "Intermedio", "Avanzado"});

        JPanel panel = new JPanel(new GridLayout(4, 2, 6, 6));
        panel.add(new JLabel("Nombre de la Rutina:")); panel.add(txtNombre);
        panel.add(new JLabel("Objetivo Principal:")); panel.add(txtObjetivo);
        panel.add(new JLabel("Nivel de Dificultad:")); panel.add(cbNivel);
        if (cbInstructor != null) {
            panel.add(new JLabel("Instructor Creador:")); panel.add(cbInstructor);
        } else {
            panel.add(new JLabel("ID Instructor:")); panel.add(txtInstructorId);
        }

        while (true) {
            int res = JOptionPane.showConfirmDialog(this, panel, "Crear Nueva Rutina", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) {
                break;
            }

            if (txtNombre.getText().isBlank() || txtObjetivo.getText().isBlank()
                    || (txtInstructorId != null && txtInstructorId.getText().isBlank())) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Datos Incompletos", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            String instructorId;
            if (cbInstructor != null) {
                if (!(cbInstructor.getSelectedItem() instanceof InstructorItem item)) {
                    JOptionPane.showMessageDialog(this, "Seleccione un instructor válido.", "Datos Incompletos", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                instructorId = item.inst.getId();
            } else {
                instructorId = txtInstructorId.getText().trim();
            }

            try {
                rutinaService.crearRutina(txtNombre.getText().trim(), txtObjetivo.getText().trim(), (String) cbNivel.getSelectedItem(), instructorId);
                cargarRutinas();
                JOptionPane.showMessageDialog(this, "Rutina creada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                break;
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Error al crear rutina: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarDialogoAgregarEjercicio() {
        Rutina rutina = getRutinaSeleccionada();
        if (rutina == null) {
            JOptionPane.showMessageDialog(this, "Seleccione primero una rutina.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField txtNombre = new JTextField();
        JComboBox<GrupoMuscular> cbGrupo = new JComboBox<>(GrupoMuscular.values());
        JTextField txtSeries = new JTextField("4");
        JTextField txtReps = new JTextField("10");
        JTextField txtPeso = new JTextField("50");
        JTextField txtDescanso = new JTextField("60");
        JTextField txtNotas = new JTextField("Técnica controlada");

        JPanel panel = new JPanel(new GridLayout(7, 2, 6, 6));
        panel.add(new JLabel("Nombre Ejercicio:")); panel.add(txtNombre);
        panel.add(new JLabel("Grupo Muscular:")); panel.add(cbGrupo);
        panel.add(new JLabel("Series:")); panel.add(txtSeries);
        panel.add(new JLabel("Repeticiones:")); panel.add(txtReps);
        panel.add(new JLabel("Peso Sugerido (kg):")); panel.add(txtPeso);
        panel.add(new JLabel("Descanso (segundos):")); panel.add(txtDescanso);
        panel.add(new JLabel("Notas Técnicas:")); panel.add(txtNotas);

        while (true) {
            int res = JOptionPane.showConfirmDialog(this, panel, "Agregar Ejercicio a " + rutina.getNombre(), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) {
                break;
            }

            if (txtNombre.getText().isBlank() || txtSeries.getText().isBlank() || txtReps.getText().isBlank()
                    || txtPeso.getText().isBlank() || txtDescanso.getText().isBlank()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios.", "Datos Incompletos", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            try {
                int series = Integer.parseInt(txtSeries.getText().trim());
                int reps = Integer.parseInt(txtReps.getText().trim());
                double peso = Double.parseDouble(txtPeso.getText().trim());
                int descanso = Integer.parseInt(txtDescanso.getText().trim());
                if (series <= 0 || reps <= 0 || peso <= 0 || descanso <= 0) {
                    JOptionPane.showMessageDialog(this, "Series, repeticiones, peso y descanso deben ser números positivos mayores a 0.", "Datos Inválidos", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                rutinaService.agregarEjercicioARutina(
                        rutina.getId(), txtNombre.getText().trim(), (GrupoMuscular) cbGrupo.getSelectedItem(),
                        series, reps, peso, descanso, txtNotas.getText().trim()
                );

                cargarRutinas();
                JOptionPane.showMessageDialog(this, "Ejercicio agregado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                break;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Series, repeticiones, peso y descanso deben ser números válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar ejercicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarEjercicioSeleccionado() {
        try {
            Rutina rutina = getRutinaSeleccionada();
            int row = tablaEjercicios.getSelectedRow();
            if (rutina == null || row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un ejercicio de la tabla inferior.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int modelRow = tablaEjercicios.convertRowIndexToModel(row);
            String ejercicioId = (String) modeloEjercicios.getValueAt(modelRow, 0);
            int resp = JOptionPane.showConfirmDialog(this, "¿Eliminar este ejercicio de la rutina?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                rutinaService.eliminarEjercicioDeRutina(rutina.getId(), ejercicioId);
                cargarRutinas();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el ejercicio: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void asignarRutinaASocio() {
        try {
            Rutina rutina = getRutinaSeleccionada();
            if (rutina == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una rutina.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Socio> socios = socioService.listarTodos();
            if (socios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay socios registrados para asignar la rutina.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<SocioItem> cb = new JComboBox<>();
            for (Socio s : socios) {
                cb.addItem(new SocioItem(s));
            }

            int res = JOptionPane.showConfirmDialog(this, cb, "Seleccione el socio destinatario:", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION && cb.getSelectedItem() instanceof SocioItem item) {
                rutinaService.asignarRutinaASocio(rutina.getId(), item.socio.getId());
                JOptionPane.showMessageDialog(this,
                        "Rutina '" + rutina.getNombre() + "' asignada con éxito a " + item.socio.getNombreCompleto(),
                        "Prescripción Confirmada", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo asignar la rutina: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verFichaRutina() {
        try {
            Rutina r = getRutinaSeleccionada();
            if (r == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una rutina.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JTextArea txt = new JTextArea(r.mostrarFichaRutina());
            txt.setFont(new Font("Monospaced", Font.PLAIN, 12));
            txt.setEditable(false);
            txt.setCaretPosition(0);
            JScrollPane sp = new JScrollPane(txt);
            sp.setPreferredSize(new Dimension(540, 340));
            JOptionPane.showMessageDialog(this, sp, "Ficha de Entrenamiento - " + r.getNombre(), JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar la ficha: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarRutinaSeleccionada() {
        try {
            Rutina r = getRutinaSeleccionada();
            if (r == null) {
                JOptionPane.showMessageDialog(this, "Seleccione una rutina a eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int resp = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar la rutina '" + r.getNombre() + "' y todos sus ejercicios asociados (Composición)?",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (resp == JOptionPane.YES_OPTION) {
                rutinaService.eliminarRutina(r.getId());
                cargarRutinas();
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar la rutina: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

    private static class SocioItem {
        final Socio socio;
        SocioItem(Socio socio) { this.socio = socio; }
        @Override
        public String toString() {
            return String.format("[%s] %s", socio.getNumeroSocio(), socio.getNombreCompleto());
        }
    }
}
