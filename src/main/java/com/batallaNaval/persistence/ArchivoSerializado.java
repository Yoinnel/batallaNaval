package com.batallaNaval.persistence;

import com.batallaNaval.model.Juego;

import java.io.*;

/**
 * Gestiona la serialización y deserialización del estado del juego.
 * Permite guardar y cargar partidas completas mediante archivos binarios (.dat).
 *
 * Ruta: data/partida_guardada.dat
 */
public class ArchivoSerializado {

    private static final String RUTA = "data/partida_guardada.dat";

    public ArchivoSerializado() {
        crearDirectorioSiNoExiste();
    }

    /**
     * Crea el directorio "data" si no existe.
     */
    private void crearDirectorioSiNoExiste() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Serializa el objeto Juego al archivo binario.
     *
     * @param juego estado del juego a guardar
     */
    public void guardar(Juego juego) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(RUTA))) {
            oos.writeObject(juego);
            System.out.println("Partida guardada exitosamente en: " + RUTA);
        } catch (IOException e) {
            System.err.println("Error al guardar partida: " + e.getMessage());
        }
    }

    /**
     * Deserializa el objeto Juego desde el archivo binario.
     *
     * @return el estado del juego cargado, o null si ocurre un error
     */
    public Juego cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(RUTA))) {
            return (Juego) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al cargar partida: " + e.getMessage());
            return null;
        }
    }

    /**
     * @return true si existe un archivo de partida guardada
     */
    public boolean existeArchivo() {
        return new File(RUTA).exists();
    }

    /**
     * Elimina el archivo de partida guardada.
     */
    public void eliminar() {
        File archivo = new File(RUTA);
        if (archivo.exists()) {
            if (archivo.delete()) {
                System.out.println("Archivo de partida eliminado.");
            }
        }
    }
}
