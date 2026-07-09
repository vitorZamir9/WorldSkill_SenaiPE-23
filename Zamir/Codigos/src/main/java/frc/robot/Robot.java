package frc.robot;

import java.util.Map;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import com.studica.frc.Titan;
import com.studica.frc.MockDS;

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
 *  CHASSI OMNI 3 RODAS - Navegação por Plano Cartesiano
 * ============================================================
 *
 *  A sequência de missão é definida em FieldMap.java:
 *    - OBSTACULOS[][] : retângulos proibidos (x_min, y_min, x_max, y_max) em cm
 *    - WAYPOINTS[][]  : lista de destinos (x, y, angulo_final) em cm/graus
 *    - INICIO_X/Y/ANGULO : posição e orientação iniciais
 *
 *  O robô:
 *    1. Calcula vetor do ponto atual ao próximo waypoint
 *    2. Gira para alinhar com essa direção (executarGiro)
 *    3. Avança a distância linear calculada (moverDistancia "F")
 *    4. Se houver ângulo final no waypoint, gira para ele
 *    5. Repete para o próximo waypoint
 *
 *  Proteção de parede via ultrassônicos mantida intacta.
 * ============================================================
 */
public class Robot extends TimedRobot {

    // =========================================================
    //  Configurações - translação e giro
    // =========================================================
    private static final double VEL_MOVE        = 0.7;
    private static final double VEL_RECUO       = 0.5;
    private static final double DIST_POR_GRAU   = 0.003840; // m/grau (raio=0.22m)
    private static final double VEL_GIRO        = 0.35;
    private static final double TOLERANCE_DEG   = 3.0;
    private static final int    CICLOS_ESTAVEIS = 10;

    // Sensores de parede
    private static final double DIST_PAREDE_MM  = 200.0;
    private static final double DIST_PAREDE_MM2 = 10.0;
    private static final double DIST_RECUO_M    = 0.15;

    // =========================================================
    //  Hardware
    // =========================================================
    private AHRS navx;
    private Titan titan;
    private Titan.Motor  motor0, motor2, motor3;
    private Titan.Encoder enc0,  enc2,   enc3;
    private MockDS ds;
    private DigitalInput  btnStart, btnStop;
    private DigitalOutput led1, led2;
    private Ultrasonic    ultra0;
    private AnalogInput   ultra1, ultra2;
    private boolean lastStart = false, lastStop = false;

    // =========================================================
    //  Pose estimada do robô (odometria simples)
    //  Atualizada a cada ciclo com base nos encoders
    // =========================================================
    private double poseX_cm;   // posição atual X em cm
    private double poseY_cm;   // posição atual Y em cm
    private double poseAng_deg; // ângulo atual em graus (0 = +X, 90 = +Y)

    // Acumuladores de encoder para odometria
    private double lastEnc2 = 0.0;
    private double lastEnc3 = 0.0;

    // =========================================================
    //  Máquina de estados da missão
    // =========================================================
    // Fases dentro de cada waypoint:
    //   FASE 0: calcular giro de alinhamento -> inicia executarGiro
    //   FASE 1: giro de alinhamento em execução
    //   FASE 2: avançar distância -> inicia moverDistancia "F"
    //   FASE 3: movimento em execução
    //   FASE 4: giro final (se definido no waypoint) -> inicia executarGiro
    //   FASE 5: giro final em execução
    private int wpIndex   = 0;  // índice do waypoint atual em FieldMap.WAYPOINTS
    private int wpFase    = 0;  // fase dentro do waypoint atual

    private double giroAlvo    = 0.0;  // ângulo calculado para alinhar com próximo wp
    private double distAlvo_m  = 0.0;  // distância calculada até o próximo wp em metros

    // =========================================================
    //  Estado dos blocos primitivos (mantidos do código original)
    // =========================================================
    private boolean moveIniciado   = false;
    private boolean giroIniciado   = false;
    private int     ciclosEstaveis = 0;
    private boolean recuoAtivo     = false;
    private double  encRefRecuo0, encRefRecuo2, encRefRecuo3;

