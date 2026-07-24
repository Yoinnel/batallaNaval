package model;

import java.io.Serializable;

/**
 * Representa una casilla individual dentro del tablero 10×10.
 * Cada casilla tiene coordenadas (fila, columna), un estado y una referencia
 * opcional al barco que la ocupa.
 */
public class Casilla implements Serializable {

    private static final long serialVersionUID = 1L;

    private int fila;
    private int columna;
    private EstadoCasilla estado;
    private Barco barco;

    /**
     * Crea una casilla vacía en la posición indicada.
     *
     * @param fila    fila de la casilla (0-9)
     * @param columna columna de la casilla (0-9)
     */
    public Casilla(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.estado = EstadoCasilla.VACIA;
        this.barco = null;
    }



    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public EstadoCasilla getEstado() {
        return estado;
    }

    public void setEstado(EstadoCasilla estado) {
        this.estado = estado;
    }

    public Barco getBarco() {
        return barco;
    }

    public void setBarco(Barco barco) {
        this.barco = barco;
    }


    /**
     * @return true si la casilla tiene un barco asignado
     */
    public boolean tieneBarco() {
        return barco != null;
    }

    /**
     * @return true si la casilla ya recibió un disparo (AGUA, TOCADO o HUNDIDO)
     */
    public boolean fueDisparada() {
        return estado == EstadoCasilla.AGUA
            || estado == EstadoCasilla.TOCADO
            || estado == EstadoCasilla.HUNDIDO;
    }

    @Override
    public String toString() {
        return "Casilla(" + fila + ", " + columna + ") [" + estado + "]";
    }
}
