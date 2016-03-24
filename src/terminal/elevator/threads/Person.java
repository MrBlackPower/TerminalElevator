/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.Instant;
import terminal.elevator.errors.FalseStatement;
import terminal.elevator.helper.FileHelper;
import terminal.elevator.state.PersonState;
import terminal.elevator.helper.MathHelper;
import terminal.elevator.state.ElevatorState;
import terminal.elevator.threads.messages.CallElevator;

/**
 *
 * @author User
 */
public class Person extends Thread{
    private static int count = 0;
    
    private PersonState ps;
    private Elevator e;
    private int toFloor;
    private int floor;
    private float trolleyWeight; 
    private CallElevator cs;
    private final LinkedBlockingQueue<CallElevator> LINE;
    private final ElevatorState DIRECTION;
    private final int ID;
    private final float WEIGHT;
    private final int FROMFLOOR;
    private final int IDLETIME;
    private Instant WAKINGTIME;
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
     * @param line
     */
    public Person(LinkedBlockingQueue<CallElevator> line){
        count ++;
        ID = count;
        
        int trolleyQnt = MathHelper.randBetween(MAXTROLLEY,ZERO);
        this.LINE = line;
        ps = PersonState.SLEEPING;
        FROMFLOOR = MathHelper.randBetween(MAXFLOOR,ZERO);
        floor = FROMFLOOR;
        WEIGHT = MathHelper.randBetween(MAXWEIGHT,MINWEIGHT);
        trolleyWeight = ZERO;
        IDLETIME = MathHelper.randBetween(MAXIDLETIME, ZERO);
        
        do
            toFloor = MathHelper.randBetween(MAXFLOOR,ZERO);
        while(toFloor == FROMFLOOR);
        
        DIRECTION = (toFloor > FROMFLOOR? ElevatorState.UPWARDS : ElevatorState.DOWNWARDS);
                
        for(int i = 0; i < trolleyQnt; i++)
            trolleyWeight += MathHelper.randBetween(MAXTROLLEYWEIGHT,MINTROLLEYWEIGHT);
    }
    
    @Override
    public void run(){
        try {
            sleep(IDLETIME);
        } catch (InterruptedException ex) {
            Logger.getLogger(Person.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        WAKINGTIME = Instant.now();
        
        System.out.println("Person " + ID + " awoke after " + IDLETIME + "ms in floor " + FROMFLOOR);
        FileHelper.addText(String.format("Person "+ ID +" (Thread " + getId() + ") in floor " + FROMFLOOR + " awoke at " + WAKINGTIME.toString() ));
        
        if(ps == PersonState.SLEEPING){
            callElevator();
            ps = PersonState.WAITING;
        }
    }

    /**
     * @return the WEIGHT
     */
    public float getWeight() {
        return WEIGHT;
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
     * @return the FROMFLOOR
     */
    public int getFromFloor() {
        return FROMFLOOR;
    }
    
    /**
     * @return totalWeight
     */
    
    public float totalWeight() {
        return WEIGHT + trolleyWeight;
    }
    
    public void callElevator(){
        ps = PersonState.WAITING;
        cs = new CallElevator(this);
        try {
            LINE.put(cs);
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
     * @return the DIRECTION
     */
    public ElevatorState getDirection() {
        return DIRECTION;
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
        return LINE;
    }
    
    public void checkCompletion() throws FalseStatement{
        if(floor != toFloor){
            throw new FalseStatement("Person " + getID() + " is not really finished.");
        }
    }
    
    public void getOn(Elevator e){
        this.e = e;
        ps = PersonState.ONELEVATOR;
    }
    
    public void getOff(){
        e = null;
        ps = PersonState.FINISHED;
    }

    /**
     * @return the ID
     */
    public int getID() {
        return ID;
    }
    
    /**
     * @return the WAKINGTIME
     */
    public Instant getWakingTime() {
        return WAKINGTIME;
    }
}
