
package Agentes;


import java.io.Serializable;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.HashMap;



public class Camino implements Serializable{
    HashMap<String,CoorCoor> caminos = new HashMap<String,CoorCoor>();  //Mapa con los pasos tomados (que sean viables realmente) en la exploracion.
    CoorValor init; //Coordenada de salida del vehiculo
    HashMap<String, Integer> ordenExploracion = new HashMap<String,Integer>(); //Mapa con el orden de los pasos (usado para saltos en exploración)
    Integer ordenContador; 
    /*
    Los pasos se consideran como CoorCoor, tales que las coordenadaas iniciales son las de la casilla, y las coordenadas
    finales son las de la casilla anterior en exploración. Este orden atiende a la construción del camino, que se realiza en orden
    inverso a la exploración.
    */

    public Camino(int fila_ini, int col_ini) {
        //Inicialización de variables
        caminos = new HashMap<String,CoorCoor>();
        ordenExploracion = new HashMap<String,Integer>();
        this.ordenContador = 0;
        this.init = new CoorValor(fila_ini,col_ini,-1);
        this.init.coorF = fila_ini;
        this.init.coorC = col_ini;
        //La primera casilla explorada es la casilla de salida.
        ordenExploracion.put("Casilla_"+fila_ini+"_"+col_ini, 0);
        this.ordenContador++;
        this.caminos.put("Casilla_"+fila_ini+"_"+col_ini, new CoorCoor(fila_ini,col_ini,-1,-1));
       
    }

    
    public void add(int fila_dest,int col_dest, int fila_ini, int col_ini) {
        //Metodo que añade un paso. 
        
        String clave = "Casilla_"+fila_dest+"_"+col_dest;
        if(adyacentes(fila_dest,col_dest,fila_ini,col_ini)){ //Si la casilla actual y la casilla a explorar son adyacentes
            CoorCoor paso = new CoorCoor(fila_dest, col_dest,  fila_ini,  col_ini );
            caminos.put(clave,paso);
            ordenExploracion.put(clave, ordenContador);
            ordenContador++;
        }
        else{ //Si no lo son, hay un salto y se busca la casilla adyacente a la objetivo que se haya explorado primero
            //Por como funciona la exploración, siempre debe haber un nodo adyacente explorado previamente, excepto la casilla inicial, que se añade previamente
            //en el constructor
            String coordanterior = obtenerAnteriorAdyacente (fila_dest,col_dest);
            CoorCoor anterior = caminos.get(coordanterior);
            CoorCoor paso = new CoorCoor(fila_dest, col_dest,anterior.getFila_ini(),anterior.getCol_ini());
            caminos.put(clave,paso);
            ordenExploracion.put(clave, ordenContador);
            ordenContador++;
        }
    }
    
    public ArrayList<CoorCoor> Get_camino (int X, int Y){
        //Obtención del camino a partir de las coordenadas de destino. 
        //Finaliza al llegar a la casilla de salida
        //El resultado se devuelve en el orden recorrido ya que se añade a la lista por la cabeza. 
        
        ArrayList <CoorCoor> listaresultado = new ArrayList<CoorCoor>();
        
        String clave = "Casilla_"+X+"_"+Y;
        int xvar = -1;
        int yvar = -1;
        CoorCoor paso;
        
        while(xvar!=init.coorF || yvar!=init.coorC){
           paso = caminos.get(clave);
           //System.out.println(clave);
           //Se añade a la cabeza, por lo que listaresultado es una cola FILO
           //listaresultado.add(0, paso);
           listaresultado.add(0, new CoorCoor(paso.getFila_fin(),paso.getCol_fin(),paso.getFila_ini(),paso.getCol_ini()));
           //System.out.println(init.coorF+""+init.coorC+"");
          // System.out.println(paso.getFila_fin()+""+paso.getCol_fin()+""+paso.getFila_ini()+""+paso.getCol_ini());
           xvar = paso.fila_fin;
           yvar = paso.col_fin;
           clave = "Casilla_"+xvar+"_"+yvar;
           //System.out.println(clave);
        }
        
        
        return listaresultado;
        
        
    }

    private boolean adyacentes(int fila_dest,int col_dest, int fila_ini, int col_ini) {
        //Obtiene si las coordenadas son adyacentes
        //if (fila_ini!=init.coorF || col_ini!=init.coorC
        //TODO: DESCOMENTAR MÉTODO CUANDO LA FRONTERA EXISTA Y LA EXPLORACIÖN NO SEA ALEATORIA.
        return true;
            //return(abs(fila_dest-fila_ini)==1 && abs(col_dest-col_ini)==1);
       //}
        //else return true;  //Por aquí no debería pasar de todas formas ni al inicializar.
        
    }

    private String obtenerAnteriorAdyacente (int fila_dest,int col_dest){
        //PRE: Siempre hay un adyacente explorado antes.
        // Obtiene el adyacente explorado primero
        // Los empates son imposibles.
        String result="";
        Integer val = 9000;
        String up ="Casilla_"+(fila_dest-1)+"_"+col_dest;
        String down  ="Casilla_"+(fila_dest+1)+"_"+col_dest;
        String left ="Casilla_"+fila_dest+"_"+(col_dest-1);
        String right ="Casilla_"+fila_dest+"_"+(col_dest+1);
        
        if (ordenExploracion.keySet().contains(up)){
            if (ordenExploracion.get(up)<val){
                val = ordenExploracion.get(up);
                result = up;
            }
        }
        if (ordenExploracion.keySet().contains(down)){
            if (ordenExploracion.get(down)<val){
                val = ordenExploracion.get(down);
                result = down;
            }
        }
        if (ordenExploracion.keySet().contains(left)){
            if (ordenExploracion.get(left)<val){
                val = ordenExploracion.get(left);
                result = left;
            }
        }
        if (ordenExploracion.keySet().contains(right)){
            if (ordenExploracion.get(right)<val){
                val = ordenExploracion.get(right);
                result = right;
            }
        }        
        return result;
    }
    
}
