package com.batallaNaval.patterns;

import com.batallaNaval.model.Tablero;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Estrategia concreta: Disparo Aleatorio.
 *
 * Selecciona una casilla al azar entre todas las casillas que aún
 * no han sido disparadas en el tablero del oponente.
 */
public class DisparoAleatorio implements EstrategiaDisparo {

    private static final long serialVersionUID = 1L;

    private Random random;

    public DisparoAleatorio() {
        this.random = new Random();
    }

    /**
     * Selecciona aleatoriamente una casilla no disparada.
     *
     * @param tablero tablero del oponente
     * @return arreglo {fila, columna} del objetivo, o null si no quedan casillas
     */
    @Override
    public int[] seleccionarObjetivo(Tablero tablero) {
        List<int[]> disponibles = new ArrayList<>();

        for (int i = 0; i < Tablero.TAMANIO; i++) {
            for (int j = 0; j < Tablero.TAMANIO; j++) {
                if (!tablero.getCasilla(i, j).fueDisparada()) {
                    disponibles.add(new int[]{i, j});
                }
            }
        }

        if (disponibles.isEmpty()) {
            return null;
        }

        return disponibles.get(random.nextInt(disponibles.size()));
    }
}
