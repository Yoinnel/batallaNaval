package com.batallaNaval.exceptions;

/**
 * Excepción lanzada cuando se intenta colocar un barco fuera de los
 * límites del tablero 10×10.
 */
public class FueraDeTableroException extends Exception {

    public FueraDeTableroException(String message) {
        super(message);
    }
}
