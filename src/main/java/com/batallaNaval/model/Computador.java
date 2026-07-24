package com.batallaNaval.model;

import com.batallaNaval.exceptions.BarcoSuperpuestoException;
import com.batallaNaval.exceptions.FueraDeTableroException;
import com.batallaNaval.patterns.BarcoFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Representa al computador (oponente) en la partida.
 * Extiende de Jugador y agrega la capacidad de generar posiciones
 * aleatorias para sus barcos y la inteligencia artificial de disparo.
 */
public class Computador extends Jugador implements Serializable {

    private static final long serialVersionUID = 1L;

    private IA ia;

    public IA getIa() {
        return ia;
    }

    public void setIa(IA ia) {
        this.ia = ia;
    }

    /**
     * Crea un nuevo computador con nickname "Computador" y su IA.
     */
    public Computador() {
        super("Computador");
        this.ia = new IA();
    }

    /**
     * Genera posiciones aleatorias para toda la flota del computador.
     * Limpia el tablero antes de colocar los barcos y reintenta
     * posiciones hasta encontrar una válida para cada barco.
     */
    public void generarPosicionesAleatorias() {
        getTablero().limpiar();
        List<Barco> barcos = BarcoFactory.crearFlotaCompleta();
        Random random = new Random();

        for (Barco barco : barcos) {
            boolean colocado = false;
            int intentos = 0;

            while (!colocado && intentos < 1000) {
                int fila = random.nextInt(Tablero.TAMANIO);
                int columna = random.nextInt(Tablero.TAMANIO);
                Orientacion orientacion = random.nextBoolean()
                    ? Orientacion.HORIZONTAL
                    : Orientacion.VERTICAL;

                try {
                    getTablero().colocarBarco(barco, fila, columna, orientacion);
                    colocado = true;
                } catch (FueraDeTableroException | BarcoSuperpuestoException e) {
                    intentos++;
                }
            }
        }
    }
}
