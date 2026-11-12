package com.clubdeportivo.service;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Servicio de inicialización y siembra de datos de prueba completos para el sistema FitManage Pro.
 * Garantiza que el evaluador disponga de un entorno rico, variado y funcional desde el primer segundo.
 *
 * @author Joel
 * @author Sara
 * @version 1.0.0
 */
public class DataSeedService {

    private final SocioRepository socioRepo;
    private final InstructorRepository instructorRepo;
    private final ClaseRepository claseRepo;
    private final RutinaRepository rutinaRepo;
    private final PagoRepository pagoRepo;
    private final AsistenciaRepository asistenciaRepo;

    public DataSeedService(SocioRepository socioRepo,
                           InstructorRepository instructorRepo,
                           ClaseRepository claseRepo,
                           RutinaRepository rutinaRepo,
                           PagoRepository pagoRepo,
                           AsistenciaRepository asistenciaRepo) {
        this.socioRepo = socioRepo;
        this.instructorRepo = instructorRepo;
        this.claseRepo = claseRepo;
        this.rutinaRepo = rutinaRepo;
        this.pagoRepo = pagoRepo;
        this.asistenciaRepo = asistenciaRepo;
    }

    /**
     * Si los repositorios están vacíos, inyecta la población inicial demo.
     */
    public void sembrarSiEsNecesario() {
        if (socioRepo.contar() > 0 && instructorRepo.contar() > 0 && claseRepo.contar() > 0) {
            return; // Ya hay datos persistidos
        }

        System.out.println(">> [SEED] Inicializando semillero de datos de FitManage Pro...");

        // 1. INSTRUCTORES
        Instructor ins1 = new Instructor("INS-101", "30123456", "Lucas", "Benítez",
                "lucas.benitez@fitclub.com", "11-4567-8901", LocalDate.of(1988, 5, 12),
                LocalDate.of(2022, 2, 1), "LEG-101", "Spinning Indoor & Resistencia",
                450000.0, 6500.0, "Turno Mañana");

        Instructor ins2 = new Instructor("INS-102", "33789012", "Mariana", "Valenzuela",
                "mariana.v@fitclub.com", "11-3456-7890", LocalDate.of(1992, 9, 20),
                LocalDate.of(2023, 3, 15), "LEG-102", "CrossFit WOD & Levantamiento Olímpico",
                480000.0, 7500.0, "Turno Tarde");

        Instructor ins3 = new Instructor("INS-103", "36456789", "Esteban", "Castro",
                "esteban.c@fitclub.com", "11-9876-5432", LocalDate.of(1990, 11, 4),
                LocalDate.of(2023, 8, 1), "LEG-103", "Yoga Flow & Movilidad Articular",
                420000.0, 6000.0, "Turno Mañana");

        Instructor ins4 = new Instructor("INS-104", "38111222", "Valeria", "Ramos",
                "valeria.ramos@fitclub.com", "11-2233-4455", LocalDate.of(1994, 7, 18),
                LocalDate.of(2024, 1, 10), "LEG-104", "Entrenamiento Funcional & HIIT",
                430000.0, 6200.0, "Turno Noche");

        Instructor ins5 = new Instructor("INS-105", "29999888", "Rodrigo", "Paz",
                "rodrigo.paz@fitclub.com", "11-6677-8899", LocalDate.of(1985, 3, 30),
                LocalDate.of(2021, 6, 1), "LEG-105", "Power Boxeo & Defensa Personal",
                460000.0, 7000.0, "Turno Tarde");

        instructorRepo.guardar(ins1);
        instructorRepo.guardar(ins2);
        instructorRepo.guardar(ins3);
        instructorRepo.guardar(ins4);
        instructorRepo.guardar(ins5);

        // 2. RUTINAS Y EJERCICIOS (Composición fuerte)
        Rutina rut1 = new Rutina("RUT-101", "Hipertrofia Torso & Carga Progresiva",
                "Hipertrofia Muscular", "Avanzado",
                LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(1), ins1.getId());
        rut1.agregarEjercicio(new Ejercicio("EJE-101", "Press de Banca Plano con Barra", GrupoMuscular.PECHO, 4, 10, 80.0, 90, "Retracción escapular estricta"));
        rut1.agregarEjercicio(new Ejercicio("EJE-102", "Dominadas Pronadas Lastradas", GrupoMuscular.ESPALDA, 4, 8, 10.0, 90, "Rango de recorrido completo"));
        rut1.agregarEjercicio(new Ejercicio("EJE-103", "Press Militar con Barra de Pie", GrupoMuscular.HOMBROS, 3, 12, 50.0, 60, "Glúteos y core contraídos"));
        rutinaRepo.guardar(rut1);

        Rutina rut2 = new Rutina("RUT-102", "Fuerza Máxima de Miembros Inferiores",
                "Fuerza Máxima", "Intermedio",
                LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2), ins2.getId());
        rut2.agregarEjercicio(new Ejercicio("EJE-104", "Sentadilla Trasera Profunda", GrupoMuscular.PIERNAS, 4, 6, 100.0, 120, "Descenso controlado a 90 grados"));
        rut2.agregarEjercicio(new Ejercicio("EJE-105", "Peso Muerto Rumano con Mancuernas", GrupoMuscular.PIERNAS, 3, 10, 85.0, 90, "Foco en isquiosurales"));
        rut2.agregarEjercicio(new Ejercicio("EJE-106", "Plancha Isométrica con Disco", GrupoMuscular.CORE, 4, 45, 15.0, 45, "Alineación neutra lumbar"));
        rutinaRepo.guardar(rut2);

