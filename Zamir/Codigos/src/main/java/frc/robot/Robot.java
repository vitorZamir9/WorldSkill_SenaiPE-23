package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import com.studica.frc.Titan;
import com.studica.frc.MockDS;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * ============================================================
 *  CÓDIGO DE TESTE — Chassi Omni 3 Rodas
 * ============================================================
 *
 *  BLOCOS DISPONÍVEIS:
 *
 *  moverDistancia(String dir, double metros)
 *    dir: "F"  → frente
 *         "B"  → ré
 *         "SR" → lateral direita
 *         "SL" → lateral esquerda
 *    metros: distância alvo em metros (usa encoders)
 *
 *  executarGiro(double grausAlvo)
 *    grausAlvo: positivo = horário, negativo = anti-horário
 *    (usa gyro VMX via PID)
 *
 *  SEQUÊNCIA ATUAL:
 *  ┌──────┬──────────────────────────────────────────────────┐
 *  │  0   │ moverDistancia("F",  0.5)  → frente 0.5 m       │
 *  │  1   │ moverDistancia("SR", 0.4)  → direita 0.4 m      │
 *  │  2   │ moverDistancia("SL", 0.4)  → esquerda 0.4 m     │
 *  │  3   │ moverDistancia("B",  0.5)  → ré 0.5 m           │
 *  │  fim │ PARAR                                            │
 *  └──────┴──────────────────────────────────────────────────┘
 */
public class Robot extends TimedRobot {

    // =========================================================
    //  Configurações — translação
    // =========================================================
    private static final double VEL_MOVE      = 0.5;

    // Configurações — giro
    private static final double KP_ROT           = 0.1;
    private static final double DEADBAND_ROT     = 0.15;
    private static final double TOLERANCE_DEG    = 3.0;  // tolerância angular
    private static final int    CICLOS_ESTAVEIS  = 10;   // ciclos dentro da tolerância para confirmar parada

    // =========================================================
    //  Hardware
    // =========================================================
    private AHRS gyro;

    private Titan       titan;
    private Titan.Motor motor0;
    private Titan.Motor motor2;
    private Titan.Motor motor3;

    private Titan.Encoder enc0;
    private Titan.Encoder enc2;
    private Titan.Encoder enc3;

    private MockDS ds;

    private DigitalInput  btnStart;
    private DigitalInput  btnStop;
    private DigitalOutput led1, led2, led3, led4;

    private boolean lastStart = false;
    private boolean lastStop  = false;

    // =========================================================
    //  Estado da sequência
    // =========================================================
    private int     etapa         = 0;
    private boolean etapaIniciada = false;

    // Estado — bloco moverDistancia
    private boolean moveIniciado  = false;
    private boolean moveConcluido = false;

    // Estado — bloco executarGiro
    private boolean giroIniciado  = false;
    private boolean giroConcluido = false;
    private int     ciclosEstaveis = 0;

    // =========================================================
    @Override
    public void robotInit() {
        gyro = new AHRS(SPI.Port.kMXP);

        titan  = new Titan(Constants.TITAN_ID);
        motor0 = titan.getMotor(Constants.MOTOR_0);
        motor2 = titan.getMotor(Constants.MOTOR_2);
        motor3 = titan.getMotor(Constants.MOTOR_3);

        enc0 = titan.getEncoder(Constants.ENCODER_0, Constants.DIST_PER_TICK);
        enc2 = titan.getEncoder(Constants.ENCODER_2, Constants.DIST_PER_TICK);
        enc3 = titan.getEncoder(Constants.ENCODER_3, Constants.DIST_PER_TICK);

        ds       = new MockDS();
        btnStart = new DigitalInput(Constants.BTN_START);
        btnStop  = new DigitalInput(Constants.BTN_STOP);
        led1     = new DigitalOutput(Constants.LEDRun);
        led2     = new DigitalOutput(Constants.LEDStop);
        led3     = new DigitalOutput(Constants.LEDRunBTN);
        led4     = new DigitalOutput(Constants.LEDStopBTN);
    }

    @Override
    public void robotPeriodic() {
        Scheduler.getInstance().run();

        boolean curStart = btnStart.get();
        boolean curStop  = btnStop.get();

        if (lastStart && !curStart) {
            ds.enable();
            led1.set(true);  led2.set(false);
            led3.set(true);  led4.set(false);
        }
        if (lastStop && !curStop) {
            ds.disable();
            led1.set(false); led2.set(true);
            led3.set(false); led4.set(true);
        }

        lastStart = curStart;
        lastStop  = curStop;

        SmartDashboard.putNumber("Encoder/dist0_m",  enc0.getDistance());
        SmartDashboard.putNumber("Encoder/dist2_m",  enc2.getDistance());
        SmartDashboard.putNumber("Encoder/dist3_m",  enc3.getDistance());
        SmartDashboard.putNumber("Gyro/Angulo_deg",  gyro.getAngle());
        SmartDashboard.putNumber("Sequencia/Etapa",  etapa);
    }

