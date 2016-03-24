/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package terminal.elevator.helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public abstract class FileHelper {
    private static ArrayList<String> texts = new ArrayList<>();
    
    public static boolean writeFile(String filePath)
    {
        try
        {
            try (FileWriter writer = new FileWriter(filePath);
                 BufferedWriter buffer = new BufferedWriter(writer);) // Try-with-resources
            {
                
                for(String t : texts)
                {
                    
                    //Adds wichever text you want skipping lines
                    buffer.write(appendLine(t,1)); 
                }
            
                buffer.flush();
            }
            
            return true;
        }
        catch(java.io.IOException ex)
        {
            System.out.println(String.format("It was not possible to read the file. Message: %s", ex.getMessage()));
        }
        
        return false;
    }
    
    public static String appendLine(String text, int lineNumber)
    {
        StringBuilder strBuilder = new StringBuilder();
        
        for(int i = 0; i < lineNumber; i++)
            strBuilder.append(System.getProperty("line.separator")); //Add a line breaker in the end
        
        return String.format("%s%s", text, strBuilder.toString()); // Returns the text with the breaks
    }
    
    public static void addText(ArrayList<String> list){
        texts.addAll(list);
    }
    
    public static void addText(String text){
        texts.add(text);
    }
    
    public static boolean isEmpty(){
        return texts.isEmpty();
    }
    
    public static void clear(){
        texts.clear();
    }
}
