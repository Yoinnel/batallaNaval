package com.batallaNaval.patterns;

import com.batallaNaval.model.Tablero;

import java.io.Serializable;

/**
 * Patrón de Comportamiento: Strategy.
 *
 * Interfaz que define el contrato para las estrategias de disparo
 * de la inteligencia artificial del computador.
 *
 * Implementaciones:
 *  - DisparoAleatorio:   selecciona casillas al azar no disparadas.
 *  - DisparoInteligente:  prioriza casillas adyacentes a un impacto.
 */
public interface EstrategiaDisparo extends Serializable {

    /**
     * Selecciona las coordenadas del próximo disparo.
     *
     * @param tablero tablero del oponente
     * @return arreglo {fila, columna} del objetivo, o null si no hay casillas disponibles
     */
    int[] seleccionarObjetivo(Tablero tablero);
}
