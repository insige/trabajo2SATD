/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agentes;

import java.util.ArrayList;


public class Tablero implements java.io.Serializable {
    double [][] tablero;
    int numPersonas;
    int numBloques;
    Tablero(){
        this.tablero = new double[7][7];
    }
    Tablero(int numPersonas,int n_bloques){
        this.numPersonas = numPersonas;
        this.numBloques = n_bloques;
        this.tablero = new double[7][7];
        for (int i=0;i<7;i++){
            for (int j=0;j<7;j++){
                this.tablero[i][j] = -0.5;
            }
        }
        
        for (int i=0; i<numBloques;i++){
            boolean ocupada = true;
            int coorF = 0;
            int coorC = 0;
            while(ocupada){
                coorF = (int) (Math.random() * 7);
                coorC = (int) (Math.random() * 7);
                if (this.tablero[coorF][coorC] == -0.5){
                    ocupada = false;
                    System.out.println("Pongo bloque en: ");
                    System.out.println("Coordenada Fila: " + coorF + " Coordenada Columna: " + coorC);
                    this.tablero[coorF][coorC] = -10;
                }
            }
        }
        
        for (int i=0; i<numPersonas;i++){
            boolean ocupada = true;
            int coorF = 0;
            int coorC = 0;
            while(ocupada){
                coorF = (int) (Math.random() * 7);
                coorC = (int) (Math.random() * 7);
                if (this.tablero[coorF][coorC] == -0.5){
                    ocupada = false;
                    System.out.println("Pongo persona en: ");
                    System.out.println("Coordenada Fila: " + coorF + " Coordenada Columna: " + coorC);
                    this.tablero[coorF][coorC] = 10;
                }
            }
        }
    }

    public double[][] getTablero() {
        return tablero;
    }

    public int getNumPersonas() {
        return numPersonas;
    }

    public int getNumBloques() {
        return numBloques;
    }

    public void setTablero(double[][] tablero) {
        this.tablero = tablero;
    }

    public void setNumPersonas(int numPersonas) {
        this.numPersonas = numPersonas;
    }

    public void setNumBloques(int numBloques) {
        this.numBloques = numBloques;
    }
    
    public void setValor(int coorF, int coorC, double valor){
        this.tablero[coorF][coorC] = valor;
    }
    
    public double getValorTablero(int coorF, int coorC){
        return(this.tablero[coorF][coorC]);
    }
    
    public void mostrarTablero(){
        System.out.println("\t\t0\t\t1\t\t2\t\t3\t\t4\t\t5\t\t6       ");
        for (int i=0;i<7;i++){
            System.out.print(i + "\t");
            for (int j=0;j<7;j++){
                System.out.printf(this.tablero[i][j] + "\t\t");
            }
            System.out.print("\n");
        }
        
    }
    
    public Tablero copy(){
        Tablero t = new Tablero();
        t.setNumBloques(this.numBloques);
        t.setNumPersonas(this.numPersonas);
        for (int i=0;i<7;i++){
            for (int j=0;j<7;j++){
                t.setValor(i, j, this.tablero[i][j]);
            }
        }
        return(t);
    }
    