        Rutina rut3 = new Rutina("RUT-103", "Acondicionamiento Metabólico y Quema Grasa",
                "Descenso de Peso", "Principiante",
                LocalDate.now().minusWeeks(3), LocalDate.now().plusMonths(1), ins4.getId());
        rut3.agregarEjercicio(new Ejercicio("EJE-107", "Fondos en Paralelas Asistidos", GrupoMuscular.BRAZOS, 3, 12, 0.0, 60, "Flexión de codos a 90 grados"));
        rut3.agregarEjercicio(new Ejercicio("EJE-108", "Curl de Bíceps con Barra Z", GrupoMuscular.BRAZOS, 3, 12, 25.0, 45, "Sin balanceo de torso"));
        rut3.agregarEjercicio(new Ejercicio("EJE-109", "Crunch en Polea Alta", GrupoMuscular.CORE, 3, 15, 30.0, 45, "Contracción sostenida de 1 segundo"));
        rutinaRepo.guardar(rut3);

        // 3. SOCIOS (Con diferentes antigüedades, planes y estados de cuota)
        LocalDate hace15Meses = LocalDate.now().minusMonths(15);
        LocalDate hace6Meses = LocalDate.now().minusMonths(6);
        LocalDate hace18Meses = LocalDate.now().minusMonths(18);
        LocalDate hace2Meses = LocalDate.now().minusMonths(2);

        // Socio 1: Al día, Pase Libre, Antiguo (+12 meses -> 10% Descuento automático)
        Membresia mem1 = new Membresia("MEM-101", TipoMembresia.PASE_LIBRE, 18500.0,
                LocalDate.now().minusDays(10), LocalDate.now().plusDays(20), true);
        Socio s1 = new Socio("SOC-101", "32111222", "Juan Carlos", "Rossi",
                "jc.rossi@email.com", "11-5555-1111", LocalDate.of(1986, 4, 15),
                hace15Meses, "SOC-101", EstadoCuota.AL_DIA, mem1);
        s1.asignarRutina(rut1);

        // Socio 2: Al día, VIP Total
        Membresia mem2 = new Membresia("MEM-102", TipoMembresia.VIP_TOTAL, 25000.0,
                LocalDate.now().minusDays(5), LocalDate.now().plusDays(25), true);
        Socio s2 = new Socio("SOC-102", "38555444", "Sofía Belén", "Romero",
                "sofia.romero@email.com", "11-5555-2222", LocalDate.of(1995, 8, 22),
                hace6Meses, "SOC-102", EstadoCuota.AL_DIA, mem2);
        s2.asignarRutina(rut2);

        // Socio 3: Vencido (Moroso) -> Bloqueado para acceso
        Membresia mem3 = new Membresia("MEM-103", TipoMembresia.MUSCULACION, 12000.0,
                LocalDate.now().minusMonths(2), LocalDate.now().minusDays(5), false);
        Socio s3 = new Socio("SOC-103", "40123987", "Martín Alejandro", "Díaz",
                "martin.diaz@email.com", "11-5555-3333", LocalDate.of(1997, 1, 10),
                hace6Meses, "SOC-103", EstadoCuota.VENCIDO, mem3);

        // Socio 4: Al día, Clases Grupales, Antiguo (+12 meses)
        Membresia mem4 = new Membresia("MEM-104", TipoMembresia.CLASES_GRUPALES, 14500.0,
                LocalDate.now().minusDays(15), LocalDate.now().plusDays(15), true);
        Socio s4 = new Socio("SOC-104", "35987654", "Luciana", "Morales",
                "luciana.m@email.com", "11-5555-4444", LocalDate.of(1991, 10, 5),
                hace18Meses, "SOC-104", EstadoCuota.AL_DIA, mem4);

