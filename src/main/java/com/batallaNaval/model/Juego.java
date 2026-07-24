package com.batallaNaval.model;

import java.io.Serializable;

/**
 * Clase principal del estado del juego. Es Serializable para permitir
 * guardar y cargar partidas. Contiene referencias al jugador, al computador,
 * el turno actual y contadores de disparos.
 */
public class Juego implements Serializable {

    private static final long serialVersionUID = 1L;

    private Jugador jugador;
    private Computador computador;
    private boolean turnoJugador;
    private boolean gameOver;
    private String ganador;
    private int disparosJugador;
    private int disparosComputador;

    /**
     * Crea una nueva partida con el nickname del jugador.
     *
     * @param nickname nombre del jugador humano
     */
    public Juego(String nickname) {
        this.jugador = new Jugador(nickname);
        this.computador = new Computador();
        this.turnoJugador = true;
        this.gameOver = false;
        this.ganador = null;
        this.disparosJugador = 0;
        this.disparosComputador = 0;
    }

    /**
     * Incrementa el contador de disparos del jugador.
     */
    public void incrementarDisparosJugador() {
        disparosJugador++;
    }

    /**
     * Incrementa el contador de disparos del computador.
     */
    public void incrementarDisparosComputador() {
        disparosComputador++;
    }

    /**
     * Verifica si alguno de los dos jugadores ha ganado.
     * Si todos los barcos del computador fueron hundidos, gana el jugador.
     * Si todos los barcos del jugador fueron hundidos, gana el computador.
     */
    public void verificarFinJuego() {
        if (computador.getTablero().todosHundidos()) {
            gameOver = true;
            ganador = jugador.getNickname();
        } else if (jugador.getTablero().todosHundidos()) {
            gameOver = true;
            ganador = "Computador";
        }
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Computador getComputador() {
        return computador;
    }

    public boolean isTurnoJugador() {
        return turnoJugador;
    }

    public void setTurnoJugador(boolean turnoJugador) {
        this.turnoJugador = turnoJugador;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public int getDisparosJugador() {
        return disparosJugador;
    }

    public int getDisparosComputador() {
        return disparosComputador;
    }
}
