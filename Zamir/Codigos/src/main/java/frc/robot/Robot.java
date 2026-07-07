package frc.robot;

import java.util.Map;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import com.studica.frc.Titan;
import com.studica.frc.MockDS;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Ultrasonic;
/**
 * ============================================================
 *  CODIGO DE TESTE - Chassi Omni 3 Rodas
 * ============================================================
 *
 *  BLOCOS DISPONIVEIS:
 *
 *  moverDistancia(String dir, double metros)
 *    dir: "F"  -> frente
 *         "B"  -> re
 *         "SR" -> lateral direita
 *         "SL" -> lateral esquerda
 *    metros: distancia alvo em metros (usa encoders)
 *
 *  executarGiro(double grausAlvo)
 *    grausAlvo: positivo = horario, negativo = anti-horario
 *    (usa gyro VMX via PID)
 *
 *  SEQUENCIA EXEMPLO:
 *  0   moverDistancia("F",  0.5)  -> frente 0.5 m
 *  1   moverDistancia("SR", 0.4)  -> direita 0.4 m
 *  2   moverDistancia("SL", 0.4)  -> esquerda 0.4 m
 *  3   moverDistancia("B",  0.5)  -> re 0.5 m
 *  fim PARAR
 */
public class Robot extends TimedRobot {

    // =========================================================
    //  Configuracoes - translacao
    // =========================================================
    private static final double VEL_MOVE      = 0.7;

   // =========================================================
    //  Constante nova - adiciona junto com as outras
    // =========================================================
    private static final double DIST_POR_GRAU = 0.003840; // metros por grau (raio=0.22m)
    private static final double VEL_GIRO      = 0.35;     // potencia fixa de giro
    private static final double TOLERANCE_DEG = 3.0;
    private static final int    CICLOS_ESTAVEIS = 10;
    // =========================================================
    //  Variaveis novas - adiciona no topo da classe
    // =========================================================
    private final double yawAnterior = 0.0;
    private final double anguloAcumulado = 0.0;
    // =========================================================
    // Hardware
    // =========================================================
    private AHRS navx;

    private Titan titan;
    private Titan.Motor motor0;
    private Titan.Motor motor2;
    private Titan.Motor motor3;

    private Titan.Encoder enc0;
    private Titan.Encoder enc2;
    private Titan.Encoder enc3;

    private MockDS ds;

    private DigitalInput btnStart;
    private DigitalInput btnStop;
    private DigitalOutput led1, led2;

    private boolean lastStart = false;
    private boolean lastStop = false;

    // ==========================================================
    // LIDAR
    // ==========================================================
    private Ultrasonic  ultra0; // ultra da frente
    private AnalogInput ultra1; // ultra esquerdo
    private AnalogInput ultra2; // ultra direito

    // =========================================================
    // Estado da sequencia
    // =========================================================
    private int etapa = 0;
    private boolean etapaIniciada = false;

    // Estado - bloco moverDistancia
    private boolean moveIniciado = false;
    private boolean moveConcluido = false;

    // Estado - bloco executarGiro
    private boolean giroIniciado = false;
    private boolean giroConcluido = false;
    private int ciclosEstaveis = 0;
    // =========================================================
    // Configuracoes - sensor de parede
    // =========================================================
    private static final double DIST_PAREDE_MM = 200.0;
    private static final double DIST_PAREDE_MM2 = 10.0;
    private static final double DIST_RECUO_M = 0.15; // 10 cm de recuo fixo
    private static final double VEL_RECUO = 0.5;

    // Estado do sub-bloco de recuo
    private boolean recuoAtivo = false;
    private double encRefRecuo0 = 0.0;
    private double encRefRecuo2 = 0.0;
    private double encRefRecuo3 = 0.0;
    // shifiuboardi
    private final ShuffleboardTab tab = Shuffleboard.getTab("treinamento");
    private final NetworkTableEntry motor = tab.add("motores", 0)
                                                .withWidget(BuiltInWidgets.kNumberSlider)
                                                .withProperties(Map.of("min", 0, "max", 300))
                                                .getEntry();
    private final NetworkTableEntry ultraR = tab.add("ultraR", 0)
                                                .getEntry();
    private final NetworkTableEntry ultraL = tab.add("ultraL", 0)
                                                .getEntry();
    private final NetworkTableEntry ultraF = tab.add("ultraF", 0)
                                                .getEntry();
    private final NetworkTableEntry gyro = tab.add("navx", 0)
                                                .getEntry();

