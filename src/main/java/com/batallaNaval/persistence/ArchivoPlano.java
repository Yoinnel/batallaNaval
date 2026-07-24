package com.batallaNaval.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestiona la lectura y escritura de estadísticas de jugadores
 * en un archivo plano de texto (CSV).
 *
 * Formato del archivo: nickname,partidasJugadas,partidasGanadas,partidasPerdidas
 * Ruta: data/estadisticas.txt
 */
public class ArchivoPlano {

    private static final String RUTA = "data/estadisticas.txt";

    public ArchivoPlano() {
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
     * Registra una partida para el nickname dado.
     * Si el nickname ya existe, actualiza sus contadores.
     * Si es nuevo, lo agrega con una partida jugada.
     *
     * @param nickname nombre del jugador
     * @param victoria true si ganó, false si perdió
     */
    public void registrarPartida(String nickname, boolean victoria) {
        Map<String, int[]> stats = leerEstadisticasMap();

        int[] datos = stats.getOrDefault(nickname, new int[]{0, 0, 0});
        datos[0]++; // Partidas jugadas
        if (victoria) {
            datos[1]++; // Ganadas
        } else {
            datos[2]++; // Perdidas
        }
        stats.put(nickname, datos);

        escribirEstadisticas(stats);
    }

    /**
     * Lee todas las estadísticas del archivo plano.
     *
     * @return lista de arreglos String[] con {nickname, jugadas, ganadas, perdidas}
     */
    public List<String[]> leerEstadisticas() {
        List<String[]> resultado = new ArrayList<>();
        File archivo = new File(RUTA);

        if (!archivo.exists()) {
            return resultado;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 4) {
                    resultado.add(partes);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer estadísticas: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Lee las estadísticas como un mapa para facilitar la actualización.
     *
     * @return mapa de nickname → {jugadas, ganadas, perdidas}
     */
    private Map<String, int[]> leerEstadisticasMap() {
        Map<String, int[]> map = new LinkedHashMap<>();

        for (String[] datos : leerEstadisticas()) {
            try {
                map.put(datos[0], new int[]{
                    Integer.parseInt(datos[1].trim()),
                    Integer.parseInt(datos[2].trim()),
                    Integer.parseInt(datos[3].trim())
                });
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear estadística: " + e.getMessage());
            }
        }

        return map;
    }

    /**
     * Escribe todas las estadísticas al archivo plano, sobrescribiéndolo.
     *
     * @param stats mapa de estadísticas a escribir
     */
    private void escribirEstadisticas(Map<String, int[]> stats) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA))) {
            for (Map.Entry<String, int[]> entry : stats.entrySet()) {
                int[] d = entry.getValue();
                bw.write(entry.getKey() + "," + d[0] + "," + d[1] + "," + d[2]);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir estadísticas: " + e.getMessage());
        }
    }
}
