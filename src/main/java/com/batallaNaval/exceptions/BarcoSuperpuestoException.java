package com.batallaNaval.exceptions;

/**
 * Excepción lanzada cuando se intenta colocar un barco en una posición
 * que ya está ocupada por otro barco.
 */
public class BarcoSuperpuestoException extends Exception {

    public BarcoSuperpuestoException(String message) {
        super(message);
    }
}
