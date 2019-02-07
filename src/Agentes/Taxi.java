package Agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
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
        Camino camino = null;
        int coor_fin[];
        int coor_actual[];
        int visualizacion;
        ArrayList<CoorValor> explorados = new ArrayList<CoorValor>();
        ComportamientoPruebasTaxi(int fila,int columna){
            this.coor_ini = new int[2];
            this.coor_ini[0] = fila;
            this.coor_ini[1] = columna;
            this.coor_fin = new int[2];
            this.coor_fin[0] = fila;
            this.coor_fin[1] = columna;
            this.coor_actual = new int[2];
            this.coor_actual[0] = fila;
            this.coor_actual[1] = columna;
            this.encontrado = false;
            this.camino = new Camino(fila,columna);
            explorados.add(new CoorValor(fila, columna, 100.0));
        }
        @Override
        public void action() {
            
            if (encontrado){
                this.myAgent.blockingReceive();
                ComportamientoValidacionTaxi cvt = new ComportamientoValidacionTaxi(this.coor_ini, this.camino.Get_camino(coor_fin[0], coor_fin[1]));
                System.out.println("Salgo de comportamiento de pruebas de" + this.myAgent.getLocalName());
                this.myAgent.addBehaviour(cvt);
                this.myAgent.removeBehaviour(this);
            }else{
                try {
                    System.out.println("Se ejecuta ComportamientoPruebas de " + this.myAgent.getLocalName()+ encontrado);
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver((AID)this.myAgent.getArguments()[0]);
                    send(msg);
                    msg = this.myAgent.blockingReceive();
                    Tablero tab = (Tablero)msg.getContentObject();
                    ArrayList<CoorValor> movimientos = tab.obtenerPosiblesMovimientos(explorados);
                    double max = movimientos.get(0).getValor();
                    ArrayList<CoorValor> mejores = new ArrayList<CoorValor>();
                    
                    for(int i=0; i < movimientos.size();i++){
                        if(movimientos.get(i).getValor() > max){
                            max = movimientos.get(i).getValor();
                            mejores.clear();
                            mejores.add(movimientos.get(i));
                        }
                        else if (movimientos.get(i).getValor() == max){
                                mejores.add(movimientos.get(i));
                        }
                    }
                    
                    Random eleccion = new Random();
                    CoorValor mov = mejores.get(eleccion.nextInt(mejores.size()));

                    /*
                    CREAR OPERACIONES CON EL TABLERO Y LA DECISIÓN DE MOVIMIENTO
                    */
                    
                    int fila = mov.getCoorF();
                    int columna = mov.getCoorC();
                    System.out.println(this.myAgent.getName() + " Fila y columna " + fila + " " +columna);
                    CoorValor cv = new CoorValor(fila,columna,tab.getValorTablero(fila, columna));
                    
                    msg = msg.createReply();
                    msg.setContentObject(cv);
                    msg.setPerformative(ACLMessage.REQUEST_WHEN);
                    send(msg);
                    
                    msg = this.myAgent.blockingReceive();
                    if (msg.getPerformative() == ACLMessage.CONFIRM){
                        explorados.add(mov);
                        camino.add(fila, columna, coor_fin[0], coor_fin[1]);
                           System.out.println(this.myAgent.getName() + " Movimiento realizado a " + fila + " " +columna+ " desde "+ coor_fin[0] + " " +coor_fin[1]);

                        coor_fin[0]=fila;
                        coor_fin[1]=columna;
                        
                        if(tab.getValorTablero(fila,columna) == 10){
                            encontrado = true;
                            System.out.println(this.myAgent.getName() + "Pasajero encontrado");
                            msg = msg.createReply();
                            int [] coor = new int[2];
                            coor[0]=fila;
                            coor[1]=columna;
                            msg.setContentObject(coor);
                            msg.setPerformative(ACLMessage.CONFIRM);
                            send(msg);
                        }
                    }else if (msg.getPerformative() == ACLMessage.REFUSE){
                        
                      System.out.println(this.myAgent.getName() + " " + fila + " " +columna + " ocupada, se vuelve a pedir el tablero ");
                      //Do nothing  y volver al loop 
                    }else if (msg.getPerformative() == ACLMessage.INFORM){
                        ComportamientoValidacionTaxi cvt = new ComportamientoValidacionTaxi(this.coor_ini, this.camino.Get_camino(coor_fin[0], coor_fin[1]));
                        System.out.println("Salgo de comportamiento de pruebas de" + this.myAgent.getLocalName());
                        this.myAgent.addBehaviour(cvt);
                        this.myAgent.removeBehaviour(this);
                      /*ArrayList<CoorCoor> camin = this.camino.Get_camino(coor_fin[0], coor_fin[1]);
                      for(int i=0; i<camin.size();i++){
                        System.out.printf("De [%d,%d] a [%d,%d]%n",camin.get(i).getFila_ini()
                        ,camin.get(i).getCol_ini(),camin.get(i).getFila_fin(),camin.get(i).getCol_fin());
                        }*/
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
        boolean parar;
        int num,cont, fail;
        ArrayList<CoorCoor> list;
        ComportamientoValidacionTaxi(int [] coor,ArrayList<CoorCoor> camino){
            this.parar = false;
            this.num = camino.size();
            this.cont = 0;
            this.fail = 0;
            this.list = camino;
        }
        @Override
        public void action() {
            if(!parar){
                try {
                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver((AID)this.myAgent.getArguments()[0]);
                    CoorCoor cc = list.get(cont);
                    //System.out.println("num =  "+num+"cont = "+cont);
                    msg.setContentObject(cc);
                    send(msg);
                    
                    msg = this.myAgent.blockingReceive();
                    if(msg.getPerformative() == ACLMessage.REFUSE){
                        fail++;
                    }else if(msg.getPerformative() == ACLMessage.CONFIRM){
                        this.cont++;
                        fail = 0;
                    }
                    if ( this.cont >= this.num || fail == 20){
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