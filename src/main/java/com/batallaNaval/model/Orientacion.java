package com.batallaNaval.model;

import java.io.Serializable;

/**
 * Enum que representa la orientación de un barco al ser colocado en el tablero.
 */
public enum Orientacion implements Serializable {

    /** El barco se extiende hacia la derecha (columna + tamaño) */
    HORIZONTAL,

    /** El barco se extiende hacia abajo (fila + tamaño) */
    VERTICAL
}
