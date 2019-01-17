package Agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
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
                    msg_inicial.addReceiver((AID)this.myAgent.getArguments()[0]);
                    msg_inicial.setContentObject(coor);
                    send(msg_inicial);
                    msg_inicial = this.myAgent.blockingReceive();
                    if(0 == Integer.parseInt(msg_inicial.getContent())){
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
        int coor_ini[];
        boolean encontrado;
        ComportamientoPruebasTaxi(int fila,int columna){
            this.coor_ini = new int[2];
            this.coor_ini[0] = fila;
            this.coor_ini[1] = columna;
            this.encontrado = false;
        }
        @Override
        public void action() {
            System.out.println("Se ejecuta ComportamientoPruebas de " + this.myAgent.getLocalName());
            if (encontrado){
                this.myAgent.blockingReceive();
                ComportamientoValidacionTaxi cvt = new ComportamientoValidacionTaxi(this.coor_ini);
                this.myAgent.addBehaviour(cvt);
                this.myAgent.removeBehaviour(this);
            }else{
                try {
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver((AID)this.myAgent.getArguments()[0]);
                    send(msg);
                    
                    msg = this.myAgent.blockingReceive();
                    Tablero tab = (Tablero)msg.getContentObject();
                    
                    /*
                    CREAR OPERACIONES CON EL TABLERO Y LA DECISIÓN DE MOVIMIENTO
                    */
                    
                    int fila = (int) (Math.random() * 7);
                    int columna = (int) (Math.random() * 7);
                    System.out.println(this.myAgent.getName() + " Fila y columna " + fila + " " +columna);
                    CoorValor cv = new CoorValor(fila,columna,tab.getValorTablero(fila, columna));
                    
                    msg = msg.createReply();
                    msg.setContentObject(cv);
                    msg.setPerformative(ACLMessage.REQUEST_WHEN);
                    send(msg);
                    
                    msg = this.myAgent.blockingReceive();
                    if (msg.getPerformative() == ACLMessage.CONFIRM){
                        /*
                        AÑADIR MOVIMIENTO REALIZADO
                        */
                        System.out.println(this.myAgent.getName() + " Movimiento realizado " + fila + " " +columna);
                        if(tab.getValorTablero(fila,columna) == 10){
                            encontrado = true;
                            msg = msg.createReply();
                            int [] coor = new int[2];
                            coor[0]=fila;
                            coor[1]=columna;
                            msg.setContentObject(coor);
                            msg.setPerformative(ACLMessage.CONFIRM);
                            send(msg);
                        }
                    }else if (msg.getPerformative() == ACLMessage.REFUSE){
                        /*
                        TRATAMIENTO AL MOVIMIENTO NO REALIZADO
                        */
                    }
                } catch (UnreadableException ex) {
                    Logger.getLogger(Taxi.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Taxi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
    }
    
    
    //Creación del comportamiento asociado a la fase de validación de las pruebas realizadas
    public class ComportamientoValidacionTaxi extends CyclicBehaviour {
        int coor_ini[];
        int coor_act[];
        boolean parar;
        int num,cont;
        /*
        EN ESTA CREACIÓN DE COMPORTAMIENTO QUEDA METER EL CAMINO.
        */
        ComportamientoValidacionTaxi(int [] coor){
            this.coor_ini = coor;
            this.coor_act = coor;
            this.parar = false;
            this.num = (int) (Math.random() * 7);
            this.cont = 0;
        }
        @Override
        public void action() {
            if(!parar){
                /*
                TRATAMIENTO DEL CAMINO
                */
                try {
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver((AID)this.myAgent.getArguments()[0]);
                    /*
                    EDITAR, ESTO ES PARA PROBAR
                    */
                    int [] coor= new int[2];
                    coor[0] = (int) (Math.random() * 7);
                    coor[1] = (int) (Math.random() * 7);
                    CoorCoor cc = new CoorCoor(this.coor_act[0],this.coor_act[1],coor[0],coor[1]);
                    msg.setContentObject(cc);
                    send(msg);
                    
                    msg = this.myAgent.blockingReceive();
                    if(msg.getPerformative() == ACLMessage.REFUSE){
                        /*
                        TRATAMIENTO MOVIMIENTO NO REALIZADO
                        */
                    }else if(msg.getPerformative() == ACLMessage.CONFIRM){
                        this.coor_act = coor;
                        /*
                        TRATAMIENTO MOVIMIENTO REALIZADO
                        */
                    }
                    this.cont++;
                    if ( this.cont >= this.num){
                        parar = true;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Taxi.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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