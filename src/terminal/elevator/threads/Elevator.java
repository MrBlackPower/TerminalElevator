/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import terminal.elevator.state.ElevatorState;
import terminal.elevator.errors.*;

/**
 *
 * @author User
 */
public class Elevator extends Thread {
    private final int id;
    private final int MAXWEIGHT = 600;
    private final int lowestfloor = 0;
    
    private int floor;
    private int highestFloor;
    private int currentWeight;
    private ElevatorState es;
    private ArrayList<Person> onBoard;
    
    public Elevator(int id){
        this.id = id;
        floor = 0;
        currentWeight = 0;
        highestFloor = 0;
        es = ElevatorState.IDLE;
    }
    
    @Override
    public void run(){
        while(true){
            if(es != ElevatorState.IDLE && es != ElevatorState.DEAD){
                
            }
            if(es == ElevatorState.DEAD)
                break;
        }
    }
    
    public void checkFloor(ArrayList<Person> persons){
        //People Leaving
        for(Person p : onBoard){
            if(floor == p.getToFloor())
                getOut(p);
            
        }
        
        //People coming in
        for(Person p : persons){
            if(p.getElevatorDirection() == es){
                try {
                    getOn(p);
                } catch (WrongDirection ex) {
                    Logger.getLogger(Elevator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void getOn(Person p) throws WrongDirection{
        if(p.getElevatorDirection() != es)
            throw new WrongDirection("Wrong Direction passenger trying to board");
        
        if(currentWeight + p.totalWeight() >= MAXWEIGHT){
            System.out.println("Overweight detected, person will not board.");
            return;
        }
        
        if(null != es)switch (es) {
            case DOWNWARDS:
                if(floor > p.getToFloor())
                    throw new WrongDirection("Passenger Confused");
                
                break;
            case UPWARDS:
                if(floor < p.getToFloor())
                    throw new WrongDirection("Passenger Confused");
                
                highestFloor = p.getToFloor();
                
                break;
            default:
                throw new WrongDirection("Elevator Confused");
        }
        
        p.getOn(this);
        currentWeight += p.totalWeight();
        onBoard.add(p);
    }
    
    public void getOut(Person p){
        currentWeight -= p.totalWeight();
        p.getOut();
        if(onBoard.contains(p))
            onBoard.remove(p);
    }
    
    public void move() throws InterruptedException{
        if(es == ElevatorState.UPWARDS){
            if(floor < 10 && floor < highestFloor){
                floor += 1;
                sleep(100);
            } else {
                highestFloor = 0;
                es = ElevatorState.DOWNWARDS;
                move();
            }
            return;
        }
        
        if(es == ElevatorState.DOWNWARDS){
            if(floor > 0 && floor > lowestfloor){
                floor -= 1;
                sleep(100);
            } else {
                es = ElevatorState.IDLE;
                move();
            }
            return;
        }
        
        if(es == ElevatorState.IDLE){
            if(floor > 0){
                if(floor > lowestfloor){
                    floor -= 1;
                    sleep(100);
                } else {
                    es = ElevatorState.IDLE;
                    move();
                }            
            }
            return;
        }
          
        System.out.println("Elevator bugged");
    }
}
