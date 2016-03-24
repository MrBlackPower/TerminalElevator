/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import terminal.elevator.state.ElevatorManagerState;
import terminal.elevator.state.ElevatorState;
import terminal.elevator.state.PersonState;
import terminal.elevator.threads.messages.AssignedCall;
import terminal.elevator.threads.messages.CallElevator;
import terminal.elevator.threads.messages.FloorReply;
import terminal.elevator.threads.messages.FloorRequest;

/**
 *
 * @author User
 */
public class ElevatorManager extends Thread{
    private ArrayList<Elevator> elevators;
    private ArrayList<Person> persons;
    private LinkedBlockingQueue<CallElevator> newCalls;//Calls people make
    private LinkedBlockingQueue<AssignedCall> assignedCalls;//Calls assigned to a elevator
    private LinkedBlockingQueue<FloorRequest> floorRequests;//Floor requests by elevators
    private LinkedBlockingQueue<FloorReply> floorReplies;//Floor replies
    private JProgressBar progressBar;
    
    public ElevatorManagerState ems;
    public int addPerson;
    public int personsQnt;
    public int elevatorsQnt;
    
    /**
     * 
     * @param personsQnt
     * @param elevatorsQnt
     */
    public ElevatorManager(int personsQnt, int elevatorsQnt,JProgressBar progressBar){
        ems = ElevatorManagerState.STARTING;
        elevators = new ArrayList<>();
        persons   = new ArrayList<>();
        newCalls  = new LinkedBlockingQueue<>();
        assignedCalls = new LinkedBlockingQueue<>();
        floorReplies = new LinkedBlockingQueue<>();
        floorRequests = new LinkedBlockingQueue<>();
        this.personsQnt = personsQnt;
        this.elevatorsQnt = elevatorsQnt;
        this.progressBar = progressBar;
        
        //creates new persons
        for(int i = 0; i < personsQnt; i++){
            Person p = new Person(newCalls);
            persons.add(p);
        }
        
        //creates new elevators
        for(int i = 0; i < elevatorsQnt; i++){
            Elevator e = new Elevator(assignedCalls,floorRequests,floorReplies);
            elevators.add(e);
        }
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
            for (Elevator e: elevators){
                e.start();
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
                    System.out.println("Call in  Floor " + nc.getFromFloor());
                    
                    Elevator e = getElevator(nc);
                    System.out.println("Elevator " + e.ID + " assigned.");
                    
                    AssignedCall ac = new AssignedCall(nc, e);
                    try {
                        assignedCalls.put(ac);
                    } catch (InterruptedException ex) {
                        System.out.println(ex.getMessage());
                    }
                    
                }
                
                //Verify if elevator is requesting some floor information
                while(!floorRequests.isEmpty()){
                    //Sends floor information
                    for(FloorRequest fRequest : floorRequests){
                        Elevator e = fRequest.getElevator();
                        int floor = fRequest.getFloor();
                        ArrayList<Person> personsOnFloor = new ArrayList<>();
                        //TODO
                        for(Person p : persons){
                            if(!p.isAlive()){
                                if(p.getPs() == PersonState.WAITING && p.getFloor() == e.floorMirror){
                                    personsOnFloor.add(p);
                                }
                            }
                        }
                        
                        //Create Reply
                        FloorReply fReply = new FloorReply(e, personsOnFloor);
                        
                        //Send Reply
                        try {
                            floorReplies.put(fReply);
                        } catch (InterruptedException ex) {
                            System.out.println(ex.getMessage());
                        }
                        
                        floorRequests.remove(fRequest);
                    }
                }
                
                personsQnt = persons.size();
                elevatorsQnt = elevators.size();
                progressBar.setValue(100*getFinishedPersons()/personsQnt);
            }
        }
        
        for(Elevator e : elevators){
            e.dead = true;
        }
        
        System.out.println("Manager Finished");
    }
    
    public int distance (Elevator e, CallElevator ce) {
        int dist;
        if (ce.getFromFloor() < e.floorMirror) {
            if (e.getDirection() == ElevatorState.UPWARDS) {
                if (ce.getDirection() == ElevatorState.UPWARDS) 
                    dist = (10 - e.floorMirror) + 10 + ce.getFromFloor();
                else
                    dist = 2 * (10 - e.floorMirror) + (e.floorMirror - ce.getFromFloor());
            } else {
                if (ce.getDirection() == ElevatorState.UPWARDS) {
                    dist = e.floorMirror + ce.getFromFloor();
                } else {
                    dist = e.floorMirror - ce.getFromFloor();
                }
            }
        } else if (ce.getFromFloor() > e.floorMirror) {
            if (e.getDirection() == ElevatorState.UPWARDS) {
                if (ce.getDirection() == ElevatorState.UPWARDS) 
                    dist = ce.getFromFloor() - e.floorMirror;
                else
                    dist = (10 - e.floorMirror) + (10 - ce.getFromFloor());
            } else {
                if (ce.getDirection() == ElevatorState.UPWARDS) {
                    dist = e.floorMirror + ce.getFromFloor();
                } else {
                    dist = e.floorMirror + 10 + (10 - ce.getFromFloor());
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

    private Elevator getElevator(CallElevator ce) {
        Elevator assignedElevator = elevators.get(0);
        for (Elevator e : elevators) {
            if (distance(e, ce) < distance(assignedElevator, ce)) {
                assignedElevator = e;
            }
        }
        return assignedElevator;
    }
    
    private FloorReply resolveFloorRequest(FloorRequest fr){
        throw new UnsupportedOperationException("ablidebob");
    }
    
    private int getFinishedPersons(){
        int i = 0;
        for(Person p : persons){
            if(!p.isAlive())
                if(p.getPs() == PersonState.FINISHED)
                    i++;
        }
        return i;
    }
}
