package Agentes;

import jade.core.AID;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;

public class creacionAgentes {
    
    public static void main (String [ ] args) throws StaleProxyException{
        int n_personas = 4;
        int n_bloques = 10;
        int n_taxis = 5;
        Runtime rt;
        rt = Runtime.instance();
        ContainerController cc;
        cc = rt.createMainContainer(new ProfileImpl());
        
        ArrayList<AgentController> list = new ArrayList<AgentController>();
        Object arg_central[];
        arg_central = new Object[3];
        arg_central[0] = n_personas;
        arg_central[1] = n_bloques;
        arg_central[2] = n_taxis;
        list.add(cc.createNewAgent("central","Agentes.Central",arg_central));
        
        Object arg_taxi[] = new Object[1];
        arg_taxi[0] = new AID("central",AID.ISLOCALNAME);
        for(int i = 1;i<=n_taxis;i++){
            list.add(cc.createNewAgent("taxi"+i,"Agentes.Taxi",arg_taxi));
        }
        
        for(AgentController a:list){
            a.start();
        }
        
        /*
        Tablero t = new Tablero(2,3);
        t.mostrarTablero();
        */
    }
}