    // =========================================================
    @Override
    public void autonomousInit() {
        while (gyro.isCalibrating()) {
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }
        gyro.zeroYaw();

        enc0.reset();
        enc2.reset();
        enc3.reset();

        resetarTudo();
    }

    @Override
    public void autonomousPeriodic() {
        switch (etapa) {
            case 0: moverDistancia("F",   0.5); break;
            case 1: moverDistancia("SR",  0.4); break;
            case 2: moverDistancia("SL",  0.4); break;
            case 3: moverDistancia("B",   0.5); break;
            default: stopMotors();              break;
        }

        SmartDashboard.putNumber("Sequencia/Etapa", etapa);
    }

    // =========================================================
    //  BLOCO 1 — moverDistancia
    // =========================================================
    private void moverDistancia(String dir, double metros) {

        // Inicializa a etapa: zera encoders uma única vez
        if (!moveIniciado) {
            enc0.reset();
            enc2.reset();
            enc3.reset();
            moveIniciado = true;
            return; // espera o próximo ciclo para começar a ler
        }

        // Distância percorrida: média absoluta dos encoders relevantes
        double distPercorrida;
        switch (dir) {
            case "F":
            case "B":
                distPercorrida = (Math.abs(enc2.getDistance())
                               +  Math.abs(enc3.getDistance())) / 2.0;
                break;
            case "SR":
            case "SL":
                distPercorrida = (Math.abs(enc0.getDistance())
                               +  Math.abs(enc2.getDistance())
                               +  Math.abs(enc3.getDistance())) / 3.0;
                break;
            default:
                distPercorrida = 0.0;
        }

        SmartDashboard.putNumber("Move/distPercorrida_m", distPercorrida);

        // Meta atingida → para e avança
        if (distPercorrida >= metros) {
            stopMotors();
            avancarEtapa();
            return;
        }

        // Aplica os vetores omni
        switch (dir) {
            case "F":
                motor0.set( 0.0);
                motor2.set( VEL_MOVE);
                motor3.set(-VEL_MOVE);
                break;
            case "B":
                motor0.set( 0.0);
                motor2.set(-VEL_MOVE);
                motor3.set( VEL_MOVE);
                break;
            case "SR":
                motor0.set( VEL_MOVE);
                motor2.set(-VEL_MOVE * 0.3);
                motor3.set(-VEL_MOVE * 0.5);
                break;
            case "SL":  // espelho exato do SR
                motor0.set(-VEL_MOVE);
                motor2.set( VEL_MOVE * 0.3);
                motor3.set( VEL_MOVE * 0.5);
                break;
            default:
                stopMotors();
        }
    }

    // =========================================================
    //  BLOCO 2 — executarGiro
    // =========================================================
    private void executarGiro(double grausAlvo) {

        if (!giroIniciado) {
            gyro.zeroYaw();
            ciclosEstaveis = 0;
            giroIniciado = true;
            return; // espera o próximo ciclo para começar a ler
        }

        double anguloAtual = gyro.getAngle();
        double erro        = normalizeAngle180(grausAlvo - anguloAtual);

        SmartDashboard.putNumber("Giro/erroGraus",     erro);
        SmartDashboard.putNumber("Giro/ciclosEstaveis", ciclosEstaveis);

        if (Math.abs(erro) < TOLERANCE_DEG) {
            ciclosEstaveis++;
            stopMotors();
            if (ciclosEstaveis >= CICLOS_ESTAVEIS) {
                avancarEtapa();
            }
            return;
        }

        // Saiu da tolerância — reseta o contador
        ciclosEstaveis = 0;

        double omega = KP_ROT * erro;
        omega = clamp(omega, -VEL_MOVE, VEL_MOVE);
        omega = applyDeadband(omega, DEADBAND_ROT);

        motor0.set( omega);
        motor2.set( omega);
        motor3.set( omega);
    }

    // =========================================================
    //  Avança etapa — reseta APENAS as flags dos blocos
    // =========================================================
    private void avancarEtapa() {
        etapa++;
        moveIniciado   = false;
        moveConcluido  = false;
        giroIniciado   = false;
        giroConcluido  = false;
        ciclosEstaveis = 0;
    }

    // =========================================================
    //  Reset completo
    // =========================================================
    private void resetarTudo() {
        etapa          = 0;
        etapaIniciada  = false;
        moveIniciado   = false;
        moveConcluido  = false;
        giroIniciado   = false;
        giroConcluido  = false;
        ciclosEstaveis = 0;
    }

    // =========================================================
    //  Utilitários
    // =========================================================
    private double applyDeadband(double v, double db) {
        if (Math.abs(v) < db) return 0.0;
        double sign = Math.signum(v);
        return sign * clamp((Math.abs(v) - db) / (1.0 - db), 0.0, 1.0);
    }

    private double clamp(double v, double min, double max) {
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
    @Override
    public void disabledInit() {
        stopMotors();
        resetarTudo();
        led1.set(false); led2.set(true);
        led3.set(false); led4.set(true);
    }

    @Override
    public void disabledPeriodic() { stopMotors(); }
}