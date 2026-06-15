package frc.robot;

import com.studica.frc.Titan;
import com.studica.frc.MockDS;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Robot extends TimedRobot {

    private Titan titan;
    private Titan.Motor motor0;
    private Titan.Motor motor1;
    private Titan.Motor motor2;

    private MockDS ds;

    private DigitalInput btnStart;
    private DigitalInput btnStop;
    private DigitalOutput led1;
    private DigitalOutput led2;

    private boolean active = false;
    private boolean lastStart = false; 
    private boolean lastStop = false;

    private Timer autoTimer = new Timer();

    @Override
    public void robotInit() {
        titan = new Titan(Constants.TITAN_ID);
        motor0 = titan.getMotor(Constants.MOTOR_0);
        motor1 = titan.getMotor(Constants.MOTOR_1);
        motor2 = titan.getMotor(Constants.MOTOR_2);

        ds = new MockDS();

        btnStart = new DigitalInput(Constants.BTN_START);
        btnStop = new DigitalInput(Constants.BTN_STOP);
        led1 = new DigitalOutput(Constants.LED_1);
        led2 = new DigitalOutput(Constants.LED_2);
    }

    @Override
    public void robotPeriodic() {
        Scheduler.getInstance().run();

        // 1. Lê o estado ATUAL dos botões
        boolean curStart = btnStart.get();
        boolean curStop = btnStop.get();

        // 2. Detecta se o botão START foi pressionado AGORA
        // (A lógica aqui assume que o botão retorna false ao ser pressionado - active low)
        if (lastStart && !curStart) { 
            ds.enable();
            led1.set(false);
            led2.set(true);
            System.out.println("Comando: Habilitar enviado.");
        }

        // 3. Detecta se o botão STOP foi pressionado AGORA
        if (lastStop && !curStop) {
            ds.disable();
            led1.set(true);
            led2.set(false);
            System.out.println("Comando: Desabilitar enviado.");
        }

        // 4. Salva o estado atual para comparar na próxima volta do loop
        lastStart = curStart;
        lastStop = curStop;
    }

    @Override
    public void autonomousInit() {
        autoTimer.reset();
        autoTimer.start();
    }

    @Override
    public void autonomousPeriodic() {

        double time = autoTimer.get();
        
        // Sequência de 3 blocos de movimento
        if (time < 1.0) {
            // Bloco 1
            //motor0.set(1.0); //gira
            //motor1.set(1.0);
            //motor2.set(1.0);
        } else if (time < 2.0) {
            // Bloco 2
            motor0.set(0.0);
            motor1.set(0.5); //frente
            motor2.set(-0.5);
        } else if (time < 3.0) {
            // Bloco 3
            motor0.set(0.0);
            motor1.set(-0.5); //tras
            motor2.set(0.5);
        }else if (time < 4.0) {
            // Bloco 4
            motor0.set(-0.5);
            motor1.set(0.5); //esquerda
            motor2.set(0.0);
        }else if (time < 5.0) {
            // Bloco 5
            motor0.set(0.5);
            motor1.set(-0.5); //esquerda pro meio
            motor2.set(0.0);
        }else if (time < 6.0) {
            // Bloco 6
            motor0.set(0.5);
            motor1.set(0.0); //direita
            motor2.set(-0.5);
        }else if (time < 7.0) {
            // Bloco 7
            motor0.set(-0.5);
            motor1.set(0.0); //direita pro meio
            motor2.set(0.5);
        }else if (time < 8.0) {
            // Bloco 8
            motor0.set(1.0); 
            motor1.set(1.0); //gira
            motor2.set(1.0);
        }
        else if (time < 13.0) {
            //nada
        }else {
            // Finalizado
            stopMotors();
            ds.disable();
        }
    }
    
    private void stopMotors() {
        motor0.set(0.0);
        motor1.set(0.0);
        motor2.set(0.0);
    }

    @Override
    public void disabledPeriodic() {
        stopMotors();
        led1.set(true);
    }

    @Override
    public void disabledInit() {
        stopMotors();
        led1.set(true);
    }
}