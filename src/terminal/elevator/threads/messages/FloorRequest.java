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
public class FloorRequest {
    private final int id;
    private final Elevator elevator;
    private final int floor;
    
    static int count = 1;
    
    public FloorRequest (Elevator elevator, int floor) {
        this.elevator = elevator;
        this.floor = floor;
        id = count;
        count = count + 1;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the elevator
     */
    public Elevator getElevator() {
        return elevator;
    }

    /**
     * @return the floor
     */
    public int getFloor() {
        return floor;
    }
}
