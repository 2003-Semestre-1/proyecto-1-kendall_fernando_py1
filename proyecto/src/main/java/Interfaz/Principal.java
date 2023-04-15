/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Interfaz;

import java.awt.Color;
import Logica.Computer;
import Logica.Memory;
import Logica.Process;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author kenda
 */
public class Principal extends javax.swing.JFrame {
    DefaultTableModel modelFiles; 
    DefaultTableModel modelProcess; 
    DefaultTableModel modelMainMemory; 
    DefaultTableModel modelHardMemory; 
    DefaultTableModel modelQueue;
    Computer compu =  new Computer();
    /**
     * Creates new form Principal
     */
    public Principal() {
        try{
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
        catch (Exception e)
        {
         e.printStackTrace();
        }
        initComponents();
        setModels();
        this.pack();        
    }
    public void setModels(){
        modelFiles = (DefaultTableModel) tblArchivo.getModel();
        modelProcess = (DefaultTableModel) tblProcesos.getModel();
        modelMainMemory =  (DefaultTableModel) tblMemoria.getModel();
        modelHardMemory =  (DefaultTableModel) tblDisco.getModel();
        modelQueue = (DefaultTableModel) tblCola.getModel();
    }
    
    public void actualizarElementosVisuales(){
        tiempoEjecucion.setText(String.valueOf(compu.getExecutionTime()));
        txtSalida.setText(compu.getOutput());
        updateProcessTable();
        
        updateMainMemoryTable();
        updateHardMemoryTable();
        updateQueueTable();
        updateCore1Table();
        updateCore2Table();
//        this.repaint();
        this.pack();
    }
    public void updateProcessTable(){
        clearTable(modelProcess);
        ArrayList<Process> processList = compu.getProcessList();

        for (int i=0; i<processList.size();i++){
            Process temp = processList.get(i);
            Object[] objectData = new Object[] { 
                String.valueOf(temp.getID()),
                temp.getName(),
                temp.getEstado(),
                temp.getAC(),
                temp.getAX(),
                temp.getBX(),
                temp.getCX(),
                temp.getDX()
                        };
            modelProcess.addRow(objectData);
            
        }
        tblProcesos.setModel(modelProcess);
     
    }
    public void updateHardMemoryTable(){
        clearTable(modelHardMemory);
        Memory tempMemory = compu.getHardDiskMemory();
         ArrayList<Process> processInMemory = tempMemory.getInstrucctionsByProcess();
        
        for (int i=0; i<processInMemory.size();i++){
            
            Process temp = processInMemory.get(i);
            ArrayList<String> instrucString = temp.getInstructions();
            
            for (int j=0; j<instrucString.size();j++){
                Object[] objectData = new Object[] { 
                    String.valueOf(temp.getID()),
                    instrucString.get(j),
                };
                modelHardMemory.addRow(objectData);
                
            } 
        }
        tblDisco.setModel(modelHardMemory);
        
        int porcentMemory = tempMemory.getMemoryUsed();
        porcentajeDisco.setText(String.valueOf(porcentMemory) + "%");
        if(porcentMemory<70){
            porcentajeDisco.setForeground(Color.GREEN);
        }else{
            porcentajeDisco.setForeground(Color.RED);
        }
    }
    public void updateMainMemoryTable(){
        clearTable(modelMainMemory);
        Memory tempMemory = compu.getMainMemory();
        ArrayList<Process> processInMemory = tempMemory.getInstrucctionsByProcess();
        
        for (int i=0; i<processInMemory.size();i++){
            
            Process temp = processInMemory.get(i);
            ArrayList<String> instrucString = temp.getInstructions();
            
            for (int j=0; j<instrucString.size();j++){
                Object[] objectData = new Object[] { 
                    String.valueOf(temp.getID()),
                    instrucString.get(j),
                };
                modelMainMemory.addRow(objectData);
                
            } 
        }
        tblMemoria.setModel(modelMainMemory);
        
        int porcentMemory = tempMemory.getMemoryUsed();
        porcentajeMemoria.setText(String.valueOf(porcentMemory) + "%");
        if(porcentMemory<70){
            porcentajeMemoria.setForeground(Color.GREEN);
        }else{
            porcentajeMemoria.setForeground(Color.RED);
        }
    }
    public void updateQueueTable(){
        clearTable(modelQueue);
        Memory tempMemory = compu.getMainMemory();
        ArrayList<Process> processInMemory = tempMemory.getInstrucctionsByProcess();
        
        for (int i=0; i<processInMemory.size();i++){
            
            Process temp = processInMemory.get(i);
            ArrayList<String> instrucString = temp.getInstructions();
            
            for (int j=0; j<instrucString.size();j++){
                Object[] objectData = new Object[] { 
                    String.valueOf(temp.getID()),
                    temp.getCPU(),
                    instrucString.get(j),
                };
                modelQueue.addRow(objectData);
                
            } 
        }
        tblCola.setModel(modelQueue);
    }
    public void updateCore1Table(){
        if(compu.getCore1().getCurrentProcess()!= null){
            ci1.setText(compu.getCore1().getCurrentInstruction());
            cp1.setText(String.valueOf(compu.getCore1().getCurrentProcess().getID()));
            tr1.setText(String.valueOf(compu.getCore1().getRemainingInsTime()));
        }
        else{
            ci1.setText("");
            cp1.setText("");
            tr1.setText("");
        }
    }
    
    public void updateCore2Table(){
        if(compu.getCore2().getCurrentProcess()!= null){
            ci2.setText(compu.getCore2().getCurrentInstruction());
            cp2.setText(String.valueOf(compu.getCore2().getCurrentProcess().getID()));
            tr2.setText(String.valueOf(compu.getCore2().getRemainingInsTime()));
        }else{
            ci2.setText("");
            cp2.setText("");
            tr2.setText("");
        }
        
    }
    private void clearTable(DefaultTableModel p_model){
        if(p_model.getRowCount()>0){
            int countrows=p_model.getRowCount();
            for (int i = countrows; i> 0;i--){
                p_model.removeRow(i-1);
            }
        }
    }
    
    private void ejecutarPrograma() throws InterruptedException{
        while(true){
            if(!compu.finishProgram()){
                compu.nextInstruction();
                actualizarElementosVisuales();
            }
            else{
                break;
            }
            Thread.sleep(1000);
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Core1 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        cp1 = new javax.swing.JLabel();
        tr1 = new javax.swing.JLabel();
        ci1 = new javax.swing.JLabel();
        Core2 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        ci2 = new javax.swing.JLabel();
        cp2 = new javax.swing.JLabel();
        tr2 = new javax.swing.JLabel();
        panelTerminal = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtTerminal = new javax.swing.JTextField();
        panelSalida = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtSalida = new javax.swing.JTextPane();
        panelCola = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane11 = new javax.swing.JScrollPane();
        tblCola = new javax.swing.JTable();
        panelProceso = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblProcesos = new javax.swing.JTable();
        panelMemoria = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        porcentajeMemoria = new javax.swing.JLabel();
        jScrollPane12 = new javax.swing.JScrollPane();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblMemoria = new javax.swing.JTable();
        panelDiscoDuro = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        porcentajeDisco = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        tblDisco = new javax.swing.JTable();
        panelTiempo = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tiempoEjecucion = new javax.swing.JLabel();
        btnSiguiente = new javax.swing.JButton();
        panelArchivos = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblArchivo = new javax.swing.JTable();
        panelBotones = new javax.swing.JPanel();
        btnArchivo = new javax.swing.JButton();
        btnEjecutar = new javax.swing.JButton();
        btnStep = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        panelConfiguracion = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtMemoria = new javax.swing.JTextField();
        txtDisco = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setForeground(java.awt.Color.white);
        setResizable(false);

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(69, 90, 100));
        jLabel16.setText("Core 1");

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(69, 90, 100));
        jLabel19.setText("Proceso ID:");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(69, 90, 100));
        jLabel18.setText("Instruccion:");

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(69, 90, 100));
        jLabel20.setText("Tiempo restante:");

        cp1.setBackground(new java.awt.Color(255, 255, 255));
        cp1.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N

        tr1.setBackground(new java.awt.Color(255, 255, 255));
        tr1.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N

        ci1.setBackground(new java.awt.Color(255, 255, 255));
        ci1.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N

        javax.swing.GroupLayout Core1Layout = new javax.swing.GroupLayout(Core1);
        Core1.setLayout(Core1Layout);
        Core1Layout.setHorizontalGroup(
            Core1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Core1Layout.createSequentialGroup()
                .addGroup(Core1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Core1Layout.createSequentialGroup()
                        .addGap(108, 108, 108)
                        .addComponent(jLabel20))
                    .addGroup(Core1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16)
                        .addGap(24, 24, 24)
                        .addGroup(Core1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19))
                        .addGap(67, 67, 67)
                        .addGroup(Core1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ci1, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                            .addComponent(cp1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tr1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Core1Layout.setVerticalGroup(
            Core1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Core1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(Core1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(Core1Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(22, 22, 22)
                        .addGroup(Core1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel16)
                            .addComponent(jLabel18)))
                    .addGroup(Core1Layout.createSequentialGroup()
                        .addComponent(cp1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ci1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(Core1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tr1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 24)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(69, 90, 100));
        jLabel21.setText("Core 2");

        jLabel22.setBackground(new java.awt.Color(255, 255, 255));
        jLabel22.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(69, 90, 100));
        jLabel22.setText("Proceso ID:");

        jLabel23.setBackground(new java.awt.Color(255, 255, 255));
        jLabel23.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(69, 90, 100));
        jLabel23.setText("Instruccion:");

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(69, 90, 100));
        jLabel24.setText("Tiempo restante:");

        ci2.setBackground(new java.awt.Color(255, 255, 255));
        ci2.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N

        cp2.setBackground(new java.awt.Color(255, 255, 255));
        cp2.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N

        tr2.setBackground(new java.awt.Color(255, 255, 255));
        tr2.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N

        javax.swing.GroupLayout Core2Layout = new javax.swing.GroupLayout(Core2);
        Core2.setLayout(Core2Layout);
        Core2Layout.setHorizontalGroup(
            Core2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Core2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21)
                .addGap(33, 33, 33)
                .addGroup(Core2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24)
                    .addComponent(jLabel22))
                .addGap(27, 27, 27)
                .addGroup(Core2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ci2, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                    .addComponent(cp2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tr2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Core2Layout.setVerticalGroup(
            Core2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Core2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(Core2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(cp2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(Core2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel23)
                    .addComponent(ci2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(Core2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tr2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(69, 90, 100));
        jLabel10.setText("Terminal");

        javax.swing.GroupLayout panelTerminalLayout = new javax.swing.GroupLayout(panelTerminal);
        panelTerminal.setLayout(panelTerminalLayout);
        panelTerminalLayout.setHorizontalGroup(
            panelTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTerminalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTerminalLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtTerminal))
                .addContainerGap())
        );
        panelTerminalLayout.setVerticalGroup(
            panelTerminalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTerminalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTerminal, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(69, 90, 100));
        jLabel11.setText("Salida");

        jScrollPane5.setViewportView(txtSalida);

        javax.swing.GroupLayout panelSalidaLayout = new javax.swing.GroupLayout(panelSalida);
        panelSalida.setLayout(panelSalidaLayout);
        panelSalidaLayout.setHorizontalGroup(
            panelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalidaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelSalidaLayout.setVerticalGroup(
            panelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalidaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(69, 90, 100));
        jLabel15.setText("Cola");

        tblCola.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Proceso ID", "Core", "Instruccion"
            }
        ));
        jScrollPane11.setViewportView(tblCola);
        if (tblCola.getColumnModel().getColumnCount() > 0) {
            tblCola.getColumnModel().getColumn(0).setMinWidth(100);
            tblCola.getColumnModel().getColumn(0).setMaxWidth(100);
            tblCola.getColumnModel().getColumn(1).setMinWidth(100);
            tblCola.getColumnModel().getColumn(1).setMaxWidth(100);
        }

        javax.swing.GroupLayout panelColaLayout = new javax.swing.GroupLayout(panelCola);
        panelCola.setLayout(panelColaLayout);
        panelColaLayout.setHorizontalGroup(
            panelColaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelColaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelColaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        panelColaLayout.setVerticalGroup(
            panelColaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelColaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(69, 90, 100));
        jLabel9.setText("Procesos");

        tblProcesos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Nombre", "Estado", "AC", "AX", "BX", "CX", "DX"
            }
        ));
        jScrollPane4.setViewportView(tblProcesos);
        if (tblProcesos.getColumnModel().getColumnCount() > 0) {
            tblProcesos.getColumnModel().getColumn(0).setMinWidth(50);
            tblProcesos.getColumnModel().getColumn(0).setMaxWidth(50);
        }

        javax.swing.GroupLayout panelProcesoLayout = new javax.swing.GroupLayout(panelProceso);
        panelProceso.setLayout(panelProcesoLayout);
        panelProcesoLayout.setHorizontalGroup(
            panelProcesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProcesoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelProcesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelProcesoLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4))
                .addContainerGap())
        );
        panelProcesoLayout.setVerticalGroup(
            panelProcesoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelProcesoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(69, 90, 100));
        jLabel13.setText("Memoria Principal");

        porcentajeMemoria.setBackground(new java.awt.Color(255, 255, 255));
        porcentajeMemoria.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        porcentajeMemoria.setForeground(new java.awt.Color(69, 90, 100));
        porcentajeMemoria.setText("0%");

        tblMemoria.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Proceso ID", "Instruccion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(tblMemoria);
        if (tblMemoria.getColumnModel().getColumnCount() > 0) {
            tblMemoria.getColumnModel().getColumn(0).setMinWidth(80);
            tblMemoria.getColumnModel().getColumn(0).setMaxWidth(80);
            tblMemoria.getColumnModel().getColumn(1).setMinWidth(170);
            tblMemoria.getColumnModel().getColumn(1).setMaxWidth(170);
        }

        jScrollPane12.setViewportView(jScrollPane7);

        javax.swing.GroupLayout panelMemoriaLayout = new javax.swing.GroupLayout(panelMemoria);
        panelMemoria.setLayout(panelMemoriaLayout);
        panelMemoriaLayout.setHorizontalGroup(
            panelMemoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMemoriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMemoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelMemoriaLayout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(porcentajeMemoria)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelMemoriaLayout.setVerticalGroup(
            panelMemoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelMemoriaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelMemoriaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(porcentajeMemoria))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(69, 90, 100));
        jLabel12.setText("Disco Duro");

        porcentajeDisco.setBackground(new java.awt.Color(255, 255, 255));
        porcentajeDisco.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        porcentajeDisco.setForeground(new java.awt.Color(69, 90, 100));
        porcentajeDisco.setText("0%");

        tblDisco.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Proceso ID", "Instruccion"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(tblDisco);
        if (tblDisco.getColumnModel().getColumnCount() > 0) {
            tblDisco.getColumnModel().getColumn(0).setMinWidth(80);
            tblDisco.getColumnModel().getColumn(0).setMaxWidth(80);
            tblDisco.getColumnModel().getColumn(1).setMinWidth(170);
            tblDisco.getColumnModel().getColumn(1).setMaxWidth(170);
        }

        javax.swing.GroupLayout panelDiscoDuroLayout = new javax.swing.GroupLayout(panelDiscoDuro);
        panelDiscoDuro.setLayout(panelDiscoDuroLayout);
        panelDiscoDuroLayout.setHorizontalGroup(
            panelDiscoDuroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDiscoDuroLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDiscoDuroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelDiscoDuroLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(porcentajeDisco)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelDiscoDuroLayout.setVerticalGroup(
            panelDiscoDuroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDiscoDuroLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelDiscoDuroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(porcentajeDisco))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(69, 90, 100));
        jLabel1.setText("Tiempo de ejecucion");

        tiempoEjecucion.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        tiempoEjecucion.setForeground(new java.awt.Color(69, 90, 100));
        tiempoEjecucion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tiempoEjecucion.setText("0");
        tiempoEjecucion.setToolTipText("");
        tiempoEjecucion.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnSiguiente.setBackground(new java.awt.Color(69, 90, 100));
        btnSiguiente.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        btnSiguiente.setForeground(new java.awt.Color(255, 255, 255));
        btnSiguiente.setText("Siguiente");
        btnSiguiente.setEnabled(false);
        btnSiguiente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSiguienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTiempoLayout = new javax.swing.GroupLayout(panelTiempo);
        panelTiempo.setLayout(panelTiempoLayout);
        panelTiempoLayout.setHorizontalGroup(
            panelTiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTiempoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTiempoLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTiempoLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panelTiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tiempoEjecucion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnSiguiente, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        panelTiempoLayout.setVerticalGroup(
            panelTiempoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTiempoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tiempoEjecucion, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSiguiente))
        );

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(69, 90, 100));
        jLabel2.setText("Archivos");

        tblArchivo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre", "Dirección"
            }
        ));
        jScrollPane2.setViewportView(tblArchivo);
        if (tblArchivo.getColumnModel().getColumnCount() > 0) {
            tblArchivo.getColumnModel().getColumn(0).setMinWidth(50);
            tblArchivo.getColumnModel().getColumn(0).setMaxWidth(50);
            tblArchivo.getColumnModel().getColumn(1).setMinWidth(150);
            tblArchivo.getColumnModel().getColumn(1).setMaxWidth(150);
        }

        jScrollPane1.setViewportView(jScrollPane2);

        btnArchivo.setBackground(new java.awt.Color(69, 90, 100));
        btnArchivo.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        btnArchivo.setForeground(new java.awt.Color(255, 255, 255));
        btnArchivo.setText("Cargar archivos");
        btnArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArchivoActionPerformed(evt);
            }
        });

        btnEjecutar.setBackground(new java.awt.Color(69, 90, 100));
        btnEjecutar.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        btnEjecutar.setForeground(new java.awt.Color(255, 255, 255));
        btnEjecutar.setText("Ejecutar");
        btnEjecutar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarActionPerformed(evt);
            }
        });

        btnStep.setBackground(new java.awt.Color(69, 90, 100));
        btnStep.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        btnStep.setForeground(new java.awt.Color(255, 255, 255));
        btnStep.setText("Paso a paso");
        btnStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStepActionPerformed(evt);
            }
        });

        btnLimpiar.setBackground(new java.awt.Color(69, 90, 100));
        btnLimpiar.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        btnLimpiar.setForeground(new java.awt.Color(255, 255, 255));
        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBotonesLayout = new javax.swing.GroupLayout(panelBotones);
        panelBotones.setLayout(panelBotonesLayout);
        panelBotonesLayout.setHorizontalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnStep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE))
                .addGap(96, 96, 96)
                .addGroup(panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEjecutar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnLimpiar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelBotonesLayout.setVerticalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnArchivo)
                    .addComponent(btnEjecutar))
                .addGap(18, 18, 18)
                .addGroup(panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStep)
                    .addComponent(btnLimpiar))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelArchivosLayout = new javax.swing.GroupLayout(panelArchivos);
        panelArchivos.setLayout(panelArchivosLayout);
        panelArchivosLayout.setHorizontalGroup(
            panelArchivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArchivosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelArchivosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 817, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(panelBotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelArchivosLayout.setVerticalGroup(
            panelArchivosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArchivosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(panelBotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 24)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(69, 90, 100));
        jLabel17.setText("Configuraciones");

        jLabel3.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(69, 90, 100));
        jLabel3.setText("Kb");

        txtMemoria.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        txtMemoria.setText("256");
        txtMemoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMemoriaActionPerformed(evt);
            }
        });

        txtDisco.setFont(new java.awt.Font("Microsoft YaHei UI", 0, 12)); // NOI18N
        txtDisco.setText("512");
        txtDisco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDiscoActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(69, 90, 100));
        jLabel4.setText("Almacenamiento");

        jLabel5.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(69, 90, 100));
        jLabel5.setText("Kb");

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Microsoft YaHei UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(69, 90, 100));
        jLabel6.setText("Memoria");

        javax.swing.GroupLayout panelConfiguracionLayout = new javax.swing.GroupLayout(panelConfiguracion);
        panelConfiguracion.setLayout(panelConfiguracionLayout);
        panelConfiguracionLayout.setHorizontalGroup(
            panelConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelConfiguracionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel4)
                    .addGroup(panelConfiguracionLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(171, 171, 171)
                        .addGroup(panelConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtDisco, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMemoria, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelConfiguracionLayout.setVerticalGroup(
            panelConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelConfiguracionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMemoria)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelConfiguracionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDisco, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(panelTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(panelMemoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(panelDiscoDuro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8))
                            .addComponent(panelArchivos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelConfiguracion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(Core1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(42, 42, 42)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelProceso, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelSalida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelCola, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(panelTerminal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Core2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelSalida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelCola, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(panelTerminal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(109, 109, 109))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelArchivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelConfiguracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelTiempo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelMemoria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelDiscoDuro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Core2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Core1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(7, 7, 7))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtMemoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMemoriaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMemoriaActionPerformed

    private void txtDiscoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDiscoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDiscoActionPerformed

    private void btnArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchivoActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "ASM FILES", "asm", "asm");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            
            File[] files = chooser.getSelectedFiles();
            int cont=1;
            for (int i=0; i<files.length;i++){
                File temp = files[i];
                compu.addFile(temp);
                modelFiles.addRow(new Object[]{cont,temp.getName(),temp.getPath()});
            }
            
            tblArchivo.setModel(modelFiles);
        }
    }//GEN-LAST:event_btnArchivoActionPerformed

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed
        // TODO add your handling code here:
        int memoria=Integer.parseInt(txtMemoria.getText());
        int discoDuro=Integer.parseInt(txtDisco.getText());
        if(memoria>255 && discoDuro>511){
            txtMemoria.setEditable(false);
            txtDisco.setEditable(false);
            if(compu.getSizeOfLoadFiles()>0){
                compu.loadProcessfromFile();     
                compu.sendMessagetoOutput("Archivos cargados exitosamente.");
                compu.sendMessagetoOutput("Ejecucion iniciada");
                btnEjecutar.setEnabled(false);
                btnArchivo.setEnabled(false);
                Thread t1=new Thread(){
                    public void run(){
                        try {
                            ejecutarPrograma();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                t1.start();
                }else{
                    compu.sendMessagetoOutput("Por favor cargar algunos archivos primeramente.");
                }
                actualizarElementosVisuales();
        }else{
            JOptionPane.showMessageDialog(null,"La memoria y el almacenamiento debe ser mayor o igual 256 y 512 respectivamente.");
        }
        
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        // TODO add your handling code here:
        btnEjecutar.setEnabled(true);
        btnArchivo.setEnabled(true);
        btnSiguiente.setEnabled(false);
        clearTable(modelProcess);
        clearTable(modelFiles);
        clearTable(modelMainMemory);
        clearTable(modelHardMemory);
        clearTable(modelQueue);
        porcentajeDisco.setText("0%");
        porcentajeMemoria.setText("0%");
        tiempoEjecucion.setText("0");
        txtMemoria.setText("256");
        txtDisco.setText("512");
        cp1.setText("");
        tr1.setText("");
        ci1.setText("");
        cp2.setText("");
        tr2.setText("");
        ci2.setText(""); 
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStepActionPerformed
        // TODO add your handling code here:
        int memoria=Integer.parseInt(txtMemoria.getText());
        int discoDuro=Integer.parseInt(txtDisco.getText());
        if(memoria>255 && discoDuro>511){
            txtMemoria.setEditable(false);
            txtDisco.setEditable(false);
            if(compu.getSizeOfLoadFiles()>0){
                compu.loadProcessfromFile();     
                compu.sendMessagetoOutput("Archivos cargados exitosamente.");
                compu.sendMessagetoOutput("Ejecucion iniciada");
                btnEjecutar.setEnabled(false);
                btnArchivo.setEnabled(false);
                btnSiguiente.setEnabled(true);
                }else{
                    compu.sendMessagetoOutput("Por favor cargar algunos archivos primeramente.");
                }
                actualizarElementosVisuales();
        }else{
            JOptionPane.showMessageDialog(null,"La memoria y el almacenamiento debe ser mayor o igual 256 y 512 respectivamente.");
        }
    }//GEN-LAST:event_btnStepActionPerformed

    private void btnSiguienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSiguienteActionPerformed
        // TODO add your handling code here:
        if(!compu.finishProgram()){
            compu.nextInstruction();
            actualizarElementosVisuales();
        }
        else{
            btnSiguiente.setEnabled(false);
        }
    }//GEN-LAST:event_btnSiguienteActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Core1;
    private javax.swing.JPanel Core2;
    private javax.swing.JButton btnArchivo;
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JButton btnSiguiente;
    private javax.swing.JButton btnStep;
    private javax.swing.JLabel ci1;
    private javax.swing.JLabel ci2;
    private javax.swing.JLabel cp1;
    private javax.swing.JLabel cp2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPanel panelArchivos;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelCola;
    private javax.swing.JPanel panelConfiguracion;
    private javax.swing.JPanel panelDiscoDuro;
    private javax.swing.JPanel panelMemoria;
    private javax.swing.JPanel panelProceso;
    private javax.swing.JPanel panelSalida;
    private javax.swing.JPanel panelTerminal;
    private javax.swing.JPanel panelTiempo;
    private javax.swing.JLabel porcentajeDisco;
    private javax.swing.JLabel porcentajeMemoria;
    private javax.swing.JTable tblArchivo;
    private javax.swing.JTable tblCola;
    private javax.swing.JTable tblDisco;
    private javax.swing.JTable tblMemoria;
    private javax.swing.JTable tblProcesos;
    private javax.swing.JLabel tiempoEjecucion;
    private javax.swing.JLabel tr1;
    private javax.swing.JLabel tr2;
    private javax.swing.JTextField txtDisco;
    private javax.swing.JTextField txtMemoria;
    private javax.swing.JTextPane txtSalida;
    private javax.swing.JTextField txtTerminal;
    // End of variables declaration//GEN-END:variables
}
