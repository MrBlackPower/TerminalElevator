/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads.messages;

import terminal.elevator.state.CallState;
import terminal.elevator.state.ElevatorState;
import terminal.elevator.threads.Person;

/**
 *
 * @author User
 */
public class OrderElevator {
    private final int id;
    private final int toFloor;
    private final CallState cs;
    
    static int callNumber = 1;
    
    public OrderElevator(Person p){
        this.id = callNumber;
        toFloor = p.getToFloor();
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
     * @return the toFloor
     */
    public int getToFloor() {
        return toFloor;
    }
}
