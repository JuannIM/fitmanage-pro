package com.clubdeportivo.test;

import com.clubdeportivo.gui.components.StatusBadgeCellRenderer;
import com.clubdeportivo.gui.components.TableFilterHelper;
import com.clubdeportivo.gui.theme.ThemeConfig;
import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.*;
import com.clubdeportivo.service.*;
import com.clubdeportivo.util.IdGenerator;
import com.clubdeportivo.util.JsonPersistenceEngine;
import com.clubdeportivo.util.ValidationUtils;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Suite integral de verificación de lógica de negocio y arquitectura POO para FitManage Pro.
 * Diseñada para ejecutarse de manera autónoma con 'bash scripts/test.sh'.
 *
 * @author Joel
 * @author Juan
 * @version 1.0.0
 */
public class SistemaTest {

    private static int testsEjecutados = 0;
    private static int testsExitosos = 0;
    private static int testsFallidos = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  SUITE DE PRUEBAS AUTOMATIZADAS - FITMANAGE PRO (JAVA 21)     ");
        System.out.println("===============================================================");

        ejecutarPrueba("1. Herencia y Polimorfismo en Jerarquía Persona", SistemaTest::testHerenciaYPolimorfismo);
        ejecutarPrueba("2. Composición Fuerte: Rutina y Ejercicios (Volumen)", SistemaTest::testComposicionRutinaEjercicio);
        ejecutarPrueba("3. Control Estricto de Cupo y Lanzamiento de Excepción", SistemaTest::testControlDeCupoClaseGrupal);
        ejecutarPrueba("4. Reglas de Facturación y Descuento por Antigüedad (10%)", SistemaTest::testReglasDeFacturacionYDescuento);
        ejecutarPrueba("5. Validación de Check-In en Molinete (AccesoService)", SistemaTest::testValidacionCheckInAcceso);
        ejecutarPrueba("6. Persistencia JSON Round-Trip Atómica", SistemaTest::testPersistenciaRoundTrip);
        ejecutarPrueba("7. Interfaces Autenticable y Liquidación Docente", SistemaTest::testInterfacesAutenticableYLiquidacion);
        ejecutarPrueba("8. Sincronización de IdGenerator tras reinicio (BUG-01, BUG-18)", SistemaTest::testSincronizacionIdGeneratorTrasReinicio);
        ejecutarPrueba("9. Hidratación de ClaseGrupal con socio VENCIDO (BUG-02)", SistemaTest::testHidratacionClaseConSocioVencido);
        ejecutarPrueba("10. Concurrencia multi-hilo en repositorios y JSON (BUG-03)", SistemaTest::testConcurrenciaMultiHiloRepositorio);
        ejecutarPrueba("11. JSON malformado y PersistenceException (BUG-04, BUG-13)", SistemaTest::testJsonMalformadoYPersistenceException);
        ejecutarPrueba("12. Round-Trip de RutinaService.asignarRutinaASocio (BUG-05)", SistemaTest::testRoundTripAsignacionRutinaASocio);
        ejecutarPrueba("13. Check-In estricto, período de gracia 5 días e Instructores (BUG-06, BUG-12, BUG-20)", SistemaTest::testCheckInEstrictoGraciaEInstructores);
        ejecutarPrueba("14. DataSeedService idempotente ante repositorios parciales (BUG-07)", SistemaTest::testDataSeedIdempotente);
        ejecutarPrueba("15. Servicios Socio, Instructor, Clase e integridad referencial (BUG-14, BUG-15, BUG-17, BUG-38)", SistemaTest::testServiciosEIntegridadReferencial);
        ejecutarPrueba("16. FacturacionService, RutinaService, bono docente e invariantes (BUG-18..52)", SistemaTest::testFacturacionRutinaBonoEInvariantes);
        ejecutarPrueba("17. Smoke tests GUI headless-safe (BUG-08, BUG-09)", SistemaTest::testGuiSmokeHeadless);

        System.out.println("===============================================================");
        System.out.printf("  RESUMEN DE EJECUCIÓN: %d/%d TESTS EXITOSOS (%d fallos)%n",
                testsExitosos, testsEjecutados, testsFallidos);
        System.out.println("===============================================================");

