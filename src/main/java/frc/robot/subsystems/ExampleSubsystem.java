// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.XboxController;

import edu.wpi.first.wpilibj.Timer;
//import com.revrobotics.CANSparkMaxLowLevel.IdleMode;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;



public class ExampleSubsystem extends SubsystemBase {
  /** Creates a new ExampleSubsystem. */
  public ExampleSubsystem() {
    
  }


  private final XboxController controller = new XboxController(0); 
  public final Timer timer = new Timer();
  public final ColorSensorV3 CSensor = new ColorSensorV3(I2C.Port.kMXP);


  @Override
  public void periodic() {
   

    System.out.println(CSensor.getProximity());
    
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
  
}
