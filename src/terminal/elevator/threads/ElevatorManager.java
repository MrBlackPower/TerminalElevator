/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import terminal.elevator.state.ElevatorManagerState;
import terminal.elevator.state.ElevatorState;
import terminal.elevator.threads.*;
import terminal.elevator.threads.messages.CallElevator;

/**
 *
 * @author User
 */
public class ElevatorManager extends Thread{
    private ArrayList<Elevator> elevators;
    private ArrayList<Person> persons;
    private LinkedBlockingQueue<CallElevator> newCalls;
    private ArrayList<CallElevator> calls;
    public ElevatorManagerState ems;
    public int addPerson;
    
    /**
     * 
     * @param personsQnt
     * @param elevatorsQnt
     */
    public ElevatorManager(int personsQnt, int elevatorsQnt){
        ems = ElevatorManagerState.STARTING;
        elevators = new ArrayList<>();
        persons   = new ArrayList<>();
        this.newCalls  = new LinkedBlockingQueue<>();
        this.calls     = new ArrayList<>();
        
        //creates new persons
        for(int i = 0; i < personsQnt; i++){
            Person p = new Person(newCalls);
            persons.add(p);
        }
        
        //creates new elevators
        for(int i = 0; i < elevatorsQnt; i++){
            Elevator e = new Elevator();
            elevators.add(e);
        }
    }
    
    
    /**
     * When called with no arguments, the default behavior is to consider
     * a single elevator system.
     */
    public ElevatorManager () {
        this(20,1);
    }
    
    public void addPerson (int qnt) {
        addPerson = qnt;
    }
    
    public void addPerson (ArrayList<Person> p) {
        this.persons.addAll(p);
        
        for(Person pe : p){
            pe.start();
        }
    }
    
    public void run() {
        if (!persons.isEmpty()) {
            for (Person p : persons)  {
                p.start();
            }
            while(ems != ElevatorManagerState.DEAD){
                //Verify if it has to add people in it
                if(ems == ems.ADDINGPEOPLE){
                    for(int i = 0; i < addPerson; i++){
                        Person p = new Person(newCalls);
                        p.start();
                    }
                    
                    addPerson = 0;
                    ems = ElevatorManagerState.RUNNING;
                }
                    
                //Verify is someone ordered a elevator
                while(!newCalls.isEmpty()){
                    //Atributes a elevator to it
                    CallElevator nc = newCalls.poll();
                    System.out.println("Call in  Floor" + nc.getFromFloor());
//                    getElevator(nc);
                }
                
                //Verify if elevator is requesting some floor information
                
                //Sends floor information
                
            }

//            while (!this.persons.get(0).getCallList().isEmpty()) {
//                try {
//                    this.newCalls.add(this.persons.get(0).getCallList().take());
//                } catch (NoSuchElementException ex) {
//                    System.out.println(ex.toString());
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(ElevatorManager.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                
//            }
        }
        System.out.println("Manager Finished");
    }
    
    public int distance (Elevator e, CallElevator ce) {
        int dist;
        if (ce.getFromFloor() < e.getFloor()) {
            if (e.getDirection() == ElevatorState.UPWARDS) {
                if (ce.getDirection() == ElevatorState.UPWARDS) 
                    dist = (10 - e.getFloor()) + 10 + ce.getFromFloor();
                else
                    dist = 2 * (10 - e.getFloor()) + (e.getFloor() - ce.getFromFloor());
            } else {
                if (ce.getDirection() == ElevatorState.UPWARDS) {
                    dist = e.getFloor() + ce.getFromFloor();
                } else {
                    dist = e.getFloor() - ce.getFromFloor();
                }
            }
        } else if (ce.getFromFloor() > e.getFloor()) {
            if (e.getDirection() == ElevatorState.UPWARDS) {
                if (ce.getDirection() == ElevatorState.UPWARDS) 
                    dist = ce.getFromFloor() - e.getFloor();
                else
                    dist = (10 - e.getFloor()) + (10 - ce.getFromFloor());
            } else {
                if (ce.getDirection() == ElevatorState.UPWARDS) {
                    dist = e.getFloor() + ce.getFromFloor();
                } else {
                    dist = e.getFloor() + 10 + (10 - ce.getFromFloor());
                }
            }
        } else {
            if (ce.getDirection() == e.getDirection()) {
                dist = 0;
            } else if (ce.getDirection() == ElevatorState.UPWARDS) {
                dist = 2 * ce.getFromFloor();
            } else {
                dist = 2 * (10 - ce.getFromFloor());
            }
        }
        return dist;
    }

    private void getElevator(CallElevator ce) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
