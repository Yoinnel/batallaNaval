package com.batallaNaval.controller;

import com.batallaNaval.exceptions.BarcoSuperpuestoException;
import com.batallaNaval.exceptions.FueraDeTableroException;
import com.batallaNaval.model.*;
import com.batallaNaval.patterns.BarcoFactory;
import com.batallaNaval.util.NavegadorVistas;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controlador de la pantalla de Colocación de barcos (Colocacion.fxml).
 *
 * Responsabilidades:
 *  - Construir el tablero visual del jugador (10×10).
 *  - Mostrar los barcos disponibles en el panel lateral.
 *  - Permitir seleccionar un barco y colocarlo haciendo clic en el tablero.
 *  - Rotar orientación, colocación aleatoria, limpiar tablero.
 *  - Ver tablero del computador (popup de verificación).
 *  - Iniciar la batalla cuando todos los barcos estén colocados.
 */
public class ColocacionController {

    @FXML private GridPane gridTablero;
    @FXML private VBox panelBarcos;
    @FXML private Button btnRotar;
    @FXML private Button btnAleatorio;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVerEnemigo;
    @FXML private Button btnIniciarBatalla;
    @FXML private Label lblOrientacion;
    @FXML private Label lblInfo;

    private Juego juego;
    private Orientacion orientacionActual = Orientacion.HORIZONTAL;
    private List<Barco> barcosDisponibles;
    private Barco barcoSeleccionado;
    private Pane[][] celdas;

    /**
     * Inicializa el controlador con el estado del juego.
     * Genera las posiciones del computador, crea la flota del jugador,
     * y construye la interfaz visual.
     *
     * @param juego estado del juego actual
     */
    public void inicializar(Juego juego) {
        this.juego = juego;
        this.celdas = new Pane[Tablero.TAMANIO][Tablero.TAMANIO];


        juego.getComputador().generarPosicionesAleatorias();


        barcosDisponibles = BarcoFactory.crearFlotaCompleta();

        construirTablero();
        construirPanelBarcos();
        actualizarInfoOrientacion();
    }


    /**
     * Construye el GridPane del tablero con encabezados de fila/columna
     * y celdas interactivas.
     */
    private void construirTablero() {
        gridTablero.getChildren().clear();

        // Encabezados de columna (A-J)
        for (int col = 0; col < Tablero.TAMANIO; col++) {
            Label lbl = new Label(String.valueOf((char) ('A' + col)));
            lbl.getStyleClass().add("header-label");
            lbl.setMinSize(40, 40);
            lbl.setAlignment(Pos.CENTER);
            gridTablero.add(lbl, col + 1, 0);
        }

        // Encabezados de fila (1-10)
        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            Label lbl = new Label(String.valueOf(fila + 1));
            lbl.getStyleClass().add("header-label");
            lbl.setMinSize(40, 40);
            lbl.setAlignment(Pos.CENTER);
            gridTablero.add(lbl, 0, fila + 1);
        }

