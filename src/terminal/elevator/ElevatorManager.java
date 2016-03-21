/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator;

import java.util.ArrayList;
import terminal.elevator.threads.*;

/**
 *
 * @author User
 */
public class ElevatorManager {
    public ArrayList<Elevator> elevators;
    public ArrayList<Person> persons;
    
    public ElevatorManager(){
        elevators = new ArrayList<>();
        persons = new ArrayList<>();
        
    }
}
