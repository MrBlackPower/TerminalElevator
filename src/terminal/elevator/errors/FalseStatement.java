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
public class FalseStatement extends Exception {

    /**
     * Creates a new instance of <code>FalseStatement</code> without detail
     * message.
     */
    public FalseStatement() {
    }

    /**
     * Constructs an instance of <code>FalseStatement</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public FalseStatement(String msg) {
        super(msg);
    }
}
