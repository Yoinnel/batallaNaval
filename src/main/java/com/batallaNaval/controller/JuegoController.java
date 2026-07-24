package com.batallaNaval.controller;

import com.batallaNaval.exceptions.CasillaYaDisparadaException;
import com.batallaNaval.model.*;
import com.batallaNaval.patterns.PersistenciaFacade;
import com.batallaNaval.util.NavegadorVistas;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static com.batallaNaval.util.NavegadorVistas.irAInicio;

/**
 * Controlador de la pantalla de Juego/Batalla (Juego.fxml).
 *
 * Responsabilidades:
 *  - Mostrar ambos tableros (jugador y enemigo).
 *  - Gestionar el turno del jugador (clic para disparar).
 *  - Ejecutar el turno del computador (IA con Strategy).
 *  - Actualizar el estado visual de las celdas con imágenes PNG.
 *  - Detectar fin de juego (victoria o derrota).
 *  - Guardar partida en curso.
 */
public class JuegoController {

    @FXML private GridPane gridJugador;
    @FXML private GridPane gridEnemigo;
    @FXML private Label lblTurno;
    @FXML private Label lblInfoJugador;
    @FXML private Label lblInfoEnemigo;
    @FXML private Label lblEstado;
    @FXML private Button btnGuardar;
    @FXML private Label lblNickname;

    private Juego juego;
    private Pane[][] celdasJugador;
    private Pane[][] celdasEnemigo;
    private PersistenciaFacade persistencia;
    private boolean procesandoTurno = false;

    /**
     * Inicializa el controlador con el estado del juego.
     *
     * @param juego estado del juego actual (nuevo o cargado)
     */
    public void inicializar(Juego juego) {
        this.juego = juego;
        this.celdasJugador = new Pane[Tablero.TAMANIO][Tablero.TAMANIO];
        this.celdasEnemigo = new Pane[Tablero.TAMANIO][Tablero.TAMANIO];
        this.persistencia = new PersistenciaFacade();

        lblNickname.setText("Jugador: " + juego.getJugador().getNickname());

        construirTableroJugador();
        construirTableroEnemigo();
        actualizarInfo();
        actualizarEstadoTurno();
    }

    // ── Construcción de tableros ───────────────────────────────────

