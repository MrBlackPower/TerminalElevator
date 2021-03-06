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
public class CallElevator {
    private final int id;
    private final int fromFloor;
    private final CallState cs;
    private final ElevatorState direction;
    
    static int callNumber = 1;
    
    public CallElevator(Person p){
        this.id = callNumber;
        fromFloor = p.getFromFloor();
        direction = p.getDirection();
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
        return fromFloor;
    }

    /**
     * @return the direction
     */
    public ElevatorState getDirection() {
        return direction;
    }
}
