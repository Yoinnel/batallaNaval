package com.batallaNaval.patterns;

import com.batallaNaval.model.Juego;
import com.batallaNaval.persistence.ArchivoPlano;
import com.batallaNaval.persistence.ArchivoSerializado;

import java.util.List;

/**
 * Patrón Estructural: Facade.
 *
 * Proporciona una interfaz simplificada y unificada para las operaciones
 * de persistencia del juego. Encapsula la complejidad de trabajar con
 * dos subsistemas diferentes (ArchivoPlano y ArchivoSerializado) detrás
 * de métodos claros y sencillos.
 */
public class PersistenciaFacade {

    private final ArchivoPlano archivoPlano;
    private final ArchivoSerializado archivoSerializado;

    public PersistenciaFacade() {
        this.archivoPlano = new ArchivoPlano();
        this.archivoSerializado = new ArchivoSerializado();
    }


    /**
     * Guarda el estado actual de la partida en un archivo serializado.
     *
     * @param juego estado del juego a guardar
     */
    public void guardarPartida(Juego juego) {
        archivoSerializado.guardar(juego);
    }

    /**
     * Carga una partida guardada previamente.
     *
     * @return el estado del juego cargado, o null si no existe o hay error
     */
    public Juego cargarPartida() {
        return archivoSerializado.cargar();
    }

    /**
     * @return true si existe un archivo de partida guardada
     */
    public boolean existePartidaGuardada() {
        return archivoSerializado.existeArchivo();
    }

    /**
     * Elimina el archivo de partida guardada.
     */
    public void eliminarPartidaGuardada() {
        archivoSerializado.eliminar();
    }


    /**
     * Registra el resultado de una partida en las estadísticas.
     *
     * @param nickname nombre del jugador
     * @param victoria true si ganó, false si perdió
     */
    public void guardarEstadistica(String nickname, boolean victoria) {
        archivoPlano.registrarPartida(nickname, victoria);
    }

    /**
     * Carga todas las estadísticas de jugadores.
     *
     * @return lista de arreglos {nickname, jugadas, ganadas, perdidas}
     */
    public List<String[]> cargarEstadisticas() {
        return archivoPlano.leerEstadisticas();
    }
}