    /**
     * Construye el tablero visual del jugador mostrando sus barcos
     * y los impactos recibidos.
     */
    private void construirTableroJugador() {
        construirEncabezados(gridJugador);

        Tablero tablero = juego.getJugador().getTablero();

        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int col = 0; col < Tablero.TAMANIO; col++) {
                Pane celda = new Pane();
                celda.setMinSize(38, 38);
                celda.setMaxSize(38, 38);
                celda.getStyleClass().add("celda");

                Casilla casilla = tablero.getCasilla(fila, col);
                aplicarEstiloCelda(celda, casilla, true);

                celdasJugador[fila][col] = celda;
                gridJugador.add(celda, col + 1, fila + 1);
            }
        }
    }

    /**
     * Construye el tablero visual del enemigo. Cada celda es clicable
     * para disparar, y muestra solo las casillas ya atacadas.
     */
    private void construirTableroEnemigo() {
        construirEncabezados(gridEnemigo);

        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            for (int col = 0; col < Tablero.TAMANIO; col++) {
                Pane celda = new Pane();
                celda.setMinSize(38, 38);
                celda.setMaxSize(38, 38);
                celda.getStyleClass().add("celda");
                celda.getStyleClass().add("celda-enemigo");

                // Si es partida cargada, restaurar las celdas ya disparadas
                Casilla casilla = juego.getComputador().getTablero().getCasilla(fila, col);
                if (casilla.fueDisparada()) {
                    aplicarEstiloCelda(celda, casilla, false);
                }

                final int f = fila, c = col;
                celda.setOnMouseClicked(e -> onDisparoJugador(f, c));

                // Hover
                celda.setOnMouseEntered(e -> {
                    if (!celda.getStyleClass().contains("celda-agua")
                        && !celda.getStyleClass().contains("celda-tocado")
                        && !celda.getStyleClass().contains("celda-hundido")) {
                        celda.getStyleClass().add("celda-hover");
                    }
                });
                celda.setOnMouseExited(e -> celda.getStyleClass().remove("celda-hover"));

                celdasEnemigo[fila][col] = celda;
                gridEnemigo.add(celda, col + 1, fila + 1);
            }
        }
    }

    /**
     * Agrega encabezados de columna (A-J) y fila (1-10) a un GridPane.
     */
    private void construirEncabezados(GridPane grid) {
        for (int col = 0; col < Tablero.TAMANIO; col++) {
            Label lbl = new Label(String.valueOf((char) ('A' + col)));
            lbl.getStyleClass().add("header-label");
            lbl.setMinSize(38, 38);
            lbl.setAlignment(Pos.CENTER);
            grid.add(lbl, col + 1, 0);
        }

        for (int fila = 0; fila < Tablero.TAMANIO; fila++) {
            Label lbl = new Label(String.valueOf(fila + 1));
            lbl.getStyleClass().add("header-label");
            lbl.setMinSize(38, 38);
            lbl.setAlignment(Pos.CENTER);
            grid.add(lbl, 0, fila + 1);
        }
    }

    // ── Estilo visual de celdas ────────────────────────────────────

    /**
     * Aplica el estilo visual a una celda según el estado de su casilla.
     * Usa imágenes PNG cuando están disponibles, con CSS como fallback.
     *
     * @param celda      Pane visual a estilizar
     * @param casilla    datos de la casilla del modelo
     * @param esJugador  true si es el tablero del jugador (muestra barcos)
     */
    private void aplicarEstiloCelda(Pane celda, Casilla casilla, boolean esJugador) {
        celda.getChildren().clear();
        celda.getStyleClass().removeAll(
            "celda-vacia", "celda-barco", "celda-agua",
            "celda-tocado", "celda-hundido", "celda-enemigo");

        String imagenRuta = null;

        switch (casilla.getEstado()) {
            case VACIA:
                celda.getStyleClass().add(esJugador ? "celda-vacia" : "celda-enemigo");
                break;

            case OCUPADA:
                if (esJugador) {
                    celda.getStyleClass().add("celda-barco");
                    imagenRuta = "/com/batallaNaval/images/barcos/"
                        + casilla.getBarco().getImagenNombre();
                } else {
                    celda.getStyleClass().add("celda-enemigo");
                }
                break;

            case AGUA:
                celda.getStyleClass().add("celda-agua");
                imagenRuta = "/com/batallaNaval/images/estados/agua.png";
                break;

            case TOCADO:
                celda.getStyleClass().add("celda-tocado");
                imagenRuta = "/com/batallaNaval/images/estados/tocado.png";
                break;

            case HUNDIDO:
                celda.getStyleClass().add("celda-hundido");
                imagenRuta = "/com/batallaNaval/images/estados/hundido.png";
                break;
        }

        if (imagenRuta != null) {
            try {
                var stream = getClass().getResourceAsStream(imagenRuta);
                if (stream != null) {
                    ImageView img = new ImageView(new Image(stream));
                    img.setFitWidth(34);
                    img.setFitHeight(34);
                    img.setPreserveRatio(true);
                    celda.getChildren().add(img);
                }
            } catch (Exception e) {
                // CSS fallback
            }
        }
    }

    // ── Turno del jugador ──────────────────────────────────────────

    /**
     * Maneja el disparo del jugador al hacer clic en una celda del tablero enemigo.
     */
    private void onDisparoJugador(int fila, int col) {
        if (juego.isGameOver() || !juego.isTurnoJugador() || procesandoTurno) {
            return;
        }

        procesandoTurno = true;

        try {
            EstadoCasilla resultado = juego.getComputador().getTablero()
                .recibirDisparo(fila, col);
            juego.incrementarDisparosJugador();

            // Actualizar celda enemiga
            Casilla casilla = juego.getComputador().getTablero().getCasilla(fila, col);
            aplicarEstiloCelda(celdasEnemigo[fila][col], casilla, false);

            // Mensaje según resultado
            if (resultado == EstadoCasilla.HUNDIDO) {
                Barco barcoHundido = casilla.getBarco();
                // Actualizar todas las celdas del barco hundido
                for (int[] pos : barcoHundido.getPosiciones()) {
                    Casilla c = juego.getComputador().getTablero()
                        .getCasilla(pos[0], pos[1]);
                    aplicarEstiloCelda(celdasEnemigo[pos[0]][pos[1]], c, false);
                }
                lblEstado.setText("¡Hundiste el " + barcoHundido.getNombre() + "!");
            } else if (resultado == EstadoCasilla.TOCADO) {
                lblEstado.setText("¡Tocado!");
            } else {
                lblEstado.setText("Agua...");
            }

            juego.verificarFinJuego();
            actualizarInfo();

            if (juego.isGameOver()) {
                finalizarJuego();
                procesandoTurno = false;
                return;
            }

            if (resultado == EstadoCasilla.TOCADO || resultado == EstadoCasilla.HUNDIDO) {
                // El jugador acertó, conserva el turno y puede disparar de inmediato
                procesandoTurno = false;
            } else {
                // El jugador falló (AGUA), el turno pasa al computador
                juego.setTurnoJugador(false);
                actualizarEstadoTurno();

                PauseTransition pausa = new PauseTransition(Duration.millis(800));
                pausa.setOnFinished(e -> {
                    turnoComputador();
                });
                pausa.play();
            }

        } catch (CasillaYaDisparadaException e) {
            lblEstado.setText("Ya disparaste ahí. Elige otra casilla.");
            procesandoTurno = false;
        }
    }

    // ── Turno del computador ───────────────────────────────────────

    /**
     * Ejecuta el turno del computador usando su IA (patrón Strategy).
     */
    private void turnoComputador() {
        if (juego.isGameOver()) return;

        int[] objetivo = juego.getComputador().getIa()
            .seleccionarObjetivo(juego.getJugador().getTablero());

        if (objetivo == null) return;

        try {
            EstadoCasilla resultado = juego.getJugador().getTablero()
                .recibirDisparo(objetivo[0], objetivo[1]);
            juego.incrementarDisparosComputador();

            Casilla casilla = juego.getJugador().getTablero()
                .getCasilla(objetivo[0], objetivo[1]);
            aplicarEstiloCelda(celdasJugador[objetivo[0]][objetivo[1]], casilla, true);

            String coordenada = "(" + (objetivo[0] + 1) + ", "
                + (char) ('A' + objetivo[1]) + ")";

            // Notificar a la IA y mostrar mensaje
            switch (resultado) {
                case TOCADO:
                    juego.getComputador().getIa()
                        .notificarImpacto(objetivo[0], objetivo[1]);
                    lblEstado.setText("El computador tocó tu barco en "
                        + coordenada + "!");
                    break;

                case HUNDIDO:
                    juego.getComputador().getIa().notificarHundido();
                    Barco barcoHundido = casilla.getBarco();
                    for (int[] pos : barcoHundido.getPosiciones()) {
                        Casilla c = juego.getJugador().getTablero()
                            .getCasilla(pos[0], pos[1]);
                        aplicarEstiloCelda(celdasJugador[pos[0]][pos[1]], c, true);
                    }
                    lblEstado.setText("¡El computador hundió tu "
                        + barcoHundido.getNombre() + "!");
                    break;

                case AGUA:
                    juego.getComputador().getIa().notificarAgua();
                    lblEstado.setText("El computador disparó al agua en "
                        + coordenada);
                    break;

                default:
                    break;
            }

            juego.verificarFinJuego();
            actualizarInfo();

            if (juego.isGameOver()) {
                finalizarJuego();
                return;
            }

            if (resultado == EstadoCasilla.TOCADO || resultado == EstadoCasilla.HUNDIDO) {
                // El computador acertó, conserva el turno. Ejecuta otro disparo tras una breve pausa.
                PauseTransition pausa = new PauseTransition(Duration.millis(800));
                pausa.setOnFinished(e -> {
                    turnoComputador();
                });
                pausa.play();
            } else {
                // El computador falló (AGUA), el turno regresa al jugador humano
                juego.setTurnoJugador(true);
                actualizarEstadoTurno();
                procesandoTurno = false;
            }

        } catch (CasillaYaDisparadaException e) {
            // No debería ocurrir con la IA, pero por seguridad reintentar
            turnoComputador();
        }
    }

    // ── Actualización de interfaz ──────────────────────────────────

    /**
     * Actualiza las etiquetas de información de barcos restantes.
     */
    private void actualizarInfo() {
        Tablero tableroJugador = juego.getJugador().getTablero();
        Tablero tableroEnemigo = juego.getComputador().getTablero();

        lblInfoJugador.setText("Tus barcos: " + tableroJugador.getBarcosRestantes()
            + " restantes | " + tableroJugador.getBarcosHundidos() + " hundidos");
        lblInfoEnemigo.setText("Barcos enemigos: " + tableroEnemigo.getBarcosRestantes()
            + " restantes | " + tableroEnemigo.getBarcosHundidos() + " hundidos");
    }

    /**
     * Actualiza la etiqueta de turno actual.
     */
    private void actualizarEstadoTurno() {
        if (juego.isTurnoJugador()) {
            lblTurno.setText("Tu turno - Haz clic en el tablero enemigo");
            lblTurno.getStyleClass().removeAll("turno-enemigo");
            lblTurno.getStyleClass().add("turno-jugador");
        } else {
            lblTurno.setText("Turno del computador...");
            lblTurno.getStyleClass().removeAll("turno-jugador");
            lblTurno.getStyleClass().add("turno-enemigo");
        }
    }

    // ── Fin del juego ──────────────────────────────────────────────

    /**
     * Maneja el fin del juego: muestra el resultado, guarda estadísticas,
     * y ofrece iniciar nueva partida o volver al inicio.
     */
    private void finalizarJuego() {
        boolean victoria = juego.getGanador()
            .equals(juego.getJugador().getNickname());

        // Guardar estadísticas y eliminar partida guardada
        persistencia.guardarEstadistica(juego.getJugador().getNickname(), victoria);
        persistencia.eliminarPartidaGuardada();

        String mensaje = victoria
            ? "¡VICTORIA!\n\n¡Felicidades " + juego.getJugador().getNickname()
                + "!\nHundiste todos los barcos enemigos.\n\nDisparos realizados: "
                + juego.getDisparosJugador()
            : "DERROTA\n\nEl computador hundió toda tu flota.\n\nDisparos del computador: "
                + juego.getDisparosComputador();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin del juego");
        alert.setHeaderText(victoria ? "¡Has ganado!" : "Has perdido");
        alert.setContentText(mensaje);

        ButtonType btnNueva = new ButtonType("Nueva partida");
        ButtonType btnSalir = new ButtonType("Volver al inicio");
        alert.getButtonTypes().setAll(btnNueva, btnSalir);

        alert.showAndWait().ifPresent(btn -> {
            if (btn == btnNueva) {
                Juego nuevoJuego = new Juego(juego.getJugador().getNickname());
                NavegadorVistas.irAColocacion(nuevoJuego);
            } else {
                irAInicio();
            }
        });
    }

    // ── Guardar partida ────────────────────────────────────────────

    /**
     * Guarda el estado actual de la partida en un archivo serializado.
     */
    @FXML
    private void onGuardar() {
        persistencia.guardarPartida(juego);
        lblEstado.setText("Partida guardada exitosamente.");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Partida guardada");
        alert.setHeaderText(null);
        alert.setContentText("Tu partida ha sido guardada. Puedes cerrar el juego y continuar después.");
        alert.showAndWait();
    }

    @FXML
    private void onSalir() {
        irAInicio();
    }
}
