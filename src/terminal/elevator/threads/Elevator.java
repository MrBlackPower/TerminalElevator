/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import terminal.elevator.state.ElevatorState;
import terminal.elevator.errors.*;
import terminal.elevator.helper.FileHelper;
import terminal.elevator.threads.messages.AssignedCall;
import terminal.elevator.threads.messages.CallElevator;
import terminal.elevator.threads.messages.FloorReply;
import terminal.elevator.threads.messages.FloorRequest;
import terminal.elevator.threads.messages.OrderElevator;

/**
 *
 * @author User
 */
public class Elevator extends Thread {
    protected int floorMirror;
    protected final int ID;
    protected boolean dead;
    
    private ElevatorState es;
    private final int TICKTIME = 1000;
    private int floor;
    private static int count = 0;
    private final int MAXWEIGHT = 600;
    private final int GROUNDFLOOR = 0;
    private final int TOPFLOOR = 10;
    private int currentWeight;
    private ArrayList<Person> onBoard;
    private ArrayList<CallElevator> calls;
    private ArrayList<OrderElevator> orders;
    private LinkedBlockingQueue<AssignedCall> assignedCalls;
    private LinkedBlockingQueue<FloorRequest> floorRequests;
    private LinkedBlockingQueue<FloorReply> floorReplies;
    
    public Elevator(LinkedBlockingQueue<AssignedCall> assignedCalls,
            LinkedBlockingQueue<FloorRequest> floorRequests,
            LinkedBlockingQueue<FloorReply> floorReplies){
        count ++;
        ID = count;
        floor = 0;
        floorMirror = floor;
        currentWeight = 0;
        es = ElevatorState.IDLE;
        onBoard = new ArrayList<>();
        calls = new ArrayList<>();
        orders = new ArrayList<>();
        this.assignedCalls = assignedCalls;
        this.floorReplies = floorReplies;
        this.floorRequests = floorRequests;
        dead = false;
    }
    
