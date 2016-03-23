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
public class ThreadStillAlive extends Exception {

    /**
     * Creates a new instance of <code>ThreadStillAlive</code> without detail
     * message.
     */
    public ThreadStillAlive() {
    }

    /**
     * Constructs an instance of <code>ThreadStillAlive</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ThreadStillAlive(String msg) {
        super(msg);
    }
}