    // =========================================================
    @Override
    public void robotInit() {
        navx = new AHRS(SPI.Port.kMXP);

        titan = new Titan(Constants.TITAN_ID);
        motor0 = titan.getMotor(Constants.MOTOR_0);
        motor2 = titan.getMotor(Constants.MOTOR_2);
        motor3 = titan.getMotor(Constants.MOTOR_3);

        enc0 = titan.getEncoder(Constants.ENCODER_0, Constants.DIST_PER_TICK);
        enc2 = titan.getEncoder(Constants.ENCODER_2, Constants.DIST_PER_TICK);
        enc3 = titan.getEncoder(Constants.ENCODER_3, Constants.DIST_PER_TICK);

        ds = new MockDS();
        btnStart = new DigitalInput(Constants.BTN_START);
        btnStop = new DigitalInput(Constants.BTN_STOP);
        led1 = new DigitalOutput(Constants.LEDRun);
        led2 = new DigitalOutput(Constants.LEDStop);

        try {
            ultra0 = new Ultrasonic(Constants.TRIG1, Constants.ECHO1);
            ultra0.setAutomaticMode(true);
            // sonarOk = true;
            // System.out.println("Ultrassonico iniciado com sucesso.");
        } catch (final Exception e) {
            // sonarOk = false;
            // System.out.println("Erro ao iniciar ultrassonico:");
            e.printStackTrace();
        }
        ultra1 = new AnalogInput(Constants.ULTRA1);
        ultra2 = new AnalogInput(Constants.ULTRA2);
        ultra0.setEnabled(true);

    }

    @Override
    public void robotPeriodic() {
        Scheduler.getInstance().run();

        final boolean curStart = btnStart.get();
        final boolean curStop = btnStop.get();

        if (lastStart && !curStart) {
            ds.enable();
            led1.set(true);
            led2.set(false);
        }
        if (lastStop && !curStop) {
            ds.disable();
            led1.set(false);
            led2.set(true);
        }

        lastStart = curStart;
        lastStop = curStop;

        SmartDashboard.putNumber("Encoder/dist0_m", enc0.getDistance());
        SmartDashboard.putNumber("Encoder/dist2_m", enc2.getDistance());
        SmartDashboard.putNumber("Encoder/dist3_m", enc3.getDistance());
        gyro.setDouble(navx.getYaw());
        SmartDashboard.putNumber("Gyro/Angulo_deg", navx.getAngle());
        SmartDashboard.putNumber("Sequencia/Etapa", etapa);

        // ========================================================================
        // LIDAR DEBUG
        // ========================================================================
        ultraF.setDouble(getDistance());
        ultraL.setDouble(getDistance1());
        ultraR.setDouble(getDistance2());
        SmartDashboard.putNumber("Distancia/Ultra1_mm_FRENTE", getDistance());
        SmartDashboard.putNumber("Distancia/Ultra2_mm_ESQUERDA", getDistance1());
        SmartDashboard.putNumber("Distancia/Ultra3_mm_DIREITA", getDistance2());
    }

    // =========================================================
    @Override
    public void autonomousInit() {
        while (navx.isCalibrating()) {
            try {
                Thread.sleep(50);
            } catch (final InterruptedException e) {
            }
        }
        navx.zeroYaw();

        enc0.reset();
        enc2.reset();
        enc3.reset();

        resetarTudo();
    }

    @Override
    public void autonomousPeriodic() {
        switch (etapa) {
        case 0:
            moverDistancia("F", 1.0);
            break;
        case 1:
            moverDistancia("SL", 1.5);
            break;
        case 2:
            moverDistancia("B", 1.0);
            break;
        default:
            stopMotors();
            break;
        }

        SmartDashboard.putNumber("Sequencia/Etapa", etapa);
    }

