/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

import java.util.ArrayList;
import terminal.elevator.state.ElevatorState;

/**
 *
 * @author User
 */
public class Elevator extends Thread {
    private final int id;
    private final int MAXWEIGHT = 600;
    
    private int floor;
    private int currentWeight;
    private ElevatorState es;
    private ArrayList<Person> onBoard;
    
    public Elevator(int id){
        this.id = id;
        floor = 0;
        currentWeight = 0;
        es = ElevatorState.IDLE;
    }
    
    public void getOn(Person p){
        
    }
    
    public void getOut(Person p){
        
    }
    
    public void move() throws InterruptedException{
        if(es == ElevatorState.UPWARDS){
            if(floor < 10){
                floor += 1;
                sleep(100);
            } else {
                es = ElevatorState.IDLE;
            }
                
            return;
        }
        if(es == ElevatorState.DOWNWARDS){
            if(floor > 0){
                floor -= 1;
                sleep(100);
            } else {
                es = ElevatorState.IDLE;
            }
            return;
        }
        System.out.println("Elevator bugged");
    }
}