    @Override
    public void run(){
        while(!dead){
            checkCalls();
            try {
                if(null != es)
                    switch (es) {
                    case IDLE:
                        idle();
                        break;
                    case DOWNWARDS:
                        downwards();
                        break;
                    case UPWARDS:
                        upwards();
                        break;
                    default:
                        break;
                }
            } catch (InterruptedException | OutOfBounds ex) {
                Logger.getLogger(Elevator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        es = ElevatorState.DEAD;
        System.out.println("Elevator " + ID + " finished.");
    }
    
    public void checkFloor(){
        FloorRequest fRequest = new FloorRequest(this,floor);
        ArrayList<Person> personsOnFloor = null;
        
        try {
            floorRequests.put(fRequest);
        } catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
        }
        
        while (personsOnFloor == null) {            
            if(!floorReplies.isEmpty()){
                for(FloorReply fReply : floorReplies){
                    Elevator e = fReply.getElevator();
                    if(e.ID == ID){
                        personsOnFloor = fReply.getPersons();
                        floorReplies.remove(fReply);
                    }
                    
                }
            }
            if(dead)
                return;
                
        }
        
        checkFloorAux(personsOnFloor);
        
    }
    
    public void checkFloorAux(ArrayList<Person> persons){
        //People Leaving
        getOut();
        
        if (es != ElevatorState.IDLE) {
            //People going in and going in the same direction
            for (Person p : persons) {
                if (p.getFloor() == floor) {
                    if (p.getDirection() == es) {
                        try {
                            getOn(p);
                        } catch (WrongDirection | ThreadStillAlive ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }

            //Checks if can change direction
            boolean changeDirection = (es == ElevatorState.UPWARDS ? !hasAboveActions() : !hasBelowActions());

            //People going in in the opposite direction get in if the elevator
            //is empty and it`s possible to change the direction
            if (isEmpty() && changeDirection) {
                for (Person p : persons) {
                    //They only enter if the elevator is empty
                    if (p.getFloor() == floor) {
                        try {
                            getOn(p);
                        } catch (WrongDirection | ThreadStillAlive ex) {
                            System.out.println(ex.getMessage());
                        }
                        
                    }
                }
            }
        } else {
            //If elevator is idle the person just needs to be in the same floor
            for (Person p : persons) {
                if (p.getFloor() == floor) {
                    try {
                        getOn(p);
                    } catch (WrongDirection | ThreadStillAlive ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }        
        }
    }
    
    public void getOn(Person p) throws WrongDirection, ThreadStillAlive{
        //Checks weight
        if(currentWeight + p.totalWeight() >= MAXWEIGHT){
            System.out.println("Overweight detected, person will not board.");
            return;
        }
        
        //Remove Call
        removeCalls(p.getCs());
        
        //Add Order
        orders.add(new OrderElevator(p));
        
        if(p.isAlive())
            throw new ThreadStillAlive("Person still alive");
            
        p.getOn(this);
        System.out.println("Person " + p.getID()+ " got in Elevator " + ID);
        Instant now = Instant.now();
        FileHelper.addText(String.format("Person " + p.getID()+ "(Thread " + p.getId() + ") got in Elevator " + ID + " at " + now.toString()));
        currentWeight += p.totalWeight();
        onBoard.add(p);
    }
    
    public void getOut(){
        ArrayList<Person> delete = new ArrayList<>();
        
        for(Person p : onBoard){
            if(floor == p.getToFloor())
                delete.add(p);
        }
        
        for(Person p : delete){
            currentWeight -= p.totalWeight();
            p.getOff();
            System.out.println("Person " + p.getID() + " got off.");
            Instant now = Instant.now();
            FileHelper.addText(String.format("Person " + p.getID()+ "(Thread " + p.getId() + ") got off at " + now.toString()));
        }
        
        
        boolean chng = onBoard.removeAll(delete);
        
        //Remove Order
        removeOrders();
    }
    
    public int getFloor () {
        return floor;
    }
    
    public ElevatorState getDirection () {
        return es;
    }
    
    public void move() throws InterruptedException, OutOfBounds{
        if(!isInBounds())
            throw new OutOfBounds("Elevator out of terminal bounds");
        
        String direction = "";
        
        if(es == ElevatorState.UPWARDS){
            if(!isOnTopFloor()){
                floor += 1;
                floorMirror = floor;
                sleep(TICKTIME);
                direction = "UP";
            } else {
                es = ElevatorState.IDLE;
                return;
            }
        }
        
        if(es == ElevatorState.DOWNWARDS){
            if(!isOnGroundFloor()){
                floor -= 1;
                floorMirror = floor;
                sleep(TICKTIME);
                direction = "DOWN";
            } else {
                es = ElevatorState.IDLE;
                return;
            }
        }
        
        String msg ="Elevator " + ID + " now in floor " + floor + " going " + direction;
        Instant now = Instant.now();
        FileHelper.addText(String.format("Elevator " + ID+ "(Thread " + this.getId() + ") now in floor " + floor + " going " + direction + " " + now.toString()));
        System.out.println(msg);
        for(Person p : onBoard){
            p.setFloor(floor);
        }
        
    }
    
    public boolean isOnGroundFloor(){
        return floor == GROUNDFLOOR;
    }
    
    public boolean isOnTopFloor(){
        return floor == TOPFLOOR;
    }
    
    public boolean isInBounds(){
        return (floor >= GROUNDFLOOR && floor <= TOPFLOOR);
    }
    
    public boolean isEmpty(){
        return onBoard.isEmpty();
    }
    
    
    private void idle() throws InterruptedException, OutOfBounds{
        checkFloor();
        if(isOnGroundFloor())
            if(hasAboveActions())
                es = ElevatorState.UPWARDS;
    }
    
    private void upwards() throws OutOfBounds, InterruptedException{
        checkFloor();
        if(!isOnTopFloor()){
            if(hasAboveActions())
                move();
            else
                es = ElevatorState.DOWNWARDS;
        } else {
            if(hasAboveActions())
                throw new OutOfBounds("Has actions beyond top floor");
            
            es = ElevatorState.DOWNWARDS;
        }
    }
    
    private void downwards() throws InterruptedException, OutOfBounds{
        checkFloor();
        if(!isOnGroundFloor()){
            if(!hasBelowActions() && hasAboveActions())
                es = ElevatorState.UPWARDS;
            else
                move();
        } else {
            if(hasBelowActions()){
                throw new OutOfBounds("Has actions below ground floor");
            }
            if(hasAboveActions()){
                es = ElevatorState.UPWARDS;
                return;
            }
            es = ElevatorState.IDLE;
        }
    }
    
    private void removeOrders(){
        ArrayList<OrderElevator> delete = new ArrayList<>();
        
        for(OrderElevator o : orders){
            if(o.getToFloor() == floor)
                delete.add(o);
        }
        
        orders.removeAll(delete);
    }
    
    private boolean hasOrders(){
        return !orders.isEmpty();
    }
    
    private boolean hasAboveOrders(){
        for(OrderElevator o : orders){
            if(o.getToFloor() > floor)
                return true;
        }
        return false;
    }
    
    private boolean hasBelowOrders(){
        for(OrderElevator o : orders){
            if(o.getToFloor() < floor)
                return true;
        }
        return false;
    }
    
    private void removeCalls(CallElevator c){
        calls.remove(c);
    }
    
    private boolean hasCalls(){
        return !calls.isEmpty();
    }
    
    private boolean hasAboveCalls(){
        for(CallElevator c : calls){
            if(c.getFromFloor() > floor)
                return true;
        }
        return false;
    }
    
    private boolean hasBelowCalls(){
        for(CallElevator c : calls){
            if(c.getFromFloor() < floor)
                return true;
        }
        return false;
    }
    
    private boolean hasActions(){
        return hasCalls() || hasOrders();
    }
    
    private boolean hasAboveActions(){
        return hasAboveCalls() || hasAboveOrders();
    }
    
    private boolean hasBelowActions(){
        return hasBelowCalls() || hasBelowOrders();
    }
    
    public void checkCalls(){
        AssignedCall acc = null;
        if(!assignedCalls.isEmpty()){
            for(AssignedCall ac : assignedCalls){
                Elevator e = ac.getE();
                if(e.ID == ID){
                    calls.add(ac.getCall());
                    acc = ac;
                    assignedCalls.remove(ac);
                }
            }
        }
    }
}
