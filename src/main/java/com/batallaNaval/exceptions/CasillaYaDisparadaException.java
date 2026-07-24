package com.batallaNaval.exceptions;

/**
 * Excepción lanzada cuando se intenta disparar a una casilla que
 * ya fue atacada previamente.
 */
public class CasillaYaDisparadaException extends Exception {

    public CasillaYaDisparadaException(String message) {
        super(message);
    }
}
