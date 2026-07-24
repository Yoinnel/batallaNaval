package com.batallaNaval.model;

import java.io.Serializable;

/**
 * Representa al jugador humano en la partida.
 * Contiene su nickname y su tablero de juego.
 */
public class Jugador implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nickname;
    private Tablero tablero;

    /**
     * Crea un nuevo jugador con el nickname proporcionado y un tablero vacío.
     *
     * @param nickname nombre del jugador
     */
    public Jugador(String nickname) {
        this.nickname = nickname;
        this.tablero = new Tablero();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public void setTablero(Tablero tablero) {
        this.tablero = tablero;
    }

    @Override
    public String toString() {
        return "Jugador: " + nickname;
    }
}
