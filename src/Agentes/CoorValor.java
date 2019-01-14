/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Agentes;


public class CoorValor {
    int coorF;
    int coorC;
    double valor;
    CoorValor(int F, int C, int valor){
        this.coorF = F;
        this.coorC = C;
        this.valor = valor;
    }

    public int getCoorF() {
        return coorF;
    }
    
    public int getCoorC() {
        return coorC;
    }
    
    public double getValor() {
        return valor;
    }
    
    public void setCoorF(int coorF) {
        this.coorF = coorF;
    }
    
    public void setCoorC(int coorC) {
        this.coorC = coorC;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
    
}
