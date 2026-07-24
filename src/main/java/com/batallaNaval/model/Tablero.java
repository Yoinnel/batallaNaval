package com.batallaNaval.model;

import com.batallaNaval.exceptions.BarcoSuperpuestoException;
import com.batallaNaval.exceptions.CasillaYaDisparadaException;
import com.batallaNaval.exceptions.FueraDeTableroException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el tablero de juego de 10×10 casillas.
 * Gestiona la colocación de barcos, la recepción de disparos y
 * la consulta del estado general de la flota.
 */
public class Tablero implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Tamaño del tablero (10×10) */
    public static final int TAMANIO = 10;

    private Casilla[][] casillas;
    private List<Barco> barcos;

    /**
     * Crea un tablero vacío de 10×10.
     */
    public Tablero() {
        casillas = new Casilla[TAMANIO][TAMANIO];
        barcos = new ArrayList<>();
        inicializarCasillas();
    }

    /**
     * Inicializa todas las casillas del tablero como VACIAS.
     */
    private void inicializarCasillas() {
        for (int i = 0; i < TAMANIO; i++) {
            for (int j = 0; j < TAMANIO; j++) {
                casillas[i][j] = new Casilla(i, j);
            }
        }
    }

    /**
     * Coloca un barco en el tablero en la posición y orientación indicadas.
     *
     * @param barco       barco a colocar
     * @param fila        fila de inicio (0-9)
     * @param columna     columna de inicio (0-9)
     * @param orientacion orientación del barco
     * @throws FueraDeTableroException   si el barco se sale de los límites
     * @throws BarcoSuperpuestoException si la posición está ocupada por otro barco
     */
    public void colocarBarco(Barco barco, int fila, int columna, Orientacion orientacion)
            throws FueraDeTableroException, BarcoSuperpuestoException {

        // Validar límites del tablero
        if (orientacion == Orientacion.HORIZONTAL) {
            if (fila < 0 || fila >= TAMANIO || columna < 0 || columna + barco.getTamanio() > TAMANIO) {
                throw new FueraDeTableroException(
                    "El barco '" + barco.getNombre() + "' se sale del tablero en posición ("
                    + fila + ", " + columna + ") horizontal.");
            }
        } else {
            if (columna < 0 || columna >= TAMANIO || fila < 0 || fila + barco.getTamanio() > TAMANIO) {
                throw new FueraDeTableroException(
                    "El barco '" + barco.getNombre() + "' se sale del tablero en posición ("
                    + fila + ", " + columna + ") vertical.");
            }
        }

        // Calcular posiciones y verificar superposiciones
        List<int[]> posiciones = new ArrayList<>();
        for (int i = 0; i < barco.getTamanio(); i++) {
            int f = (orientacion == Orientacion.VERTICAL)   ? fila + i    : fila;
            int c = (orientacion == Orientacion.HORIZONTAL) ? columna + i : columna;

            if (casillas[f][c].tieneBarco()) {
                throw new BarcoSuperpuestoException(
                    "La casilla (" + f + ", " + c + ") ya está ocupada por '"
                    + casillas[f][c].getBarco().getNombre() + "'.");
            }
            posiciones.add(new int[]{f, c});
        }

        // Colocar el barco
        barco.setOrientacion(orientacion);
        barco.setFilaInicio(fila);
        barco.setColumnaInicio(columna);
        barco.setPosiciones(posiciones);
        barco.setColocado(true);

        for (int[] pos : posiciones) {
            casillas[pos[0]][pos[1]].setBarco(barco);
            casillas[pos[0]][pos[1]].setEstado(EstadoCasilla.OCUPADA);
        }

        barcos.add(barco);
    }

    /**
     * Recibe un disparo en la casilla indicada y devuelve el resultado.
     *
     * @param fila    fila del disparo (0-9)
     * @param columna columna del disparo (0-9)
     * @return el estado resultante: AGUA, TOCADO o HUNDIDO
     * @throws CasillaYaDisparadaException si la casilla ya fue atacada
     */
    public EstadoCasilla recibirDisparo(int fila, int columna) throws CasillaYaDisparadaException {
        Casilla casilla = casillas[fila][columna];

        if (casilla.fueDisparada()) {
            throw new CasillaYaDisparadaException(
                "La casilla (" + fila + ", " + columna + ") ya fue disparada.");
        }

        if (casilla.tieneBarco()) {
            Barco barco = casilla.getBarco();
            barco.registrarImpacto();

            if (barco.estaHundido()) {
                // Marcar todas las casillas del barco como HUNDIDO
                for (int[] pos : barco.getPosiciones()) {
                    casillas[pos[0]][pos[1]].setEstado(EstadoCasilla.HUNDIDO);
                }
                return EstadoCasilla.HUNDIDO;
            } else {
                casilla.setEstado(EstadoCasilla.TOCADO);
                return EstadoCasilla.TOCADO;
            }
        } else {
            casilla.setEstado(EstadoCasilla.AGUA);
            return EstadoCasilla.AGUA;
        }
    }

    /**
     * @return true si todos los barcos del tablero están hundidos
     */
    public boolean todosHundidos() {
        for (Barco barco : barcos) {
            if (!barco.estaHundido()) {
                return false;
            }
        }
        return !barcos.isEmpty();
    }

    /**
     * Limpia completamente el tablero y la lista de barcos.
     */
    public void limpiar() {
        barcos.clear();
        inicializarCasillas();
    }

    /**
     * Remueve un barco específico del tablero.
     *
     * @param barco barco a remover
     */
    public void removerBarco(Barco barco) {
        if (barco.isColocado()) {
            for (int[] pos : barco.getPosiciones()) {
                casillas[pos[0]][pos[1]].setBarco(null);
                casillas[pos[0]][pos[1]].setEstado(EstadoCasilla.VACIA);
            }
            barco.setColocado(false);
            barco.setPosiciones(new ArrayList<>());
            barcos.remove(barco);
        }
    }

    /**
     * @return cantidad de barcos que han sido hundidos
     */
    public int getBarcosHundidos() {
        return (int) barcos.stream().filter(Barco::estaHundido).count();
    }

    /**
     * @return cantidad de barcos que aún no han sido hundidos
     */
    public int getBarcosRestantes() {
        return (int) barcos.stream().filter(b -> !b.estaHundido()).count();
    }

    // ── Getters ────────────────────────────────────────────────────

    public Casilla[][] getCasillas() {
        return casillas;
    }

    public Casilla getCasilla(int fila, int columna) {
        return casillas[fila][columna];
    }

    public List<Barco> getBarcos() {
        return barcos;
    }
}
