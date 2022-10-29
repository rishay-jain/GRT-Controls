// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;


import com.revrobotics.ColorSensorV3; // color sensor library
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX; // internal Falcon motors
import edu.wpi.first.wpilibj.AnalogPotentiometer; // analog sensor lib for IR sensors
import edu.wpi.first.wpilibj.XboxController; // controller input library
import edu.wpi.first.wpilibj.I2C; // I2C comm protocol

import com.revrobotics.CANSparkMax; //shooter flywheel
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Timer; // timer 

public class Shooter extends SubsystemBase {
  public Shooter() {
    entrymotor.configFactoryDefault(); // resets motor config to default
    topmotor.configFactoryDefault();
  }

  private final WPI_TalonSRX entrymotor = new WPI_TalonSRX​(13); // entry motor
  private final WPI_TalonSRX topmotor = new WPI_TalonSRX​(14); // top motor (drives a chain+roller too)
  private final CANSparkMax flywheel = new CANSparkMax(15, MotorType.kBrushless); // flywheel for launching

  private final XboxController controller = new XboxController(0); // controller
  public final Timer timer = new Timer();

  public final AnalogPotentiometer EntryIR = new AnalogPotentiometer(0); // IR sensor at entry
  public final AnalogPotentiometer TopIR = new AnalogPotentiometer(1); // IR sensor at the top
  public final ColorSensorV3 CSensor = new ColorSensorV3(I2C.Port.kMXP); // color sensor in the middle

  boolean loaded_primary = false; // state variable tracking whether the top spot has a ball
  boolean loaded_secondary = false; // state variable tracking whether the bottom spot has a ball
  boolean execute = true; // boolean that prevents the system from cycling the balls if both spots are full
  // execute is not necessary for the function of the code, but it serves as a safeguard
  boolean cycle2top = false; // triggers the process to move the ball to the primary (top) position
  boolean cycle2sec = false; // triggers the process to move the ball to the secondary (bottom) storage position
  boolean enable_flywheel = false; // enables the ball to be released into the flywheel
  int flywheel_count = 0; //used as a count variable
  /* once flywheel_count reaches a certain number of periodic cycles, 
  it triggers the post-launch cycling of secondary to primary */


  @Override
  public void periodic() {
    flywheel.set(0.35); // starts the flywheel (always spinning)

    //Sensor calibration code:
    //System.out.println("entry " + EntryIR.get());
    //System.out.println("top " + TopIR.get());

    if(loaded_primary && loaded_secondary){ // if both storage spots are full
        execute = false; // don't cycle anything
        enable_flywheel = true; // allow a ball to be launched
    }

    if(execute){ // if both spots aren't full, execute the cycling processes

        double entrydist = EntryIR.get(); // get a value from the IR sensor close to the intake
        if(entrydist >= 0.08){ // if the ball is detected in front of the IR sensor
            if(loaded_primary){ // check if there is already a ball in the top spot
                if(loaded_secondary){ // check if there is already a ball in the secondary spot
                    System.out.println("Storage full."); // both spots are filled, so storage is full
                    
                }
                else{ // if there is a ball in the primary spot, but none in secondary
                cycle2sec = true; // set the correct state variable to cycle to secondary position
                System.out.println("Cycling to secondary storage position.");
                entrymotor.set(-0.25); // turn on the motors to get the ball moving
                
                }
            }
            else{ // if there is no ball in the top spot or secondary spot
                cycle2top = true; // set the correct state variable to cycle to the primary position
                System.out.println("Cycling to primary storage position.");
                entrymotor.set(-0.25); // turn on the motors to get the ball moving
                topmotor.set(-0.25); 
            }
        }


    }

    if(cycle2top){ // cycles ball to top position if boolean was enabled 
        double topdist = TopIR.get(); // checks whether the ball is present or not at the top sensor
        if(topdist >= 0.35){ // if there is something in front of the sensor (such as the ball), stop cycling
            
            entrymotor.set(0.0); // stop the roller motors
            topmotor.set(0.0);
            cycle2top = false; // stop cycling to top
            loaded_primary = true; // update state variable to reflect new ball
        }

    }

    if(cycle2sec){ // cycles ball to bottom (secondary) storage position
        double colorsensed = CSensor.getProximity(); // gets proximity from color sensor
        if(colorsensed >= 1400){ //if the color sensor detects the ball, stop cycling through
            
            entrymotor.set(0.0);
            topmotor.set(0.0);
            cycle2sec = false; // stop cycling
            loaded_secondary = true; // update state variable
        }

    }

    if(enable_flywheel){ 
        
        if(controller.getRightBumper()){ // don't launch anything unless the button is pressed
            topmotor.set(-0.25); // push the primary ball into the flywheel (launch it)
        
        }
        
        
        if(flywheel_count >= 400){ // wait some amount of time to allow for launch
            
            enable_flywheel = false; // stop the launch process from continuing
            loaded_primary = false; // the top spot is empty (albeit for a very short time)
            execute = true; // we're back to one ball now, so go back to managing storage
            cycle2top = true; // cycle the secondary ball to top spot
            loaded_secondary = false; 
            // cycle2top doesn't know it's cycling a ball from secondary, so the soon-empty secondary needs to be accounted for here
           
            entrymotor.set(-0.25); 
            /* cycle2top requires both the interior motors to be running to get the ball to the top spot
            since the top motor is already spinning, the entry motor is the only one that needs to be started */

        }
        else{
             
            flywheel_count += 1; // if the wait condition hasn't been met, add one and repeat periodic
        }
        


    }
    
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
  
}
