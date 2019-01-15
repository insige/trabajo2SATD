package Agentes;

import java.io.Serializable;

public class CoorCoor implements Serializable{
    int fila_ini;
    int col_ini;
    int fila_fin;
    int col_fin;

    public CoorCoor(int fila_ini, int col_ini, int fila_fin, int col_fin) {
        this.fila_ini = fila_ini;
        this.col_ini = col_ini;
        this.fila_fin = fila_fin;
        this.col_fin = col_fin;
    }

    public int getFila_ini() {
        return fila_ini;
    }

    public int getCol_ini() {
        return col_ini;
    }

    public int getFila_fin() {
        return fila_fin;
    }

    public int getCol_fin() {
        return col_fin;
    }

    public void setFila_ini(int fila_ini) {
        this.fila_ini = fila_ini;
    }

    public void setCol_ini(int col_ini) {
        this.col_ini = col_ini;
    }

    public void setFila_fin(int fila_fin) {
        this.fila_fin = fila_fin;
    }

    public void setCol_fin(int col_fin) {
        this.col_fin = col_fin;
    }
    
}
