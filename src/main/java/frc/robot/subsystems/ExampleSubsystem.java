// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.XboxController;

import edu.wpi.first.wpilibj.Timer;
import com.revrobotics.CANSparkMaxLowLevel.IdleMode;

public class ExampleSubsystem extends SubsystemBase {
  /** Creates a new ExampleSubsystem. */
  public ExampleSubsystem() {
    motor.restoreFactoryDefaults();
    motor_the_second.restoreFactoryDefaults();
    motor_the_second.follow(motor);
  }

  private final CANSparkMax motor = new CANSparkMax(1, MotorType.kBrushless);
  private final CANSparkMax motor_the_second = new CANSparkMax(17, MotorType.kBrushless);
  motor.setIdleMode(idleMode.kcoast);
  private final XboxController controller = new XboxController(0); 
  public final Timer timer = new Timer();
  public final AnalogPotentiometer pot = new AnalogPotentiometer(0, 180, 30);
  //public static int c_SparkMax_SetIdleMode​(long handle, int idlemode)


  //private final JoyStickButton button = new JoyStickButton(controller,XboxController.Button.kA.value);
  boolean toggle;
  int status = 0;
  int dist = 0; 


  @Override
  public void periodic() {
    //System.out.println("Print anything");
    
    //toggle motors
    //motor.set(controller.getLeftY());
    // if(controller.getRightBumper()){
    //   if(toggle){
    //     toggle = false;
    //   }
    //   else{
    //     toggle = true;
    //   }
    // }
    // if(toggle){
    //   motor.set(0.5);
    //   motor_the_second.set(0.5);
    // }
    // else{
    //   motor.set(0.0);
    //   motor_the_second.set(0.0);
      
    // }

//motor spool up
    // System.out.println(timer.get());
    // if(controller.getRightBumper()){
    //   status += 1;
    //   timer.start();
    // }
    
    // if(status == 1 && timer.get() <= 4.0){
    //   motor.set(timer.get()/4.0);
    // }
    
    // if(status == 2 && timer.get() <= 4.0){
    //   motor.set(1);
    // }

    // if(status == 3 && timer.get() <= 4.0){
    //   motor.set((1 - timer.get())/4.0);
    // }
    
    // if(timer.get() >= 4.0){
    //   status += 1;
    //   timer.reset(); 
    // }

    System.out.println(pot.get());
    dist = pot.get() * 46.15;
    motor.set(dist/30);
    
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
  
}
