package com.clubdeportivo.gui;

import com.clubdeportivo.gui.components.MetricCard;
import com.clubdeportivo.gui.components.ModernButton;
import com.clubdeportivo.gui.components.StatusBadgeCellRenderer;
import com.clubdeportivo.gui.theme.ThemeConfig;
import com.clubdeportivo.model.Asistencia;
import com.clubdeportivo.model.EstadoCuota;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.service.*;
import com.clubdeportivo.util.Formatters;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel principal de control (Dashboard Ejecutivo).
 * Presenta indicadores KPI en tiempo real y el monitor en vivo del molinete de acceso.
 *
 * @author Nicolás Martínez (Integrante 5 - Diseñador UI/UX & Especialista Swing)
 * @author Juan Pérez (Integrante 1 - Arquitectura Core)
 * @version 1.0.0
 */
public class DashboardPanel extends JPanel {

    private final SocioService socioService;
    private final InstructorService instructorService;
    private final ClaseService claseService;
    private final FacturacionService facturacionService;
    private final AccesoService accesoService;
    private final Runnable abrirCheckInModal;

    private MetricCard cardSocios;
    private MetricCard cardPresentes;
    private MetricCard cardClases;
    private MetricCard cardCaja;

    private JTable tablaAsistencias;
    private DefaultTableModel modeloAsistencias;

