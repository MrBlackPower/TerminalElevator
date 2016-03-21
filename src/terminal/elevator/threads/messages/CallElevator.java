/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads.messages;

import terminal.elevator.state.CallState;
import terminal.elevator.threads.Person;

/**
 *
 * @author User
 */
public class CallElevator {
    private final int id;
    private final Person p;
    private final CallState cs;
    
    static int callNumber = 1;
    
    public CallElevator(Person p){
        this.id = callNumber;
        this.p = p;
        cs = CallState.WAITING;
        callNumber += 1;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the fromFloor
     */
    public int getFromFloor() {
        return p.getFromFloor();
    }

    /**
     * @return the toFloor
     */
    public int getToFloor() {
        return p.getToFloor();
    }

    /**
     * @return the p
     */
    public Person getP() {
        return p;
    }
    
    /**
     * @return the total weight associated with the call
     */
    public float getTotalWeight(){
        return p.totalWeight();
    }
            
}
