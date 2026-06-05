package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.Constants;
import com.studica.frc.MockDS;

public class Robot extends TimedRobot {
    
    private MockDS ds;

    @Override
    public void robotInit() {
        // Inicializa o MockDS (Ele cria a ponte de rede internamente)
        ds = new MockDS(); 
      }
      
      @Override
      public void robotPeriodic() {
        Scheduler.getInstance().run();
        //driveTrain.mover(0.3, 0.3); 
    }

    // --- TESTE DE HARDWARE ---
    
    @Override
    public void autonomousPeriodic() {
        // Se você colocar o MockDS em modo "Autonomous" e habilitar (Enable), os motores vão girar!
        //driveTrain.mover(0.3, 0.3); // 30% da velocidade
    }

    @Override
    public void disabledPeriodic() {
        // Quando você der "Disable" no MockDS, isso garante que o robô para na hora (Segurança!)
        //driveTrain.parar();
    }
}