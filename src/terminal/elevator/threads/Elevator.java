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
                
            }
        }
    }
    
    public void checkFloor(ArrayList<Person> persons){
        //People Leaving
        for(Person p : onBoard){
            if(floor == p.getToFloor())
                getOut(p);
            
        }
        
        //People going in and going in the same direction
        for(Person p : persons){
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
        
        //People going in in the opposite direction
        if (isEmpty()) {
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
    }
    
    public void getOn(Person p) throws WrongDirection{
        //Checks weight
        if(currentWeight + p.totalWeight() >= MAXWEIGHT){
            System.out.println("Overweight detected, person will not board.");
            return;
        }
        
        //Remove Call
        removeCalls();
        
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
    
    public void move() throws InterruptedException, OutOfBounds{
        if(!isInBounds())
            throw new OutOfBounds("Elevator out of terminal bounds");
        
        if(es == ElevatorState.UPWARDS || es == ElevatorState.DOWNWARDS){
            changeFloor();
            return;
        }
        
        if(es == ElevatorState.IDLE){
            idle();
            return;
        }
          
        System.out.println("Elevator bugged");
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
    
    private void changeFloor() throws InterruptedException, OutOfBounds{
        if(es != ElevatorState.DOWNWARDS && es!= ElevatorState.UPWARDS)
            return;
        
        //Move elevator upwards
        if(es == ElevatorState.UPWARDS){
            if(!isOnTopFloor()){
                floor += 1;
                sleep(100);
            } else {
                es = ElevatorState.IDLE;
                move();
            }
            return;
        }
        
        //Move elevator downwards
        if(es == ElevatorState.DOWNWARDS){
            //In case there`s a call above and the elevator is empty
            if(onBoard.isEmpty()){
                es = ElevatorState.UPWARDS;
                move();
            }
            //If it`s not on ground floor
            if(!isOnGroundFloor()){
                floor -= 1;
                sleep(100);
            } else {
                es = ElevatorState.IDLE;
                move();
            }
            return;
        }
        
        for(Person p : onBoard){
            p.setFloor(floor);
        }
    }
    
    private void idle() throws InterruptedException, OutOfBounds{
        //If it`s not on ground floor
        if(!isOnGroundFloor()){
            if(!hasActions()){
                es = ElevatorState.DOWNWARDS;
                move();
            }
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
    
    private boolean aboveOrders(){
        for(OrderElevator o : orders){
            if(o.getToFloor() > floor)
                return true;
        }
        return false;
    }
    
    private boolean belowOrders(){
        for(OrderElevator o : orders){
            if(o.getToFloor() < floor)
                return true;
        }
        return false;
    }
    
    private void removeCalls(){
        for(CallElevator c : calls){
            if(c.getFromFloor() == floor)
                orders.remove(c);
        }
    }
    
    private boolean hasCalls(){
        return !calls.isEmpty();
    }
    
    private boolean aboveCalls(){
        for(CallElevator c : calls){
            if(c.getFromFloor() > floor)
                return true;
        }
        return false;
    }
    
    private boolean belowCalls(){
        for(CallElevator c : calls){
            if(c.getFromFloor() < floor)
                return true;
        }
        return false;
    }
    
    private boolean hasActions(){
        return hasCalls() || hasOrders();
    }
}
