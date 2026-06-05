package frc.robot.subsystems;

// WPI imports
// A importação do wpilibj2 foi removida para evitar o erro
import frc.robot.Constants;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;

//Java imports
import java.util.Map;

//Vendor imports
import com.kauailabs.navx.frc.AHRS;
import com.studica.frc.Titan;
import com.studica.frc.Cobra;
import com.studica.frc.Servo;
import com.studica.frc.ServoContinuous;

public class Training extends Subsystem
{
    //Motors
    private Titan motor;
    private Servo servo;
    private ServoContinuous servoC; // Corrigido para C maiúsculo

    //Sensors
    private Cobra cobra;
    private Ultrasonic sonic;
    private AnalogInput sharp;
    private AHRS gyro;

    //Shufleboard
    private ShuffleboardTab tab = Shuffleboard.getTab("Training");
    private NetworkTableEntry servoPos = tab.add("Servo Position", 0)
                                            .withWidget(BuiltInWidgets.kNumberSlider)
                                            .withProperties(Map.of("min", 0, "max", 300))
                                            .getEntry();
    private NetworkTableEntry servoSpeed = tab.add("Servo Speed", 0)
                                              .withWidget(BuiltInWidgets.kNumberSlider)
                                              .withProperties(Map.of("min", -1, "max", 1))
                                              .getEntry();
    private NetworkTableEntry sharpIR = tab.add("Sharp IR", 0)
                                           .getEntry();
    private NetworkTableEntry ultraSonic = tab.add("Ultrasonic", 0)
                                              .getEntry();
    private NetworkTableEntry cobraRaw = tab.add("Cobra Raw", 0)
                                            .getEntry();
    private NetworkTableEntry cobraVoltage = tab.add("Cobra Voltage", 0)
                                                .getEntry();
    private NetworkTableEntry navX = tab.add("NavX Yaw", 0)
                                        .getEntry();

    public Training() 
    {
        //Motors - Nomes corrigidos para bater exatamente com o seu Constants.java
        motor = new Titan(Constants.TITAN_ID, Constants.MOTOR);
        servo = new Servo(Constants.SERVO);
        servoC = new ServoContinuous(Constants.SERVO_C);

        //Sensors
        cobra = new Cobra();
        sharp = new AnalogInput(Constants.SHARP);
        sonic = new Ultrasonic(Constants.SONIC_TRIGG, Constants.SONIC_ECHO);
        gyro = new AHRS(SPI.Port.kMXP);
    }

    // Método obrigatório adicionado para a classe Subsystem compilar
    @Override
    protected void initDefaultCommand() {
        // Deixe vazio por enquanto
    }

    /**
     * Call for the raw ADC value
     */
    public int getCobraRawValue(int channel)
    {
        return cobra.getRawValue(channel);
    }

    /**
     * Call for the voltage from the ADC
     */
    public double getCobraVoltage(int channel)
    {
        return cobra.getVoltage(channel);
    }

    /**
     * Call for the distance measured by the Sharp IR Sensor
     */
    public double getIRDistance()
    {
        return (Math.pow(sharp.getAverageVoltage(), -1.2045) * 27.726);
    }

    /**
     * Call for the distance measured by the Ultrasonic Sensor
     */
    public double getSonicDistance(boolean metric)
    {
        sonic.ping();
        Timer.delay(0.005);
        if (metric)
            return sonic.getRangeMM();
        else
            return sonic.getRangeInches();
    }

    /**
     * Call for the current angle from the internal NavX
     */
    public double getYaw()
    {
        return gyro.getYaw();
    }

    /**
     * Resets the yaw angle back to zero
     */
    public void resetGyro()
    {
        gyro.zeroYaw();
    }

    /**
     * Sets the servo angle based on the input from the shuffleboard widget
     */
    public void setServoAngle()
    {
        servo.setAngle(servoPos.getDouble(0.0));
    }

    /**
     * Sets the servo angle
     */
    public void setServoAngle(double degrees)
    {
        servo.setAngle(degrees);
    }

    /**
     * Sets the servo speed based on the input from the shuffleboard widget
     */
    public void setServoSpeed()
    {
        servoC.set(servoSpeed.getDouble(0.0));
    }

    /**
     * Sets the servo speed
     */
    public void setServoSpeed(double speed)
    {
        servoC.set(speed);
    }

    /**
     * Sets the speed of the motor
     * <p>
     * @param speed range -1 to 1 (0 stop)
     */
    public void setMotorSpeed(double speed)
    {
        setMotorSpeed(speed);
    }

    /**
     * Code that runs once every robot loop
     */
    @Override
    public void periodic()
    {
        setServoAngle(); // Updates the servo

        /**
         * Updates for outputs to the shuffleboard
         */
        sharpIR.setDouble(getIRDistance());
        ultraSonic.setDouble(getSonicDistance(true)); //set to true because we want metric
        cobraRaw.setDouble(getCobraRawValue(0)); //Just going to use channel 0 for demo
        cobraVoltage.setDouble(getCobraVoltage(0));
        navX.setDouble(getYaw());
    }
}