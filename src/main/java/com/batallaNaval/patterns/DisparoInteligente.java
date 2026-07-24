package com.batallaNaval.patterns;

import com.batallaNaval.model.Tablero;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Estrategia concreta: Disparo Inteligente.
 *
 * Cuando la IA toca un barco, agrega las casillas adyacentes (arriba,
 * abajo, izquierda, derecha) como objetivos prioritarios. Mientras haya
 * objetivos prioritarios válidos, los dispara en orden. Si se agotan,
 * delega al DisparoAleatorio como fallback.
 *
 * Utiliza una cola (Deque/ArrayDeque) como estructura de datos auxiliar
 * para gestionar los objetivos prioritarios.
 */
public class DisparoInteligente implements EstrategiaDisparo {

    private static final long serialVersionUID = 1L;

    /** Cola de objetivos prioritarios (casillas adyacentes a un impacto) */
    private Deque<int[]> objetivosPrioritarios;

    /** Estrategia de respaldo cuando no hay objetivos prioritarios */
    private DisparoAleatorio fallback;

    public DisparoInteligente() {
        this.objetivosPrioritarios = new ArrayDeque<>();
        this.fallback = new DisparoAleatorio();
    }

    /**
     * Selecciona el próximo objetivo. Primero intenta los objetivos
     * prioritarios (adyacentes a impactos). Si no hay válidos, usa
     * la estrategia aleatoria.
     *
     * @param tablero tablero del oponente
     * @return arreglo {fila, columna} del objetivo
     */
    @Override
    public int[] seleccionarObjetivo(Tablero tablero) {

        while (!objetivosPrioritarios.isEmpty()) {
            int[] objetivo = objetivosPrioritarios.poll();
            int f = objetivo[0];
            int c = objetivo[1];

            // Validar que el objetivo esté dentro del tablero y no haya sido disparado
            if (f >= 0 && f < Tablero.TAMANIO
                && c >= 0 && c < Tablero.TAMANIO
                && !tablero.getCasilla(f, c).fueDisparada()) {
                return objetivo;
            }
        }

        return fallback.seleccionarObjetivo(tablero);
    }

    /**
     * Agrega las 4 casillas adyacentes a un impacto como objetivos prioritarios.
     *
     * @param fila    fila del impacto
     * @param columna columna del impacto
     */
    public void agregarObjetivoPrioritario(int fila, int columna) {
        int[][] adyacentes = {
            {fila - 1, columna},  // Arriba
            {fila + 1, columna},  // Abajo
            {fila, columna - 1},  // Izquierda
            {fila, columna + 1}   // Derecha
        };

        for (int[] adj : adyacentes) {
            objetivosPrioritarios.add(adj);
        }
    }

    /**
     * Limpia todos los objetivos prioritarios.
     * Se llama cuando un barco ha sido hundido completamente.
     */
    public void limpiarObjetivosPrioritarios() {
        objetivosPrioritarios.clear();
    }

    /**
     * @return true si hay objetivos prioritarios pendientes
     */
    public boolean tieneObjetivosPrioritarios() {
        return !objetivosPrioritarios.isEmpty();
    }
}
