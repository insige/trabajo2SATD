package Agentes;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.io.Serializable;
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
            tab_taxis = tab_inicial.copy();
            this.correctos = new ACLMessage(ACLMessage.CONFIRM);
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
                        send(this.correctos);
                        ComportamientoCentral cc = new ComportamientoCentral(this.tab_inicial, this.tab_taxis,this.n_taxis);
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
        ACLMessage confirmados;
        int conf;
        ComportamientoCentral(Tablero tab,Tablero tab_taxis, int n_taxis){
            this.tablero = tab;
            this.tablero_taxis = tab_taxis;
            this.numTaxis = n_taxis;
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
                
            }else if(msg.getPerformative() == ACLMessage.REQUEST_WHEN){ //Se reciben coordenadas y el valor que el taxi tiene
                
            } else if(msg.getPerformative() == ACLMessage.CONFIRM){
                confirmados.addReceiver(msg.getSender());
                conf++;
                if(conf == this.numTaxis){
                    ComportamientoFinal cf = new ComportamientoFinal(this.tablero_taxis,this.confirmados);
                    this.myAgent.addBehaviour(cf);
                    this.myAgent.removeBehaviour(this);
                }
            }
            
            /*
            Cuando vaya recibiendo los mensajes de confirmación de que han ido acabando meter al que ha 
            enviado el mensaje en el mensaje confirmados 
            confirmados va a enviarse con al comportamiento final. 
            */
        }
    }

    //Comportamiento final, asociado a el tiempo de validación de las pruebas realizadas por parte de los taxis. 
    public class ComportamientoFinal extends CyclicBehaviour {
        Tablero tablero;
        ComportamientoFinal(Tablero tab, ACLMessage confirmados){
            System.out.println("Creo en ComportamientoFinal");
            this.tablero = tab;
            send(confirmados);
            /*
            Envía el mensaje de que pueden empezar a probar sus caminos.
            */
        }
        @Override
        public void action() {
            System.out.println("Entro en ComportamientoFinal");
        }
    }
    
    /*Asignacion de comportamientos*/
    @Override
    protected void setup() {
        System.out.println("Inicio del central con nombre: " + this.getLocalName());
        int n_taxis = Integer.parseInt(this.getArguments()[0].toString());
        int n_bloques = Integer.parseInt(this.getArguments()[1].toString());
        int n_personas = Integer.parseInt(this.getArguments()[2].toString());
        
        ComportamientoInicial ci = new ComportamientoInicial(n_personas,n_bloques,n_taxis);
        this.addBehaviour(ci);
    }
}