    // =========================================================
    //  Shuffleboard
    // =========================================================
    private final ShuffleboardTab   tab    = Shuffleboard.getTab("navegacao");
    private final NetworkTableEntry ntUltraR = tab.add("ultraR", 0).getEntry();
    private final NetworkTableEntry ntUltraL = tab.add("ultraL", 0).getEntry();
    private final NetworkTableEntry ntUltraF = tab.add("ultraF", 0).getEntry();
    private final NetworkTableEntry ntGyro   = tab.add("navx",   0).getEntry();
    private final NetworkTableEntry ntPoseX  = tab.add("poseX",  0).getEntry();
    private final NetworkTableEntry ntPoseY  = tab.add("poseY",  0).getEntry();
    private final NetworkTableEntry ntWpIdx  = tab.add("waypoint",0).getEntry();
    private final NetworkTableEntry ntWpFase = tab.add("fase",   0).getEntry();

    // =========================================================
    @Override
    public void robotInit() {
        navx = new AHRS(SPI.Port.kMXP);

        titan  = new Titan(Constants.TITAN_ID);
        motor0 = titan.getMotor(Constants.MOTOR_0);
        motor2 = titan.getMotor(Constants.MOTOR_2);
        motor3 = titan.getMotor(Constants.MOTOR_3);
        enc0   = titan.getEncoder(Constants.ENCODER_0, Constants.DIST_PER_TICK);
        enc2   = titan.getEncoder(Constants.ENCODER_2, Constants.DIST_PER_TICK);
        enc3   = titan.getEncoder(Constants.ENCODER_3, Constants.DIST_PER_TICK);

        ds       = new MockDS();
        btnStart = new DigitalInput(Constants.BTN_START);
        btnStop  = new DigitalInput(Constants.BTN_STOP);
        led1     = new DigitalOutput(Constants.LEDRun);
        led2     = new DigitalOutput(Constants.LEDStop);

        try {
            ultra0 = new Ultrasonic(Constants.TRIG1, Constants.ECHO1);
            ultra0.setAutomaticMode(true);
        } catch (final Exception e) {
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
        final boolean curStop  = btnStop.get();
        if (lastStart && !curStart) { ds.enable();  led1.set(true);  led2.set(false); }
        if (lastStop  && !curStop)  { ds.disable(); led1.set(false); led2.set(true);  }
        lastStart = curStart;
        lastStop  = curStop;

        SmartDashboard.putNumber("Encoder/dist0_m", enc0.getDistance());
        SmartDashboard.putNumber("Encoder/dist2_m", enc2.getDistance());
        SmartDashboard.putNumber("Encoder/dist3_m", enc3.getDistance());
        ntGyro.setDouble(navx.getYaw());
        ntUltraF.setDouble(getDistanceF());
        ntUltraL.setDouble(getDistanceL());
        ntUltraR.setDouble(getDistanceR());
        ntPoseX.setDouble(poseX_cm);
        ntPoseY.setDouble(poseY_cm);
        ntWpIdx.setDouble(wpIndex);
        ntWpFase.setDouble(wpFase);

        SmartDashboard.putNumber("Pose/X_cm",  poseX_cm);
        SmartDashboard.putNumber("Pose/Y_cm",  poseY_cm);
        SmartDashboard.putNumber("Pose/Ang",   poseAng_deg);
        SmartDashboard.putNumber("Nav/WP",     wpIndex);
        SmartDashboard.putNumber("Nav/Fase",   wpFase);
    }

    // =========================================================
    @Override
    public void autonomousInit() {
        while (navx.isCalibrating()) {
            try { Thread.sleep(50); } catch (final InterruptedException e) {}
        }
        navx.zeroYaw();
        enc0.reset(); enc2.reset(); enc3.reset();

        // Inicializa pose com posição e ângulo definidos no FieldMap
        poseX_cm   = FieldMap.INICIO_X_CM;
        poseY_cm   = FieldMap.INICIO_Y_CM;
        poseAng_deg = FieldMap.INICIO_ANGULO;
        lastEnc2 = 0.0;
        lastEnc3 = 0.0;

        resetarTudo();
    }

    // =========================================================
    //  LOOP PRINCIPAL DA MISSÃO
    // =========================================================
    @Override
    public void autonomousPeriodic() {
        atualizarOdometria();

        // Missão concluída
        if (wpIndex >= FieldMap.WAYPOINTS.length) {
            stopMotors();
            SmartDashboard.putString("Nav/Status", "MISSAO_COMPLETA");
            return;
        }

        final double[] wp = FieldMap.WAYPOINTS[wpIndex];
        final double wpX       = wp[0];
        final double wpY       = wp[1];
        final double wpAngFinal = wp[2]; // -999 = não girar no final

        switch (wpFase) {

            // --------------------------------------------------
            //  FASE 0: calcular giro necessário para alinhar com o waypoint
            // --------------------------------------------------
            case 0: {
                final double dx = wpX - poseX_cm;
                final double dy = wpY - poseY_cm;
                distAlvo_m = Math.sqrt(dx * dx + dy * dy) / 100.0; // cm -> m

                // Ângulo do vetor destino em coordenadas do campo (graus, 0=+X, 90=+Y)
                final double angDestino = Math.toDegrees(Math.atan2(dy, dx));

                // Quanto girar a partir da orientação atual
                giroAlvo = normalizeAngle180(angDestino - poseAng_deg);

                SmartDashboard.putNumber("Nav/distAlvo_m",  distAlvo_m);
                SmartDashboard.putNumber("Nav/angDestino",  angDestino);
                SmartDashboard.putNumber("Nav/giroAlvo",    giroAlvo);
                SmartDashboard.putString("Nav/Status",
                    "WP" + wpIndex + " ALINHANDO dx=" + String.format("%.1f", dx) + " dy=" + String.format("%.1f", dy));

                // Se a distância for desprezível, pula direto pro ângulo final
                if (distAlvo_m < (FieldMap.TOLERANCIA_CHEGADA_CM / 100.0)) {
                    wpFase = (wpAngFinal > -900) ? 4 : 6;
                    return;
                }

                // Se giro necessário for menor que tolerância, pula direto pro movimento
                if (Math.abs(giroAlvo) < TOLERANCE_DEG) {
                    wpFase = 2;
                } else {
                    resetBlocos();
                    wpFase = 1;
                }
                break;
            }

            // --------------------------------------------------
            //  FASE 1: executar giro de alinhamento
            // --------------------------------------------------
            case 1: {
                final boolean concluido = executarGiro(giroAlvo);
                if (concluido) {
                    poseAng_deg = normalizeAngle180(poseAng_deg + giroAlvo);
                    resetBlocos();
                    wpFase = 2;
                }
                break;
            }

            // --------------------------------------------------
            //  FASE 2: iniciar movimento para frente até o waypoint
            // --------------------------------------------------
            case 2: {
                resetBlocos();
                wpFase = 3;
                break;
            }

            // --------------------------------------------------
            //  FASE 3: executar moverDistancia até o waypoint
            // --------------------------------------------------
            case 3: {
                final boolean concluido = moverDistancia("F", distAlvo_m);
                if (concluido) {
                    // Atualiza pose para a posição do waypoint
                    poseX_cm   = wpX;
                    poseY_cm   = wpY;
                    resetBlocos();
                    wpFase = (wpAngFinal > -900) ? 4 : 6;
                }
                break;
            }

            // --------------------------------------------------
            //  FASE 4: calcular giro final do waypoint
            // --------------------------------------------------
            case 4: {
                giroAlvo = normalizeAngle180(wpAngFinal - poseAng_deg);
                SmartDashboard.putNumber("Nav/giroFinal", giroAlvo);
                if (Math.abs(giroAlvo) < TOLERANCE_DEG) {
                    wpFase = 6;
                } else {
                    resetBlocos();
                    wpFase = 5;
                }
                break;
            }

            // --------------------------------------------------
            //  FASE 5: executar giro final
            // --------------------------------------------------
            case 5: {
                final boolean concluido = executarGiro(giroAlvo);
                if (concluido) {
                    poseAng_deg = wpAngFinal;
                    wpFase = 6;
                }
                break;
            }

            // --------------------------------------------------
            //  FASE 6: waypoint concluído -> avança para o próximo
            // --------------------------------------------------
            case 6: {
                stopMotors();
                SmartDashboard.putString("Nav/Status", "WP" + wpIndex + " CONCLUIDO");
                wpIndex++;
                wpFase = 0;
                resetBlocos();
                break;
            }

            default:
                stopMotors();
                break;
        }
    }

    // =========================================================
    //  ODOMETRIA SIMPLES
    //  Atualiza poseX, poseY com base no deslocamento dos encoders
    //  Usa M2 e M3 (rodas F/B) como referência de translação linear
    // =========================================================
    private void atualizarOdometria() {
        final double cur2 = enc2.getDistance(); // metros
        final double cur3 = enc3.getDistance();

        final double delta2 = cur2 - lastEnc2;
        final double delta3 = cur3 - lastEnc3;

        // Distância linear percorrida (média das duas rodas de translação)
        final double deltaD_m = (Math.abs(delta2) + Math.abs(delta3)) / 2.0;
        // Sinal: M2 negativo = frente, M3 positivo = frente (conforme código original)
        final double sinal = ((-delta2) + delta3) / 2.0 >= 0 ? 1.0 : -1.0;
        final double deltaD_cm = sinal * deltaD_m * 100.0;

        final double angRad = Math.toRadians(poseAng_deg);
        poseX_cm += deltaD_cm * Math.cos(angRad);
        poseY_cm += deltaD_cm * Math.sin(angRad);

        lastEnc2 = cur2;
        lastEnc3 = cur3;
    }

    // =========================================================
    //  BLOCO moverDistancia - agora retorna boolean (concluido)
    //  Mantém toda a lógica original de recuo + proteção de parede
    // =========================================================
    private boolean moverDistancia(final String dir, final double metros) {

        if (!moveIniciado) {
            enc0.reset(); enc2.reset(); enc3.reset();
            moveIniciado = true;
            recuoAtivo   = false;
            lastEnc2     = 0.0;
            lastEnc3     = 0.0;
            return false;
        }

        // ---- Sub-bloco de recuo ----
        if (recuoAtivo) {
            double recuoFeito;
            switch (dir) {
                case "F": case "B":
                    recuoFeito = (Math.abs(enc2.getDistance() - encRefRecuo2) +
                                  Math.abs(enc3.getDistance() - encRefRecuo3)) / 2.0;
                    break;
                default:
                    recuoFeito = (Math.abs(enc0.getDistance() - encRefRecuo0) +
                                  Math.abs(enc2.getDistance() - encRefRecuo2) +
                                  Math.abs(enc3.getDistance() - encRefRecuo3)) / 3.0;
                    break;
            }
            SmartDashboard.putNumber("Move/recuoFeito_m", recuoFeito);

            if (recuoFeito >= DIST_RECUO_M) {
                stopMotors();
                recuoAtivo = false;
                return true; // parede -> considera etapa concluída (navegação vai decidir)
            }

            aplicarRecuo(dir);
            return false;
        }

        // ---- Distância percorrida ----
        double distPercorrida;
        switch (dir) {
            case "F": case "B":
                distPercorrida = (Math.abs(enc2.getDistance()) + Math.abs(enc3.getDistance())) / 2.0;
                break;
            default:
                distPercorrida = (Math.abs(enc0.getDistance()) + Math.abs(enc2.getDistance()) +
                                  Math.abs(enc3.getDistance())) / 3.0;
                break;
        }
        SmartDashboard.putNumber("Move/distPercorrida_m", distPercorrida);

        if (distPercorrida >= metros) {
            stopMotors();
            return true;
        }

        // ---- Verifica parede ----
        boolean parede = false;
        switch (dir) {
            case "F":
                parede = ultra0.isRangeValid() && ultra0.getRangeMM() < DIST_PAREDE_MM;
                break;
            case "SL":
                parede = getDistanceL() < DIST_PAREDE_MM2;
                break;
            case "SR":
                parede = getDistanceR() < DIST_PAREDE_MM2;
                break;
            default:
                parede = false;
        }

        if (parede) {
            encRefRecuo0 = enc0.getDistance();
            encRefRecuo2 = enc2.getDistance();
            encRefRecuo3 = enc3.getDistance();
            recuoAtivo   = true;
            stopMotors();
            return false;
        }

        // ---- Movimento normal ----
        aplicarMovimento(dir);
        return false;
    }

    private void aplicarMovimento(final String dir) {
        switch (dir) {
            case "F":
                motor0.set(0.0); motor2.set(-VEL_MOVE); motor3.set(VEL_MOVE);
                break;
            case "B":
                motor0.set(0.0); motor2.set(VEL_MOVE);  motor3.set(-VEL_MOVE);
                break;
            case "SR":
                motor0.set(-VEL_MOVE); motor2.set(VEL_MOVE * 0.45);  motor3.set(VEL_MOVE * 0.52);
                break;
            case "SL":
                motor0.set(VEL_MOVE);  motor2.set(-VEL_MOVE * 0.45); motor3.set(-VEL_MOVE * 0.52);
                break;
            default:
                stopMotors();
        }
    }

    private void aplicarRecuo(final String dir) {
        switch (dir) {
            case "F":
                motor0.set(0.0); motor2.set(VEL_RECUO); motor3.set(-VEL_RECUO);
                break;
            case "B":
                motor0.set(0.0); motor2.set(-VEL_RECUO); motor3.set(VEL_RECUO);
                break;
            case "SL":
                motor0.set(-VEL_RECUO); motor2.set(VEL_RECUO * 0.45);  motor3.set(VEL_RECUO * 0.52);
                break;
            case "SR":
                motor0.set(VEL_RECUO);  motor2.set(-VEL_RECUO * 0.45); motor3.set(-VEL_RECUO * 0.52);
                break;
            default:
                stopMotors();
        }
    }

    // =========================================================
    //  BLOCO executarGiro - agora retorna boolean (concluido)
    //  Mantém lógica original por encoder
    // =========================================================
    private boolean executarGiro(final double grausAlvo) {

        if (!giroIniciado) {
            enc0.reset(); enc2.reset(); enc3.reset();
            giroIniciado   = true;
            ciclosEstaveis = 0;
            return false;
        }

        final double distAlvo     = Math.abs(grausAlvo) * DIST_POR_GRAU;
        final double distPercorrida = (Math.abs(enc0.getDistance()) +
                                       Math.abs(enc2.getDistance()) +
                                       Math.abs(enc3.getDistance())) / 3.0;
        final double grausPercorridos = distPercorrida / DIST_POR_GRAU;
        final double erro = Math.abs(grausAlvo) - grausPercorridos;

        SmartDashboard.putNumber("Giro/erro_graus",      erro);
        SmartDashboard.putNumber("Giro/ciclosEstaveis",  ciclosEstaveis);

        if (erro < TOLERANCE_DEG) {
            ciclosEstaveis++;
            stopMotors();
            if (ciclosEstaveis >= CICLOS_ESTAVEIS) {
                return true;
            }
            return false;
        }

        ciclosEstaveis = 0;
        final double sinal = Math.signum(grausAlvo);
        motor0.set(sinal * VEL_GIRO);
        motor2.set(sinal * VEL_GIRO);
        motor3.set(sinal * VEL_GIRO);
        return false;
    }

    // =========================================================
    //  Helpers
    // =========================================================
    private void resetBlocos() {
        moveIniciado   = false;
        giroIniciado   = false;
        ciclosEstaveis = 0;
        recuoAtivo     = false;
    }

    private void resetarTudo() {
        wpIndex    = 0;
        wpFase     = 0;
        giroAlvo   = 0.0;
        distAlvo_m = 0.0;
        resetBlocos();
    }

    private double normalizeAngle180(double deg) {
        while (deg >  180.0) deg -= 360.0;
        while (deg < -180.0) deg += 360.0;
        return deg;
    }

    private void stopMotors() {
        motor0.set(0.0); motor2.set(0.0); motor3.set(0.0);
    }

    // =========================================================
    //  Sensores de distância
    // =========================================================
    private double getDistanceF() { return ultra0.getRangeMM(); }
    private double getDistanceL() { return Math.pow(ultra1.getAverageVoltage(), -1.2045) * 27.726; }
    private double getDistanceR() { return Math.pow(ultra2.getAverageVoltage(), -1.2045) * 27.726; }

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