    // =========================================================
    // BLOCO 1 - moverDistancia (com protecao de parede)
    // =========================================================
    private void moverDistancia(final String dir, final double metros) {

        if (!moveIniciado) {
            enc0.reset();
            enc2.reset();
            enc3.reset();
            moveIniciado = true;
            recuoAtivo = false;
            return;
        }

        // ---- Sub-bloco de recuo ----
        if (recuoAtivo) {
            // Distancia recuada desde o inicio do recuo (absoluta)
            double recuoFeito;
            switch (dir) {
            case "F":
            case "B":
                recuoFeito = (Math.abs(enc2.getDistance() - encRefRecuo2) + Math.abs(enc3.getDistance() - encRefRecuo3))
                        / 2.0;
                break;
            default: // SR, SL
                recuoFeito = (Math.abs(enc0.getDistance() - encRefRecuo0) + Math.abs(enc2.getDistance() - encRefRecuo2)
                        + Math.abs(enc3.getDistance() - encRefRecuo3)) / 3.0;
                break;
            }

            SmartDashboard.putNumber("Move/recuoFeito_m", recuoFeito);

            if (recuoFeito >= DIST_RECUO_M) {
                stopMotors();
                recuoAtivo = false;
                avancarEtapa();
                return;
            }

            // Aplica recuo (oposto ao movimento)
            switch (dir) {
            case "F":
                motor0.set(0.0);
                motor2.set(VEL_RECUO);
                motor3.set(-VEL_RECUO);
                break;
            case "B":
                motor0.set(0.0);
                motor2.set(-VEL_RECUO);
                motor3.set(VEL_RECUO);
                break;
            case "SL":
                motor0.set(-VEL_RECUO);
                motor2.set(VEL_RECUO * 0.45);
                motor3.set(VEL_RECUO * 0.52);
                break;
            case "SR":
                motor0.set(VEL_RECUO);
                motor2.set(-VEL_RECUO * 0.45);
                motor3.set(-VEL_RECUO * 0.52);
                break;
            default:
                stopMotors();
            }
            return;
        }

        // ---- Distancia percorrida (movimento normal) ----
        double distPercorrida;
        switch (dir) {
        case "F":
        case "B":
            distPercorrida = (Math.abs(enc2.getDistance()) + Math.abs(enc3.getDistance())) / 2.0;
            break;
        default:
            distPercorrida = (Math.abs(enc0.getDistance()) + Math.abs(enc2.getDistance())
                    + Math.abs(enc3.getDistance())) / 3.0;
            break;
        }

        SmartDashboard.putNumber("Move/distPercorrida_m", distPercorrida);
        SmartDashboard.putBoolean("Move/recuoAtivo", recuoAtivo);

        // Meta por encoder atingida normalmente -> avanca sem recuo
        if (distPercorrida >= metros) {
            stopMotors();
            avancarEtapa();
            return;
        }

        // ---- Verifica parede ----
        boolean paredeDetectada = false;
        switch (dir) {
        case "F":
            paredeDetectada = ultra0.isRangeValid() && ultra0.getRangeMM() < DIST_PAREDE_MM;
            SmartDashboard.putNumber("Move/ultra_frente_mm", ultra0.getRangeMM());
            break;
        case "SL":
            final double distSL = getDistance1();
            paredeDetectada = distSL < DIST_PAREDE_MM2;
            SmartDashboard.putNumber("Move/ultra_esq_mm", distSL);
            break;
        case "SR":
            final double distSR = getDistance2();
            paredeDetectada = distSR < DIST_PAREDE_MM2;
            SmartDashboard.putNumber("Move/ultra_dir_mm", distSR);
            break;
        default:
            paredeDetectada = false;
        }

        if (paredeDetectada) {
            // Captura referencia exata dos encoders no momento da deteccao
            encRefRecuo0 = enc0.getDistance();
            encRefRecuo2 = enc2.getDistance();
            encRefRecuo3 = enc3.getDistance();
            recuoAtivo = true;
            stopMotors();
            return;
        }

        // ---- Movimento normal ----
        switch (dir) {
        case "F":
            motor0.set(0.0);
            motor2.set(-VEL_MOVE);
            motor3.set(VEL_MOVE);
            break;
        case "B":
            motor0.set(0.0);
            motor2.set(VEL_MOVE);
            motor3.set(-VEL_MOVE);
            break;
        case "SR":
            motor0.set(-VEL_MOVE);
            motor2.set(VEL_MOVE * 0.45);
            motor3.set(VEL_MOVE * 0.52);
            break;
        case "SL":
            motor0.set(VEL_MOVE);
            motor2.set(-VEL_MOVE * 0.45);
            motor3.set(-VEL_MOVE * 0.52);
            break;
        default:
            stopMotors();
        }
    }