        if (testsFallidos > 0) {
            System.err.println(">> [FALLO] Hubo pruebas que no superaron las aserciones.");
            System.exit(1);
        } else {
            System.out.println(">> [ÉXITO TOTAL] Todas las aserciones de la cátedra se cumplieron.");
            System.exit(0);
        }
    }

    private static void ejecutarPrueba(String nombre, RunnableTest test) {
        testsEjecutados++;
        System.out.print("  [TEST] " + nombre + " ... ");
        try {
            test.run();
            testsExitosos++;
            System.out.println("\u001B[32m[PASS]\u001B[0m");
        } catch (Throwable t) {
            testsFallidos++;
            System.out.println("\u001B[31m[FAIL]\u001B[0m -> " + t.getMessage());
            t.printStackTrace(System.err);
        }
    }

    // =========================================================================
    // CASOS DE PRUEBA ESPECÍFICOS
    // =========================================================================

    /**
     * 1. Comprueba la herencia y el polimorfismo sobre Persona.
     */
    private static void testHerenciaYPolimorfismo() {
        Membresia mem = new Membresia("MEM-T1", TipoMembresia.PASE_LIBRE, 18500.0, LocalDate.now(), LocalDate.now().plusMonths(1), true);
        Persona p1 = new Socio("S-01", "30111222", "Carlos", "Gómez", "carlos@test.com", "111",
                LocalDate.of(1990, 1, 1), LocalDate.now().minusMonths(6), "SOC-01", EstadoCuota.AL_DIA, mem);

        Persona p2 = new Instructor("I-01", "28333444", "Valeria", "Ramos", "valeria@test.com", "222",
                LocalDate.of(1985, 5, 10), LocalDate.now().minusYears(2), "LEG-01", "Spinning",
                400000.0, 5000.0, "Turno Mañana");

        List<Persona> personas = new ArrayList<>();
        personas.add(p1);
        personas.add(p2);

        // Verificación de llamadas polimórficas
        afirmar("Socio".equals(personas.get(0).getRol()), "El rol de p1 debe ser Socio");
        afirmar("Instructor".equals(personas.get(1).getRol()), "El rol de p2 debe ser Instructor");

        afirmar(personas.get(0).mostrarDetalle().contains("Socio [SOC-01]"), "mostrarDetalle polimórfico de Socio");
        afirmar(personas.get(1).mostrarDetalle().contains("Prof. [LEG-01]"), "mostrarDetalle polimórfico de Instructor");

        // Polimorfismo en validación de ingreso
        afirmar(personas.get(0).validarIngreso(TipoAcceso.GENERAL), "Socio al día debe tener ingreso válido");
        afirmar(personas.get(1).validarIngreso(TipoAcceso.GENERAL), "Instructor debe tener ingreso válido");
    }

    /**
     * 2. Comprueba la composición fuerte entre Rutina y Ejercicios.
     */
    private static void testComposicionRutinaEjercicio() {
        Rutina rutina = new Rutina("RUT-T1", "Fuerza Torso", "Fuerza", "Avanzado",
                LocalDate.now(), LocalDate.now().plusMonths(1), "INS-01");

        // Ejercicio 1: 4 series x 10 reps @ 80kg = 3200 kg
        Ejercicio e1 = new Ejercicio("E1", "Press Plano", GrupoMuscular.PECHO, 4, 10, 80.0, 90, "Nota 1");
        // Ejercicio 2: 3 series x 12 reps @ 20kg = 720 kg
        Ejercicio e2 = new Ejercicio("E2", "Aperturas Mancuerna", GrupoMuscular.PECHO, 3, 12, 20.0, 60, "Nota 2");
        // Ejercicio 3: 4 series x 8 reps @ 50kg = 1600 kg
        Ejercicio e3 = new Ejercicio("E3", "Press Militar", GrupoMuscular.HOMBROS, 4, 8, 50.0, 90, "Nota 3");

        rutina.agregarEjercicio(e1);
        rutina.agregarEjercicio(e2);
        rutina.agregarEjercicio(e3);

        afirmar(rutina.getCantidadEjercicios() == 3, "La rutina debe contener exactamente 3 ejercicios");

        // Volumen global esperado: 3200 + 720 + 1600 = 5520 kg
        double volumenEsperado = 3200.0 + 720.0 + 1600.0;
        double delta = Math.abs(rutina.calcularVolumenGlobal() - volumenEsperado);
        afirmar(delta < 0.001, "El volumen global debe ser 5520.0 kg. Obtenido: " + rutina.calcularVolumenGlobal());

        // Comprobación de ciclo de vida compuesto: remover un ejercicio
        boolean removido = rutina.eliminarEjercicio("E2");
        afirmar(removido, "Debe removerse el ejercicio E2");
        afirmar(rutina.getCantidadEjercicios() == 2, "Deben quedar 2 ejercicios");
        afirmar(Math.abs(rutina.calcularVolumenGlobal() - (3200.0 + 1600.0)) < 0.001, "El volumen debe actualizarse tras la baja");
    }

    /**
     * 3. Comprueba el control estricto de cupo y el lanzamiento de CupoAgotadoException.
     */
    private static void testControlDeCupoClaseGrupal() {
        Instructor inst = new Instructor("INS-T", "111", "Lucas", "B", "l@b.com", "111",
                LocalDate.of(1990, 1, 1), LocalDate.now(), "LEG-T", "CrossFit", 300000.0, 5000.0, "Tarde");

        // Clase configurada con cupo máximo estricto de 2 alumnos
        ClaseGrupal clase = new ClaseGrupal("CLS-T", "WOD Reducido", Disciplina.CROSSFIT,
                DayOfWeek.MONDAY, LocalTime.of(18, 0), 60, "Box 1", 2, inst);

        Membresia mem = new Membresia("M", TipoMembresia.PASE_LIBRE, 18500.0, LocalDate.now(), LocalDate.now().plusMonths(1), true);
        Socio s1 = new Socio("S1", "101", "Alumno 1", "A", "a1@test.com", "1", LocalDate.now().minusYears(20), LocalDate.now(), "SOC-01", EstadoCuota.AL_DIA, mem);
        Socio s2 = new Socio("S2", "102", "Alumno 2", "B", "a2@test.com", "2", LocalDate.now().minusYears(20), LocalDate.now(), "SOC-02", EstadoCuota.AL_DIA, mem);
        Socio s3 = new Socio("S3", "103", "Alumno 3", "C", "a3@test.com", "3", LocalDate.now().minusYears(20), LocalDate.now(), "SOC-03", EstadoCuota.AL_DIA, mem);

        try {
            boolean ok1 = clase.inscribirSocio(s1);
            boolean ok2 = clase.inscribirSocio(s2);
            afirmar(ok1 && ok2, "Las dos primeras inscripciones deben ser exitosas");
            afirmar(!clase.tieneCupoDisponible(), "La clase no debe tener cupo disponible tras 2 inscripciones");
            afirmar(clase.getCuposRestantes() == 0, "Los cupos restantes deben ser 0");

            // Intento del 3er socio: DEBE lanzar CupoAgotadoException
            clase.inscribirSocio(s3);
            afirmar(false, "El sistema debió arrojar CupoAgotadoException al superar el aforo");
        } catch (CupoAgotadoException e) {
            // Comportamiento esperado
            afirmar(e.getIdClase().equals("CLS-T"), "El ID de clase de la excepción coincide");
            afirmar(e.getCupoMaximo() == 2, "El cupo máximo de la excepción coincide");
        }
    }

    /**
     * 4. Comprueba las reglas de negocio de Facturación y el 10% de descuento por antigüedad.
     */
    private static void testReglasDeFacturacionYDescuento() {
        // Socio con 18 meses de antigüedad (> 12 meses)
        LocalDate ingresoAntiguo = LocalDate.now().minusMonths(18);
        Membresia memPaseLibre = new Membresia("M-101", TipoMembresia.PASE_LIBRE, 18500.0, LocalDate.now(), LocalDate.now().plusMonths(1), true);

        Socio socioAntiguo = new Socio("S-ANT", "22111333", "Roberto", "Pérez", "r@p.com", "111",
                LocalDate.of(1980, 2, 2), ingresoAntiguo, "SOC-ANT", EstadoCuota.AL_DIA, memPaseLibre);

        afirmar(socioAntiguo.tieneDescuentoAntiguedad(), "Socio con 18 meses debe calificar para descuento de antigüedad");

        // Tarifa base: 18500. Con 10% de descuento: 18500 * 0.90 = 16650.0
        double montoEsperado = 18500.0 * 0.90;
        afirmar(Math.abs(socioAntiguo.calcularMontoTotal() - montoEsperado) < 0.001,
                "El monto con 10% de descuento debe ser 16650.0. Obtenido: " + socioAntiguo.calcularMontoTotal());

        // Socio nuevo con 3 meses de antigüedad (< 12 meses)
        LocalDate ingresoNuevo = LocalDate.now().minusMonths(3);
        Socio socioNuevo = new Socio("S-NUE", "33222444", "Agustín", "López", "a@l.com", "222",
                LocalDate.of(1995, 3, 3), ingresoNuevo, "SOC-NUE", EstadoCuota.AL_DIA, memPaseLibre);

        afirmar(!socioNuevo.tieneDescuentoAntiguedad(), "Socio con 3 meses no debe tener descuento por antigüedad");
        afirmar(Math.abs(socioNuevo.calcularMontoTotal() - 18500.0) < 0.001,
                "El monto de socio nuevo debe ser la tarifa plena (18500.0)");

        // Verificar generación de comprobante (Interface Facturable)
        String comprobante = socioAntiguo.generarComprobante();
        afirmar(comprobante.contains("LIQUIDACIÓN DE CUOTA SOCIAL"), "El comprobante debe contener el encabezado");
        afirmar(comprobante.contains("10% Antigüedad Aplicado"), "El comprobante debe detallar el descuento aplicado");
    }

    /**
     * 5. Comprueba la validación de Check-In en molinete por AccesoService.
     */
    private static void testValidacionCheckInAcceso() throws Exception {
        Path tempDir = crearTempDir("acceso");
        Path tempSocios = tempDir.resolve("socios.json");
        Path tempAsist = tempDir.resolve("asistencias.json");
        Path tempClases = tempDir.resolve("clases.json");

        try {
            SocioRepository socioRepo = new SocioRepository(tempSocios);
            AsistenciaRepository asistRepo = new AsistenciaRepository(tempAsist);
            ClaseRepository claseRepo = new ClaseRepository(tempClases, socioRepo, null);

            AccesoService accesoService = new AccesoService(socioRepo, asistRepo, claseRepo);

            // Socio 1: Al día
            Membresia mem = new Membresia("M1", TipoMembresia.PASE_LIBRE, 18500.0, LocalDate.now(), LocalDate.now().plusMonths(1), true);
            Socio sAlDia = new Socio("SOC-01", "30000001", "Juan", "AlDía", "j@test.com", "1",
                    LocalDate.of(1990, 1, 1), LocalDate.now(), "SOC-01", EstadoCuota.AL_DIA, mem);
            socioRepo.guardar(sAlDia);

            // Socio 2: Vencido
            Socio sVencido = new Socio("SOC-02", "30000002", "Pedro", "Vencido", "p@test.com", "2",
                    LocalDate.of(1992, 1, 1), LocalDate.now(), "SOC-02", EstadoCuota.VENCIDO, mem);
            socioRepo.guardar(sVencido);

            // Test 1: Check-in de socio al día
            Asistencia a1 = accesoService.registrarCheckIn("30000001", TipoAcceso.GENERAL, null);
            afirmar(a1.esAccesoValido(), "Socio al día debe tener acceso concedido");
            afirmar(a1.getEstadoAcceso() == EstadoAcceso.PERMITIDO, "Estado de acceso debe ser PERMITIDO");

            // Test 2: Check-in de socio vencido
            Asistencia a2 = accesoService.registrarCheckIn("30000002", TipoAcceso.GENERAL, null);
            afirmar(!a2.esAccesoValido(), "Socio vencido debe tener acceso denegado");
            afirmar(a2.getEstadoAcceso() == EstadoAcceso.DENEGADO_CUOTA_VENCIDA, "Estado debe ser DENEGADO_CUOTA_VENCIDA");

            // Test 3: Check-in con DNI inexistente
            Asistencia a3 = accesoService.registrarCheckIn("99999999", TipoAcceso.GENERAL, null);
            afirmar(!a3.esAccesoValido(), "Socio inexistente debe ser rechazado");
            afirmar(a3.getEstadoAcceso() == EstadoAcceso.DENEGADO_NO_ENCONTRADO, "Estado debe ser DENEGADO_NO_ENCONTRADO");
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 6. Comprueba el ciclo completo de persistencia JSON atómica (escritura y lectura).
     */
    private static void testPersistenciaRoundTrip() throws Exception {
        Path tempDir = crearTempDir("roundtrip");
        Path tempPath = tempDir.resolve("socios.json");
        try {
            SocioRepository repoEscritura = new SocioRepository(tempPath);

            Membresia mem = new Membresia("MEM-RT", TipoMembresia.VIP_TOTAL, 25000.0, LocalDate.now(), LocalDate.now().plusMonths(1), true);
            Socio socioOriginal = new Socio("SOC-RT", "38999888", "Estela", "Márquez",
                    "estela@test.com", "11-4433-2211", LocalDate.of(1993, 7, 14),
                    LocalDate.now().minusMonths(5), "SOC-RT", EstadoCuota.AL_DIA, mem);

            repoEscritura.guardar(socioOriginal);

            // Simular reinicio creando una nueva instancia del repositorio leyendo desde el archivo
            SocioRepository repoLectura = new SocioRepository(tempPath);
            afirmar(repoLectura.contar() == 1, "El repositorio recuperado debe tener exactamente 1 socio");

            Socio socioRecuperado = repoLectura.buscarPorId("SOC-RT").orElseThrow();
            afirmar("Estela Márquez".equals(socioRecuperado.getNombreCompleto()), "El nombre completo debe coincidir");
            afirmar("38999888".equals(socioRecuperado.getDni()), "El DNI recuperado debe coincidir");
            afirmar(socioRecuperado.getEstadoCuota() == EstadoCuota.AL_DIA, "El estado de cuota debe coincidir");
            afirmar(socioRecuperado.getMembresiaActual() != null, "La membresía asociada no debe ser nula");
            afirmar(socioRecuperado.getMembresiaActual().getTipo() == TipoMembresia.VIP_TOTAL, "El tipo de membresía coincide");
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 7. Comprueba la interface Autenticable y la liquidación docente de Instructor.
     */
    private static void testInterfacesAutenticableYLiquidacion() {
        Membresia mem = new Membresia("M", TipoMembresia.MUSCULACION, 12000.0, LocalDate.now(), LocalDate.now().plusMonths(1), false);
        Socio socio = new Socio("SOC-AUT", "35123123", "Lucía", "Paz", "lucia@test.com", "111",
                LocalDate.of(1991, 1, 1), LocalDate.now(), "SOC-AUT", EstadoCuota.AL_DIA, mem);

        // Autenticación por DNI, N° de socio o email
        afirmar(socio.autenticar("35123123"), "Debe autenticar con DNI");
        afirmar(socio.autenticar("SOC-AUT"), "Debe autenticar con N° de Socio");
        afirmar(socio.autenticar("lucia@test.com"), "Debe autenticar con Email");
        afirmar(!socio.autenticar("credencial_falsa"), "Debe rechazar credenciales inválidas");

        // Liquidación docente
        Instructor inst = new Instructor("INS-L", "25111222", "Martín", "Rios", "m@r.com", "222",
                LocalDate.of(1980, 1, 1), LocalDate.now(), "LEG-L", "Spinning", 400000.0, 6000.0, "Mañana");

        // 0 clases asignadas
        afirmar(Math.abs(inst.calcularLiquidacionMensual() - 400000.0) < 0.001, "Sin clases, cobra sueldo base 400000.0");

        // Asignar 2 clases: 2 clases * 4 semanas = 8 horas * 6000 = 48000 variable
        ClaseGrupal c1 = new ClaseGrupal("C1", "Spinning 1", Disciplina.SPINNING, DayOfWeek.MONDAY, LocalTime.of(8, 0), 50, "S1", 15, inst);
        ClaseGrupal c2 = new ClaseGrupal("C2", "Spinning 2", Disciplina.SPINNING, DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), 50, "S1", 15, inst);
        double liqEsperada = 400000.0 + (2 * 4 * 6000.0);
        afirmar(Math.abs(inst.calcularLiquidacionMensual() - liqEsperada) < 0.001,
                "Liquidación esperada con 2 clases: " + liqEsperada + ". Obtenido: " + inst.calcularLiquidacionMensual());
    }

    /**
     * 8. Comprueba la sincronización de IdGenerator tras reinicio en los 6 repositorios (BUG-01, BUG-18).
     */
    private static void testSincronizacionIdGeneratorTrasReinicio() throws Exception {
        Path tempDir = crearTempDir("idgen");
        try {
            Path sociosPath = tempDir.resolve("socios.json");
            Path instPath = tempDir.resolve("instructores.json");
            Path clasesPath = tempDir.resolve("clases.json");
            Path rutinasPath = tempDir.resolve("rutinas.json");
            Path pagosPath = tempDir.resolve("pagos.json");
            Path asistPath = tempDir.resolve("asistencias.json");

            SocioRepository socioRepo1 = new SocioRepository(sociosPath);
            InstructorRepository instRepo1 = new InstructorRepository(instPath);
            ClaseRepository claseRepo1 = new ClaseRepository(clasesPath, socioRepo1, instRepo1);
            RutinaRepository rutinaRepo1 = new RutinaRepository(rutinasPath);
            PagoRepository pagoRepo1 = new PagoRepository(pagosPath);
            AsistenciaRepository asistRepo1 = new AsistenciaRepository(asistPath);

            Membresia mem = new Membresia("MEM-120", TipoMembresia.PASE_LIBRE, 18500.0,
                    LocalDate.now(), LocalDate.now().plusMonths(1), true);
            socioRepo1.guardar(new Socio("SOC-120", "30120120", "Ana", "Max", "ana120@test.com", "1144556677",
                    LocalDate.of(1990, 1, 1), LocalDate.now(), "SOC-120", EstadoCuota.AL_DIA, mem));

            Instructor inst = new Instructor("INS-120", "28120120", "Luis", "Max", "luis120@test.com", "1144556688",
                    LocalDate.of(1985, 1, 1), LocalDate.now(), "LEG-120", "CrossFit", 400000.0, 5000.0, "Mañana");
            instRepo1.guardar(inst);

            claseRepo1.guardar(new ClaseGrupal("CLS-120", "WOD Max", Disciplina.CROSSFIT,
                    DayOfWeek.MONDAY, LocalTime.of(10, 0), 60, "Sala 1", 10, inst));

            Rutina rutina = new Rutina("RUT-120", "Rutina Max", "Fuerza", "Avanzado",
                    LocalDate.now(), LocalDate.now().plusMonths(1), "INS-120");
            rutina.agregarEjercicio(new Ejercicio("EJE-1020", "Sentadilla", GrupoMuscular.PIERNAS,
                    4, 10, 100.0, 90, "Profunda"));
            rutinaRepo1.guardar(rutina);

            pagoRepo1.guardar(new Pago("PAG-1020", "REC-2026-1020", "SOC-120", 18500.0,
                    LocalDateTime.now(), MetodoPago.EFECTIVO, "Cuota"));

            asistRepo1.guardar(new Asistencia("ASI-10020", "SOC-120", LocalDateTime.now(),
                    TipoAcceso.GENERAL, null, EstadoAcceso.PERMITIDO, "OK"));

            // Simular reinicio creando nuevas instancias de los 6 repositorios
            SocioRepository socioRepo2 = new SocioRepository(sociosPath);
            InstructorRepository instRepo2 = new InstructorRepository(instPath);
            ClaseRepository claseRepo2 = new ClaseRepository(clasesPath, socioRepo2, instRepo2);
            RutinaRepository rutinaRepo2 = new RutinaRepository(rutinasPath);
            PagoRepository pagoRepo2 = new PagoRepository(pagosPath);
            AsistenciaRepository asistRepo2 = new AsistenciaRepository(asistPath);

            String nuevoSocioId = IdGenerator.generarIdSocio();
            String nuevoInstId = IdGenerator.generarIdInstructor();
            String nuevaClaseId = IdGenerator.generarIdClase();
            String nuevaRutinaId = IdGenerator.generarIdRutina();
            String nuevoEjeId = IdGenerator.generarIdEjercicio();
            String nuevoPagoId = IdGenerator.generarIdPago();
            String nuevoRecibo = IdGenerator.generarNumeroComprobante();
            String nuevaAsistId = IdGenerator.generarIdAsistencia();

            afirmar(IdGenerator.extraerSufijoNumerico(nuevoSocioId, "SOC-") > 120, "SOC > 120: " + nuevoSocioId);
            afirmar(IdGenerator.extraerSufijoNumerico(nuevoInstId, "INS-") > 120, "INS > 120: " + nuevoInstId);
            afirmar(IdGenerator.extraerSufijoNumerico(nuevaClaseId, "CLS-") > 120, "CLS > 120: " + nuevaClaseId);
            afirmar(IdGenerator.extraerSufijoNumerico(nuevaRutinaId, "RUT-") > 120, "RUT > 120: " + nuevaRutinaId);
            afirmar(IdGenerator.extraerSufijoNumerico(nuevoEjeId, "EJE-") > 1020, "EJE > 1020: " + nuevoEjeId);
            afirmar(IdGenerator.extraerSufijoNumerico(nuevoPagoId, "PAG-") > 1020, "PAG > 1020: " + nuevoPagoId);
            afirmar(IdGenerator.extraerSufijoNumerico(nuevoRecibo, "REC-2026-") > 1020, "REC > 1020: " + nuevoRecibo);
            afirmar(IdGenerator.extraerSufijoNumerico(nuevaAsistId, "ASI-") > 10020, "ASI > 10020: " + nuevaAsistId);

            // Verificar que los registros existentes siguen intactos
            afirmar(socioRepo2.buscarPorId("SOC-120").isPresent(), "SOC-120 debe seguir presente");
            afirmar(instRepo2.buscarPorId("INS-120").isPresent(), "INS-120 debe seguir presente");
            afirmar(claseRepo2.buscarPorId("CLS-120").isPresent(), "CLS-120 debe seguir presente");
            afirmar(rutinaRepo2.buscarPorId("RUT-120").isPresent(), "RUT-120 debe seguir presente");
            afirmar(pagoRepo2.buscarPorId("PAG-1020").isPresent(), "PAG-1020 debe seguir presente");
            afirmar(asistRepo2.buscarPorId("ASI-10020").isPresent(), "ASI-10020 debe seguir presente");
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 9. Comprueba la hidratación de ClaseGrupal cuando un socio inscripto pasa a VENCIDO (BUG-02).
     */
    private static void testHidratacionClaseConSocioVencido() throws Exception {
        Path tempDir = crearTempDir("hidratacion-clase");
        try {
            Path sociosPath = tempDir.resolve("socios.json");
            Path instPath = tempDir.resolve("instructores.json");
            Path clasesPath = tempDir.resolve("clases.json");

            SocioRepository socioRepo1 = new SocioRepository(sociosPath);
            InstructorRepository instRepo1 = new InstructorRepository(instPath);
            ClaseRepository claseRepo1 = new ClaseRepository(clasesPath, socioRepo1, instRepo1);

            Instructor inst = new Instructor("INS-H1", "27000111", "Carla", "Díaz", "carla@test.com", "1144550011",
                    LocalDate.of(1988, 3, 10), LocalDate.now(), "LEG-H1", "Yoga", 350000.0, 5000.0, "Tarde");
            instRepo1.guardar(inst);

            Membresia mem = new Membresia("MEM-H", TipoMembresia.PASE_LIBRE, 18500.0,
                    LocalDate.now(), LocalDate.now().plusMonths(1), true);
            Socio s1 = new Socio("SOC-H1", "31000111", "Sofía", "AlDía", "sofia@test.com", "1144550022",
                    LocalDate.of(1992, 4, 5), LocalDate.now(), "SOC-H1", EstadoCuota.AL_DIA, mem);
            Socio s2 = new Socio("SOC-H2", "31000222", "Marcos", "LuegoVencido", "marcos@test.com", "1144550033",
                    LocalDate.of(1993, 6, 7), LocalDate.now(), "SOC-H2", EstadoCuota.AL_DIA, mem);
            socioRepo1.guardar(s1);
            socioRepo1.guardar(s2);

            ClaseGrupal clase = new ClaseGrupal("CLS-H1", "Yoga Flow", Disciplina.YOGA,
                    DayOfWeek.TUESDAY, LocalTime.of(19, 0), 60, "Sala Zen", 10, inst);
            clase.inscribirSocio(s1);
            clase.inscribirSocio(s2);
            claseRepo1.guardar(clase);

            // Pasar s2 a VENCIDO y persistir
            s2.setEstadoCuota(EstadoCuota.VENCIDO);
            socioRepo1.guardar(s2);

            // Recargar desde disco
            SocioRepository socioRepo2 = new SocioRepository(sociosPath);
            InstructorRepository instRepo2 = new InstructorRepository(instPath);
            ClaseRepository claseRepo2 = new ClaseRepository(clasesPath, socioRepo2, instRepo2);

            ClaseGrupal recargada = claseRepo2.buscarPorId("CLS-H1").orElseThrow();
            afirmar(recargada.getSociosInscriptos().size() == 2,
                    "Debe conservar ambos socios inscriptos tras recargar, incluso el VENCIDO. Obtenido: "
                            + recargada.getSociosInscriptos().size());
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 10. Comprueba concurrencia multi-hilo en SocioRepository y JsonPersistenceEngine (BUG-03).
     */
    private static void testConcurrenciaMultiHiloRepositorio() throws Exception {
        Path tempDir = crearTempDir("concurrencia");
        try {
            Path sociosPath = tempDir.resolve("socios.json");
            SocioRepository socioRepo = new SocioRepository(sociosPath);

            int numHilos = 10;
            CountDownLatch inicioSimultaneo = new CountDownLatch(1);
            CountDownLatch finTodos = new CountDownLatch(numHilos);
            List<Throwable> errores = new CopyOnWriteArrayList<>();

            for (int i = 0; i < numHilos; i++) {
                final int idx = i;
                Thread t = new Thread(() -> {
                    try {
                        inicioSimultaneo.await(5, TimeUnit.SECONDS);
                        String id = "SOC-CONC-" + idx;
                        String dni = String.format("400000%02d", idx);
                        Membresia mem = new Membresia("MEM-C-" + idx, TipoMembresia.PASE_LIBRE, 18500.0,
                                LocalDate.now(), LocalDate.now().plusMonths(1), true);
                        Socio s = new Socio(id, dni, "Socio" + idx, "Concurrente",
                                "conc" + idx + "@test.com", "11440000" + String.format("%02d", idx),
                                LocalDate.of(1995, 1, 1), LocalDate.now(), id, EstadoCuota.AL_DIA, mem);
                        socioRepo.guardar(s);
                    } catch (Throwable ex) {
                        errores.add(ex);
                    } finally {
                        finTodos.countDown();
                    }
                });
                t.start();
            }

            inicioSimultaneo.countDown();
            boolean terminado = finTodos.await(15, TimeUnit.SECONDS);
            afirmar(terminado, "Todos los hilos concurrentes deben finalizar a tiempo");
            afirmar(errores.isEmpty(), "No debe haber excepciones concurrentes: " + errores);

            Object rawJson = JsonPersistenceEngine.readFromFile(sociosPath);
            afirmar(rawJson instanceof List<?>, "El archivo JSON persistido debe ser una lista válida");

            SocioRepository repoRecargado = new SocioRepository(sociosPath);
            afirmar(repoRecargado.contar() == numHilos,
                    "Deben persistir exactamente " + numHilos + " socios. Obtenido: " + repoRecargado.contar());
            for (int i = 0; i < numHilos; i++) {
                afirmar(repoRecargado.buscarPorId("SOC-CONC-" + i).isPresent(),
                        "Falta socio concurrente SOC-CONC-" + i);
            }
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 11. Comprueba rechazo de JSON malformado y PersistenceException sin pérdida de caché ni archivo (BUG-04, BUG-13).
     */
    private static void testJsonMalformadoYPersistenceException() throws Exception {
        String[] payloadsInvalidos = {"{\"a\":\"sin cerrar", "[1,]", "{} extra", "+10"};
        for (String payload : payloadsInvalidos) {
            boolean lanzo = false;
            try {
                JsonPersistenceEngine.parseJson(payload);
            } catch (IllegalArgumentException e) {
                lanzo = true;
            }
            afirmar(lanzo, "parseJson debe lanzar IllegalArgumentException para: " + payload);
        }

        Path tempDir = crearTempDir("json-corrupto");
        try {
            Path sociosPath = tempDir.resolve("socios.json");
            SocioRepository socioRepo = new SocioRepository(sociosPath);
            Membresia mem = new Membresia("MEM-P", TipoMembresia.MUSCULACION, 12000.0,
                    LocalDate.now(), LocalDate.now().plusMonths(1), true);
            socioRepo.guardar(new Socio("SOC-P1", "32111222", "Nora", "Válida", "nora@test.com", "1144556600",
                    LocalDate.of(1990, 2, 2), LocalDate.now(), "SOC-P1", EstadoCuota.AL_DIA, mem));

            // Corromper el archivo en disco
            Files.writeString(sociosPath, "{\"corrupto\": [1,}", StandardCharsets.UTF_8);

            boolean lanzoPersistence = false;
            try {
                socioRepo.cargarDesdeDisco();
            } catch (PersistenceException e) {
                lanzoPersistence = true;
            }
            afirmar(lanzoPersistence, "cargarDesdeDisco debe lanzar PersistenceException ante JSON corrupto");
            afirmar(socioRepo.contar() == 1, "La caché previa no debe vaciarse ante fallo de lectura");
            afirmar(Files.exists(sociosPath), "El archivo en disco no debe eliminarse ante JSON corrupto");
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 12. Comprueba el round-trip completo de RutinaService.asignarRutinaASocio (BUG-05).
     */
    private static void testRoundTripAsignacionRutinaASocio() throws Exception {
        Path tempDir = crearTempDir("rutina-asignacion");
        try {
            Path sociosPath = tempDir.resolve("socios.json");
            Path rutinasPath = tempDir.resolve("rutinas.json");

            RutinaRepository rutinaRepo1 = new RutinaRepository(rutinasPath);
            SocioRepository socioRepo1 = new SocioRepository(sociosPath);
            socioRepo1.setRutinaRepository(rutinaRepo1);

            RutinaService rutinaService = new RutinaService(rutinaRepo1, socioRepo1);

            Rutina rutina = rutinaService.crearRutina("Hipertrofia Full", "Masa Muscular", "Intermedio", "INS-01");
            rutinaService.agregarEjercicioARutina(rutina.getId(), "Press Banca", GrupoMuscular.PECHO, 4, 10, 70.0, 90, "Controlado");

            Membresia mem = new Membresia("MEM-R", TipoMembresia.VIP_TOTAL, 25000.0,
                    LocalDate.now(), LocalDate.now().plusMonths(1), true);
            Socio socio = new Socio("SOC-R1", "34555666", "Elena", "Ríos", "elena@test.com", "1144557788",
                    LocalDate.of(1994, 8, 12), LocalDate.now(), "SOC-R1", EstadoCuota.AL_DIA, mem);
            socioRepo1.guardar(socio);

            rutinaService.asignarRutinaASocio(rutina.getId(), socio.getId());

            // Recargar desde disco en nuevas instancias
            RutinaRepository rutinaRepo2 = new RutinaRepository(rutinasPath);
            SocioRepository socioRepo2 = new SocioRepository(sociosPath);
            socioRepo2.setRutinaRepository(rutinaRepo2);

            Socio socioRecargado = socioRepo2.buscarPorId(socio.getId()).orElseThrow();
            afirmar(socioRecargado.getRutinasAsignadas().size() == 1,
                    "El socio recargado debe conservar 1 rutina asignada");
            afirmar(rutina.getId().equals(socioRecargado.getRutinasAsignadas().get(0).getId()),
                    "El ID de la rutina hidratada debe coincidir");
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 13. Comprueba Check-In estricto, período de gracia de 5 días e Instructores (BUG-06, BUG-12, BUG-20).
     */
    private static void testCheckInEstrictoGraciaEInstructores() throws Exception {
        Path tempDir = crearTempDir("checkin-estricto");
        try {
            SocioRepository socioRepo = new SocioRepository(tempDir.resolve("socios.json"));
            InstructorRepository instRepo = new InstructorRepository(tempDir.resolve("instructores.json"));
            AsistenciaRepository asistRepo = new AsistenciaRepository(tempDir.resolve("asistencias.json"));
            ClaseRepository claseRepo = new ClaseRepository(tempDir.resolve("clases.json"), socioRepo, instRepo);

            AccesoService accesoService = new AccesoService(socioRepo, instRepo, asistRepo, claseRepo);

            Membresia memActiva = new Membresia("MEM-OK", TipoMembresia.PASE_LIBRE, 18500.0,
                    LocalDate.now().minusDays(10), LocalDate.now().plusDays(20), true);
            Socio sBase = new Socio("SOC-AUT0", "35000000", "Base", "Test", "base@test.com", "1144001100",
                    LocalDate.of(1990, 1, 1), LocalDate.now(), "SOC-AUT0", EstadoCuota.AL_DIA, memActiva);
            afirmar(!sBase.autenticar(""), "autenticar(\"\") debe retornar false");
            afirmar(!sBase.autenticar("   "), "autenticar(\"   \") debe retornar false");

            // 1. Socio sin membresía -> DENEGADO_CUOTA_VENCIDA
            Socio sSinMem = new Socio("SOC-SM", "35000001", "Sin", "Membresia", "sm@test.com", "1144001101",
                    LocalDate.of(1990, 1, 1), LocalDate.now(), "SOC-SM", EstadoCuota.AL_DIA, null);
            socioRepo.guardar(sSinMem);
            afirmar(accesoService.registrarCheckIn("35000001", TipoAcceso.GENERAL, null).getEstadoAcceso()
                    == EstadoAcceso.DENEGADO_CUOTA_VENCIDA, "Socio sin membresía debe ser DENEGADO_CUOTA_VENCIDA");

            // 2. Socio AL_DIA pero vencimiento ayer -> DENEGADO_CUOTA_VENCIDA
            Membresia memAyer = new Membresia("MEM-AYER", TipoMembresia.PASE_LIBRE, 18500.0,
                    LocalDate.now().minusMonths(1), LocalDate.now().minusDays(1), true);
            Socio sAyer = new Socio("SOC-AY", "35000002", "Ayer", "Vencio", "ay@test.com", "1144001102",
                    LocalDate.of(1990, 1, 1), LocalDate.now(), "SOC-AY", EstadoCuota.AL_DIA, memAyer);
            socioRepo.guardar(sAyer);
            afirmar(accesoService.registrarCheckIn("35000002", TipoAcceso.GENERAL, null).getEstadoAcceso()
                    == EstadoAcceso.DENEGADO_CUOTA_VENCIDA, "AL_DIA con vencimiento ayer debe ser denegado");

            // 3. Socio PENDIENTE con vencimiento hace 2 días -> PERMITIDO (gracia 5 días)
            Membresia memGraciaOk = new Membresia("MEM-G2", TipoMembresia.PASE_LIBRE, 18500.0,
                    LocalDate.now().minusMonths(1), LocalDate.now().minusDays(2), true);
            Socio sGraciaOk = new Socio("SOC-G2", "35000003", "Gracia", "Dentro", "g2@test.com", "1144001103",
                    LocalDate.of(1990, 1, 1), LocalDate.now(), "SOC-G2", EstadoCuota.PENDIENTE, memGraciaOk);
            socioRepo.guardar(sGraciaOk);
            afirmar(accesoService.registrarCheckIn("35000003", TipoAcceso.GENERAL, null).getEstadoAcceso()
                    == EstadoAcceso.PERMITIDO, "PENDIENTE dentro de 5 días de gracia debe ser PERMITIDO");

            // 4. Socio PENDIENTE con vencimiento hace 6 días -> DENEGADO_CUOTA_VENCIDA
            Membresia memGraciaExp = new Membresia("MEM-G6", TipoMembresia.PASE_LIBRE, 18500.0,
                    LocalDate.now().minusMonths(1), LocalDate.now().minusDays(6), true);
            Socio sGraciaExp = new Socio("SOC-G6", "35000004", "Gracia", "Fuera", "g6@test.com", "1144001104",
                    LocalDate.of(1990, 1, 1), LocalDate.now(), "SOC-G6", EstadoCuota.PENDIENTE, memGraciaExp);
            socioRepo.guardar(sGraciaExp);
            afirmar(accesoService.registrarCheckIn("35000004", TipoAcceso.GENERAL, null).getEstadoAcceso()
                    == EstadoAcceso.DENEGADO_CUOTA_VENCIDA, "PENDIENTE con 6 días vencido debe ser DENEGADO_CUOTA_VENCIDA");

            // 5. CLASE_GRUPAL con claseId nulo/inexistente/no inscripto vs inscripto en clase llena 1/1
            Instructor inst = new Instructor("INS-CHK", "26000001", "Profe", "Check", "pchk@test.com", "1144001199",
                    LocalDate.of(1985, 1, 1), LocalDate.now(), "LEG-CHK", "Spinning", 400000.0, 5000.0, "Mañana");
            instRepo.guardar(inst);

            Socio sInscripto = new Socio("SOC-INS", "35000005", "Inscripto", "Full", "ins@test.com", "1144001105",
                    LocalDate.of(1990, 1, 1), LocalDate.now(), "SOC-INS", EstadoCuota.AL_DIA, memActiva);
            Socio sNoInscripto = new Socio("SOC-NO", "35000006", "NoInscripto", "Full", "no@test.com", "1144001106",
                    LocalDate.of(1990, 1, 1), LocalDate.now(), "SOC-NO", EstadoCuota.AL_DIA, memActiva);
            socioRepo.guardar(sInscripto);
            socioRepo.guardar(sNoInscripto);

            ClaseGrupal claseLlena = new ClaseGrupal("CLS-1X1", "Spinning 1x1", Disciplina.SPINNING,
                    DayOfWeek.WEDNESDAY, LocalTime.of(18, 0), 45, "Sala S", 1, inst);
            claseLlena.inscribirSocio(sInscripto);
            claseRepo.guardar(claseLlena);

            afirmar(accesoService.registrarCheckIn("35000005", TipoAcceso.CLASE_GRUPAL, null).getEstadoAcceso()
                    == EstadoAcceso.DENEGADO_MEMBRESIA_INSUFICIENTE, "claseId null debe denegarse");
            afirmar(accesoService.registrarCheckIn("35000005", TipoAcceso.CLASE_GRUPAL, "CLS-NOEXISTE").getEstadoAcceso()
                    == EstadoAcceso.DENEGADO_MEMBRESIA_INSUFICIENTE, "claseId inexistente debe denegarse");
            afirmar(accesoService.registrarCheckIn("35000006", TipoAcceso.CLASE_GRUPAL, "CLS-1X1").getEstadoAcceso()
                    == EstadoAcceso.DENEGADO_MEMBRESIA_INSUFICIENTE, "Socio no inscripto debe denegarse");
            afirmar(accesoService.registrarCheckIn("35000005", TipoAcceso.CLASE_GRUPAL, "CLS-1X1").getEstadoAcceso()
                    == EstadoAcceso.PERMITIDO, "Socio ya inscripto en clase llena 1/1 debe ser PERMITIDO");

            // 6. Instructor check-in -> PERMITIDO
            Asistencia asistInst = accesoService.registrarCheckIn("26000001", TipoAcceso.GENERAL, null);
            afirmar(asistInst.getEstadoAcceso() == EstadoAcceso.PERMITIDO,
                    "Check-in de Instructor debe ser PERMITIDO");

            // 7. Dos check-ins aprobados del mismo socio en el día -> contarPresentesHoy() cuenta únicos
            int presentesAntes = accesoService.contarPresentesHoy();
            accesoService.registrarCheckIn("35000005", TipoAcceso.GENERAL, null);
            int presentesDespues = accesoService.contarPresentesHoy();
            afirmar(presentesDespues == presentesAntes,
                    "Dos check-ins del mismo socio en el día no deben duplicar contarPresentesHoy()");

            // Verificar aislamiento de conteo diario en repo limpio con 2 check-ins del mismo socio
            Path tempDirUnico = crearTempDir("presentes-unico");
            try {
                SocioRepository sr2 = new SocioRepository(tempDirUnico.resolve("s.json"));
                AsistenciaRepository ar2 = new AsistenciaRepository(tempDirUnico.resolve("a.json"));
                AccesoService as2 = new AccesoService(sr2, ar2, null);
                sr2.guardar(sInscripto);
                as2.registrarCheckIn("35000005", TipoAcceso.GENERAL, null);
                as2.registrarCheckIn("35000005", TipoAcceso.GENERAL, null);
                afirmar(as2.contarPresentesHoy() == 1,
                        "Dos check-ins aprobados del mismo socio en el día deben dar contarPresentesHoy() == 1");
            } finally {
                eliminarRecursivo(tempDirUnico);
            }
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 14. Comprueba idempotencia de DataSeedService ante repositorios parcialmente poblados (BUG-07).
     */
    private static void testDataSeedIdempotente() throws Exception {
        Path tempDirParcial = crearTempDir("seed-parcial");
        Path tempDirVacio = crearTempDir("seed-vacio");
        try {
            // Caso A: SocioRepository con 1 socio, resto vacío -> no debe sembrar ni sobrescribir
            SocioRepository sParcial = new SocioRepository(tempDirParcial.resolve("socios.json"));
            InstructorRepository iParcial = new InstructorRepository(tempDirParcial.resolve("instructores.json"));
            ClaseRepository cParcial = new ClaseRepository(tempDirParcial.resolve("clases.json"), sParcial, iParcial);
            RutinaRepository rParcial = new RutinaRepository(tempDirParcial.resolve("rutinas.json"));
            PagoRepository pParcial = new PagoRepository(tempDirParcial.resolve("pagos.json"));
            AsistenciaRepository aParcial = new AsistenciaRepository(tempDirParcial.resolve("asistencias.json"));

            Membresia mem = new Membresia("MEM-SEED", TipoMembresia.MUSCULACION, 12000.0,
                    LocalDate.now(), LocalDate.now().plusMonths(1), true);
            sParcial.guardar(new Socio("SOC-EXIST", "39111222", "Único", "Socio", "unico@test.com", "1144559900",
                    LocalDate.of(1995, 5, 5), LocalDate.now(), "SOC-EXIST", EstadoCuota.AL_DIA, mem));

            DataSeedService seedParcial = new DataSeedService(sParcial, iParcial, cParcial, rParcial, pParcial, aParcial);
            seedParcial.sembrarSiEsNecesario();

            afirmar(sParcial.contar() == 1, "No debe sobrescribir ni agregar socios si ya hay datos");
            afirmar(iParcial.contar() == 0 && cParcial.contar() == 0 && rParcial.contar() == 0
                    && pParcial.contar() == 0 && aParcial.contar() == 0,
                    "No debe sembrar el resto de repositorios si al menos uno ya tiene datos");

            // Caso B: Todos vacíos -> siembra completa (10/5/5/3/5/4)
            SocioRepository sVacio = new SocioRepository(tempDirVacio.resolve("socios.json"));
            InstructorRepository iVacio = new InstructorRepository(tempDirVacio.resolve("instructores.json"));
            ClaseRepository cVacio = new ClaseRepository(tempDirVacio.resolve("clases.json"), sVacio, iVacio);
            RutinaRepository rVacio = new RutinaRepository(tempDirVacio.resolve("rutinas.json"));
            PagoRepository pVacio = new PagoRepository(tempDirVacio.resolve("pagos.json"));
            AsistenciaRepository aVacio = new AsistenciaRepository(tempDirVacio.resolve("asistencias.json"));

            DataSeedService seedVacio = new DataSeedService(sVacio, iVacio, cVacio, rVacio, pVacio, aVacio);
            seedVacio.sembrarSiEsNecesario();

            afirmar(sVacio.contar() == 10, "Debe sembrar 10 socios. Obtenido: " + sVacio.contar());
            afirmar(iVacio.contar() == 5, "Debe sembrar 5 instructores. Obtenido: " + iVacio.contar());
            afirmar(cVacio.contar() == 5, "Debe sembrar 5 clases. Obtenido: " + cVacio.contar());
            afirmar(rVacio.contar() == 3, "Debe sembrar 3 rutinas. Obtenido: " + rVacio.contar());
            afirmar(pVacio.contar() == 5, "Debe sembrar 5 pagos. Obtenido: " + pVacio.contar());
            afirmar(aVacio.contar() == 4, "Debe sembrar 4 asistencias. Obtenido: " + aVacio.contar());
        } finally {
            eliminarRecursivo(tempDirParcial);
            eliminarRecursivo(tempDirVacio);
        }
    }

    /**
     * 15. Comprueba SocioService, InstructorService, ClaseService e integridad referencial (BUG-14, BUG-15, BUG-17, BUG-38).
     */
    private static void testServiciosEIntegridadReferencial() throws Exception {
        Path tempDir = crearTempDir("servicios-integridad");
        try {
            SocioRepository socioRepo = new SocioRepository(tempDir.resolve("socios.json"));
            InstructorRepository instRepo = new InstructorRepository(tempDir.resolve("instructores.json"));
            ClaseRepository claseRepo = new ClaseRepository(tempDir.resolve("clases.json"), socioRepo, instRepo);
            PagoRepository pagoRepo = new PagoRepository(tempDir.resolve("pagos.json"));
            AsistenciaRepository asistRepo = new AsistenciaRepository(tempDir.resolve("asistencias.json"));

            SocioService socioService = new SocioService(socioRepo, claseRepo, pagoRepo, asistRepo);
            InstructorService instructorService = new InstructorService(instRepo, claseRepo, null);
            ClaseService claseService = new ClaseService(claseRepo, socioRepo, instRepo);
            FacturacionService facturacionService = new FacturacionService(pagoRepo, socioRepo);

            Socio s1 = socioService.registrarSocio("36111222", "Laura", "Gómez", "laura@test.com",
                    "1144551122", LocalDate.of(1992, 3, 15), TipoMembresia.PASE_LIBRE);
            Socio s2 = socioService.registrarSocio("36111333", "Diego", "Sosa", "diego@test.com",
                    "1144551133", LocalDate.of(1991, 4, 20), TipoMembresia.MUSCULACION);

            // DNI duplicado
            boolean lanzoDniDup = false;
            try {
                socioService.registrarSocio("36111222", "Otra", "Persona", "otra@test.com",
                        "1144559999", LocalDate.of(1990, 1, 1), TipoMembresia.PASE_LIBRE);
            } catch (IllegalStateException e) {
                lanzoDniDup = true;
            }
            afirmar(lanzoDniDup, "Debe rechazar DNI duplicado en SocioService");

            // Email duplicado
            boolean lanzoEmailDup = false;
            try {
                socioService.registrarSocio("36999888", "Otra", "Persona", "laura@test.com",
                        "1144559999", LocalDate.of(1990, 1, 1), TipoMembresia.PASE_LIBRE);
            } catch (IllegalStateException e) {
                lanzoEmailDup = true;
            }
            afirmar(lanzoEmailDup, "Debe rechazar email duplicado en SocioService");

            Instructor inst1 = instructorService.registrarInstructor("27111222", "Mateo", "Silva", "mateo@test.com",
                    "1144552233", LocalDate.of(1986, 6, 10), "Funcional", 400000.0, 5000.0, "Tarde");
            Instructor inst2 = instructorService.registrarInstructor("27111333", "Clara", "Vega", "clara@test.com",
                    "1144552244", LocalDate.of(1987, 7, 11), "Boxeo", 400000.0, 5000.0, "Tarde");

            ClaseGrupal clase1 = claseService.crearClase("Funcional A", Disciplina.FUNCIONAL,
                    DayOfWeek.MONDAY, LocalTime.of(18, 0), 60, "Sala A", 1, inst1);

            // Conflicto horario en misma sala con otro instructor
            boolean lanzoConflictoSala = false;
            try {
                claseService.crearClase("Boxeo A", Disciplina.BOXEO,
                        DayOfWeek.MONDAY, LocalTime.of(18, 30), 60, "Sala A", 10, inst2);
            } catch (IllegalStateException e) {
                lanzoConflictoSala = true;
            }
            afirmar(lanzoConflictoSala, "Debe rechazar conflicto horario en la misma sala");

            // Conflicto horario con mismo instructor en otra sala
            boolean lanzoConflictoInst = false;
            try {
                claseService.crearClase("Funcional B", Disciplina.FUNCIONAL,
                        DayOfWeek.MONDAY, LocalTime.of(18, 30), 60, "Sala B", 10, inst1);
            } catch (IllegalStateException e) {
                lanzoConflictoInst = true;
            }
            afirmar(lanzoConflictoInst, "Debe rechazar conflicto horario del mismo instructor");

            // Inscribir s1 (ocupa el cupo 1/1)
            afirmar(claseService.inscribirSocioEnClase(clase1.getId(), s1.getId()), "s1 debe inscribirse");

            // Rechazar reducción de cupoMaximo por debajo de inscriptos
            boolean lanzoCupoMenor = false;
            try {
                clase1.setCupoMaximo(0);
            } catch (IllegalArgumentException e) {
                lanzoCupoMenor = true;
            }
            afirmar(lanzoCupoMenor, "Debe rechazar cupoMaximo < inscriptos");

            // Motivos específicos de rechazo de inscripción
            String motivoYaInscripto = claseService.obtenerMotivoRechazoInscripcion(clase1.getId(), s1.getId());
            afirmar(motivoYaInscripto != null && motivoYaInscripto.contains("ya está inscripto"),
                    "Motivo esperado ya inscripto, obtenido: " + motivoYaInscripto);

            String motivoSinCupo = claseService.obtenerMotivoRechazoInscripcion(clase1.getId(), s2.getId());
            afirmar(motivoSinCupo != null && motivoSinCupo.contains("cupos disponibles"),
                    "Motivo esperado sin cupos, obtenido: " + motivoSinCupo);

            ClaseGrupal clase2 = claseService.crearClase("Boxeo Libre", Disciplina.BOXEO,
                    DayOfWeek.THURSDAY, LocalTime.of(20, 0), 60, "Sala B", 10, inst2);
            String motivoMembresia = claseService.obtenerMotivoRechazoInscripcion(clase2.getId(), s2.getId());
            afirmar(motivoMembresia != null && motivoMembresia.contains("membresía"),
                    "Motivo esperado membresía insuficiente para MUSCULACION, obtenido: " + motivoMembresia);

            // Bloquear baja de socio con pagos históricos
            facturacionService.cobrarCuota(s1.getId(), MetodoPago.EFECTIVO, "Cuota 1");
            boolean lanzoBajaConPagos = false;
            try {
                socioService.eliminarSocio(s1.getId());
            } catch (IllegalStateException e) {
                lanzoBajaConPagos = true;
            }
            afirmar(lanzoBajaConPagos, "Debe bloquear baja de socio con pagos históricos");

            // eliminarClase sincroniza Instructor.clasesAsignadas
            afirmar(inst1.getClasesAsignadas().size() == 1, "inst1 debe tener 1 clase asignada");
            afirmar(claseService.eliminarClase(clase1.getId()), "eliminarClase debe retornar true");
            Instructor inst1Actualizado = instRepo.buscarPorId(inst1.getId()).orElseThrow();
            afirmar(inst1Actualizado.getClasesAsignadas().isEmpty(),
                    "eliminarClase debe remover la clase de Instructor.clasesAsignadas");
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 16. Comprueba FacturacionService, RutinaService, bono docente e invariantes (BUG-18, BUG-19, BUG-32, BUG-33, BUG-35, BUG-52).
     */
    private static void testFacturacionRutinaBonoEInvariantes() throws Exception {
        Path tempDir = crearTempDir("facturacion-rutina-invariantes");
        try {
            SocioRepository socioRepo = new SocioRepository(tempDir.resolve("socios.json"));
            PagoRepository pagoRepo = new PagoRepository(tempDir.resolve("pagos.json"));
            RutinaRepository rutinaRepo = new RutinaRepository(tempDir.resolve("rutinas.json"));

            FacturacionService facturacionService = new FacturacionService(pagoRepo, socioRepo);
            RutinaService rutinaService = new RutinaService(rutinaRepo, socioRepo);

            // Socio con membresía vencida para verificar renovación
            Membresia memVencida = new Membresia("MEM-V", TipoMembresia.PASE_LIBRE, 18500.0,
                    LocalDate.now().minusMonths(2), LocalDate.now().minusDays(5), true);
            Socio socio = new Socio("SOC-FAC", "37111222", "Pablo", "Núñez", "r@p.com", "1144553344",
                    LocalDate.of(1990, 5, 5), LocalDate.now().minusMonths(2), "SOC-FAC", EstadoCuota.VENCIDO, memVencida);
            socioRepo.guardar(socio);

            // Rechazar metodoPago == null
            boolean lanzoMetodoNull = false;
            try {
                facturacionService.cobrarCuota(socio.getId(), null, "Cuota");
            } catch (IllegalArgumentException e) {
                lanzoMetodoNull = true;
            }
            afirmar(lanzoMetodoNull, "cobrarCuota debe rechazar metodoPago == null");

            // Rechazar membresia == null
            Socio socioSinMem = new Socio("SOC-NMEM", "37111333", "Sin", "Mem", "smem@test.com", "1144553355",
                    LocalDate.of(1990, 5, 5), LocalDate.now(), "SOC-NMEM", EstadoCuota.VENCIDO, null);
            socioRepo.guardar(socioSinMem);
            boolean lanzoMemNull = false;
            try {
                facturacionService.cobrarCuota(socioSinMem.getId(), MetodoPago.EFECTIVO, "Cuota");
            } catch (IllegalStateException e) {
                lanzoMemNull = true;
            }
            afirmar(lanzoMemNull, "cobrarCuota debe rechazar membresía null");

            // Rechazar monto <= 0 y setMetodoPago(null) en Pago
            boolean lanzoMontoCero = false;
            try {
                new Pago("PAG-ERR", "REC-1", "SOC-FAC", 0.0, LocalDateTime.now(), MetodoPago.EFECTIVO, "Test");
            } catch (IllegalArgumentException e) {
                lanzoMontoCero = true;
            }
            afirmar(lanzoMontoCero, "Pago debe rechazar monto <= 0");

            Pago pagoValido = facturacionService.cobrarCuota(socio.getId(), MetodoPago.TARJETA_CREDITO, "Renovación");
            afirmar(pagoValido.getId() != null && pagoValido.getId().startsWith("PAG-"),
                    "El ID de pago generado debe comenzar con PAG-: " + pagoValido.getId());

            boolean lanzoSetMetodoNull = false;
            try {
                pagoValido.setMetodoPago(null);
            } catch (IllegalArgumentException e) {
                lanzoSetMetodoNull = true;
            }
            afirmar(lanzoSetMetodoNull, "Pago.setMetodoPago(null) debe lanzar IllegalArgumentException");

            Socio socioRenovado = socioRepo.buscarPorId(socio.getId()).orElseThrow();
            afirmar(LocalDate.now().equals(socioRenovado.getMembresiaActual().getFechaInicio()),
                    "Renovar membresía vencida debe actualizar fechaInicio a hoy");
            afirmar(socioRenovado.getEstadoCuota() == EstadoCuota.AL_DIA,
                    "Renovar membresía debe pasar el socio a AL_DIA");

            // RutinaService validaciones y desasignación al eliminar
            Rutina rutina = rutinaService.crearRutina("Fuerza", "Potencia", "Medio", "INS-01");
            boolean lanzoSeriesCero = false;
            try {
                rutinaService.agregarEjercicioARutina(rutina.getId(), "Curl", GrupoMuscular.BRAZOS, 0, 10, 20.0, 60, "");
            } catch (IllegalArgumentException e) {
                lanzoSeriesCero = true;
            }
            afirmar(lanzoSeriesCero, "agregarEjercicioARutina debe rechazar series <= 0");

            boolean lanzoGrupoNull = false;
            try {
                rutinaService.agregarEjercicioARutina(rutina.getId(), "Curl", null, 3, 10, 20.0, 60, "");
            } catch (IllegalArgumentException e) {
                lanzoGrupoNull = true;
            }
            afirmar(lanzoGrupoNull, "agregarEjercicioARutina debe rechazar grupo == null");

            rutinaService.asignarRutinaASocio(rutina.getId(), socio.getId());
            afirmar(socioRepo.buscarPorId(socio.getId()).orElseThrow().getRutinasAsignadas().size() == 1,
                    "El socio debe tener la rutina asignada antes de eliminarla");
            afirmar(rutinaService.eliminarRutina(rutina.getId()), "eliminarRutina debe retornar true");
            afirmar(socioRepo.buscarPorId(socio.getId()).orElseThrow().getRutinasAsignadas().isEmpty(),
                    "eliminarRutina debe desasignar la rutina de los socios");

            // Bono docente Instructor con > 3 disciplinas distintas (+15000.0)
            Instructor instBono = new Instructor("INS-BONO", "25999888", "Multi", "Profe", "multi@test.com", "1144559988",
                    LocalDate.of(1984, 1, 1), LocalDate.now(), "LEG-BONO", "Multi", 100000.0, 1000.0, "Completo");
            new ClaseGrupal("CB-1", "C1", Disciplina.SPINNING, DayOfWeek.MONDAY, LocalTime.of(9, 0), 50, "S1", 10, instBono);
            new ClaseGrupal("CB-2", "C2", Disciplina.CROSSFIT, DayOfWeek.TUESDAY, LocalTime.of(9, 0), 50, "S1", 10, instBono);
            new ClaseGrupal("CB-3", "C3", Disciplina.YOGA, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), 50, "S1", 10, instBono);
            new ClaseGrupal("CB-4", "C4", Disciplina.BOXEO, DayOfWeek.THURSDAY, LocalTime.of(9, 0), 50, "S1", 10, instBono);
            double liqConBono = 100000.0 + (4 * 4 * 1000.0) + 15000.0;
            afirmar(Math.abs(instBono.calcularLiquidacionMensual() - liqConBono) < 0.001,
                    "Debe aplicar bono de 15000.0 con > 3 disciplinas distintas");

            // ValidationUtils
            afirmar(!ValidationUtils.esEmailValido(".."), "Email \"..\" debe ser rechazado");
            afirmar(!ValidationUtils.esTelefonoValido("------"), "Teléfono \"------\" debe ser rechazado");
            afirmar(ValidationUtils.esEmailValido("r@p.com"), "Email \"r@p.com\" debe ser aceptado");
        } finally {
            eliminarRecursivo(tempDir);
        }
    }

    /**
     * 17. Smoke tests GUI headless-safe (BUG-08, BUG-09).
     */
    private static void testGuiSmokeHeadless() {
        StatusBadgeCellRenderer renderer = new StatusBadgeCellRenderer();

        renderer.getTableCellRendererComponent(null, "Al Día", false, false, 0, 0);
        afirmar(ThemeConfig.STATUS_GREEN.equals(renderer.getForeground()),
                "\"Al Día\" debe usar color STATUS_GREEN");

        renderer.getTableCellRendererComponent(null, "DISPONIBLE", false, false, 0, 0);
        afirmar(ThemeConfig.STATUS_GREEN.equals(renderer.getForeground()),
                "\"DISPONIBLE\" debe usar color STATUS_GREEN");

        renderer.getTableCellRendererComponent(null, "COMPLETO", false, false, 0, 0);
        afirmar(ThemeConfig.STATUS_RED.equals(renderer.getForeground()),
                "\"COMPLETO\" debe usar color STATUS_RED");

        renderer.getTableCellRendererComponent(null, "PENDIENTE", false, false, 0, 0);
        afirmar(ThemeConfig.STATUS_AMBER.equals(renderer.getForeground()),
                "\"PENDIENTE\" debe usar color STATUS_AMBER");

        // Verificar que pintar sobre un BufferedImage en modo headless funciona sin Window
        BufferedImage img = new BufferedImage(120, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        try {
            renderer.setSize(120, 32);
            renderer.paint(g2);
        } finally {
            g2.dispose();
        }
        Color bgSample = new Color(img.getRGB(60, 16), true);
        afirmar(bgSample.getAlpha() > 0, "El badge debe renderizar fondo no transparente en headless");

        // TableFilterHelper con caracteres especiales regex
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Nombre", "Tag"}, 0);
        model.addRow(new Object[]{"Juan", "(011) [vip]*+"});
        model.addRow(new Object[]{"Ana", "regular"});
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);

        TableFilterHelper.aplicarFiltroTexto(sorter, "(011) [vip]*+");
        afirmar(sorter.getViewRowCount() == 1,
                "El filtro con regex especial debe coincidir con 1 fila sin PatternSyntaxException");
    }

    private static Path crearTempDir(String prefijo) throws IOException {
        return Files.createTempDirectory("fitmanage-test-" + prefijo + "-");
    }

    private static void eliminarRecursivo(Path dir) {
        if (dir == null || !Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException ignored) {
                }
            });
        } catch (IOException ignored) {
        }
    }

    private static void afirmar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError("Fallo de aserción: " + mensaje);
        }
    }

    @FunctionalInterface
    private interface RunnableTest {
        void run() throws Throwable;
    }
}
