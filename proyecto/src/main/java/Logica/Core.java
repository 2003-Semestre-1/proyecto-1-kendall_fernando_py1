/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica;

import java.util.ArrayList;
import org.json.simple.JSONObject;

/**
 *
 * @author Kendall Tenorio Chevez
 */
public class Core {
    private String name = "";
    private ArrayList<String> availableRegisters = new ArrayList<String>() ;
    private JSONObject timeAndMemorySizeConfig = new JSONObject();
    private Process currentProcess = null;
    private int remainingInsTime = 0;
    private String currentInstruction = "";
    private int countInstruction = 0;
    private ArrayList<String> instructions = new ArrayList<>();
    private boolean flag = false;
    
    private Computer compu;
    
    private boolean tempDX = false;
    
    public Core(JSONObject p_timeAndMemorySizeConfig,Computer p_compu,String coreName){
        this.timeAndMemorySizeConfig = p_timeAndMemorySizeConfig;
        this.compu = p_compu;
        this.name=coreName;
    }

    public Process getCurrentProcess() {
        availableRegisters.add("AC");
        availableRegisters.add("AX");
        availableRegisters.add("BX");
        availableRegisters.add("CX");
        availableRegisters.add("DX");
        return currentProcess;
    }

    public void setCurrentProcess(Process p_currentProcess) {
        this.currentProcess = p_currentProcess;
        this.instructions = this.currentProcess.getInstructions();
        this.countInstruction = 0;
        this.currentInstruction = this.instructions.get(countInstruction);
        this.remainingInsTime = getTimeForInstruction(this.currentInstruction);
        this.currentProcess.setEstado("ejecucion");
        
    }

    public int getRemainingInsTime() {
        return remainingInsTime;
    }

    public void setRemainingInsTime(int remainingInsTime) {
        this.remainingInsTime = remainingInsTime;
    }

    public String getCurrentInstruction() {
        return currentInstruction;
    }

    public void setCurrentInstruction(String currentInstruction) {
        this.currentInstruction = currentInstruction;
    }
    
