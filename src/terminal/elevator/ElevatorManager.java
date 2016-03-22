/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import terminal.elevator.threads.*;
import terminal.elevator.threads.messages.CallElevator;

/**
 *
 * @author User
 */
public class ElevatorManager {
    public ArrayList<Elevator> elevators;
    public ArrayList<Person> persons;
    public LinkedBlockingQueue<CallElevator> newCalls;
    public ArrayList<CallElevator> calls;
    
    /**
     * 
     * @param persons
     * @param elevators 
     */
    public ElevatorManager(ArrayList<Person> persons, ArrayList<Elevator> elevators){
        this.elevators = elevators;
        this.persons   = persons;
        this.newCalls  = new LinkedBlockingQueue();
        this.calls     = new ArrayList();
    }
    
    public ElevatorManager (ArrayList<Elevator> elevators) {
        this.elevators = elevators;
        this.persons   = new ArrayList();
        this.newCalls  = new LinkedBlockingQueue();
        this.calls     = new ArrayList();
    }
    
    /**
     * When called with no arguments, the default behavior is to consider
     * a single elevator system.
     */
    public ElevatorManager () {
        this.elevators = new ArrayList();
        elevators.add(new Elevator(1));
        this.persons  = new ArrayList();
        this.newCalls = new LinkedBlockingQueue();
        this.calls    = new ArrayList();
    }
    
    public void addPerson (Person p) {
        this.persons.add(p);
    }
    
    public void addPerson (ArrayList<Person> p) {
        this.persons.addAll(p);
    }
    
    public void start() {
        if (!this.persons.isEmpty()) {
            for (Person p : this.persons)  {
                p.start();
            }
            while (!this.persons.get(0).getCallList().isEmpty()) {
                try {
                    this.newCalls.add(this.persons.get(0).getCallList().take());
                } catch (NoSuchElementException ex) {
                    System.out.println(ex.toString());
                } catch (InterruptedException ex) {
                    Logger.getLogger(ElevatorManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
    }
    
    public int distance () {
        return 0;
    }
}
