/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica;

import Interfaz.Estadisticas;
import java.io.File;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;
/**
 *
 * @author Kendall Tenorio Chevez
 */
public class Computer {
     /*Files*/
    private ArrayList<File> loadFiles = new ArrayList<>();
    
    /*System*/
    private JSONObject timeAndMemorySizeConfig = new JSONObject();
    private int executionTime = 0;
    private String output = "";
    ArrayList<Color> colorList = new ArrayList<Color>();
    
    /*Process*/
    private ArrayList<Process> processList = new ArrayList<Process>();
    private int processId = 0;
    
    /*Memory*/
    private int lastIdProcessLoad = 0;
    private Memory mainMemory = new Memory(256);
    private Memory hardDisk = new Memory(512);
    
    /*Cores*/
    private Core core1 = new Core(timeAndMemorySizeConfig,this,"core1");
    private Core core2 = new Core(timeAndMemorySizeConfig,this,"core2");
    
    private ArrayList<Process> waitProcess = new ArrayList<Process>();
    
    private boolean inputWait = false;
    
   
    public Computer(){
        setMemoryConfig();      //Se establece la memoria principal
        //System.out.println(timeAndMemorySizeConfig.get("STORE"));
    }

    /* Establece los arvhicos que se cargan en la aplicacion */
    public void addFile(File p_file){
        loadFiles.add(p_file);        
    }
    
    public int getSizeOfLoadFiles (){
        return loadFiles.size();
    }
    
    /* Establece la configuracion de los tiempos de ejecucion y la memoria */
    public void setMemoryConfig(){        
        timeAndMemorySizeConfig.put("LOAD", 2);
        timeAndMemorySizeConfig.put("STORE", 2);
        timeAndMemorySizeConfig.put("MOV", 1);
        timeAndMemorySizeConfig.put("ADD", 3);
        timeAndMemorySizeConfig.put("SUB", 3);
        timeAndMemorySizeConfig.put("INC", 1);
        timeAndMemorySizeConfig.put("INT", 0);
        timeAndMemorySizeConfig.put("DEC", 1);
        timeAndMemorySizeConfig.put("SWAP", 1);
        timeAndMemorySizeConfig.put("CMP", 2);
        timeAndMemorySizeConfig.put("JMP", 2);
        timeAndMemorySizeConfig.put("JE", 2);
        timeAndMemorySizeConfig.put("JNE", 2);
        timeAndMemorySizeConfig.put("PUSH", 1);
        timeAndMemorySizeConfig.put("POP", 1);
        timeAndMemorySizeConfig.put("PARAM", 3);        
    }
    
    /*incremente el tiempo de ejecuccion del la computadora*/
    public void increaseTime(){
        executionTime++;
    }
    
    /*Obtiene el tiempo de ejeuccion de la computadora*/
    public int getExecutionTime(){
        return executionTime;
    }
    
    /*Envia mensajes al output*/
    public void sendMessagetoOutput(String message){
        output+= "> " + message + "\n";
    }
    
    //Obtiene la informacion del output
    public String getOutput(){
        return output;
    }
    
    public Memory getMainMemory(){
        return this.mainMemory;
    }
    public void setMainMemory(int tamano){
        this.mainMemory.memorySize=tamano;
    }
    
    public Memory getHardDiskMemory(){
        return this.hardDisk;
    }
    
    public void setHardDiskMemory(int tamano){
        this.hardDisk.memorySize=tamano;
    }
    
    // carga los al planificador de processos
    public void loadProcessfromFile(){
        for (int i = 0; i < loadFiles.size(); i++) {
            Process tempProcess = new Process(processId,"nuevo",loadFiles.get(i).getName(),loadFiles.get(i).getPath(),java.time.LocalTime.now().toString());
            processId++;
            processList.add(tempProcess);
        }
        loadNewProcessToMemory();
    }
    
    public void loadNewProcessToMemory(){
        if(lastIdProcessLoad<processList.size() && hardDisk.getInstrucctionsByProcess().size()==0){
            while(mainMemory.canInsert(processList.get(lastIdProcessLoad))){
                mainMemory.insertInstructions(processList.get(lastIdProcessLoad));
                processList.get(lastIdProcessLoad).setEstado("preparado");

                lastIdProcessLoad++;
                if(lastIdProcessLoad>=processList.size()){
                    break;
                }
            }
            
        }
        
        if(lastIdProcessLoad<processList.size()){
            
            while(hardDisk.canInsert(processList.get(lastIdProcessLoad))){
                hardDisk.insertInstructions(processList.get(lastIdProcessLoad));
                processList.get(lastIdProcessLoad).setEstado("nuevo");

                lastIdProcessLoad++;
                if(lastIdProcessLoad>=processList.size()){
                    break;
                }
            }
         }
        
        addProcesstoQueue();
    }
     
