package com.clubdeportivo.gui;

import com.clubdeportivo.gui.components.ModernButton;
import com.clubdeportivo.gui.theme.ThemeConfig;
import com.clubdeportivo.model.*;
import com.clubdeportivo.service.AccesoService;
import com.clubdeportivo.service.ClaseService;
import com.clubdeportivo.service.SocioService;
import com.clubdeportivo.util.Formatters;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Modal interactivo para el control rápido de ingresos en molinete o recepción.
 * Proporciona validación visual instantánea con código de colores según el dictamen.
 *
 * @author Sara
 * @author Joel
 * @version 1.0.0
 */
public class CheckInDialog extends JDialog {

    private final AccesoService accesoService;
    private final SocioService socioService;
    private final ClaseService claseService;
    private final Runnable onCheckInExitoso;

    private JTextField txtCredencial;
    private JRadioButton rbGeneral;
    private JRadioButton rbClase;
    private JComboBox<ClaseGrupalItem> cbClases;
    private JPanel panelResultado;
    private JLabel lblResultadoTitulo;
    private JLabel lblResultadoMensaje;
    private JLabel lblResultadoDetalle;

    public CheckInDialog(Frame parent, AccesoService accesoService,
                         SocioService socioService, ClaseService claseService,
                         Runnable onCheckInExitoso) {
        super(parent, "Control de Acceso / Molinete - FitManage Pro", true);
        this.accesoService = accesoService;
        this.socioService = socioService;
        this.claseService = claseService;
        this.onCheckInExitoso = onCheckInExitoso;

        setSize(560, 480);
        setLocationRelativeTo(parent);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(15, 15));
        root.setBackground(ThemeConfig.BG_MAIN);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Encabezado
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 4, 4));
        pnlHeader.setOpaque(false);
        JLabel lblTitulo = new JLabel("Puesto de Control de Ingreso (Molinete)");
        lblTitulo.setFont(ThemeConfig.FONT_TITLE);
        lblTitulo.setForeground(ThemeConfig.TEXT_MAIN);

        JLabel lblSub = new JLabel("Ingrese el DNI o N° de Socio para autorizar el paso.");
        lblSub.setFont(ThemeConfig.FONT_REGULAR);
        lblSub.setForeground(ThemeConfig.TEXT_MUTED);

        pnlHeader.add(lblTitulo);
        pnlHeader.add(lblSub);
        root.add(pnlHeader, BorderLayout.NORTH);

        // Centro: Formulario + Tarjeta de Resultado
        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setOpaque(false);

        // Input de credencial
        JLabel lblInput = new JLabel("DNI o Número de Socio:");
        lblInput.setFont(ThemeConfig.FONT_BOLD);
        lblInput.setForeground(ThemeConfig.TEXT_MAIN);
        lblInput.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCenter.add(lblInput);
        pnlCenter.add(Box.createVerticalStrut(6));

        txtCredencial = new JTextField();
        txtCredencial.setFont(new Font("SansSerif", Font.BOLD, 16));
        txtCredencial.setBorder(new CompoundBorder(
                new LineBorder(ThemeConfig.BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        txtCredencial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtCredencial.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlCenter.add(txtCredencial);
        pnlCenter.add(Box.createVerticalStrut(12));

        // Opciones de Acceso
        JPanel pnlOpciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlOpciones.setOpaque(false);
        pnlOpciones.setAlignmentX(Component.LEFT_ALIGNMENT);

        rbGeneral = new JRadioButton("Acceso General (Pesas/Cardio)", true);
        rbGeneral.setFont(ThemeConfig.FONT_REGULAR);
        rbGeneral.setOpaque(false);

        rbClase = new JRadioButton("Clase Grupal:");
        rbClase.setFont(ThemeConfig.FONT_REGULAR);
        rbClase.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        group.add(rbGeneral);
        group.add(rbClase);

        cbClases = new JComboBox<>();
        cbClases.setEnabled(false);
        cbClases.setFont(ThemeConfig.FONT_REGULAR);
        cargarClasesCombo();

        rbGeneral.addActionListener(e -> cbClases.setEnabled(false));
        rbClase.addActionListener(e -> cbClases.setEnabled(true));

        pnlOpciones.add(rbGeneral);
        pnlOpciones.add(rbClase);
        pnlOpciones.add(cbClases);
        pnlCenter.add(pnlOpciones);
        pnlCenter.add(Box.createVerticalStrut(16));

        // Tarjeta de Resultado Dinámica
        panelResultado = new JPanel(new BorderLayout(8, 8));
        panelResultado.setBackground(Color.WHITE);
        panelResultado.setBorder(new CompoundBorder(
                new LineBorder(ThemeConfig.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        panelResultado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelResultado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        lblResultadoTitulo = new JLabel("ESPERANDO CREDENCIAL...");
        lblResultadoTitulo.setFont(ThemeConfig.FONT_SUBTITLE);
        lblResultadoTitulo.setForeground(ThemeConfig.TEXT_MUTED);

        lblResultadoMensaje = new JLabel("Ingrese datos y presione 'Validar Ingreso' o ENTER");
        lblResultadoMensaje.setFont(ThemeConfig.FONT_REGULAR);
        lblResultadoMensaje.setForeground(ThemeConfig.TEXT_MUTED);

        lblResultadoDetalle = new JLabel(" ");
        lblResultadoDetalle.setFont(ThemeConfig.FONT_SMALL);
        lblResultadoDetalle.setForeground(ThemeConfig.TEXT_MUTED);

        JPanel pnlTextosRes = new JPanel(new GridLayout(3, 1, 2, 2));
        pnlTextosRes.setOpaque(false);
        pnlTextosRes.add(lblResultadoTitulo);
        pnlTextosRes.add(lblResultadoMensaje);
        pnlTextosRes.add(lblResultadoDetalle);

        panelResultado.add(pnlTextosRes, BorderLayout.CENTER);
        pnlCenter.add(panelResultado);

        root.add(pnlCenter, BorderLayout.CENTER);

        // Botonera Inferior
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlBotones.setOpaque(false);

        ModernButton btnCerrar = ModernButton.createSecondary("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        ModernButton btnValidar = ModernButton.createPrimary("Validar Ingreso (ENTER)");
        btnValidar.addActionListener(e -> procesarCheckIn());

        // Evento ENTER sobre el campo de texto
        txtCredencial.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    procesarCheckIn();
                }
            }
        });

        pnlBotones.add(btnCerrar);
        pnlBotones.add(btnValidar);
        root.add(pnlBotones, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void cargarClasesCombo() {
        cbClases.removeAllItems();
        List<ClaseGrupal> clases = claseService.listarTodas();
        for (ClaseGrupal c : clases) {
            cbClases.addItem(new ClaseGrupalItem(c));
        }
    }

    private void procesarCheckIn() {
        String cred = txtCredencial.getText().trim();
        if (cred.isEmpty()) {
            mostrarResultado(false, "CAMPO VACÍO", "Debe ingresar un DNI o N° de Socio para continuar.", "-");
            return;
        }

        TipoAcceso tipo = rbClase.isSelected() ? TipoAcceso.CLASE_GRUPAL : TipoAcceso.GENERAL;
        String claseId = null;
        if (rbClase.isSelected() && cbClases.getSelectedItem() instanceof ClaseGrupalItem item) {
            claseId = item.clase.getId();
        }

        Asistencia asistencia = accesoService.registrarCheckIn(cred, tipo, claseId);

        boolean ok = asistencia.esAccesoValido();
        String detalleSocio = "-";
        var optSocio = socioService.buscarPorId(asistencia.getSocioId());
        if (optSocio.isEmpty()) {
            optSocio = socioService.buscarPorDni(cred);
        }
        if (optSocio.isPresent()) {
            Socio s = optSocio.get();
            detalleSocio = String.format("Socio: %s (N° %s) | Plan: %s | Cuota: %s",
                    s.getNombreCompleto(), s.getNumeroSocio(),
                    (s.getMembresiaActual() != null ? s.getMembresiaActual().getTipo().getNombre() : "-"),
                    s.getEstadoCuota().getEtiqueta());
        }

        String titulo = ok ? "✔ ACCESO CONCEDIDO" : "✖ ACCESO DENEGADO";
        mostrarResultado(ok, titulo, asistencia.getObservacion(), detalleSocio);

        if (onCheckInExitoso != null) {
            onCheckInExitoso.run();
        }

        txtCredencial.selectAll();
        txtCredencial.requestFocusInWindow();
    }

    private void mostrarResultado(boolean aprobado, String titulo, String mensaje, String detalle) {
        Color bg = aprobado ? ThemeConfig.STATUS_GREEN_BG : ThemeConfig.STATUS_RED_BG;
        Color border = aprobado ? ThemeConfig.STATUS_GREEN : ThemeConfig.STATUS_RED;
        Color fg = aprobado ? ThemeConfig.STATUS_GREEN : ThemeConfig.STATUS_RED;

        panelResultado.setBackground(bg);
        panelResultado.setBorder(new CompoundBorder(
                new LineBorder(border, 2, true),
                new EmptyBorder(14, 16, 14, 16)
        ));

        lblResultadoTitulo.setText(titulo);
        lblResultadoTitulo.setForeground(fg);

        lblResultadoMensaje.setText(mensaje);
        lblResultadoMensaje.setForeground(ThemeConfig.TEXT_MAIN);

        lblResultadoDetalle.setText(detalle);
        lblResultadoDetalle.setForeground(ThemeConfig.TEXT_MUTED);

        panelResultado.revalidate();
        panelResultado.repaint();
    }

    private static class ClaseGrupalItem {
        final ClaseGrupal clase;

        ClaseGrupalItem(ClaseGrupal clase) {
            this.clase = clase;
        }

        @Override
        public String toString() {
            return String.format("%s (%d/%d)", clase.getNombre(),
                    clase.getSociosInscriptos().size(), clase.getCupoMaximo());
        }
    }
}