    public void increasetime(){
        if(currentProcess!=null){
            //aumentamo el tiempo de ejecucion 
            int executionTime = Integer.parseInt(this.currentProcess.getTranscuredTime());
            executionTime++;
            this.currentProcess.setTranscuredTime(String.valueOf(executionTime));
            //
            if(!this.currentProcess.getEstado().equals("en espera")){
                if(countInstruction<=instructions.size()-1){
                    if(remainingInsTime==0){
                        executeInstruction(this.currentInstruction);
                        if(!this.currentProcess.getEstado().equals("en espera")){
                            this.countInstruction++;
                            if(countInstruction<=instructions.size()-1){
                                this.currentInstruction = this.instructions.get(countInstruction);
                                this.remainingInsTime = getTimeForInstruction(this.currentInstruction);
                            }
                        }
                    }
                    else{
                        this.remainingInsTime--; 
                    }  
                }
                else{
                    this.currentProcess.setFinishTime(java.time.LocalTime.now().toString());
                    this.currentProcess.setEstado("finalizado");
                    this.currentProcess = null;

                }  
            } 
        }
    }
    
    
    public int getTimeForInstruction(String inst){
        String[] subinst = inst.split("\\s+|,");
        String type = subinst[0].toUpperCase();
        int res =0;
        try
        {
            res=(int)this.timeAndMemorySizeConfig.get(type);
        }
        catch(NullPointerException e)
        {
           
        }

        return res;
    }
    
    
    public void executeInstruction(String inst){
        String[] subinst = inst.replace(",", " ").split("\\s+");
        String type = subinst[0].toUpperCase();
        
        switch(type){
            case "LOAD":
                if(subinst.length == 2){
                    if(availableRegisters.contains(subinst[1].toUpperCase())){
                        String reg = subinst[1].toUpperCase();
                        if(reg.equals("AX")){
                            this.currentProcess.setAC(this.currentProcess.getAX());
                        }else if (reg.equals("BX")){
                            this.currentProcess.setAC(this.currentProcess.getBX());
                        }else if (reg.equals("CX")){
                            this.currentProcess.setAC(this.currentProcess.getCX());
                        }else{
                            this.currentProcess.setAC(this.currentProcess.getDX());
                        }
                    }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                break;
            case "STORE":
                if(subinst.length == 2){
                    if(availableRegisters.contains(subinst[1].toUpperCase())){
                        String reg = subinst[1].toUpperCase();
                        if(reg.equals("AX")){
                            this.currentProcess.setAX(this.currentProcess.getAC());
                        }else if (reg.equals("BX")){
                            this.currentProcess.setBX(this.currentProcess.getAC());
                        }else if (reg.equals("CX")){
                            this.currentProcess.setCX(this.currentProcess.getAC());
                        }else{
                            this.currentProcess.setDX(this.currentProcess.getAC());
                        }
                    }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                break;
            case "MOV":
                if(subinst.length == 3){
                    if(availableRegisters.contains(subinst[1].toUpperCase()) && (tryParseInt(subinst[2]) || availableRegisters.contains(subinst[2].toUpperCase())) ){
                        String reg1 = subinst[1].toUpperCase();
                        String reg2 = subinst[2].toUpperCase();
                        if(reg1.equals("AX")){
                            if(reg2.equals("AX")){
                                this.currentProcess.setAX(this.currentProcess.getAX());
                            }else if(reg2.equals("BX")){
                                this.currentProcess.setAX(this.currentProcess.getBX());
                            }else if(reg2.equals("CX")){
                                this.currentProcess.setAX(this.currentProcess.getCX());
                            }else if(reg2.equals("DX")){
                                this.currentProcess.setAX(this.currentProcess.getDX());
                            }else if(tryParseInt(reg2)){ //verifica si es un numero el tercer parametro
                                this.currentProcess.setAX(reg2);
                            }else{
                                this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                            }
                        }else if(reg1.equals("BX")){
                            if(reg2.equals("AX")){
                                this.currentProcess.setBX(this.currentProcess.getAX());
                            }else if(reg2.equals("BX")){
                                this.currentProcess.setBX(this.currentProcess.getBX());
                            }else if(reg2.equals("CX")){
                                this.currentProcess.setBX(this.currentProcess.getCX());
                            }else if(reg2.equals("DX")){
                                this.currentProcess.setBX(this.currentProcess.getDX());
                            }else if(tryParseInt(reg2)){ //verifica si es un numero el tercer parametro                                
                                this.currentProcess.setBX(reg2);
                            }else{
                                this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                            }
                        }else if(reg1.equals("CX")){
                            if(reg2.equals("AX")){
                                this.currentProcess.setCX(this.currentProcess.getAX());
                            }else if(reg2.equals("BX")){
                                this.currentProcess.setCX(this.currentProcess.getBX());
                            }else if(reg2.equals("CX")){
                                this.currentProcess.setCX(this.currentProcess.getCX());
                            }else if(reg2.equals("DX")){
                                this.currentProcess.setCX(this.currentProcess.getDX());
                            }else if(tryParseInt(reg2)){ //verifica si es un numero el tercer parametro
                                this.currentProcess.setCX(reg2);
                            }else{
                                this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                            }
                        }else{
                            if(reg2.equals("AX")){
                                this.currentProcess.setDX(this.currentProcess.getAX());
                            }else if(reg2.equals("BX")){
                                this.currentProcess.setDX(this.currentProcess.getBX());
                            }else if(reg2.equals("CX")){
                                this.currentProcess.setDX(this.currentProcess.getCX());
                            }else if(reg2.equals("DX")){
                                this.currentProcess.setDX(this.currentProcess.getDX());
                            }else if(tryParseInt(reg2)){//verifica si es un numero el tercer parametro
                                this.currentProcess.setDX(reg2);
                            }else{
                                this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                            }
                        }
                    }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                break;
            case "ADD":
                if(subinst.length == 2){
                    if(availableRegisters.contains(subinst[1].toUpperCase())){
                        int valAC = Integer.parseInt(this.currentProcess.getAC());
                        String reg = subinst[1].toUpperCase();
                        int valreg = 0;
                        if(reg.equals("AX")){
                            valreg = Integer.parseInt(this.currentProcess.getAX());
                        }else if (reg.equals("BX")){
                            valreg = Integer.parseInt(this.currentProcess.getBX());
                        }else if (reg.equals("CX")){
                            valreg = Integer.parseInt(this.currentProcess.getCX());
                        }else{
                           valreg = Integer.parseInt(this.currentProcess.getDX());
                        }
                        int res = valAC + valreg;
                        this.currentProcess.setAC(String.valueOf(res));
                    }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                }else{
                        this.compu.sendMessagetoOutput("invalid instruction: " +inst);
                    }
                break;
            case "SUB": 
                if(subinst.length == 2){
                    if(availableRegisters.contains(subinst[1].toUpperCase())){
                        int valAC = Integer.parseInt(this.currentProcess.getAC());
                        String reg = subinst[1].toUpperCase();
                        int valreg = 0;
                        if(reg.equals("AX")){
                            valreg = Integer.parseInt(this.currentProcess.getAX());
                        }else if (reg.equals("BX")){
                            valreg = Integer.parseInt(this.currentProcess.getBX());
                        }else if (reg.equals("CX")){
                            valreg = Integer.parseInt(this.currentProcess.getCX());
                        }else{
                           valreg = Integer.parseInt(this.currentProcess.getDX());
                        }
                        int res = valAC - valreg;
                        this.currentProcess.setAC(String.valueOf(res));
                    }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                break;
            case "INC":
                if(subinst.length == 1){
                    int valAC = Integer.parseInt(this.currentProcess.getAC());
                    int res = valAC + 1 ;
                    this.currentProcess.setAC(String.valueOf(res));
                }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                break;
            case "DEC":  
                if(subinst.length == 1){
                    int valAC = Integer.parseInt(this.currentProcess.getAC());
                    int res = valAC - 1 ;
                    this.currentProcess.setAC(String.valueOf(res));
                }else{
                    this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                }
                break;
            case "INT":
                if(subinst.length == 2){
                    this.currentProcess.setEstado("esperando");
                    compu.addProcessToWait(this.currentProcess);
                    if(subinst[1].toUpperCase().equals("20H")){
                        this.countInstruction=this.instructions.size();
                    }else if(subinst[1].toUpperCase().equals("09H")){
                        compu.sendMessagetoOutput(this.name+ " / DX: ");
                        compu.sendMessagetoOutput(this.name+ " / ENTER para continuar");
                    }else if(subinst[1].toUpperCase().equals("10H")){
                        compu.sendMessagetoOutput(this.name+ this.currentProcess.getDX());
                        this.tempDX=true;
                    }else if(subinst[1].toUpperCase().equals("21H")){
                        this.countInstruction=this.instructions.size();
                    }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }   
                }else{
                    this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                }
                break;
            case "JMP":  
                if(subinst.length == 2){
                   if(tryParseInt(subinst[1])){
                       int jump = Integer.parseInt(subinst[1]);
                       if(jump>0){
                           if(jump + this.countInstruction < this.instructions.size()){
                               this.countInstruction+=jump-1;
                           }else{
                               this.compu.sendMessagetoOutput("Fallo de segmentacion: " +inst);
                           }
                       }else{
                           if(this.countInstruction + jump > 0){
                               this.countInstruction+=jump-1;
                           }else{
                               this.compu.sendMessagetoOutput("Fallo de segmentacion: " +inst);
                           }
                       }
                   }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                }else{
                    this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                }
                break;
            case "CMP":
                if(subinst.length == 3){
                   if(availableRegisters.contains(subinst[1].toUpperCase()) && 
                      availableRegisters.contains(subinst[2].toUpperCase())){
                       int val1;
                       int val2;
                       String reg1 = subinst[1].toUpperCase();
                       String reg2 = subinst[2].toUpperCase();
                       //buscar el valor de el primer registro
                       if(reg1.equals("AX")){
                            val1 = Integer.parseInt(this.currentProcess.getAX());
                        }else if (reg1.equals("BX")){
                            val1 = Integer.parseInt(this.currentProcess.getBX());
                        }else if (reg1.equals("CX")){
                           val1 = Integer.parseInt(this.currentProcess.getCX());
                        }else{
                           val1 = Integer.parseInt(this.currentProcess.getDX());
                        }
                       //buscar el valor de el segundo registro
                       if(reg2.equals("AX")){
                                val2 = Integer.parseInt(this.currentProcess.getAX());
                        }else if (reg2.equals("BX")){
                            val2 = Integer.parseInt(this.currentProcess.getBX());
                        }else if (reg2.equals("CX")){
                           val2 = Integer.parseInt(this.currentProcess.getCX());
                        }else{
                           val2 = Integer.parseInt(this.currentProcess.getDX());
                        }
                       if(val1 == val2){
                           this.flag = true;
                       }else{
                           this.flag = false;
                       }
                   }else{
                       this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                   }
                }else{
                    this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                }
                break;
            case "JE":
                if(subinst.length == 2){
                    if(tryParseInt(subinst[1])){
                       int jump = Integer.parseInt(subinst[1]);
                       if(this.flag){
                           if(jump>0){
                                if(jump + this.countInstruction < this.instructions.size()){
                                    this.countInstruction+=jump-1;
                                }else{
                                    this.compu.sendMessagetoOutput("Fallo de segmentacion: " +inst);
                                }
                            }else{
                                if(this.countInstruction + jump > 0){
                                    this.countInstruction+=jump-1;
                                }else{
                                    this.compu.sendMessagetoOutput("Fallo de segmentacion: " +inst);
                                }
                            }
                       }
                    }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                }else{
                    this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                }
                break;
            case "JNE":
                if(subinst.length == 2){
                    if(tryParseInt(subinst[1])){
                       int jump = Integer.parseInt(subinst[1]);
                       if(!this.flag){
                           if(jump>0){
                                if(jump + this.countInstruction < this.instructions.size()){
                                    this.countInstruction+=jump-1;
                                }else{
                                    this.compu.sendMessagetoOutput("Fallo de segmentacion: " +inst);
                                }
                            }else{
                                if(this.countInstruction + jump > 0){
                                    this.countInstruction+=jump-1;
                                }else{
                                    this.compu.sendMessagetoOutput("Fallo de segmentacion: " +inst);
                                }
                            }
                       }
                    }else{
                        this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                    }
                }else{
                    this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                }
                break;
            case "PUSH":
                if(subinst.length == 2){
                    if(availableRegisters.contains(subinst[1].toUpperCase())){
                        if(this.currentProcess.getPila().size()<=4){
                            String reg = subinst[1].toUpperCase();
                            if(reg.equals("AX")){
                                this.currentProcess.getPila().add(this.currentProcess.getAX());
                            }else if (reg.equals("BX")){
                                this.currentProcess.getPila().add(this.currentProcess.getBX());
                            }else if (reg.equals("CX")){
                               this.currentProcess.getPila().add(this.currentProcess.getCX());
                            }else{
                               this.currentProcess.getPila().add(this.currentProcess.getDX());
                            }
                        }
                    }else{
                        this.compu.sendMessagetoOutput("La cola esta llena");
                    }
                }
                else{
                    this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                }
                break;
            case "POP":
                if(subinst.length == 2){
                    if(availableRegisters.contains(subinst[1].toUpperCase())){
                        if(this.currentProcess.getPila().size()>0){
                            String reg = subinst[1].toUpperCase();
                            String val = this.currentProcess.getPila().get(0);
                            this.currentProcess.getPila().remove(0);
                            if(reg.equals("AX")){
                                this.currentProcess.setAX(val);
                            }else if (reg.equals("BX")){
                                this.currentProcess.setBX(val);
                            }else if (reg.equals("CX")){
                               this.currentProcess.setCX(val);
                            }else{
                               this.currentProcess.setDX(val);
                            }
                        }
                    }else{
                        this.compu.sendMessagetoOutput("La cola esta Vacia");
                    }
                }
                else{
                    this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                }
                break;
            case "PARAM": 
                if(subinst.length >= 2 && subinst.length <= 4){
                    if(subinst.length == 2){
                       String val = subinst[1].toUpperCase();
                       if(tryParseInt(val)){
                            if(this.currentProcess.getPila().size()<=4){
                                this.currentProcess.getPila().add(val);
                            }else{
                                this.compu.sendMessagetoOutput("La cola esta llena.");
                            }
                       }else{
                           this.compu.sendMessagetoOutput("Parametro invalido: " + val);
                       }
                    }
                    if(subinst.length == 3){
                       String val = subinst[1].toUpperCase();
                       String val2 = subinst[2].toUpperCase();
                       if(tryParseInt(val) && tryParseInt(val2)){
                           if(this.currentProcess.getPila().size()<=3){
                                this.currentProcess.getPila().add(val);
                                this.currentProcess.getPila().add(val2);
                            }else{
                                this.compu.sendMessagetoOutput("La cola esta llena.");
                            }
                       }else{
                           this.compu.sendMessagetoOutput("Parametro invalido: " + val);
                       }
                    }
                    if(subinst.length == 4){
                       String val = subinst[1].toUpperCase();
                       String val2 = subinst[2].toUpperCase();
                       String val3 = subinst[3].toUpperCase();
                       if(tryParseInt(val) && tryParseInt(val2) && tryParseInt(val3)){
                           if(this.currentProcess.getPila().size()<=2){
                                this.currentProcess.getPila().add(val);
                                this.currentProcess.getPila().add(val2);
                                this.currentProcess.getPila().add(val3);
                            }else{
                                this.compu.sendMessagetoOutput("La cola esta llena.");
                            }
                       }else{
                           this.compu.sendMessagetoOutput("Parametro invalido: " + val);
                       }
                    } 
                }
                else{
                    this.compu.sendMessagetoOutput("Instruccion invalida: " +inst);
                }
                break;
            default:
                break;
        }
        
        
    }
    
    
    boolean tryParseInt(String value) {  
     try {  
         Integer.parseInt(value);  
         return true;  
      } catch (NumberFormatException e) {  
         return false;  
      }  
    }
    
    
    public void continueExecution(String entrada){
        if(tempDX){
            if(entrada.equals("")){
                this.currentProcess.setDX("0");
            }else{
                this.currentProcess.setDX(entrada);
            }
            
            this.tempDX=false;
        }
        this.currentProcess.setEstado("ejecucion");
        this.countInstruction++;
        if(countInstruction<=instructions.size()-1){
            this.currentInstruction = this.instructions.get(countInstruction);
            this.remainingInsTime = getTimeForInstruction(this.currentInstruction);
        }
    }
}
