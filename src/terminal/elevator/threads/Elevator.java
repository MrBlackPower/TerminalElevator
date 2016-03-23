/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.threads;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import terminal.elevator.state.ElevatorState;
import terminal.elevator.errors.*;
import terminal.elevator.threads.messages.CallElevator;
import terminal.elevator.threads.messages.OrderElevator;

/**
 *
 * @author User
 */
public class Elevator extends Thread {
    private final int ID;
    private final int MAXWEIGHT = 600;
    private final int GROUNDFLOOR = 0;
    private final int TOPFLOOR = 10;
    
    private int floor;
    private int currentWeight;
    private ElevatorState es;
    private ArrayList<Person> onBoard;
    private ArrayList<CallElevator> calls;
    private ArrayList<OrderElevator> orders;
    
    public Elevator(int ID){
        this.ID = ID;
        floor = 0;
        currentWeight = 0;
        es = ElevatorState.IDLE;
        onBoard = new ArrayList<>();
        calls = new ArrayList<>();
        orders = new ArrayList<>();
    }
    
    @Override
    public void run(){
        while(es != ElevatorState.DEAD){
            if(es != ElevatorState.IDLE){
                try {
                    idle();
                } catch (InterruptedException | OutOfBounds ex) {
                    Logger.getLogger(Elevator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public void checkFloor(){
        System.out.println("Unsupported method");
    }
    
    public void checkFloor(ArrayList<Person> persons){
        //People Leaving
        for(Person p : onBoard){
            if(floor == p.getToFloor())
                getOut(p);
            
        }
        
        if (es != ElevatorState.IDLE) {
            //People going in and going in the same direction
            for (Person p : persons) {
                if (p.getFloor() == floor) {
                    if (p.getDirection() == es) {
                        try {
                            getOn(p);
                            persons.remove(p);
                        } catch (WrongDirection ex) {
                            Logger.getLogger(Elevator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            //Checks if can change direction
            boolean changeDirection = (es == ElevatorState.UPWARDS ? hasAboveActions() : hasBelowActions());

            //People going in in the opposite direction get in if the elevator
            //is empty and it`s possible to change the direction
            if (isEmpty() && changeDirection) {
                for (Person p : persons) {
                    //They only enter if the elevator is empty
                    if (p.getFloor() == floor) {
                        try {
                            getOn(p);
                        } catch (WrongDirection ex) {
                            Logger.getLogger(Elevator.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
            }
        } else {
        }
    }
    
    public void getOn(Person p) throws WrongDirection{
        //Checks weight
        if(currentWeight + p.totalWeight() >= MAXWEIGHT){
            System.out.println("Overweight detected, person will not board.");
            return;
        }
        
        //Remove Call
        removeCalls(p.getCs());
        
        //Add Order
        orders.add(new OrderElevator(p));
        
        p.getOn(this);
        currentWeight += p.totalWeight();
        onBoard.add(p);
    }
    
    public void getOut(Person p){
        currentWeight -= p.totalWeight();
        
        p.getOut();
        if(onBoard.contains(p))
            onBoard.remove(p);
        
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
        
        if(es == ElevatorState.UPWARDS){
            if(!isOnTopFloor()){
                floor += 1;
                sleep(100);
            } else {
                es = ElevatorState.IDLE;
            }
            return;
        }
        
        if(es == ElevatorState.DOWNWARDS){
            if(!isOnGroundFloor()){
                floor -= 1;
                sleep(100);
            } else {
                es = ElevatorState.IDLE;
            }
            return;
        }
        
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
        if(isOnGroundFloor()){
            checkFloor();
            if(hasAboveActions())
                es = ElevatorState.UPWARDS;
        } else {
            if(!hasActions()){
                es = ElevatorState.DOWNWARDS;
            }
        }
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
        for(OrderElevator o : orders){
            if(o.getToFloor() == floor)
                orders.remove(o);
        }
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
}
