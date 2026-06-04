package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import com.studica.frc.Titan;

public class DriveTrain extends Subsystem {
    
    private final Titan titan0;
    
    public final Titan.Motor leftMotor0;
    public final Titan.Motor leftMotor1;
    public final Titan.Motor rightMotor0;
    public final Titan.Motor rightMotor1;

    public DriveTrain() {
        titan0 = new Titan(42); 
        
        leftMotor0 = titan0.getMotor(0);
        leftMotor1 = titan0.getMotor(1);
        rightMotor0 = titan0.getMotor(2);
        rightMotor1 = titan0.getMotor(3);
    }

    @Override
    protected void initDefaultCommand() {
        // Deixamos vazio! O robô não tem controle de joystick (condução manual)
    }

    public void mover(double velocidadeEsquerda, double velocidadeDireita) {
        leftMotor0.set(velocidadeEsquerda);
        leftMotor1.set(velocidadeEsquerda);
        rightMotor0.set(-velocidadeDireita); 
        rightMotor1.set(-velocidadeDireita);
    }

    public void parar() {
        mover(0.0, 0.0);
    }
}