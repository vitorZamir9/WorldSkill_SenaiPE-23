package frc.robot;

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
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Ultrasonic;

public class Robot extends TimedRobot {

    // =========================================================
    //  Configuracoes de movimento e giro
    // =========================================================
    private static final double VEL_MOVE        = 0.7;
    private static final double VEL_RECUO       = 0.5;
    private static final double DIST_POR_GRAU   = 0.003840;
    private static final double VEL_GIRO        = 0.35;
    private static final double TOLERANCE_DEG   = 3.0;
    private static final int    CICLOS_ESTAVEIS = 10;
    private static final double LARGURA_ROBO_CM = 40.0;

    // Protecao de parede (apenas sensores fisicos)
    private static final double DIST_PAREDE_MM  = 200.0;
    private static final double DIST_PAREDE_MM2 = 10.0;
    private static final double DIST_RECUO_M    = 0.15;

    // =========================================================
    //  Hardware
    // =========================================================
    private AHRS navx;
    private Titan titan;
    private Titan.Motor   motor0, motor2, motor3;
    private Titan.Encoder enc0,   enc2,   enc3;
    private MockDS ds;
    private DigitalInput  btnStart, btnStop;
    private DigitalOutput led1, led2;
    private Ultrasonic    ultra0;
    private AnalogInput   ultra1, ultra2;
    private boolean lastStart = false, lastStop = false;

    // =========================================================
    //  Pose estimada (odometria)
    // =========================================================
    private double poseX_cm;
    private double poseY_cm;
    private double poseAng_deg;
    private double lastEnc2 = 0.0;
    private double lastEnc3 = 0.0;

    // =========================================================
    //  Plano de sub-segmentos
    // =========================================================
    private static final int MAX_SEGS = 4;
    private double[] segX_cm  = new double[MAX_SEGS];
    private double[] segY_cm  = new double[MAX_SEGS];
    private int      segCount = 0;
    private int      segIdx   = 0;

    // =========================================================
    //  Maquina de estados
    // =========================================================
    private int    wpIndex    = 0;
    private int    wpFase     = 0;
    private double giroAlvo   = 0.0;
    private double distAlvo_m = 0.0;

    private boolean moveIniciado   = false;
    private boolean giroIniciado   = false;
    private int     ciclosEstaveis = 0;
    private boolean recuoAtivo     = false;
    private double  encRefRecuo0, encRefRecuo2, encRefRecuo3;

    // =========================================================
    //  Shuffleboard
    // =========================================================
    private final ShuffleboardTab   tab      = Shuffleboard.getTab("navegacao");
    private final NetworkTableEntry ntUltraR = tab.add("ultraR",   0).getEntry();
    private final NetworkTableEntry ntUltraL = tab.add("ultraL",   0).getEntry();
    private final NetworkTableEntry ntUltraF = tab.add("ultraF",   0).getEntry();
    private final NetworkTableEntry ntGyro   = tab.add("navx",     0).getEntry();
    private final NetworkTableEntry ntPoseX  = tab.add("poseX",    0).getEntry();
    private final NetworkTableEntry ntPoseY  = tab.add("poseY",    0).getEntry();
    private final NetworkTableEntry ntWpIdx  = tab.add("waypoint", 0).getEntry();
    private final NetworkTableEntry ntWpFase = tab.add("fase",     0).getEntry();
    private final NetworkTableEntry ntSegIdx = tab.add("segmento", 0).getEntry();

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
        ntSegIdx.setDouble(segIdx);

