package com.batallaNaval.model;

import java.io.Serializable;

/**
 * Enum que representa los posibles estados de una casilla en el tablero.
 * Se utiliza para controlar la lógica de disparos y la representación visual.
 */
public enum EstadoCasilla implements Serializable {

    /** Casilla vacía, sin barco ni disparo */
    VACIA,

    /** Casilla ocupada por un barco (no disparada aún) */
    OCUPADA,

    /** Disparo fallido — no hay barco en esta casilla */
    AGUA,

    /** Disparo acertado — hay barco pero no está hundido todavía */
    TOCADO,

    /** Barco completamente hundido — todas sus casillas fueron tocadas */
    HUNDIDO
}
