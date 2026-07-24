package com.batallaNaval.util;

import com.batallaNaval.controller.ColocacionController;
import com.batallaNaval.controller.JuegoController;
import com.batallaNaval.model.Juego;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utilidad para la navegación entre las diferentes vistas (FXML) del juego.
 * Centraliza el cambio de escenas y la carga de hojas de estilo CSS.
 * Gestiona la referencia al Stage principal de la aplicación.
 */
public class NavegadorVistas {

    private static Stage stagePrincipal;

    /**
     * Establece la referencia al Stage principal de la aplicación.
     * Debe llamarse una vez al iniciar la aplicación.
     *
     * @param stage el Stage principal
     */
    public static void setStage(Stage stage) {
        stagePrincipal = stage;
    }

    /**
     * @return el Stage principal de la aplicación
     */
    public static Stage getStage() {
        return stagePrincipal;
    }

    /**
     * Navega a la pantalla de Inicio.
     */
    public static void irAInicio() {
        cargarVista("/com/batallaNaval/fxml/Inicio.fxml", "Batalla Naval - Inicio");
    }

    /**
     * Navega a la pantalla de Colocación de barcos.
     * Pasa el objeto Juego al controlador para inicializarlo.
     *
     * @param juego estado del juego actual
     */
    public static void irAColocacion(Juego juego) {
        try {
            FXMLLoader loader = new FXMLLoader(
                NavegadorVistas.class.getResource("/com/batallaNaval/fxml/Colocacion.fxml"));
            Parent root = loader.load();

            ColocacionController controller = loader.getController();
            controller.inicializar(juego);

            Scene scene = new Scene(root);
            agregarEstilos(scene);
            stagePrincipal.setScene(scene);
            stagePrincipal.setTitle("Batalla Naval - Colocación de Barcos");
            stagePrincipal.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al cargar vista de Colocación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navega a la pantalla de Juego (batalla).
     * Pasa el objeto Juego al controlador para inicializarlo.
     *
     * @param juego estado del juego actual
     */
    public static void irAJuego(Juego juego) {
        try {
            FXMLLoader loader = new FXMLLoader(
                NavegadorVistas.class.getResource("/com/batallaNaval/fxml/Juego.fxml"));
            Parent root = loader.load();

            JuegoController controller = loader.getController();
            controller.inicializar(juego);

            Scene scene = new Scene(root);
            agregarEstilos(scene);
            stagePrincipal.setScene(scene);
            stagePrincipal.setTitle("Batalla Naval - ¡En Batalla!");
            stagePrincipal.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al cargar vista de Juego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga una vista FXML genérica (sin pasar datos al controlador).
     *
     * @param fxmlPath ruta del archivo FXML
     * @param titulo   título de la ventana
     */
    private static void cargarVista(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                NavegadorVistas.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            agregarEstilos(scene);
            stagePrincipal.setScene(scene);
            stagePrincipal.setTitle(titulo);
            stagePrincipal.centerOnScreen();
        } catch (IOException e) {
            System.err.println("Error al cargar vista: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Agrega la hoja de estilos CSS a la escena.
     *
     * @param scene escena a la que agregar los estilos
     */
    private static void agregarEstilos(Scene scene) {
        String css = NavegadorVistas.class.getResource("/com/batallaNaval/css/styles.css")
            .toExternalForm();
        scene.getStylesheets().add(css);
    }
}
