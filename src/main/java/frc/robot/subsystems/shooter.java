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

public class shooter extends SubsystemBase {
  /** Creates a new ExampleSubsystem. */
  public shooter() {
    motor1.restoreFactoryDefaults();
    motor2.restoreFactoryDefaults();
  }

  private final WPI_TalonSRX motor1 = new WPI_TalonSRX​(13); //motor 1 --> first motor (bottom)
  private final WPI_TalonSRX motor2 = new WPI_TalonSRX​(14); //motor 2 --> second motor (top)
  
  private final XboxController controller = new XboxController(0); 
  public final Timer timer = new Timer();

  public final AnalogPotentiometer EntryIR = new AnalogPotentiometer(0, 180, 30); //IR sensor at entry
  public final AnalogPotentiometer TopIR = new AnalogPotentiometer(0, 180, 30); //IR sensor at the top
  public final ColorSensorV3 CSensor = new ColorSensorV3(I2C.Port.kMXP);
  
  public final double signal_average; // get an average signal value so we can create a signal to 'noise' ratio later
  for(int i = 0; i<=5; i++){
    signal_average += CSensor.getProximity();
  }
  signal_average = signal_average/5; 
  // all the signal average stuff is not necessary, but I need to know the base value to replace it

  boolean loaded_primary = false; // state variable tracking whether the top spot has a ball
  boolean loaded_secondary = false; // state variable tracking whether the bottom spot has a ball
  boolean storage_full = false; // is the storage full? it shouldn't take the comment to figure out what that means
  boolean execute = false; // toggles the pseudo intake mechanism (so it only runs after the button has been pressed)
  boolean cycle2top = false; // triggers the process to move the ball to the top position
  boolean cycle2sec = false; // triggers the process to move the ball to the bottom storage position

  @Override
  public void periodic() {

    if(controller.getRightBumper()){ //toggle switch for process
      if(execute){
        execute = false;
      }
      else{
        execute = true;
      }
    }

    if(loaded_primary && loaded_secondary){
        storage_full = true;
        execute = false;
    }

    if(execute){ // if the button was pressed to enable, execute the cycling processes

        int entrydist = EntryIR.get() * 46.15; // get a value from the IR sensor close to the intake
        if(entrydist <= 10){ // if the ball is detected in front of the IR sensor
            if(loaded_primary){ // check if there is already a ball in the top spot
                if(loaded_secondary){ // check if there is already a ball in the secondary spot
                    System.out.println("Storage full."); // both spots are filled, so storage is full
                }
                else{ // if there is a ball in the top spot, but none in secondary
                cycle2sec = true; // set the correct state variable to cycle to secondary position
                System.out.println("Cycling to secondary storage position.");
                motor1.set(0.25); // turn on the motors to get the ball moving
                motor2.set(0.25);
                }
            }
            else{ // if there is no ball in the top spot
                cycle2top = true; // set the correct state variable to cycle to the primary position
                System.out.println("Cycling to primary storage position.");
                motor1.set(0.25); // turn on the motors to get the ball moving
                motor2.set(0.25); 
            }
        }


    }

    if(cycle2top){ // cycles ball to top position
        int topdist = TopIR.get() * 46.15; // checks whether the ball is present or not at the top sensor
        if(topdist <= 10){ // if there is something in front of the sensor (such as the ball), stop cycling
            
            motor1.set(0.0);
            motor2.set(0.0);
            cycle2top = false;
            loaded_primary = true;
        }

    }

    if(cycle2sec){ // cycles ball to bottom (secondary) storage position
        int colorsensed = CSensor.getProximity(); // gets proximity from color sensor
        if((colorsensed/signal_average) >= 1.5){ //if the color sensor detects the ball, stop cycling through
            
            motor1.set(0.0);
            motor2.set(0.0);
            cycle2sec = false;
            loaded_secondary = true; 
        }

    }
    
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
  
}
