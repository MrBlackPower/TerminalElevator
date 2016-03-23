/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator;

import terminal.elevator.threads.ElevatorManager;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import terminal.elevator.state.ElevatorManagerState;
import terminal.elevator.threads.Person;

/**
 *
 * @author User
 */
public class TerminalElevator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO code application logic here
        ElevatorManager em = new ElevatorManager(2, 1);
        em.start();
        em.addPerson = 5;
        em.ems = ElevatorManagerState.ADDINGPEOPLE;
    }
}