    // =========================================================
    // BLOCO executarGiro - versao por encoder
    // =========================================================
    private void executarGiro(final double grausAlvo) {

        // grausAlvo positivo = horario (M0+, M2+, M3+)
        // grausAlvo negativo = anti-horario

        if (!giroIniciado) {
            enc0.reset();
            enc2.reset();
            enc3.reset();
            giroIniciado = true;
            ciclosEstaveis = 0;
            return;
        }

        // Distancia alvo em metros
        final double distAlvo = Math.abs(grausAlvo) * DIST_POR_GRAU;

        // Media absoluta dos 3 encoders
        final double distPercorrida = (Math.abs(enc0.getDistance()) + Math.abs(enc2.getDistance())
                + Math.abs(enc3.getDistance())) / 3.0;

        // Graus equivalentes ja girados
        final double grausPercorridos = distPercorrida / DIST_POR_GRAU;
        final double erro = Math.abs(grausAlvo) - grausPercorridos;

        SmartDashboard.putNumber("Giro/distAlvo_m", distAlvo);
        SmartDashboard.putNumber("Giro/distPercorrida_m", distPercorrida);
        SmartDashboard.putNumber("Giro/grausPercorridos", grausPercorridos);
        SmartDashboard.putNumber("Giro/erro_graus", erro);
        SmartDashboard.putNumber("Giro/ciclosEstaveis", ciclosEstaveis);

        // Meta atingida -> confirma parada
        if (erro < TOLERANCE_DEG) {
            ciclosEstaveis++;
            stopMotors();
            if (ciclosEstaveis >= CICLOS_ESTAVEIS) {
                avancarEtapa();
            }
            return;
        }

        ciclosEstaveis = 0;

        // Direcao do giro pelo sinal de grausAlvo
        final double sinal = Math.signum(grausAlvo);

        motor0.set(sinal * VEL_GIRO);
        motor2.set(sinal * VEL_GIRO);
        motor3.set(sinal * VEL_GIRO);
    }

    // =========================================================
    // Avanca etapa - reseta APENAS as flags dos blocos
    // =========================================================
    private void avancarEtapa() {
        etapa++;
        moveIniciado = false;
        moveConcluido = false;
        giroIniciado = false;
        giroConcluido = false;
        ciclosEstaveis = 0;
        recuoAtivo = false;
    }

    // =========================================================
    // Reset completo
    // =========================================================
    private void resetarTudo() {
        etapa = 0;
        etapaIniciada = false;
        moveIniciado = false;
        moveConcluido = false;
        giroIniciado = false;
        giroConcluido = false;
        ciclosEstaveis = 0;
        recuoAtivo = false;
    }

    // =========================================================
    // Utilitarios
    // =========================================================
    private double applyDeadband(final double v, final double db) {
        if (Math.abs(v) < db)
            return 0.0;
        final double sign = Math.signum(v);
        return sign * clamp((Math.abs(v) - db) / (1.0 - db), 0.0, 1.0);
    }

    private double clamp(final double v, final double min, final double max) {
        return Math.max(min, Math.min(max, v));
    }

    private double normalizeAngle180(double deg) {
        while (deg >  180.0) deg -= 360.0;
        while (deg < -180.0) deg += 360.0;
        return deg;
    }

    private void stopMotors() {
        motor0.set(0.0);
        motor2.set(0.0);
        motor3.set(0.0);
    }
    // =========================================================
    //  ULTRA DIGITAL && ANALOG
    // =========================================================
    private double getDistance(){
        return ultra0.getRangeMM();
    }

    private double getDistance1(){
        return (Math.pow(ultra1.getAverageVoltage(), -1.2045)) * 27.726;
    }

    private double getDistance2(){
        
        return (Math.pow(ultra2.getAverageVoltage(), -1.2045)) * 27.726;
    }
    // =========================================================
    @Override
    public void disabledInit() {
        stopMotors();
        resetarTudo();
        led1.set(false); led2.set(true);
    }

    @Override
    public void disabledPeriodic() { stopMotors(); }

    
}
