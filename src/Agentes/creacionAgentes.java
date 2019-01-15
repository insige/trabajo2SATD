package Agentes;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;

public class creacionAgentes {
    
    public static void main (String [ ] args) throws StaleProxyException{
        int n_personas = 10;
        int n_bloques = 10;
        int n_taxis = 5;
        Runtime rt;
        rt = Runtime.instance();
        ContainerController cc;
        cc = rt.createMainContainer(new ProfileImpl());
        
        ArrayList<AgentController> list = new ArrayList<AgentController>();
        Object arg[];
        arg = new Object[3];
        arg[0] = n_personas;
        arg[1] = n_bloques;
        arg[2] = n_taxis;
        list.add(cc.createNewAgent("central","Agentes.Central",arg));
        
        for(int i = 1;i<=n_taxis;i++){
            list.add(cc.createNewAgent("taxi"+i,"Agentes.Taxi",null));
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