        // Socio 5: Pendiente (En período de gracia de 5 días)
        Membresia mem5 = new Membresia("MEM-105", TipoMembresia.PASE_LIBRE, 18500.0,
                LocalDate.now().minusMonths(1), LocalDate.now().minusDays(2), true);
        Socio s5 = new Socio("SOC-105", "28456123", "Gonzalo", "Fernández",
                "gonzalo.f@email.com", "11-5555-5555", LocalDate.of(1981, 12, 14),
                hace2Meses, "SOC-105", EstadoCuota.PENDIENTE, mem5);

        // Socio 6: Al día, Solo Musculación (No puede anotarse en clases grupales)
        Membresia mem6 = new Membresia("MEM-106", TipoMembresia.MUSCULACION, 12000.0,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(28), true);
        Socio s6 = new Socio("SOC-106", "42333444", "Florencia", "Gómez",
                "flor.gomez@email.com", "11-5555-6666", LocalDate.of(1999, 6, 18),
                hace2Meses, "SOC-106", EstadoCuota.AL_DIA, mem6);
        s6.asignarRutina(rut3);

        // Socio 7: Al día, VIP
        Membresia mem7 = new Membresia("MEM-107", TipoMembresia.VIP_TOTAL, 25000.0,
                LocalDate.now().minusDays(8), LocalDate.now().plusDays(22), true);
        Socio s7 = new Socio("SOC-107", "33888777", "Diego Armando", "Paredes",
                "diego.paredes@email.com", "11-5555-7777", LocalDate.of(1989, 2, 28),
                hace6Meses, "SOC-107", EstadoCuota.AL_DIA, mem7);

        // Socio 8: Al día, Pase Libre
        Membresia mem8 = new Membresia("MEM-108", TipoMembresia.PASE_LIBRE, 18500.0,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(29), true);
        Socio s8 = new Socio("SOC-108", "39444111", "Camila Andrea", "Navarro",
                "camila.n@email.com", "11-5555-8888", LocalDate.of(1996, 7, 7),
                hace6Meses, "SOC-108", EstadoCuota.AL_DIA, mem8);

        // Socio 9: Al día, Pase Libre
        Membresia mem9 = new Membresia("MEM-109", TipoMembresia.PASE_LIBRE, 18500.0,
                LocalDate.now().minusDays(3), LocalDate.now().plusDays(27), true);
        Socio s9 = new Socio("SOC-109", "41555666", "Mateo", "Ruiz",
                "mateo.ruiz@email.com", "11-5555-9999", LocalDate.of(1998, 9, 12),
                hace2Meses, "SOC-109", EstadoCuota.AL_DIA, mem9);

        // Socio 10: Inactivo
        Membresia mem10 = new Membresia("MEM-110", TipoMembresia.MUSCULACION, 12000.0,
                LocalDate.now().minusMonths(6), LocalDate.now().minusMonths(5), false);
        Socio s10 = new Socio("SOC-110", "36777888", "Valentina", "Herrera",
                "valentina.h@email.com", "11-5555-0000", LocalDate.of(1992, 3, 3),
                LocalDate.now().minusYears(1), "SOC-110", EstadoCuota.INACTIVO, mem10);

        socioRepo.guardar(s1);
        socioRepo.guardar(s2);
        socioRepo.guardar(s3);
        socioRepo.guardar(s4);
        socioRepo.guardar(s5);
        socioRepo.guardar(s6);
        socioRepo.guardar(s7);
        socioRepo.guardar(s8);
        socioRepo.guardar(s9);
        socioRepo.guardar(s10);

        // 4. CLASES GRUPALES (Con asignación de profesor y socios)
        ClaseGrupal c1 = new ClaseGrupal("CLS-101", "Spinning Pro Interval", Disciplina.SPINNING,
                DayOfWeek.MONDAY, LocalTime.of(8, 0), 50, "Sala Cardio 1", 20, ins1);
        try {
            c1.inscribirSocio(s1);
            c1.inscribirSocio(s4);
            c1.inscribirSocio(s8);
        } catch (CupoAgotadoException ignored) {
        }
        claseRepo.guardar(c1);

        ClaseGrupal c2 = new ClaseGrupal("CLS-102", "CrossFit WOD Elite", Disciplina.CROSSFIT,
                DayOfWeek.TUESDAY, LocalTime.of(19, 0), 60, "Box CrossFit", 15, ins2);
        try {
            c2.inscribirSocio(s1);
            c2.inscribirSocio(s2);
            c2.inscribirSocio(s7);
        } catch (CupoAgotadoException ignored) {
        }
        claseRepo.guardar(c2);

