/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

import java.util.logging.Level;
import java.util.logging.Logger;
import terminal.elevator.state.PersonState;
import terminal.elevator.helper.MathHelper;

/**
 *
 * @author User
 */
public class Person extends Thread{
    private int toFloor;
    private float trolleyWeight; 
    private PersonState ps;
    
    
    private final int id;
    private final float weight;
    private final int trolleyQnt;
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
    public Person(int id){
        this.id = id;
        ps = PersonState.SLEEPING;
        fromFloor = MathHelper.randBetween(MAXFLOOR,ZERO);
        weight = MathHelper.randBetween(MAXWEIGHT,MINWEIGHT);
        trolleyQnt = MathHelper.randBetween(MAXTROLLEY,ZERO);
        trolleyWeight = ZERO;
        idleTime = MathHelper.randBetween(MAXIDLETIME, ZERO);
        
        do
            toFloor = MathHelper.randBetween(MAXFLOOR,ZERO);
        while(toFloor == fromFloor);
                
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
    }

    /**
     * @return the weight
     */
    public float getWeight() {
        return weight;
    }

    /**
     * @return the trolleyNumber
     */
    public int getTrolleyQnt() {
        return trolleyQnt;
    }

    /**
     * @return the trolleyWeight
     */
    public float getTrolleyWeight() {
        return trolleyQnt;
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
}
