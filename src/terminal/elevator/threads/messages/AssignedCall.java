/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads.messages;

import terminal.elevator.threads.Elevator;

/**
 *
 * @author User
 */
public class AssignedCall {
    private static int count = 0;
    
    private final int ID;
    private CallElevator call;
    private Elevator e;

    public AssignedCall(CallElevator call, Elevator e) {
        count++;
        ID = count;
        this.call = call;
        this.e = e;
    }

    /**
     * @return the call
     */
    public CallElevator getCall() {
        return call;
    }

    /**
     * @return the e
     */
    public Elevator getE() {
        return e;
    }
}
