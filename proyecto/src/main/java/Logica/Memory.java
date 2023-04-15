/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica;

import java.util.ArrayList;
/**
 *
 * @author Kendall Tenorio Chevez
 */
public class Memory {
    int memorySize = 0;
    int memoryUsed = 0;
    int porcentMemory = 0; // memoryUsed * 100 / memorySize
    ArrayList<Process> instructions = new ArrayList<Process>();
    
    public Memory(int p_sizeOfMemory){
       this.memorySize = p_sizeOfMemory;
    }
    
    
    // verifica si tiene memoria sufiente para insertar en el arreglo
    public boolean canInsert(Process instructions){
        if(memoryUsed + instructions.getSizeOfInstructions()<= memorySize){
            return true;
        }else{
            return false; 
        }
    }
    
    //inserta en la lista de procesos que esta ejecutando la memoria
    public void insertInstructions (Process p_instructions){
        this.instructions.add(p_instructions);
        this.memoryUsed+=p_instructions.getSizeOfInstructions();
        memoryUsed();
    }
    
    public void removeInstructions(int sizeOfInstructions){
        this.memoryUsed-=sizeOfInstructions;
        memoryUsed();
    }
    
    
    public  ArrayList<Process> getInstrucctionsByProcess(){
        return this.instructions;
    }
    
    //Ejecuta la memoria usada 
    public void memoryUsed(){
        porcentMemory = (memoryUsed *100 / memorySize);
    }
    
    
    // obtiene el porcentaje de memoria usada 
    public int getMemoryUsed(){
       return this.porcentMemory;
    }
}