        SmartDashboard.putNumber("Pose/X_cm", poseX_cm);
        SmartDashboard.putNumber("Pose/Y_cm", poseY_cm);
        SmartDashboard.putNumber("Pose/Ang",  poseAng_deg);
        SmartDashboard.putNumber("Nav/WP",    wpIndex);
        SmartDashboard.putNumber("Nav/Fase",  wpFase);
        SmartDashboard.putNumber("Nav/Seg",   segIdx);
    }

    // =========================================================
    @Override
    public void autonomousInit() {
        while (navx.isCalibrating()) {
            try { Thread.sleep(50); } catch (final InterruptedException e) {}
        }
        navx.zeroYaw();
        enc0.reset(); enc2.reset(); enc3.reset();

        poseX_cm    = FieldMap.INICIO_X_CM;
        poseY_cm    = FieldMap.INICIO_Y_CM;
        poseAng_deg = FieldMap.INICIO_ANGULO;
        lastEnc2    = 0.0;
        lastEnc3    = 0.0;

        resetarTudo();
    }

    // =========================================================
    //  LOOP PRINCIPAL
    // =========================================================
    @Override
    public void autonomousPeriodic() {
        atualizarOdometria();

        if (wpIndex >= FieldMap.WAYPOINTS.length) {
            stopMotors();
            SmartDashboard.putString("Nav/Status", "MISSAO_COMPLETA");
            return;
        }

        final double[] wp         = FieldMap.WAYPOINTS[wpIndex];
        final double   wpAngFinal = wp[2];

        switch (wpFase) {

            case 0: {
                planejarRota(wp[0], wp[1]);
                segIdx = 0;
                SmartDashboard.putString("Nav/Status",
                    "WP" + wpIndex + " planejado segCount=" + segCount);
                wpFase = 1;
                break;
            }

            case 1: {
                if (segIdx >= segCount) {
                    poseX_cm = wp[0];
                    poseY_cm = wp[1];
                    wpFase = (wpAngFinal > -900) ? 4 : 6;
                    break;
                }

                final double destX = segX_cm[segIdx];
                final double destY = segY_cm[segIdx];
                final double dx    = destX - poseX_cm;
                final double dy    = destY - poseY_cm;
                distAlvo_m = Math.sqrt(dx * dx + dy * dy) / 100.0;

                SmartDashboard.putString("Nav/Status",
                    "SEG" + segIdx + " dest=(" + String.format("%.1f", destX) +
                    "," + String.format("%.1f", destY) + ")");

                if (distAlvo_m < (FieldMap.TOLERANCIA_CHEGADA_CM / 100.0)) {
                    segIdx++;
                    break;
                }

                giroAlvo = calcularGiroAxial(dx, dy);
                SmartDashboard.putNumber("Nav/giroAlvo", giroAlvo);

                if (Math.abs(giroAlvo) < TOLERANCE_DEG) {
                    resetBlocos();
                    wpFase = 3;
                } else {
                    resetBlocos();
                    wpFase = 2;
                }
                break;
            }

            case 2: {
                final boolean ok = executarGiro(giroAlvo);
                if (ok) {
                    poseAng_deg = normalizeAngle180(poseAng_deg + giroAlvo);
                    resetBlocos();
                    wpFase = 3;
                }
                break;
            }

            case 3: {
                final boolean ok = moverDistancia("F", distAlvo_m);
                if (ok) {
                    poseX_cm = segX_cm[segIdx];
                    poseY_cm = segY_cm[segIdx];
                    segIdx++;
                    resetBlocos();
                    wpFase = 1;
                }
                break;
            }

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

            case 5: {
                final boolean ok = executarGiro(giroAlvo);
                if (ok) {
                    poseAng_deg = wpAngFinal;
                    wpFase = 6;
                }
                break;
            }

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
    //  PLANEJAMENTO DE ROTA - SIMPLIFICADO
    //  Apenas movimento axial: tenta Y depois X, se não funcionar tenta X depois Y
    // =========================================================
    private void planejarRota(final double destX, final double destY) {
        segCount = 0;

        final double oriX = poseX_cm;
        final double oriY = poseY_cm;

        // Tenta Y primeiro, depois X
        if (!segmentoBateObstaculo(oriX, oriY, oriX, destY) &&
            !segmentoBateObstaculo(oriX, destY, destX, destY)) {
            adicionarSeg(oriX,  destY);  // Move em Y (vertical)
            adicionarSeg(destX, destY);  // Move em X (horizontal)
            SmartDashboard.putString("Nav/Rota", "Y_depois_X");
            return;
        }

        // Se não deu, tenta X primeiro, depois Y
        if (!segmentoBateObstaculo(oriX, oriY, destX, oriY) &&
            !segmentoBateObstaculo(destX, oriY, destX, destY)) {
            adicionarSeg(destX, oriY);   // Move em X (horizontal)
            adicionarSeg(destX, destY);  // Move em Y (vertical)
            SmartDashboard.putString("Nav/Rota", "X_depois_Y");
            return;
        }

        // Fallback: force Y depois X
        adicionarSeg(oriX,  destY);
        adicionarSeg(destX, destY);
        SmartDashboard.putString("Nav/Rota", "FALLBACK_Y_X");
    }

    // =========================================================
    //  Adiciona um sub-segmento ao plano
    // =========================================================
    private void adicionarSeg(final double x, final double y) {
        if (segCount < MAX_SEGS) {
            segX_cm[segCount] = x;
            segY_cm[segCount] = y;
            segCount++;
        }
    }

    // =========================================================
    //  Verifica colisao de um segmento axial com obstaculos
    //  Amostra pontos a cada 5cm ao longo do segmento
    // =========================================================
    private boolean segmentoBateObstaculo(
            final double x1, final double y1,
            final double x2, final double y2) {

        final double dx   = x2 - x1;
        final double dy   = y2 - y1;
        final double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1.0) return false;

        final int amostras = Math.max(2, (int) (dist / 5.0) + 1);
        for (int i = 0; i <= amostras; i++) {
            final double t  = (double) i / amostras;
            final double px = x1 + t * dx;
            final double py = y1 + t * dy;
            if (FieldMap.dentroDeObstaculo(px, py)) return true;
        }
        return false;
    }

    // =========================================================
    //  CALCULO DO GIRO AXIAL
    // =========================================================
    private double calcularGiroAxial(final double dx, final double dy) {
        double angDestino;
        if (Math.abs(dx) > Math.abs(dy)) {
            angDestino = (dx >= 0) ? 0.0 : 180.0;
        } else {
            angDestino = (dy >= 0) ? 90.0 : -90.0;
        }
        return normalizeAngle180(angDestino - poseAng_deg);
    }

    // =========================================================
    //  ODOMETRIA
    // =========================================================
    private void atualizarOdometria() {
        final double cur2 = enc2.getDistance();
        final double cur3 = enc3.getDistance();

        final double delta2 = cur2 - lastEnc2;
        final double delta3 = cur3 - lastEnc3;

        final double deltaD_m  = (Math.abs(delta2) + Math.abs(delta3)) / 2.0;
        final double sinal     = ((-delta2) + delta3) / 2.0 >= 0 ? 1.0 : -1.0;
        final double deltaD_cm = sinal * deltaD_m * 100.0;

        final double angRad = Math.toRadians(poseAng_deg);
        poseX_cm += deltaD_cm * Math.cos(angRad);
        poseY_cm += deltaD_cm * Math.sin(angRad);

        lastEnc2 = cur2;
        lastEnc3 = cur3;
    }

    // =========================================================
    //  moverDistancia - retorna true quando concluido
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
                return true;
            }
            aplicarRecuo(dir);
            return false;
        }

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
    //  executarGiro - retorna true quando concluido
    // =========================================================
    private boolean executarGiro(final double grausAlvo) {

        if (!giroIniciado) {
            enc0.reset(); enc2.reset(); enc3.reset();
            giroIniciado   = true;
            ciclosEstaveis = 0;
            return false;
        }

        final double distPercorrida = (Math.abs(enc0.getDistance()) +
                                       Math.abs(enc2.getDistance()) +
                                       Math.abs(enc3.getDistance())) / 3.0;
        final double grausPercorridos = distPercorrida / DIST_POR_GRAU;
        final double erro = Math.abs(grausAlvo) - grausPercorridos;

        SmartDashboard.putNumber("Giro/erro_graus",     erro);
        SmartDashboard.putNumber("Giro/ciclosEstaveis", ciclosEstaveis);

        if (erro < TOLERANCE_DEG) {
            ciclosEstaveis++;
            stopMotors();
            if (ciclosEstaveis >= CICLOS_ESTAVEIS) return true;
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
        segIdx     = 0;
        segCount   = 0;
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
    //  Sensores de distancia
    // =========================================================
    private double getDistanceF() {
        if (ultra0 == null) return 9999.0;
        return ultra0.getRangeMM();
    }
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