        ClaseGrupal c3 = new ClaseGrupal("CLS-103", "Yoga Vinyasa Relax", Disciplina.YOGA,
                DayOfWeek.WEDNESDAY, LocalTime.of(9, 30), 60, "Sala Zen", 12, ins3);
        try {
            c3.inscribirSocio(s4);
            c3.inscribirSocio(s8);
        } catch (CupoAgotadoException ignored) {
        }
        claseRepo.guardar(c3);

        // Clase con cupo reducido (2 plazas) y llena para testear CupoAgotadoException
        ClaseGrupal c4 = new ClaseGrupal("CLS-104", "Funcional HIIT Power", Disciplina.FUNCIONAL,
                DayOfWeek.THURSDAY, LocalTime.of(18, 30), 45, "Sala Funcional", 2, ins4);
        try {
            c4.inscribirSocio(s8);
            c4.inscribirSocio(s9); // Ahora está completa (2/2)
        } catch (CupoAgotadoException ignored) {
        }
        claseRepo.guardar(c4);

        ClaseGrupal c5 = new ClaseGrupal("CLS-105", "Power Boxeo & Técnica", Disciplina.BOXEO,
                DayOfWeek.FRIDAY, LocalTime.of(20, 0), 60, "Sala Ring", 10, ins5);
        try {
            c5.inscribirSocio(s2);
            c5.inscribirSocio(s7);
        } catch (CupoAgotadoException ignored) {
        }
        claseRepo.guardar(c5);

        // 5. REGISTROS HISTÓRICOS DE PAGO
        Pago p1 = new Pago("PAG-1001", "REC-2026-1001", s1.getId(), s1.calcularMontoTotal(),
                LocalDateTime.now().minusDays(10), MetodoPago.TRANSFERENCIA, "Cuota Mensual Pase Libre (Desc. 10% Antigüedad)");
        Pago p2 = new Pago("PAG-1002", "REC-2026-1002", s2.getId(), s2.calcularMontoTotal(),
                LocalDateTime.now().minusDays(5), MetodoPago.TARJETA_CREDITO, "Cuota Mensual VIP Total + Locker");
        Pago p3 = new Pago("PAG-1003", "REC-2026-1003", s4.getId(), s4.calcularMontoTotal(),
                LocalDateTime.now().minusDays(15), MetodoPago.EFECTIVO, "Cuota Mensual Clases Grupales (Desc. 10% Antigüedad)");
        Pago p4 = new Pago("PAG-1004", "REC-2026-1004", s7.getId(), s7.calcularMontoTotal(),
                LocalDateTime.now().minusDays(8), MetodoPago.TARJETA_DEBITO, "Cuota Mensual VIP Total");
        Pago p5 = new Pago("PAG-1005", "REC-2026-1005", s8.getId(), s8.calcularMontoTotal(),
                LocalDateTime.now().minusDays(1), MetodoPago.TRANSFERENCIA, "Cuota Mensual Pase Libre");

        pagoRepo.guardar(p1);
        pagoRepo.guardar(p2);
        pagoRepo.guardar(p3);
        pagoRepo.guardar(p4);
        pagoRepo.guardar(p5);

        // 6. ASISTENCIAS (De hoy y recientes)
        LocalDateTime ahora = LocalDateTime.now();
        Asistencia a1 = new Asistencia("ASI-10001", s1.getId(), ahora.minusHours(2),
                TipoAcceso.GENERAL, null, EstadoAcceso.PERMITIDO, "Ingreso general autorizado. Bienvenido/a!");
        Asistencia a2 = new Asistencia("ASI-10002", s2.getId(), ahora.minusHours(1),
                TipoAcceso.GENERAL, null, EstadoAcceso.PERMITIDO, "Ingreso general autorizado. Bienvenido/a!");
        Asistencia a3 = new Asistencia("ASI-10003", s3.getId(), ahora.minusMinutes(30),
                TipoAcceso.GENERAL, null, EstadoAcceso.DENEGADO_CUOTA_VENCIDA, "Cuota social vencida. Regularizar en caja.");
        Asistencia a4 = new Asistencia("ASI-10004", s8.getId(), ahora.minusMinutes(15),
                TipoAcceso.CLASE_GRUPAL, c4.getId(), EstadoAcceso.PERMITIDO, "Ingreso a clase Funcional HIIT.");

        asistenciaRepo.guardar(a1);
        asistenciaRepo.guardar(a2);
        asistenciaRepo.guardar(a3);
        asistenciaRepo.guardar(a4);

        s1.registrarAsistencia(a1);
        s2.registrarAsistencia(a2);
        s8.registrarAsistencia(a4);

        socioRepo.guardar(s1);
        socioRepo.guardar(s2);
        socioRepo.guardar(s8);

        System.out.println(">> [SEED] Semillero cargado con éxito. 10 socios, 5 instructores, 5 clases y 3 rutinas.");
    }
}
