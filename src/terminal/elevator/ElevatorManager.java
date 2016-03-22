/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import terminal.elevator.threads.*;
import terminal.elevator.threads.messages.CallElevator;

/**
 *
 * @author User
 */
public class ElevatorManager {
    public ArrayList<Elevator> elevators;
    public ArrayList<Person> persons;
    public LinkedBlockingQueue<CallElevator> callList;
    
    /**
     * 
     * @param persons
     * @param elevators 
     */
    public ElevatorManager(ArrayList<Person> persons, ArrayList<Elevator> elevators){
        this.elevators = elevators;
        this.persons   = persons;
        this.callList  = new LinkedBlockingQueue();
    }
    
    public ElevatorManager (ArrayList<Elevator> elevators) {
        this.elevators = elevators;
        this.persons   = new ArrayList();
        this.callList      = new LinkedBlockingQueue();
    }
    
    /**
     * When called with no arguments, the default behavior is to consider
     * a single elevator system.
     */
    public ElevatorManager () {
        this.elevators = new ArrayList();
        elevators.add(new Elevator(1));
        this.persons = new ArrayList();
        this.callList    = new LinkedBlockingQueue();
    }
    
    public void addPerson (Person p) {
        this.persons.add(p);
    }
    
    public void addPerson (ArrayList<Person> p) {
        this.persons.addAll(p);
    }
    
    public void start() {
        if (!this.persons.isEmpty()) {
            for (Person p : this.persons)
                p.start();
            callList.addAll(this.persons.get(0).getCallList());
        }
    }
}
