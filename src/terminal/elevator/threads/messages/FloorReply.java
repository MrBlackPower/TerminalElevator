/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads.messages;

import java.util.ArrayList;
import terminal.elevator.threads.Elevator;
import terminal.elevator.threads.Person;

/**
 *
 * @author User
 */
public class FloorReply {
    private final int id;
    private final Elevator elevator;
    private ArrayList<Person> persons;

    static int count = 1;
    
    public FloorReply(Elevator elevator, ArrayList<Person> persons) {
        this.elevator = elevator;
        this.persons = persons;
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
     * @return the persons
     */
    public ArrayList<Person> getPersons() {
        return persons;
    }
}
