/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agentes;


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
                    System.out.println("Coordenada Fila: " + coorF + "Coordenada Columna: " + coorC);
                    this.tablero[coorF][coorC] = -2;
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
                    System.out.println("Coordenada Fila: " + coorF + "Coordenada Columna: " + coorC);
                    this.tablero[coorF][coorC] = 2;
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
        System.out.println("      0       1       2       3       4       5       6       ");
        for (int i=0;i<7;i++){
            System.out.print(i + "   ");
            for (int j=0;j<7;j++){
                System.out.print(this.tablero[i][j] + "    ");
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
}
