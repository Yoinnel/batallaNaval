package com.batallaNaval.model;

import com.batallaNaval.patterns.DisparoAleatorio;
import com.batallaNaval.patterns.DisparoInteligente;
import com.batallaNaval.patterns.EstrategiaDisparo;

import java.io.Serializable;

/**
 * Inteligencia Artificial del computador.
 * Utiliza el patrón Strategy para alternar entre dos modos de disparo:
 *  - DisparoAleatorio: selecciona casillas al azar.
 *  - DisparoInteligente: cuando toca un barco, prioriza casillas adyacentes.
 */
public class IA implements Serializable {

    private static final long serialVersionUID = 1L;

    private EstrategiaDisparo estrategia;
    private DisparoInteligente estrategiaInteligente;
    private DisparoAleatorio estrategiaAleatoria;

    /**
     * Inicializa la IA con la estrategia aleatoria como predeterminada.
     */
    public IA() {
        this.estrategiaAleatoria = new DisparoAleatorio();
        this.estrategiaInteligente = new DisparoInteligente();
        this.estrategia = estrategiaAleatoria;
    }

    /**
     * Selecciona el siguiente objetivo de disparo según la estrategia activa.
     *
     * @param tableroEnemigo tablero del jugador humano
     * @return arreglo {fila, columna} del objetivo seleccionado
     */
    public int[] seleccionarObjetivo(Tablero tableroEnemigo) {
        return estrategia.seleccionarObjetivo(tableroEnemigo);
    }

    /**
     * Cambia la estrategia a modo inteligente (priorizar adyacentes).
     */
    public void cambiarAInteligente() {
        this.estrategia = estrategiaInteligente;
    }

    /**
     * Cambia la estrategia a modo aleatorio.
     */
    public void cambiarAAleatorio() {
        this.estrategia = estrategiaAleatoria;
    }

    /**
     * Notifica a la IA que acertó un disparo (TOCADO).
     * Agrega las casillas adyacentes como objetivos prioritarios
     * y cambia a modo inteligente.
     *
     * @param fila    fila del impacto
     * @param columna columna del impacto
     */
    public void notificarImpacto(int fila, int columna) {
        estrategiaInteligente.agregarObjetivoPrioritario(fila, columna);
        cambiarAInteligente();
    }

    /**
     * Notifica a la IA que hundió un barco completo.
     * Limpia los objetivos prioritarios y vuelve al modo aleatorio.
     */
    public void notificarHundido() {
        estrategiaInteligente.limpiarObjetivosPrioritarios();
        cambiarAAleatorio();
    }

    /**
     * Notifica a la IA que su disparo cayó al agua.
     * Si no quedan objetivos prioritarios, vuelve al modo aleatorio.
     */
    public void notificarAgua() {
        if (!estrategiaInteligente.tieneObjetivosPrioritarios()) {
            cambiarAAleatorio();
        }
    }


    public EstrategiaDisparo getEstrategia() {
        return estrategia;
    }
}
