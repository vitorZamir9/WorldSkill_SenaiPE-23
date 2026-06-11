package frc.robot;

import com.studica.frc.Titan;
import com.studica.frc.MockDS;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends TimedRobot {

    private Titan titan;
    private Titan.Motor motor0;
    private Titan.Motor motor1;
    private Titan.Motor motor2;

    private MockDS ds;

    private DigitalInput  btnStart;
    private DigitalInput  btnStop;
    private DigitalOutput led1;
    private DigitalOutput led2;

    private volatile boolean active = false;
    private Thread controlThread;

    @Override
    public void robotInit() {
        titan  = new Titan(Constants.TITAN_ID);
        motor0 = titan.getMotor(Constants.MOTOR_0);
        motor1 = titan.getMotor(Constants.MOTOR_1);
        motor2 = titan.getMotor(Constants.MOTOR_2);

        ds = new MockDS();

        btnStart = new DigitalInput(Constants.BTN_START);
        btnStop  = new DigitalInput(Constants.BTN_STOP);
        led1     = new DigitalOutput(Constants.LED_1);
        led2     = new DigitalOutput(Constants.LED_2);

        led1.set(true);
        led2.set(true);

        controlThread = new Thread(() -> {
            boolean lastStart = btnStart.get();

            while (!Thread.interrupted()) {
                boolean curStart = btnStart.get();

                // Toggle: cada clique inverte o estado
                if (!curStart && lastStart) {
                    active = !active;
                    //led1.set(active);
                    led2.set(!active);
                }

                if (active) {
                    ds.enable();
                } else {
                    ds.disable();
                }

                lastStart = curStart;

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        controlThread.setDaemon(true);
        controlThread.setPriority(Thread.MAX_PRIORITY);
        controlThread.start();
    }

    @Override
    public void robotPeriodic() {
        Scheduler.getInstance().run();
    }

    @Override
    public void autonomousPeriodic() {
        if (active) {
            motor0.set(Constants.VELOCIDADE);
            motor1.set(Constants.VELOCIDADE);
            motor2.set(Constants.VELOCIDADE);
        } else {
            motor0.set(0.0);
            motor1.set(0.0);
            motor2.set(0.0);
        }
    }

    @Override
    public void disabledPeriodic() {
        motor0.set(0.0);
        motor1.set(0.0);
        motor2.set(0.0);
    }
}