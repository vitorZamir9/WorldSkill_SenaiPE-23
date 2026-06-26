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

public class Robot extends TimedRobot {

    private static final double[][] WAYPOINTS = {
        // { x,     y,    graus,  girar }
        {  0.5,   0.5,   0.0,    0 },
        {  0.0,   0.0,   0.0,    0 },
    };

    // =========================================================
    //  PID — Translação
    // =========================================================
    private static final double KP_POS = 0.8;
    private static final double KI_POS = 0.0;
    private static final double KD_POS = 0.02;

    // =========================================================
    //  PID — Rotação
    // =========================================================
    private static final double KP_ROT = 0.005;
    private static final double KI_ROT = 0.001;
    private static final double KD_ROT = 0.0002;

    private static final double TOLERANCE_POS_M   = 0.05;
    private static final double TOLERANCE_ROT_DEG = 2.0;

    private static final double MAX_SPEED_TRANS = 0.7;
    private static final double MAX_SPEED_ROT   = 0.5;

    private static final double DEADBAND_TRANS = 0.20;
    private static final double DEADBAND_ROT   = 0.20;

    private static final double DT = 0.02;

    // Anti-windup correto — quando KI=0 o limite é infinito (integrador
    // não acumula de qualquer forma pois ki*integral = 0)
    private static final double INTEGRAL_MAX_POS = (KI_POS > 1e-9) ? (0.3 / KI_POS) : Double.MAX_VALUE;
    private static final double INTEGRAL_MAX_ROT = (KI_ROT > 1e-9) ? (0.2 / KI_ROT) : 200.0;

    // =========================================================
    //  Hardware
    // =========================================================

    private AHRS gyro;

    private Titan       titan;
    private Titan.Motor motor0;
    private Titan.Motor motor2;
    private Titan.Motor motor3;

    private MockDS ds;

    private DigitalInput  btnStart;
    private DigitalInput  btnStop;
    private DigitalOutput led1;
    private DigitalOutput led2;
    private DigitalOutput led3;
    private DigitalOutput led4;

    private boolean lastStart = false;
    private boolean lastStop  = false;

    private ThreeWheelOmniOdometry odometry;

    private int     waypointIndex = 0;
    private boolean rotating      = false;
    private boolean autoFinished  = false;

    private double integralX    = 0, integralY    = 0, integralRot  = 0;
    private double lastErrorX   = 0, lastErrorY   = 0, lastErrorRot = 0;

    @Override
    public void robotInit() {
        gyro = new AHRS(SPI.Port.kMXP);

        titan  = new Titan(Constants.TITAN_ID);
        motor0 = titan.getMotor(Constants.MOTOR_0);
        motor2 = titan.getMotor(Constants.MOTOR_2);
        motor3 = titan.getMotor(Constants.MOTOR_3);

        Titan.Encoder enc0 = titan.getEncoder(Constants.ENCODER_0, Constants.DIST_PER_TICK);
        Titan.Encoder enc2 = titan.getEncoder(Constants.ENCODER_2, Constants.DIST_PER_TICK);
        Titan.Encoder enc3 = titan.getEncoder(Constants.ENCODER_3, Constants.DIST_PER_TICK);

        odometry = new ThreeWheelOmniOdometry(enc0, enc2, enc3);

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

        double gyroAngle = -gyro.getAngle();
        odometry.updateWithGyro(gyroAngle);

        boolean curStart = btnStart.get();
        boolean curStop  = btnStop.get();

        if (lastStart && !curStart) {
            ds.enable();
            led1.set(true);  led2.set(false);
            led3.set(true);  led4.set(false);
            System.out.println("Habilitar enviado.");
        }
        if (lastStop && !curStop) {
            ds.disable();
            led1.set(false); led2.set(true);
            led3.set(false); led4.set(true);
            System.out.println("Desabilitar enviado.");
        }

        lastStart = curStart;
        lastStop  = curStop;

        SmartDashboard.putNumber("Gyro/Angle_deg",    gyroAngle);
        SmartDashboard.putBoolean("Gyro/Calibrating", gyro.isCalibrating());
    }

    @Override
    public void autonomousInit() {
        while (gyro.isCalibrating()) {
            try { Thread.sleep(10); } catch (InterruptedException e) {}
        }
        gyro.zeroYaw();

        odometry.resetPose(0.0, 0.0, 0.0);
        waypointIndex = 0;
        autoFinished  = false;
        rotating      = false;
        resetPID();
        System.out.println("Autônomo iniciado. Waypoints: " + WAYPOINTS.length);
    }

