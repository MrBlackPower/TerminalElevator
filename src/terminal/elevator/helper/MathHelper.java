/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.helper;

/**
 *
 * @author User
 */
public abstract class MathHelper {
    public static int randBetween(int max, int min){
        return (int) randBetween((float)max,(float)min);
    }
    
    public static float randBetween(float max, float min){
        float range = max - min + 1;
        return (float) (Math.random() * range + min);
    }
    
}