    public DashboardPanel(SocioService socioService,
                          InstructorService instructorService,
                          ClaseService claseService,
                          FacturacionService facturacionService,
                          AccesoService accesoService,
                          Runnable abrirCheckInModal) {
        this.socioService = socioService;
        this.instructorService = instructorService;
        this.claseService = claseService;
        this.facturacionService = facturacionService;
        this.accesoService = accesoService;
        this.abrirCheckInModal = abrirCheckInModal;

        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeConfig.BG_MAIN);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        initUI();
        actualizarMetricas();
    }

    private void initUI() {
        // ENCABEZADO
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Panel de Control General");
        lblTitle.setFont(ThemeConfig.FONT_TITLE);
        lblTitle.setForeground(ThemeConfig.TEXT_MAIN);

        JLabel lblSub = new JLabel("Monitoreo en tiempo real de operaciones, ocupación y finanzas");
        lblSub.setFont(ThemeConfig.FONT_REGULAR);
        lblSub.setForeground(ThemeConfig.TEXT_MUTED);

        JPanel pnlTitle = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlTitle.setOpaque(false);
        pnlTitle.add(lblTitle);
        pnlTitle.add(lblSub);

        JPanel pnlHeaderActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlHeaderActions.setOpaque(false);

        ModernButton btnActualizar = ModernButton.createSecondary("Actualizar Métricas");
        btnActualizar.addActionListener(e -> actualizarMetricas());

        ModernButton btnCheckIn = ModernButton.createPrimary("Molinete Check-In");
        btnCheckIn.addActionListener(e -> {
            if (abrirCheckInModal != null) abrirCheckInModal.run();
        });

        pnlHeaderActions.add(btnActualizar);
        pnlHeaderActions.add(btnCheckIn);

        pnlHeader.add(pnlTitle, BorderLayout.WEST);
        pnlHeader.add(pnlHeaderActions, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);

        // CENTRO: Tarjetas KPI + Tabla de Accesos
        JPanel pnlCenter = new JPanel(new BorderLayout(15, 15));
        pnlCenter.setOpaque(false);

        // Fila de Tarjetas KPI
        JPanel pnlKpis = new JPanel(new GridLayout(1, 4, 15, 15));
        pnlKpis.setOpaque(false);
        pnlKpis.setPreferredSize(new Dimension(100, 105));

        cardSocios = new MetricCard("Socios Al Día", "0", "Padrón activo", ThemeConfig.STATUS_GREEN);
        cardPresentes = new MetricCard("Presentes en Sala", "0", "Accesos hoy", ThemeConfig.PRIMARY);
        cardClases = new MetricCard("Clases Programadas", "0", "Horarios activos", ThemeConfig.STATUS_AMBER);
        cardCaja = new MetricCard("Recaudación en Caja", "$ 0", "Total acumulado", new Color(13, 148, 136));

        pnlKpis.add(cardSocios);
        pnlKpis.add(cardPresentes);
        pnlKpis.add(cardClases);
        pnlKpis.add(cardCaja);

        pnlCenter.add(pnlKpis, BorderLayout.NORTH);

        // Monitor de Asistencias de la Jornada
        JPanel pnlMonitor = new JPanel(new BorderLayout(10, 10));
        pnlMonitor.setBackground(ThemeConfig.BG_CARD);
        pnlMonitor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeConfig.BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel lblMonitorTitle = new JLabel("Monitor de Accesos Recientes por Molinete");
        lblMonitorTitle.setFont(ThemeConfig.FONT_SUBTITLE);
        lblMonitorTitle.setForeground(ThemeConfig.TEXT_MAIN);
        pnlMonitor.add(lblMonitorTitle, BorderLayout.NORTH);

        String[] columnas = {"Hora", "Socio", "DNI", "Tipo de Acceso", "Estado", "Observación"};
        modeloAsistencias = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAsistencias = new JTable(modeloAsistencias);
        tablaAsistencias.setRowHeight(34);
        tablaAsistencias.setFont(ThemeConfig.FONT_REGULAR);
        tablaAsistencias.getTableHeader().setFont(ThemeConfig.FONT_BOLD);
        tablaAsistencias.getTableHeader().setBackground(new Color(241, 245, 249));
        tablaAsistencias.setGridColor(ThemeConfig.BORDER_COLOR);
        tablaAsistencias.setSelectionBackground(ThemeConfig.PRIMARY_LIGHT);
        tablaAsistencias.setSelectionForeground(ThemeConfig.TEXT_MAIN);

        tablaAsistencias.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeCellRenderer());

        JScrollPane scroll = new JScrollPane(tablaAsistencias);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        pnlMonitor.add(scroll, BorderLayout.CENTER);

        pnlCenter.add(pnlMonitor, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);
    }

    public void actualizarMetricas() {
        // 1. Conteo de socios al día
        List<Socio> socios = socioService.listarTodos();
        long alDia = socios.stream().filter(s -> s.getEstadoCuota() == EstadoCuota.AL_DIA).count();
        cardSocios.setValor(String.valueOf(alDia));
        cardSocios.setSubtitulo("De " + socios.size() + " socios totales");

        // 2. Conteo de presentes hoy
        int presentes = accesoService.contarPresentesHoy();
        cardPresentes.setValor(String.valueOf(presentes));
        cardPresentes.setSubtitulo("Ingresos autorizados");

        // 3. Clases
        int clasesCount = claseService.listarTodas().size();
        cardClases.setValor(String.valueOf(clasesCount));
        cardClases.setSubtitulo(instructorService.listarTodos().size() + " profesores activos");

        // 4. Caja
        double totalCaja = facturacionService.calcularTotalRecaudado();
        cardCaja.setValor(Formatters.formatearMoneda(totalCaja));
        cardCaja.setSubtitulo(facturacionService.listarTodos().size() + " cobros registrados");

        // 5. Tabla de Asistencias
        modeloAsistencias.setRowCount(0);
        List<Asistencia> asistencias = accesoService.listarTodas();
        int max = Math.min(25, asistencias.size());
        for (int i = 0; i < max; i++) {
            Asistencia a = asistencias.get(i);
            String nombreSocio = a.getSocioId();
            String dniSocio = "-";
            var optSocio = socioService.buscarPorId(a.getSocioId());
            if (optSocio.isPresent()) {
                nombreSocio = optSocio.get().getNombreCompleto();
                dniSocio = optSocio.get().getDni();
            }
            modeloAsistencias.addRow(new Object[]{
                    Formatters.formatearFechaHora(a.getFechaHora()),
                    nombreSocio,
                    dniSocio,
                    a.getTipoAcceso().getDescripcion(),
                    a.getEstadoAcceso().getMensaje(),
                    a.getObservacion()
            });
        }
    }
}