    @Override
    public void autonomousPeriodic() {
        if (autoFinished) { stopMotors(); return; }

        double curX   = odometry.getX();
        double curY   = odometry.getY();
        double curDeg = odometry.getThetaDeg();

        double[] wp           = WAYPOINTS[waypointIndex];
        double   targetX      = wp[0];
        double   targetY      = wp[1];
        double   targetDeg    = wp[2];
        boolean  shouldRotate = (wp[3] == 1);

        SmartDashboard.putNumber("Auto/WP_index",     waypointIndex);
        SmartDashboard.putNumber("Auto/ErrorX_m",     targetX - curX);
        SmartDashboard.putNumber("Auto/ErrorY_m",     targetY - curY);
        SmartDashboard.putNumber("Auto/Dist_m",       Math.hypot(targetX - curX, targetY - curY));
        SmartDashboard.putNumber("Auto/ErrorRot_deg", normalizeAngle180(targetDeg - curDeg));

        // ---- Fase 1: translação ----------------------------------------
        if (!rotating) {
            double errorX = targetX - curX;
            double errorY = targetY - curY;
            double dist   = Math.hypot(errorX, errorY);

            if (dist < TOLERANCE_POS_M) {
                if (shouldRotate) {
                    rotating = true;
                    resetRotPID();
                    System.out.printf("WP %d: posição OK (%.3f m) → girando para %.1f°%n",
                            waypointIndex, dist, targetDeg);
                } else {
                    advanceWaypoint();
                }
                stopMotors();
                return;
            }

            // Velocidade máxima com rampa de desaceleração nos últimos 0.4 m
            // Evita o robô chegar em alta velocidade e ultrapassar o alvo
            double speedLimit = MAX_SPEED_TRANS * Math.min(1.0, dist / 0.4);
            speedLimit = Math.max(speedLimit, DEADBAND_TRANS + 0.05); // mínimo para vencer atrito

            double vxField = pidCalc(errorX, integralX, lastErrorX,
                                     KP_POS, KI_POS, KD_POS, speedLimit);
            double vyField = pidCalc(errorY, integralY, lastErrorY,
                                     KP_POS, KI_POS, KD_POS, speedLimit);

            // Deadband vetorial — preserva direção, não corta eixos individualmente
            double speedMag = Math.hypot(vxField, vyField);
            if (speedMag < DEADBAND_TRANS) {
                vxField = 0.0;
                vyField = 0.0;
            } else {
                double scale = (speedMag - DEADBAND_TRANS) / (1.0 - DEADBAND_TRANS);
                scale   = clamp(scale, 0.0, 1.0);
                vxField = (vxField / speedMag) * scale * speedLimit;
                vyField = (vyField / speedMag) * scale * speedLimit;
            }

            integralX  = clamp(integralX + errorX * DT, -INTEGRAL_MAX_POS, INTEGRAL_MAX_POS);
            integralY  = clamp(integralY + errorY * DT, -INTEGRAL_MAX_POS, INTEGRAL_MAX_POS);
            lastErrorX = errorX;
            lastErrorY = errorY;

            double[] speeds = odometry.fieldCentricSpeeds(vxField, vyField, 0.0);
            motor0.set(speeds[0]);
            motor2.set(speeds[1]);
            motor3.set(speeds[2]);

        // ---- Fase 2: rotação -------------------------------------------
        } else {
            double errorRot = normalizeAngle180(targetDeg - curDeg);

            if (Math.abs(errorRot) < TOLERANCE_ROT_DEG) {
                System.out.printf("WP %d: rotação OK (erro=%.2f°)%n",
                        waypointIndex, errorRot);
                rotating = false;
                advanceWaypoint();
                stopMotors();
                return;
            }

            double omega = pidCalc(errorRot, integralRot, lastErrorRot,
                                   KP_ROT, KI_ROT, KD_ROT, MAX_SPEED_ROT);

            omega = applyDeadbandScalar(omega, DEADBAND_ROT);

            integralRot  = clamp(integralRot + errorRot * DT,
                                 -INTEGRAL_MAX_ROT, INTEGRAL_MAX_ROT);
            lastErrorRot = errorRot;

            double[] speeds = odometry.fieldCentricSpeeds(0.0, 0.0, omega);
            motor0.set(speeds[0]);
            motor2.set(speeds[1]);
            motor3.set(speeds[2]);
        }
    }

    // =========================================================
    //  Helpers
    // =========================================================

    private void advanceWaypoint() {
        waypointIndex++;
        resetPID();
        if (waypointIndex >= WAYPOINTS.length) {
            autoFinished = true;
            stopMotors();
            ds.disable();
            led1.set(true); led2.set(false);
            System.out.println("Rota concluída!");
        } else {
            System.out.printf("Indo para WP %d -> (%.2f, %.2f)%n",
                    waypointIndex,
                    WAYPOINTS[waypointIndex][0],
                    WAYPOINTS[waypointIndex][1]);
        }
    }

    private double pidCalc(double error, double integral, double lastError,
                           double kp, double ki, double kd, double maxOut) {
        double derivative = (error - lastError) / DT;
        double output     = kp * error + ki * integral + kd * derivative;
        return clamp(output, -maxOut, maxOut);
    }

    private double applyDeadbandScalar(double value, double deadband) {
        if (Math.abs(value) < deadband) return 0.0;
        double sign  = Math.signum(value);
        double scale = (Math.abs(value) - deadband) / (1.0 - deadband);
        return sign * clamp(scale, 0.0, 1.0);
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    private void resetPID() {
        integralX = integralY = integralRot = 0;
        lastErrorX = lastErrorY = lastErrorRot = 0;
    }

    private void resetRotPID() {
        integralRot  = 0;
        lastErrorRot = 0;
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

    @Override
    public void disabledInit() {
        stopMotors();
        led1.set(false); led2.set(true);
        led3.set(false); led4.set(true);
    }

    @Override
    public void disabledPeriodic() {
        stopMotors();
        led1.set(false); led2.set(true);
        led3.set(false); led4.set(true);
    }
}