/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Taxi extends Agent {
    
    //Creación del comportamiento
    public class ComportamientoSetupTaxi extends CyclicBehaviour {
        int fila_inicial;
        int columna_inicial;
        boolean correcto;
        ComportamientoSetupTaxi(int fila,int columna){
            this.fila_inicial = fila;
            this.columna_inicial = columna;
            correcto = false;
        }
        @Override
        public void action() {
            if(!correcto){
                try {
                    int coor[] = new int[2];
                    coor[0] = this.fila_inicial;
                    coor[1] = this.columna_inicial;
                    ACLMessage msg_inicial = new ACLMessage(ACLMessage.INFORM);
                    msg_inicial.addReceiver(new AID("central",AID.ISLOCALNAME));
                    msg_inicial.setContentObject(coor);
                    send(msg_inicial);
                    msg_inicial = this.myAgent.blockingReceive();
                    if(Integer.parseInt(msg_inicial.getContent()) == 0){
                        this.fila_inicial = (int) (Math.random() * 7);
                        this.columna_inicial = (int) (Math.random() * 7);
                    }else{
                        correcto = true;
                    }   
                } catch (IOException ex) {
                    Logger.getLogger(Taxi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                this.myAgent.blockingReceive();
                ComportamientoPruebasTaxi cpt = new ComportamientoPruebasTaxi(this.fila_inicial,this.columna_inicial);
                this.myAgent.addBehaviour(cpt);
                this.myAgent.removeBehaviour(this);
            }
                        
        }
    }

    //Creación del comportamiento
    public class ComportamientoPruebasTaxi extends CyclicBehaviour {
        ComportamientoPruebasTaxi(int fila,int columna){
            
        }
        @Override
        public void action() {
            System.out.println("Se ejecuta ComportamientoPruebas de " + this.myAgent.getLocalName());
            this.myAgent.blockingReceive();
        }
    }
    
    
    //Creación del comportamiento
    public class ComportamientoValidacionTaxi extends CyclicBehaviour {
        ComportamientoValidacionTaxi(int fila,int columna){
            
        }
        @Override
        public void action() {
            
        }
    }
    /*Asignacion de comportamientos*/
    @Override
    protected void setup() {
        System.out.println("Inicio del taxi con nombre " + this.getLocalName());
        int fila = (int) (Math.random() * 7);
        int columna = (int) (Math.random() * 7);
        ComportamientoSetupTaxi cst = new ComportamientoSetupTaxi(fila,columna);
        this.addBehaviour(cst);
    }
}