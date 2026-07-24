package com.batallaNaval.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un barco dentro del juego de Batalla Naval.
 * Cada barco tiene un nombre, tamaño (cantidad de casillas que ocupa),
 * orientación, posición de inicio y un contador de impactos recibidos.
 */
public class Barco implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private int tamanio;
    private Orientacion orientacion;
    private int filaInicio;
    private int columnaInicio;
    private boolean colocado;
    private List<int[]> posiciones;
    private int impactos;

    /**
     * Crea un nuevo barco con el nombre y tamaño indicados.
     * Por defecto se crea en orientación HORIZONTAL y sin colocar.
     *
     * @param nombre  nombre del barco (ej: "Portaaviones")
     * @param tamanio cantidad de casillas que ocupa (1-4)
     */
    public Barco(String nombre, int tamanio) {
        this.nombre = nombre;
        this.tamanio = tamanio;
        this.orientacion = Orientacion.HORIZONTAL;
        this.colocado = false;
        this.posiciones = new ArrayList<>();
        this.impactos = 0;
    }


    /**
     * Registra un impacto en este barco. Se debe llamar cuando un disparo
     * acierta en una de las casillas que ocupa.
     */
    public void registrarImpacto() {
        impactos++;
    }

    /**
     * @return true si la cantidad de impactos iguala o supera el tamaño del barco
     */
    public boolean estaHundido() {
        return impactos >= tamanio;
    }

    /**
     * Devuelve el nombre del archivo de imagen PNG asociado a este tipo de barco.
     *
     * @return nombre del archivo de imagen
     */
    public String getImagenNombre() {
        return switch (nombre.toLowerCase()) {
            case "portaaviones" -> "portaaviones.png";
            case "submarino"    -> "submarino.png";
            case "destructor"   -> "destructor.png";
            default             -> "fragata.png";
        };
    }



    public String getNombre() {
        return nombre;
    }

    public int getTamanio() {
        return tamanio;
    }

    public Orientacion getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(Orientacion orientacion) {
        this.orientacion = orientacion;
    }

    public int getFilaInicio() {
        return filaInicio;
    }

    public void setFilaInicio(int filaInicio) {
        this.filaInicio = filaInicio;
    }

    public int getColumnaInicio() {
        return columnaInicio;
    }

    public void setColumnaInicio(int columnaInicio) {
        this.columnaInicio = columnaInicio;
    }

    public boolean isColocado() {
        return colocado;
    }

    public void setColocado(boolean colocado) {
        this.colocado = colocado;
    }

    public List<int[]> getPosiciones() {
        return posiciones;
    }

    public void setPosiciones(List<int[]> posiciones) {
        this.posiciones = posiciones;
    }

    public int getImpactos() {
        return impactos;
    }

    @Override
    public String toString() {
        return nombre + " (tamaño=" + tamanio + ", hundido=" + estaHundido() + ")";
    }
}