        // Celdas del tablero
        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int col = 0; col < Tablero.TAMANIO; col++) {
                Pane celda = crearCelda(fila, col);
                celdas[fila][col] = celda;
                gridTablero.add(celda, col + 1, fila + 1);
            }
        }
    }

    /**
     * Crea una celda interactiva del tablero con eventos de clic y hover.
     */
    private Pane crearCelda(int fila, int col) {
        Pane celda = new Pane();
        celda.setMinSize(40, 40);
        celda.setMaxSize(40, 40);
        celda.getStyleClass().add("celda");
        celda.getStyleClass().add("celda-vacia");

        // Clic para colocar barco
        celda.setOnMouseClicked(e -> colocarBarcoEnCelda(fila, col));

        // Hover para mostrar preview de colocación
        celda.setOnMouseEntered(e -> {
            if (barcoSeleccionado != null) {
                mostrarPreview(fila, col);
            }
        });

        celda.setOnMouseExited(e -> limpiarPreview());

        return celda;
    }



    /**
     * Muestra una previsualización del barco seleccionado en la posición
     * actual del cursor. Verde si es válida, rojo si no.
     */
    private void mostrarPreview(int fila, int col) {
        limpiarPreview();
        if (barcoSeleccionado == null) return;

        boolean valido = true;
        List<int[]> posiciones = new ArrayList<>();

        for (int i = 0; i < barcoSeleccionado.getTamanio(); i++) {
            int f = (orientacionActual == Orientacion.VERTICAL) ? fila + i : fila;
            int c = (orientacionActual == Orientacion.HORIZONTAL) ? col + i : col;

            if (f >= Tablero.TAMANIO || c >= Tablero.TAMANIO) {
                valido = false;
                break;
            }

            posiciones.add(new int[]{f, c});

            if (juego.getJugador().getTablero().getCasilla(f, c).tieneBarco()) {
                valido = false;
            }
        }

        for (int[] pos : posiciones) {
            if (pos[0] < Tablero.TAMANIO && pos[1] < Tablero.TAMANIO) {
                celdas[pos[0]][pos[1]].getStyleClass().add(
                    valido ? "celda-preview-valido" : "celda-preview-invalido");
            }
        }
    }

    /**
     * Limpia todas las clases de preview del tablero.
     */
    private void limpiarPreview() {
        for (int i = 0; i < Tablero.TAMANIO; i++) {
            for (int j = 0; j < Tablero.TAMANIO; j++) {
                celdas[i][j].getStyleClass().removeAll(
                    "celda-preview-valido", "celda-preview-invalido");
            }
        }
    }


    /**
     * Intenta colocar el barco seleccionado en la celda indicada.
     * Maneja las excepciones mostrando alertas al usuario.
     */
    private void colocarBarcoEnCelda(int fila, int col) {
        if (barcoSeleccionado == null) {
            mostrarAlerta("Selecciona un barco",
                "Primero selecciona un barco del panel derecho.",
                Alert.AlertType.INFORMATION);
            return;
        }

        try {
            juego.getJugador().getTablero().colocarBarco(
                barcoSeleccionado, fila, col, orientacionActual);

            barcosDisponibles.remove(barcoSeleccionado);
            barcoSeleccionado = null;

            actualizarTableroVisual();
            construirPanelBarcos();

            if (barcosDisponibles.isEmpty()) {
                lblInfo.setText("¡Todos los barcos colocados! Presiona 'Iniciar Batalla'");
                btnIniciarBatalla.setDisable(false);
            }
        } catch (FueraDeTableroException e) {
            mostrarAlerta("Fuera del tablero", e.getMessage(), Alert.AlertType.WARNING);
        } catch (BarcoSuperpuestoException e) {
            mostrarAlerta("Barco superpuesto", e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    /**
     * Actualiza la representación visual de todas las celdas del tablero
     * según el estado actual del modelo.
     */
    private void actualizarTableroVisual() {
        Tablero tablero = juego.getJugador().getTablero();

        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int col = 0; col < Tablero.TAMANIO; col++) {
                Casilla casilla = tablero.getCasilla(fila, col);
                Pane celda = celdas[fila][col];

                celda.getStyleClass().removeAll(
                    "celda-vacia", "celda-barco", "celda-agua",
                    "celda-tocado", "celda-hundido");
                celda.getChildren().clear();

                if (casilla.tieneBarco()) {
                    celda.getStyleClass().add("celda-barco");
                    agregarImagenACelda(celda,
                        "/com/batallaNaval/images/barcos/" + casilla.getBarco().getImagenNombre(),
                        36);
                } else {
                    celda.getStyleClass().add("celda-vacia");
                }
            }
        }
    }


    /**
     * Construye el panel lateral con los barcos disponibles para colocar.
     * Cada barco se muestra como un elemento clicable con su imagen y nombre.
     */
    private void construirPanelBarcos() {
        panelBarcos.getChildren().clear();

        Label titulo = new Label("Barcos disponibles:");
        titulo.getStyleClass().add("panel-titulo");
        panelBarcos.getChildren().add(titulo);

        for (Barco barco : barcosDisponibles) {
            HBox barcoItem = new HBox(10);
            barcoItem.setAlignment(Pos.CENTER_LEFT);
            barcoItem.getStyleClass().add("barco-item");
            barcoItem.setPadding(new Insets(8));

            // Imagen del barco
            agregarImagenAContenedor(barcoItem,
                "/com/batallaNaval/images/barcos/" + barco.getImagenNombre(), 30);

            // Nombre y tamaño
            Label lbl = new Label(barco.getNombre() + " (" + barco.getTamanio() + ")");
            lbl.getStyleClass().add("barco-label");
            barcoItem.getChildren().add(lbl);

            // Evento de selección
            barcoItem.setOnMouseClicked(e -> {
                barcoSeleccionado = barco;
                lblInfo.setText("Barco seleccionado: " + barco.getNombre()
                    + " (tamaño: " + barco.getTamanio() + ")");

                // Resaltar seleccionado
                panelBarcos.getChildren().forEach(
                    node -> node.getStyleClass().remove("barco-seleccionado"));
                barcoItem.getStyleClass().add("barco-seleccionado");
            });

            panelBarcos.getChildren().add(barcoItem);
        }
    }


    /**
     * Alterna la orientación entre HORIZONTAL y VERTICAL.
     */
    @FXML
    private void onRotar() {
        orientacionActual = (orientacionActual == Orientacion.HORIZONTAL)
            ? Orientacion.VERTICAL : Orientacion.HORIZONTAL;
        actualizarInfoOrientacion();
    }

    /**
     * Actualiza la etiqueta que muestra la orientación actual.
     */
    private void actualizarInfoOrientacion() {
        lblOrientacion.setText("Orientación: "
            + (orientacionActual == Orientacion.HORIZONTAL
                ? "↔ Horizontal" : "↕ Vertical"));
    }

    /**
     * Coloca todos los barcos aleatoriamente en el tablero del jugador.
     */
    @FXML
    private void onAleatorio() {
        juego.getJugador().getTablero().limpiar();
        barcosDisponibles = BarcoFactory.crearFlotaCompleta();
        barcoSeleccionado = null;

        Random random = new Random();
        List<Barco> copia = new ArrayList<>(barcosDisponibles);

        for (Barco barco : copia) {
            boolean colocado = false;
            int intentos = 0;
            while (!colocado && intentos < 1000) {
                int fila = random.nextInt(Tablero.TAMANIO);
                int columna = random.nextInt(Tablero.TAMANIO);
                Orientacion ori = random.nextBoolean()
                    ? Orientacion.HORIZONTAL : Orientacion.VERTICAL;

                try {
                    juego.getJugador().getTablero().colocarBarco(barco, fila, columna, ori);
                    colocado = true;
                } catch (FueraDeTableroException | BarcoSuperpuestoException e) {
                    intentos++;
                }
            }
        }

        barcosDisponibles.clear();
        actualizarTableroVisual();
        construirPanelBarcos();
        lblInfo.setText("¡Barcos colocados aleatoriamente! Presiona 'Iniciar Batalla'");
        btnIniciarBatalla.setDisable(false);
    }

    /**
     * Limpia el tablero del jugador y restaura todos los barcos al panel.
     */
    @FXML
    private void onLimpiar() {
        juego.getJugador().getTablero().limpiar();
        barcosDisponibles = BarcoFactory.crearFlotaCompleta();
        barcoSeleccionado = null;

        actualizarTableroVisual();
        construirPanelBarcos();
        btnIniciarBatalla.setDisable(true);
        lblInfo.setText("Tablero limpiado. Selecciona y coloca tus barcos.");
    }

    /**
     * Muestra un popup modal con el tablero del computador para verificar
     * que las posiciones de sus barcos fueron generadas correctamente.
     * Solo disponible en la fase de colocación (antes de iniciar la batalla).
     */
    @FXML
    private void onVerEnemigo() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Tablero del Computador (Verificación)");

        VBox contenedor = new VBox(10);
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setPadding(new Insets(20));
        contenedor.getStyleClass().add("popup-enemigo");

        Label tituloPopup = new Label("Posiciones de barcos del Computador");
        tituloPopup.getStyleClass().add("popup-titulo");
        contenedor.getChildren().add(tituloPopup);

        // Construir tablero del enemigo
        GridPane gridEnemigo = new GridPane();
        gridEnemigo.setAlignment(Pos.CENTER);
        gridEnemigo.setHgap(1);
        gridEnemigo.setVgap(1);

        // Encabezados
        for (int col = 0; col < Tablero.TAMANIO; col++) {
            Label lbl = new Label(String.valueOf((char) ('A' + col)));
            lbl.getStyleClass().add("header-label");
            lbl.setMinSize(35, 35);
            lbl.setAlignment(Pos.CENTER);
            gridEnemigo.add(lbl, col + 1, 0);
        }
        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            Label lbl = new Label(String.valueOf(fila + 1));
            lbl.getStyleClass().add("header-label");
            lbl.setMinSize(35, 35);
            lbl.setAlignment(Pos.CENTER);
            gridEnemigo.add(lbl, 0, fila + 1);
        }

        Tablero tableroComputador = juego.getComputador().getTablero();

        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int col = 0; col < Tablero.TAMANIO; col++) {
                Pane celda = new Pane();
                celda.setMinSize(35, 35);
                celda.setMaxSize(35, 35);
                celda.getStyleClass().add("celda");

                Casilla casilla = tableroComputador.getCasilla(fila, col);
                if (casilla.tieneBarco()) {
                    celda.getStyleClass().add("celda-barco");
                    agregarImagenACelda(celda,
                        "/com/batallaNaval/images/barcos/" + casilla.getBarco().getImagenNombre(),
                        31);
                } else {
                    celda.getStyleClass().add("celda-vacia");
                }

                gridEnemigo.add(celda, col + 1, fila + 1);
            }
        }

        contenedor.getChildren().add(gridEnemigo);

        // Leyenda de barcos
        VBox leyenda = new VBox(5);
        leyenda.setPadding(new Insets(10));
        Label lblLeyenda = new Label("Barcos del computador:");
        lblLeyenda.getStyleClass().add("popup-titulo");
        leyenda.getChildren().add(lblLeyenda);

        for (Barco barco : tableroComputador.getBarcos()) {
            Label lblBarco = new Label("• " + barco.getNombre()
                + " (tamaño: " + barco.getTamanio() + ")"
                + " → Pos: (" + (barco.getFilaInicio() + 1)
                + ", " + (char) ('A' + barco.getColumnaInicio()) + ")"
                + " " + barco.getOrientacion());
            lblBarco.getStyleClass().add("leyenda-texto");
            leyenda.getChildren().add(lblBarco);
        }

        contenedor.getChildren().add(leyenda);

        Button btnCerrar = new Button("Cerrar");
        btnCerrar.getStyleClass().add("btn-accion");
        btnCerrar.setOnAction(e -> popup.close());
        contenedor.getChildren().add(btnCerrar);

        Scene scene = new Scene(contenedor);
        scene.getStylesheets().add(
            getClass().getResource("/com/batallaNaval/css/styles.css").toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    /**
     * Inicia la batalla. Valida que todos los barcos estén colocados
     * y navega a la pantalla de Juego.
     */
    @FXML
    private void onIniciarBatalla() {
        if (!barcosDisponibles.isEmpty()) {
            mostrarAlerta("Barcos sin colocar",
                "Aún tienes " + barcosDisponibles.size()
                    + " barcos sin colocar. Colócalos todos antes de iniciar.",
                Alert.AlertType.WARNING);
            return;
        }

        NavegadorVistas.irAJuego(juego);
    }


    /**
     * Intenta agregar una imagen PNG a una celda del tablero.
     * Si la imagen no se encuentra, se usa el estilo CSS como fallback.
     */
    private void agregarImagenACelda(Pane celda, String ruta, double size) {
        try {
            var stream = getClass().getResourceAsStream(ruta);
            if (stream != null) {
                ImageView img = new ImageView(new Image(stream));
                img.setFitWidth(size);
                img.setFitHeight(size);
                img.setPreserveRatio(true);
                celda.getChildren().add(img);
            }
        } catch (Exception e) {
            // CSS fallback — no se requiere acción
        }
    }

    /**
     * Intenta agregar una imagen PNG a un contenedor HBox.
     */
    private void agregarImagenAContenedor(HBox contenedor, String ruta, double size) {
        try {
            var stream = getClass().getResourceAsStream(ruta);
            if (stream != null) {
                ImageView img = new ImageView(new Image(stream));
                img.setFitWidth(size);
                img.setFitHeight(size);
                img.setPreserveRatio(true);
                contenedor.getChildren().add(img);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Muestra un diálogo de alerta al usuario.
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
