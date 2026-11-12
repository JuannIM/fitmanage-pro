package com.clubdeportivo.test;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.*;
import com.clubdeportivo.service.*;
import com.clubdeportivo.util.JsonPersistenceEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        Path tempSocios = Paths.get("data", "test_socios_acceso.json");
        Path tempAsist = Paths.get("data", "test_asist_acceso.json");
        Path tempClases = Paths.get("data", "test_clases_acceso.json");

        try {
            Files.deleteIfExists(tempSocios);
            Files.deleteIfExists(tempAsist);
            Files.deleteIfExists(tempClases);

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
            try {
                Files.deleteIfExists(tempSocios);
                Files.deleteIfExists(tempAsist);
                Files.deleteIfExists(tempClases);
            } catch (IOException ignored) {}
        }
    }

    /**
     * 6. Comprueba el ciclo completo de persistencia JSON atómica (escritura y lectura).
     */
    private static void testPersistenciaRoundTrip() throws Exception {
        Path tempPath = Paths.get("data", "test_roundtrip.json");
        try {
            Files.deleteIfExists(tempPath);

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
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException ignored) {}
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
