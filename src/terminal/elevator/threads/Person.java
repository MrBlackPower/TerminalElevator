/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import terminal.elevator.state.PersonState;
import terminal.elevator.helper.MathHelper;
import terminal.elevator.state.ElevatorState;
import terminal.elevator.threads.messages.CallElevator;

/**
 *
 * @author User
 */
public class Person extends Thread{
    private int toFloor;
    private int floor;
    private float trolleyWeight; 
    private PersonState ps;
    private CallElevator cs;
    private Elevator e;
    private final LinkedBlockingQueue<CallElevator> line;
    
    private final ElevatorState direction;
    private final int id;
    private final float weight;
    private final int fromFloor;
    private final int idleTime;
    private final float MAXWEIGHT = 140;
    private final float MINWEIGHT = 50;
    private final int MAXFLOOR = 10;
    private final int ZERO = 0;
    private final int MAXTROLLEY = 5;
    private final float MAXTROLLEYWEIGHT = 32;
    private final float MINTROLLEYWEIGHT = 10;
    private final int MAXIDLETIME = 20000;
    
    /**
     * Person Constructor 
     * @param id
     */
    public Person(int id, LinkedBlockingQueue<CallElevator> line){
        int trolleyQnt = MathHelper.randBetween(MAXTROLLEY,ZERO);
        
        this.line = line;
        this.id = id;
        ps = PersonState.SLEEPING;
        fromFloor = MathHelper.randBetween(MAXFLOOR,ZERO);
        floor = fromFloor;
        weight = MathHelper.randBetween(MAXWEIGHT,MINWEIGHT);
        trolleyWeight = ZERO;
        idleTime = MathHelper.randBetween(MAXIDLETIME, ZERO);
        
        do
            toFloor = MathHelper.randBetween(MAXFLOOR,ZERO);
        while(toFloor == fromFloor);
        
        direction = (toFloor > fromFloor? ElevatorState.UPWARDS : ElevatorState.DOWNWARDS);
                
        for(int i = 0; i < trolleyQnt; i++)
            trolleyWeight += MathHelper.randBetween(MAXTROLLEYWEIGHT,MINTROLLEYWEIGHT);
    }
    
    @Override
    public void run(){
        try {
            sleep(idleTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(Person.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Thread " + id + " awoke after " + idleTime + "ms");
        
        
        callElevator();
        

        while(!(ps == PersonState.FINISHED)){
            
        }
    }
    
    public void getOn(Elevator e){
        this.e = e;
        ps = PersonState.ONELEVATOR;
    }
    
    public void getOut(){
        if (floor == toFloor) {
            ps = PersonState.FINISHED;
        }
    }

    /**
     * @return the weight
     */
    public float getWeight() {
        return weight;
    }

    /**
     * @return the trolleyWeight
     */
    public float getTrolleyWeight() {
        return trolleyWeight;
    }

    /**
     * @return the ps
     */
    public PersonState getPs() {
        return ps;
    }

    /**
     * @param ps the ps to set
     */
    public void setPs(PersonState ps) {
        this.ps = ps;
    }

    /**
     * @return the toFloor
     */
    public int getToFloor() {
        return toFloor;
    }

    /**
     * @return the fromFloor
     */
    public int getFromFloor() {
        return fromFloor;
    }
    
    /**
     * @return totalWeight
     */
    
    public float totalWeight() {
        return weight + trolleyWeight;
    }
    
    public void callElevator(){
        ps = PersonState.WAITING;
        cs = new CallElevator(this);
        try {
            line.put(cs);
        } catch (InterruptedException ex) {
            Logger.getLogger(Person.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the cs
     */
    public CallElevator getCs() {
        return cs;
    }

    /**
     * @return the direction
     */
    public ElevatorState getDirection() {
        return direction;
    }

    /**
     * @return the floor
     */
    public int getFloor() {
        return floor;
    }

    /**
     * @param floor the floor to set
     */
    public void setFloor(int floor) {
        this.floor = floor;
    }
    
    /**
     *
     * @return
     */
    public LinkedBlockingQueue<CallElevator> getCallList () {
        return line;
    }
}
