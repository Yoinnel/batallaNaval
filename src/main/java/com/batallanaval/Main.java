package com.batallanaval;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Juego "Batalla Naval"
 * Desarrollado usando JavaFX, IntelliJ Idea y SceneBuilder
 * Languages used: Java
 * <p>
 * Descripción del juego: Batalla Naval es un juego de estrategia en el cual tu objetivo es hundir todos los barcos de
 * tu oponente, antes de que este destruya tus barcos.
 *<p>
 * @author Estaban Granada Salamanca
 * @author Yoinnel Gabriel Martinez Brito
 *<p>
 * @version 1.0
 * @since 2026
 */

// Esta clase se encarga de crear la ventana principal del juego con un tamaño inicial de 1280 x 720 pixeles.

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinWidth(1280);
        primaryStage.setMinHeight(720);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}