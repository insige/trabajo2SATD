package Agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Central extends Agent {
    //Comportamiento inicial, este inicia el tablero que contiene la informacion de los taxis. 
    public class ComportamientoInicial extends CyclicBehaviour {
        int n_personas;
        int n_bloques;
        int n_taxis;
        int cont;
        ACLMessage correctos;
        Tablero tab_inicial;
        Tablero tab_taxis;
        ComportamientoInicial(int n_personas, int n_bloques, int n_taxis){
            this.cont = 0;
            this.n_personas = n_personas;
            this.n_bloques = n_bloques;
            this.n_taxis = n_taxis;
            tab_inicial = new Tablero(n_personas,n_bloques);
            tab_inicial.mostrarTablero();
            tab_taxis = tab_inicial.copy();
            this.correctos = new ACLMessage(ACLMessage.INFORM);
        }
        @Override
        public void action() {
            try {
                ACLMessage msg = this.myAgent.blockingReceive();
                int [] filaCol = (int[]) msg.getContentObject();
                System.out.println("Del taxi: " + msg.getSender() + " con fila y columna " + filaCol[0] + " " + filaCol[1]);
                if(tab_taxis.getValorTablero(filaCol[0], filaCol[1]) != -0.5){
                    msg = msg.createReply();
                    msg.setPerformative(ACLMessage.REFUSE);
                    msg.setContent("0");
                    send(msg);
                }else{
                    this.correctos.addReceiver(msg.getSender());
                    msg = msg.createReply();
                    msg.setPerformative(ACLMessage.CONFIRM);
                    msg.setContent("1");
                    send(msg);
                    tab_taxis.setValor(filaCol[0], filaCol[1], 100);
                    this.cont++;
                    if(this.cont == this.n_taxis){
                        System.out.println("Ya he recibido todos los taxis");
                        this.tab_taxis.mostrarTablero();
                        send(this.correctos);
                        ComportamientoCentral cc = new ComportamientoCentral(this.tab_inicial, this.tab_taxis,this.n_taxis,this.n_personas);
                        this.myAgent.addBehaviour(cc);
                        this.myAgent.removeBehaviour(this);
                    }
                }
            } catch (UnreadableException ex) {
                Logger.getLogger(Central.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    //Comportamiento central, asociado a el tiempo de prueba de opciones por parte de los taxis. 
    public class ComportamientoCentral extends CyclicBehaviour {
        Tablero tablero;
        Tablero tablero_taxis;
        int numTaxis;
        int numPersonas;
        ACLMessage confirmados;
        int conf;
        boolean ending;
        ComportamientoCentral(Tablero tab,Tablero tab_taxis, int n_taxis, int n_personas){
            this.tablero = tab;
            this.tablero_taxis = tab_taxis;
            this.numTaxis = n_taxis;
            this.numPersonas = n_personas;
            ending = false;
            confirmados = new ACLMessage(ACLMessage.INFORM);
        }
        @Override
        public void action() {
            //Espera a recibir mensaje
            ACLMessage msg = this.myAgent.blockingReceive(); 
            if (msg.getPerformative() == ACLMessage.REQUEST){ 
                try {
                    //Se ha pedido el tablero
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContentObject(this.tablero);
                    send(reply);
                } catch (IOException ex) {
                    Logger.getLogger(Central.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }else if(msg.getPerformative() == ACLMessage.REQUEST_WHEN && !ending){ 
                try {
                    //Se reciben coordenadas y el valor que el taxi tiene
                    CoorValor cv = (CoorValor) msg.getContentObject();
                    msg = msg.createReply();
                    if (cv.getValor() == this.tablero.getValorTablero(cv.getCoorF(), cv.getCoorC())){
                        msg.setPerformative(ACLMessage.CONFIRM);
                        this.tablero.setValor(cv.getCoorF(), cv.getCoorC(), 1.5*cv.getValor());
                    }else{
                        msg.setPerformative(ACLMessage.REFUSE);
                    }
                    send(msg);
                } catch (UnreadableException ex) {
                    Logger.getLogger(Central.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if(msg.getPerformative() == ACLMessage.CONFIRM && !ending){
                try {
                    confirmados.addReceiver(msg.getSender());
                    int [] coor =  (int []) msg.getContentObject();
                    this.tablero.setValor(coor[0], coor[1], -1000);
                    conf++;
                    if(conf == this.numTaxis){
                        send(this.confirmados);
                        ComportamientoFinal cf = new ComportamientoFinal(this.tablero_taxis);
                        this.myAgent.addBehaviour(cf);
                        this.myAgent.removeBehaviour(this);
                    }
                    else if(conf == this.numPersonas){
                        ending = true;
                    }
                } catch (UnreadableException ex) {
                    Logger.getLogger(Central.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if(ending){
                confirmados.addReceiver(msg.getSender());
                if(conf == this.numTaxis){
                        send(this.confirmados);
                        ComportamientoFinal cf = new ComportamientoFinal(this.tablero_taxis);
                        this.myAgent.addBehaviour(cf);
                        this.myAgent.removeBehaviour(this);
                }else{
                    msg.setPerformative(ACLMessage.INFORM);
                    send(msg);
                    conf++;
                }
            }
            
        }
    }

    //Comportamiento final, asociado a el tiempo de validación de las pruebas realizadas por parte de los taxis. 
    public class ComportamientoFinal extends CyclicBehaviour {
        Tablero tablero;
        Tablero tablero_inicial;
        ComportamientoFinal(Tablero tab){
            this.tablero = tab;
            this.tablero_inicial = tablero.copy();
        }
        @Override
        public void action() {
            try {
                System.out.println("Entro en ComportamientoFinal");
                ACLMessage msg = this.myAgent.blockingReceive();
                CoorCoor cc = (CoorCoor) msg.getContentObject();
                System.out.println("Se ha movido " + msg.getSender().getName() + "de: " + cc.getFila_ini() + " " + cc.getCol_ini() + " a " + cc.getFila_fin() + " " + cc.getCol_fin() );
                msg = msg.createReply();
                this.tablero.mostrarTablero();
                if(this.tablero.getValorTablero(cc.getFila_fin(), cc.getCol_fin()) == 100){
                    msg.setPerformative(ACLMessage.REFUSE);
                }else{
                    if(this.tablero_inicial.getValorTablero(cc.getFila_ini(), cc.getCol_ini()) == 100){
                        this.tablero.setValor(cc.getFila_ini(), cc.getCol_ini(), -0.5);
                    }else{
                        this.tablero.setValor(cc.getFila_ini(), cc.getCol_ini(), this.tablero_inicial.getValorTablero(cc.getFila_ini(), cc.getCol_ini()));
                    }
                    
                    this.tablero.setValor(cc.getFila_fin(), cc.getCol_fin(), 100);
                    msg.setPerformative(ACLMessage.CONFIRM);
                }
                send(msg);
                this.tablero.mostrarTablero();
                
            } catch (UnreadableException ex) {
                Logger.getLogger(Central.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    /*Asignacion de comportamientos*/
    @Override
    protected void setup() {
        System.out.println("Inicio del central con nombre: " + this.getLocalName());
        int n_personas = Integer.parseInt(this.getArguments()[0].toString());
        int n_bloques = Integer.parseInt(this.getArguments()[1].toString());
        int n_taxis = Integer.parseInt(this.getArguments()[2].toString());
        
        ComportamientoInicial ci = new ComportamientoInicial(n_personas,n_bloques,n_taxis);
        this.addBehaviour(ci);
    }
}