    // obtiene la lista de los procesos
    public ArrayList<Process> getProcessList(){
        return processList;
    }
    
    public void addProcesstoQueue(){
        ArrayList<Process> processInMainMemory = mainMemory.getInstrucctionsByProcess();
         for (int i = 0; i < processInMainMemory.size(); i++) {
            if(processInMainMemory.get(i).getCPU()<=0){
                Random rand = new Random();
                int selectedCore = rand.nextInt( 2 );
                processInMainMemory.get(i).setCPU(selectedCore+1);
                
                if(selectedCore == 0){
                    if(core1.getCurrentProcess() == null){
                        core1.setCurrentProcess(processInMainMemory.get(i));

                    }
                }else{
                    if(core2.getCurrentProcess() == null){
                        core2.setCurrentProcess(processInMainMemory.get(i));
                    }
                }
                
            }
        }
    }
    
    
    public Color getNewColor(){
        Random rand = new Random();
        float r;
        float g;
        float b;
        Color newColor;
        boolean similarFound = false;
        
        do{
            r = (float) (rand.nextFloat() / 2f + 0.5);
            g = (float) (rand.nextFloat() / 2f + 0.5);
            b = (float) (rand.nextFloat() / 2f + 0.5);
            newColor = new Color(r, g, b, 1);
            
            for(Color color : colorList){
                
                if(color.getRGB()== newColor.getRGB()){
                    similarFound = true;
                    break;
                }
            }
        }while(similarFound);
        
        return newColor;  
    }
    
    public Core getCore1(){
        return core1;
    }
    
    public Core getCore2(){
        return core2;
    }
    
    public void removeProcessFromMainMemory(int core){
        ArrayList<Process> processInMainMemory = mainMemory.getInstrucctionsByProcess();
        for (int i = 0; i < processInMainMemory.size(); i++) {
            if(processInMainMemory.get(i).getCPU()==core){
                mainMemory.removeInstructions(processInMainMemory.get(i).getSizeOfInstructions());
                processInMainMemory.remove(i);
                break;
            }
        }
    }
    
    public void bringProccesFromHarkDisk(){
        ArrayList<Process> processInHardMemory = hardDisk.getInstrucctionsByProcess();
        if(processInHardMemory.size()>0){
             Process temp = processInHardMemory.get(0);
             if(mainMemory.canInsert(temp)){
                mainMemory.insertInstructions(temp);
                hardDisk.removeInstructions(processInHardMemory.get(0).getSizeOfInstructions());
                processInHardMemory.remove(0);
                addProcesstoQueue();
                loadNewProcessToMemory();
             }
             
        }
    }
    
    public void setNewProcessToCore(int core){
        ArrayList<Process> processInMainMemory = mainMemory.getInstrucctionsByProcess();
        for (int i = 0; i < processInMainMemory.size(); i++) {
            if(processInMainMemory.get(i).getCPU()==core){
                if(core==1){
                    core1.setCurrentProcess(processInMainMemory.get(i));
                    break;
                }else{
                    core2.setCurrentProcess(processInMainMemory.get(i));
                    break;
                }
            }
        }
    }
    
    public void nextInstruction(){
        core1.increasetime();
        if(core1.getCurrentProcess()==null){
            removeProcessFromMainMemory(1);
            setNewProcessToCore(1);
            bringProccesFromHarkDisk();
        }
        core2.increasetime();
        if(core2.getCurrentProcess()==null){
            removeProcessFromMainMemory(2);
            setNewProcessToCore(2);
            bringProccesFromHarkDisk();
        }
       
        increaseTime();
        
    }
    
    
    public boolean finishProgram(){
        boolean flag = true;
        for(int i = 0 ;  i< processList.size(); i++){
            if(!processList.get(i).getEstado().equals("finalizado")){
                flag = false;
            }
        }
        if(flag){
            Estadisticas stats = new Estadisticas();
            stats.setResultProcess(this.processList);
            stats.fillTable();
            stats.setVisible(true);
        }
        return flag; 
    }

    public ArrayList<Process> getWaitProcess() {
        return waitProcess;
    }

    public void setWaitProcess(ArrayList<Process> waitProcess) {
        this.waitProcess = waitProcess;
    }
    
    public void addProcessToWait(Process p_process){
        this.waitProcess.add(p_process);
    }

    public boolean isInputWait() {
        return inputWait;
    }

    public void setInputWait(boolean inputWait) {
        this.inputWait = inputWait;
    }
    
    
    
    public void keyboardEnter(String entrada){
        if(waitProcess.size()!=0){
            this.inputWait = true;
            if(waitProcess.get(0).getCPU()==1){
                core1.continueExecution(entrada);
                waitProcess.remove(0);
            }else{
                core2.continueExecution(entrada);
                waitProcess.remove(0);
            }
        }
    }
}
