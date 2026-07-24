package com.batallaNaval.controller;

import com.batallaNaval.model.Juego;
import com.batallaNaval.patterns.PersistenciaFacade;
import com.batallaNaval.util.NavegadorVistas;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Controlador de la pantalla de Inicio (Inicio.fxml).
 *
 * Esta clase se encarga de:
 *  - Verificar si existe una partida guardada al iniciar.
 *  - Solicitar el nickname del jugador.
 *  - Iniciar nueva partida o cargar la última.
 *  - Mostrar la tabla de estadísticas de jugadores.
 */
public class InicioController {

    @FXML private TextField txtNickname;
    @FXML private TableView<String[]> tablaEstadisticas;
    @FXML private TableColumn<String[], String> colNickname;
    @FXML private TableColumn<String[], String> colJugadas;
    @FXML private TableColumn<String[], String> colGanadas;
    @FXML private TableColumn<String[], String> colPerdidas;

    private PersistenciaFacade persistencia;

    /**
     * Metodo de inicialización llamado automáticamente por JavaFX
     * después de cargar el FXML.
     */
    @FXML
    public void initialize() {
        persistencia = new PersistenciaFacade();

        configurarTablaEstadisticas();
        cargarEstadisticas();
    }

    /**
     * Maneja el evento del botón "Nueva Partida".
     * Válida el nickname y navega a la pantalla de Colocación.
     */
    @FXML
    private void onNuevaPartida() {
        String nickname = txtNickname.getText().trim();

        if (nickname.isEmpty()) {
            mostrarAlerta("Nickname requerido",
                "Por favor, ingresa un nombre usuario para continuar.",
                Alert.AlertType.WARNING);
            txtNickname.requestFocus();
            return;
        }

        if (nickname.contains(",")) {
            mostrarAlerta("Nickname inválido",
                "El nickname no puede contener comas.",
                Alert.AlertType.WARNING);
            return;
        }

        Juego juego = new Juego(nickname);
        NavegadorVistas.irAColocacion(juego);
    }

    /**
     * Maneja el evento del botón "Cargar Partida".
     * Intenta cargar la última partida guardada.
     */
    @FXML
    private void onCargarPartida() {
        if (!persistencia.existePartidaGuardada()) {
            mostrarAlerta("Sin partida guardada",
                "No se encontró ninguna partida guardada.",
                Alert.AlertType.INFORMATION);
            return;
        }

        Juego juego = persistencia.cargarPartida();
        if (juego != null) {
            NavegadorVistas.irAJuego(juego);
        } else {
            mostrarAlerta("Error",
                "No se pudo cargar la partida guardada.",
                Alert.AlertType.ERROR);
        }
    }

    /**
     * Configura las columnas de la tabla de estadísticas.
     * Cada columna extrae su valor del arreglo String[] correspondiente.
     */
    private void configurarTablaEstadisticas() {
        colNickname.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[0]));
        colJugadas.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[1]));
        colGanadas.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[2]));
        colPerdidas.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()[3]));
    }

    /**
     * Carga las estadísticas desde el archivo plano y las muestra en la tabla.
     */
    private void cargarEstadisticas() {
        List<String[]> stats = persistencia.cargarEstadisticas();
        ObservableList<String[]> data = FXCollections.observableArrayList(stats);
        tablaEstadisticas.setItems(data);
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
