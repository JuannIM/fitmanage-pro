package com.clubdeportivo.gui;

import com.clubdeportivo.gui.components.ModernButton;
import com.clubdeportivo.gui.theme.ThemeConfig;
import com.clubdeportivo.repository.*;
import com.clubdeportivo.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal (MainFrame) de FitManage Pro.
 * Diseñado con navegación fluida por CardLayout, barra lateral oscura estilo dashboard moderno
 * y coordinación unificada de los servicios de la aplicación.
 *
 * @author Sara
 * @author Juan
 * @author Joel
 * @version 1.0.0
 */
public final class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // Nombres de tarjetas para el CardLayout
    private static final String CARD_DASHBOARD = "DASHBOARD";
    private static final String CARD_SOCIOS = "SOCIOS";
    private static final String CARD_INSTRUCTORES = "INSTRUCTORES";
    private static final String CARD_CLASES = "CLASES";
    private static final String CARD_RUTINAS = "RUTINAS";
    private static final String CARD_PAGOS = "PAGOS";

    // Servicios
    private final transient SocioService socioService;
    private final transient InstructorService instructorService;
    private final transient ClaseService claseService;
    private final transient RutinaService rutinaService;
    private final transient FacturacionService facturacionService;
    private final transient AccesoService accesoService;

    // Componentes de interfaz
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private transient List<NavButton> navButtons;
    private JLabel lblEstadoPersistencia;

    // Paneles de contenido
    private DashboardPanel dashboardPanel;
    private SociosPanel sociosPanel;
    private InstructoresPanel instructoresPanel;
    private ClasesPanel clasesPanel;
    private RutinasPanel rutinasPanel;
    private PagosPanel pagosPanel;

    public MainFrame(SocioService socioService,
                     InstructorService instructorService,
                     ClaseService claseService,
                     RutinaService rutinaService,
                     FacturacionService facturacionService,
                     AccesoService accesoService) {
        this.socioService = socioService;
        this.instructorService = instructorService;
        this.claseService = claseService;
        this.rutinaService = rutinaService;
        this.facturacionService = facturacionService;
        this.accesoService = accesoService;

        setTitle("FitManage Pro - Sistema Integral de Gestión para Club Deportivo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(ThemeConfig.BG_MAIN);

        // 1. BARRA LATERAL (SIDEBAR)
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // 2. CONTENEDOR CENTRAL CON CARDLAYOUT
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(ThemeConfig.BG_MAIN);

        dashboardPanel = new DashboardPanel(socioService, instructorService, claseService, facturacionService, accesoService, this::abrirModalCheckIn);
        sociosPanel = new SociosPanel(socioService, facturacionService);
        instructoresPanel = new InstructoresPanel(instructorService);
        clasesPanel = new ClasesPanel(claseService, socioService, instructorService);
        rutinasPanel = new RutinasPanel(rutinaService, socioService, instructorService);
        pagosPanel = new PagosPanel(facturacionService, socioService);

        cardsPanel.add(dashboardPanel, CARD_DASHBOARD);
        cardsPanel.add(sociosPanel, CARD_SOCIOS);
        cardsPanel.add(instructoresPanel, CARD_INSTRUCTORES);
        cardsPanel.add(clasesPanel, CARD_CLASES);
        cardsPanel.add(rutinasPanel, CARD_RUTINAS);
        cardsPanel.add(pagosPanel, CARD_PAGOS);

        root.add(cardsPanel, BorderLayout.CENTER);

        setContentPane(root);
        actualizarEstadoPersistencia(true, null);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(ThemeConfig.BG_SIDEBAR);

        // Encabezado de la barra lateral (Logo y Título)
        JPanel pnlLogo = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlLogo.setOpaque(false);
        pnlLogo.setBorder(new EmptyBorder(24, 20, 24, 20));

        JLabel lblLogo = new JLabel("FITMANAGE PRO");
        lblLogo.setFont(ThemeConfig.FONT_TITLE);
        lblLogo.setForeground(ThemeConfig.TEXT_WHITE);

        JLabel lblSubLogo = new JLabel("Club Deportivo & Fitness");
        lblSubLogo.setFont(ThemeConfig.FONT_SMALL);
        lblSubLogo.setForeground(ThemeConfig.TEXT_SIDEBAR_MUTED);

        pnlLogo.add(lblLogo);
        pnlLogo.add(lblSubLogo);
        sidebar.add(pnlLogo, BorderLayout.NORTH);

        // Menú de Navegación Vertical
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setOpaque(false);
        pnlMenu.setBorder(new EmptyBorder(10, 12, 10, 12));

        navButtons = new ArrayList<>();

        NavButton btnDash = new NavButton("  Dashboard General", CARD_DASHBOARD, true);
        NavButton btnSocios = new NavButton("  Padrón de Socios", CARD_SOCIOS, false);
        NavButton btnInst = new NavButton("  Instructores", CARD_INSTRUCTORES, false);
        NavButton btnClases = new NavButton("  Clases Grupales", CARD_CLASES, false);
        NavButton btnRutinas = new NavButton("  Rutinas & Cargas", CARD_RUTINAS, false);
        NavButton btnPagos = new NavButton("  Caja & Facturación", CARD_PAGOS, false);

        navButtons.add(btnDash);
        navButtons.add(btnSocios);
        navButtons.add(btnInst);
        navButtons.add(btnClases);
        navButtons.add(btnRutinas);
        navButtons.add(btnPagos);

        for (NavButton btn : navButtons) {
            pnlMenu.add(btn);
            pnlMenu.add(Box.createVerticalStrut(4));
        }

        pnlMenu.add(Box.createVerticalStrut(20));

        // Botón especial de acceso rápido al Molinete
        ModernButton btnCheckInDirect = ModernButton.createPrimary("⚡ Molinete Check-In");
        btnCheckInDirect.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnCheckInDirect.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCheckInDirect.addActionListener(e -> abrirModalCheckIn());
        pnlMenu.add(btnCheckInDirect);

        sidebar.add(pnlMenu, BorderLayout.CENTER);

        // Pie de la barra lateral (Estado del Sistema)
        JPanel pnlFooter = new JPanel(new GridLayout(2, 1, 2, 2));
        pnlFooter.setOpaque(false);
        pnlFooter.setBorder(new EmptyBorder(16, 20, 16, 20));

        lblEstadoPersistencia = new JLabel("● Persistencia JSON OK");
        lblEstadoPersistencia.setFont(ThemeConfig.FONT_SMALL);
        lblEstadoPersistencia.setForeground(new Color(74, 222, 128));

        JLabel lblVersion = new JLabel("Java 21 LTS - Cátedra POO");
        lblVersion.setFont(ThemeConfig.FONT_SMALL);
        lblVersion.setForeground(ThemeConfig.TEXT_SIDEBAR_MUTED);

        pnlFooter.add(lblEstadoPersistencia);
        pnlFooter.add(lblVersion);
        sidebar.add(pnlFooter, BorderLayout.SOUTH);

        return sidebar;
    }

    public void actualizarEstadoPersistencia(boolean ok, String detalle) {
        if (lblEstadoPersistencia == null) {
            return;
        }
        if (ok) {
            String texto = (detalle != null && !detalle.isBlank())
                    ? "● " + detalle
                    : "● Persistencia JSON OK";
            lblEstadoPersistencia.setText(texto);
            lblEstadoPersistencia.setForeground(new Color(74, 222, 128));
            lblEstadoPersistencia.setToolTipText(texto);
        } else {
            lblEstadoPersistencia.setText("● Error de persistencia JSON");
            lblEstadoPersistencia.setForeground(new Color(248, 113, 113));
            lblEstadoPersistencia.setToolTipText(
                    (detalle != null && !detalle.isBlank()) ? detalle : "Error al leer o guardar datos JSON"
            );
        }
    }

    private void cambiarTarjeta(String cardName) {
        cardLayout.show(cardsPanel, cardName);

        // Actualizar visual de botones
        for (NavButton nb : navButtons) {
            nb.setActive(nb.targetCard.equals(cardName));
        }

        // Refrescar datos según tarjeta seleccionada
        try {
            switch (cardName) {
                case CARD_DASHBOARD -> dashboardPanel.actualizarMetricas();
                case CARD_SOCIOS -> sociosPanel.cargarDatos();
                case CARD_INSTRUCTORES -> instructoresPanel.cargarDatos();
                case CARD_CLASES -> clasesPanel.cargarDatos();
                case CARD_RUTINAS -> rutinasPanel.cargarRutinas();
                case CARD_PAGOS -> pagosPanel.cargarDatos();
            }
            actualizarEstadoPersistencia(true, null);
        } catch (RuntimeException ex) {
            actualizarEstadoPersistencia(false, ex.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    "Error al actualizar la vista:\n" + ex.getMessage(),
                    "Error de Persistencia",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void abrirModalCheckIn() {
        CheckInDialog dialog = new CheckInDialog(this, accesoService, socioService, instructorService, claseService, () -> {
            try {
                dashboardPanel.actualizarMetricas();
                sociosPanel.cargarDatos();
                clasesPanel.cargarDatos();
                actualizarEstadoPersistencia(true, null);
            } catch (RuntimeException ex) {
                actualizarEstadoPersistencia(false, ex.getMessage());
            }
        });
        dialog.setVisible(true);
    }

    // Botón de navegación estilizado para la barra lateral
    private final class NavButton extends JButton {
        private static final long serialVersionUID = 1L;
        final String targetCard;
        private boolean active;
        private boolean hovered;

        NavButton(String text, String targetCard, boolean active) {
            super(text);
            this.targetCard = targetCard;
            this.active = active;
            this.hovered = false;

            setFont(active ? ThemeConfig.FONT_BOLD : ThemeConfig.FONT_REGULAR);
            setForeground(active ? ThemeConfig.TEXT_WHITE : ThemeConfig.TEXT_SIDEBAR_MUTED);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            addActionListener(e -> cambiarTarjeta(targetCard));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hovered = true;
                    if (!NavButton.this.active) {
                        setForeground(ThemeConfig.TEXT_WHITE);
                        repaint();
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hovered = false;
                    if (!NavButton.this.active) {
                        setForeground(ThemeConfig.TEXT_SIDEBAR_MUTED);
                        repaint();
                    }
                }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            setForeground(active ? ThemeConfig.TEXT_WHITE : (hovered ? ThemeConfig.TEXT_WHITE : ThemeConfig.TEXT_SIDEBAR_MUTED));
            setFont(active ? ThemeConfig.FONT_BOLD : ThemeConfig.FONT_REGULAR);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active) {
                g2.setColor(ThemeConfig.BG_SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            } else if (hovered) {
                g2.setColor(ThemeConfig.BG_SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // MÉTODO MAIN DE ARRANQUE DE LA APLICACIÓN
    // =========================================================================

    public static void main(String[] args) {
        // Verificar si se solicitó sólo sembrar datos
        boolean seedOnly = args.length > 0 && "--seed-only".equalsIgnoreCase(args[0]);

        if (seedOnly) {
            try {
                SocioRepository socioRepo = new SocioRepository();
                InstructorRepository instructorRepo = new InstructorRepository();
                RutinaRepository rutinaRepo = new RutinaRepository();
                socioRepo.setRutinaRepository(rutinaRepo);
                ClaseRepository claseRepo = new ClaseRepository(Paths.get("data", "clases.json"), socioRepo, instructorRepo);
                PagoRepository pagoRepo = new PagoRepository();
                AsistenciaRepository asistenciaRepo = new AsistenciaRepository();

                DataSeedService seedService = new DataSeedService(
                        socioRepo, instructorRepo, claseRepo, rutinaRepo, pagoRepo, asistenciaRepo
                );
                seedService.sembrarSiEsNecesario();
                System.out.println(">> [OK] Semillero ejecutado en modo consola.");
            } catch (Throwable ex) {
                System.err.println(">> [ERROR] Falló la ejecución del semillero: " + ex.getMessage());
                ex.printStackTrace(System.err);
                System.exit(1);
            }
            return;
        }

        // Configurar Look & Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Inicializar Repositorios
                SocioRepository socioRepo = new SocioRepository();
                InstructorRepository instructorRepo = new InstructorRepository();
                RutinaRepository rutinaRepo = new RutinaRepository();
                socioRepo.setRutinaRepository(rutinaRepo);
                ClaseRepository claseRepo = new ClaseRepository(Paths.get("data", "clases.json"), socioRepo, instructorRepo);
                PagoRepository pagoRepo = new PagoRepository();
                AsistenciaRepository asistenciaRepo = new AsistenciaRepository();

                // 2. Sembrar datos demo si no existen
                DataSeedService seedService = new DataSeedService(
                        socioRepo, instructorRepo, claseRepo, rutinaRepo, pagoRepo, asistenciaRepo
                );
                seedService.sembrarSiEsNecesario();

                // 3. Inicializar Servicios
                SocioService socioService = new SocioService(socioRepo, claseRepo, pagoRepo, asistenciaRepo);
                InstructorService instructorService = new InstructorService(instructorRepo, claseRepo, rutinaRepo);
                ClaseService claseService = new ClaseService(claseRepo, socioRepo, instructorRepo);
                RutinaService rutinaService = new RutinaService(rutinaRepo, socioRepo, instructorRepo);
                FacturacionService facturacionService = new FacturacionService(pagoRepo, socioRepo);
                AccesoService accesoService = new AccesoService(socioRepo, instructorRepo, asistenciaRepo, claseRepo);

                // 4. Iniciar Ventana Principal
                MainFrame frame = new MainFrame(
                        socioService, instructorService, claseService,
                        rutinaService, facturacionService, accesoService
                );
                frame.setVisible(true);
            } catch (Throwable ex) {
                ex.printStackTrace(System.err);
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo iniciar FitManage Pro:\n" + ex.getMessage(),
                        "Error Crítico de Inicialización",
                        JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
}
