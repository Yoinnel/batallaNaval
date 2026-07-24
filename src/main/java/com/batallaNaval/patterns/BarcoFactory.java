package com.batallaNaval.patterns;

import com.batallaNaval.model.Barco;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Creacional
 *
 * Centraliza la creación de barcos según su tipo.
 * Permite crear barcos individuales o la flota completa del juego.
 *
 * Flota normal:
 *  - 1 Portaaviones (4 casillas)
 *  - 2 Submarinos (3 casillas c/u)
 *  - 3 Destructores (2 casillas c/u)
 *  - 4 Fragatas (1 casilla c/u)
 *  Total: 10 barcos, 20 casillas ocupadas
 */
public class BarcoFactory {

    /**
     * Crea un barco del tipo especificado.
     *
     * @param tipo tipo del barco (PORTAAVIONES, SUBMARINO, DESTRUCTOR, FRAGATA)
     * @return nueva instancia de Barco configurada
     * @throws IllegalArgumentException si el tipo no es reconocido
     */
    public static Barco crearBarco(String tipo) {
        return switch (tipo.toUpperCase()) {
            case "PORTAAVIONES" -> new Barco("Portaaviones", 4);
            case "SUBMARINO"    -> new Barco("Submarino", 3);
            case "DESTRUCTOR"   -> new Barco("Destructor", 2);
            case "FRAGATA"      -> new Barco("Fragata", 1);
            default -> throw new IllegalArgumentException("Tipo de barco desconocido: " + tipo);
        };
    }

    /**
     * Crea la flota completa con todos los barcos del juego.
     *
     * @return lista con los 10 barcos de la flota
     */
    public static List<Barco> crearFlotaCompleta() {
        List<Barco> flota = new ArrayList<>();

        // 1 Portaaviones (tamaño 4)
        flota.add(crearBarco("PORTAAVIONES"));

        // 2 Submarinos (tamaño 3)
        flota.add(crearBarco("SUBMARINO"));
        flota.add(crearBarco("SUBMARINO"));

        // 3 Destructores (tamaño 2)
        flota.add(crearBarco("DESTRUCTOR"));
        flota.add(crearBarco("DESTRUCTOR"));
        flota.add(crearBarco("DESTRUCTOR"));

        // 4 Fragatas (tamaño 1)
        flota.add(crearBarco("FRAGATA"));
        flota.add(crearBarco("FRAGATA"));
        flota.add(crearBarco("FRAGATA"));
        flota.add(crearBarco("FRAGATA"));

        return flota;
    }
}
