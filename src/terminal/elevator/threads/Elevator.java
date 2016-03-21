/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

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
    
    public void move(){
        if(es == ElevatorState.UPWARDS){
            return;
        }
        if(es == ElevatorState.DOWNWARDS){
            return;
        }
        System.out.println("Elevator bugged");
    }
}