    public ArrayList<CoorValor> obtenerPosiblesMovimientos(ArrayList<CoorValor> movs) {
        ArrayList<CoorValor> posiblesMovimientos = new ArrayList<CoorValor>();
        //Se recorre el array pasado como parametro
        for (int i=0; i<movs.size(); i++) {
            CoorValor cv = movs.get(i);
            int fila_cv = cv.getCoorF();
            int columna_cv = cv.getCoorC();
            if (fila_cv == 0 || columna_cv == 0 || fila_cv == 6 || columna_cv == 6) {
                if (fila_cv == 0) {
                    if (columna_cv == 0) {
                        CoorValor cv1 = new CoorValor(0, 1, this.tablero[0][1]);
                        CoorValor cv2 = new CoorValor(1, 0, this.tablero[1][0]);
                        posiblesMovimientos.add(cv1);
                        posiblesMovimientos.add(cv2);
                    }
                    else if (columna_cv == 6) {
                        CoorValor cv1 = new CoorValor(0, 5, this.tablero[0][5]);
                        CoorValor cv2 = new CoorValor(1, 6, this.tablero[1][6]);
                        posiblesMovimientos.add(cv1);
                        posiblesMovimientos.add(cv2);
                    }
                    else {
                        CoorValor cv1 = new CoorValor(fila_cv, columna_cv + 1, this.tablero[fila_cv][columna_cv + 1]);
                        CoorValor cv2 = new CoorValor(fila_cv + 1, columna_cv, this.tablero[fila_cv + 1][columna_cv]);
                        CoorValor cv3 = new CoorValor(fila_cv, columna_cv - 1, this.tablero[fila_cv][columna_cv - 1]);
                        posiblesMovimientos.add(cv1);
                        posiblesMovimientos.add(cv2);
                        posiblesMovimientos.add(cv3);
                    }
                }
                else if (columna_cv == 0) {
                    if (fila_cv == 6) {
                        CoorValor cv1 = new CoorValor(5, 0, this.tablero[5][0]);
                        CoorValor cv2 = new CoorValor(6, 1, this.tablero[6][1]);
                        posiblesMovimientos.add(cv1);
                        posiblesMovimientos.add(cv2);  
                    }
                    else {
                        CoorValor cv1 = new CoorValor(fila_cv, columna_cv + 1, this.tablero[fila_cv][columna_cv + 1]);
                        CoorValor cv2 = new CoorValor(fila_cv + 1, columna_cv, this.tablero[fila_cv + 1][columna_cv]);
                        CoorValor cv3 = new CoorValor(fila_cv - 1, columna_cv, this.tablero[fila_cv - 1][columna_cv]);
                        posiblesMovimientos.add(cv1);
                        posiblesMovimientos.add(cv2);
                        posiblesMovimientos.add(cv3);
                    }
                }
                else if (fila_cv == 6) {
                    if (columna_cv == 6) {
                        CoorValor cv1 = new CoorValor(5, 6, this.tablero[5][6]);
                        CoorValor cv2 = new CoorValor(6, 5, this.tablero[6][5]);
                        posiblesMovimientos.add(cv1);
                        posiblesMovimientos.add(cv2);  
                    }
                    else {
                        CoorValor cv1 = new CoorValor(fila_cv, columna_cv + 1, this.tablero[fila_cv][columna_cv + 1]);
                        CoorValor cv2 = new CoorValor(fila_cv, columna_cv - 1, this.tablero[fila_cv][columna_cv - 1]);
                        CoorValor cv3 = new CoorValor(fila_cv - 1, columna_cv, this.tablero[fila_cv - 1][columna_cv]);
                        posiblesMovimientos.add(cv1);
                        posiblesMovimientos.add(cv2);
                        posiblesMovimientos.add(cv3); 
                    }
                }
                else if (columna_cv == 6) {
                        CoorValor cv1 = new CoorValor(fila_cv + 1, columna_cv, this.tablero[fila_cv + 1][columna_cv]);
                        CoorValor cv2 = new CoorValor(fila_cv, columna_cv - 1, this.tablero[fila_cv][columna_cv - 1]);
                        CoorValor cv3 = new CoorValor(fila_cv - 1, columna_cv, this.tablero[fila_cv - 1][columna_cv]);
                        posiblesMovimientos.add(cv1);
                        posiblesMovimientos.add(cv2);
                        posiblesMovimientos.add(cv3); 
                }
            }
            else {
                CoorValor cv1 = new CoorValor(fila_cv, columna_cv + 1, this.tablero[fila_cv][columna_cv + 1]);
                CoorValor cv2 = new CoorValor(fila_cv + 1, columna_cv, this.tablero[fila_cv + 1][columna_cv]);
                CoorValor cv3 = new CoorValor(fila_cv, columna_cv - 1, this.tablero[fila_cv][columna_cv - 1]);
                CoorValor cv4 = new CoorValor(fila_cv - 1, columna_cv, this.tablero[fila_cv - 1][columna_cv]);
                posiblesMovimientos.add(cv1);
                posiblesMovimientos.add(cv2);
                posiblesMovimientos.add(cv3);
                posiblesMovimientos.add(cv4);
            }
        } //bucle for
        //Hay que eliminar de la lista posiblesMovimientos las CoorValor que estaban en la lista movs
        ArrayList<CoorValor> listaMovimientosFinal = new ArrayList<CoorValor>();
        for (int i=0; i<posiblesMovimientos.size(); i++) {
            CoorValor pm = posiblesMovimientos.get(i);
            int fila_pm = pm.getCoorF();
            int columna_pm = pm.getCoorC();
            boolean encontrado = false;
            for (int j=0; j<movs.size(); j++) {
                CoorValor cv = movs.get(j);
                int fila_cv = cv.getCoorF();
                int columna_cv = cv.getCoorC();
                if (fila_cv==fila_pm && columna_cv==columna_pm) {
                   encontrado = true; 
                }
            }
            if (!encontrado) {
                listaMovimientosFinal.add(pm);
            }
        }
        return listaMovimientosFinal;
    }
}
