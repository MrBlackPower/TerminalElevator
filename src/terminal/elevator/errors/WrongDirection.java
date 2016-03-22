/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.errors;

/**
 *
 * @author User
 */
public class WrongDirection extends Exception {

    /**
     * Creates a new instance of <code>WrongDirection</code> without detail
     * message.
     */
    public WrongDirection() {
    }

    /**
     * Constructs an instance of <code>WrongDirection</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public WrongDirection(String msg) {
        super(msg);
    }
}
