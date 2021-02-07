package br.com.ldavip.jtetris.ui.fx.console;

import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import br.com.ldavip.jtetris.game.Block;
import br.com.ldavip.jtetris.game.JTetris;
import br.com.ldavip.jtetris.game.Movement;
import br.com.ldavip.jtetris.game.Tetromino;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class Main extends Application {

    private static final long FPS = 10;
    public static final Font FONT = Font.font("Monospaced", FontWeight.NORMAL, FontPosture.REGULAR, 14.0d);

    private final TextFlow console = new TextFlow();
    private JTetris game;
    private Timer timer;

    @Override
    public void start(Stage stage) {
        stage.setTitle("JTetris");

        StackPane root = new StackPane(console);
        root.setPrefWidth(240);
        root.setPrefHeight(550);
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(this::handleInput);

        stage.setScene(scene);
        stage.show();

        initGame();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        game.stop();
    }

    private void initGame() {
        game = new JTetris();
        game.setListener(this::finish);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateView();
            }
        }, 0, FPS);
    }

    private void finish() {
        if (timer != null) {
            timer.cancel();
        }
        updateView();
    }

    private void updateView() {
        Platform.runLater(() -> {
            clearScreen();

            print("Level: ");
            Text text = new Text(game.getCurrentLevel() + "\n");
            text.setFill(Color.BLUE);
            print(text);

            print("Score: ");
            text = new Text(game.getScore() + "\n");
            text.setFill(Color.BLUE);
            print(text);

            println("Next: ");

            Tetromino next = game.getNext();
            Block[][] nextShape = next.getShape();

            Block[][] nextBoard = new Block[4][JTetris.WIDTH];

            for (int i = 0; i < nextShape.length; i++) {
                for (int nj = 0, j = JTetris.INITIAL_POSITION.getX(); nj < nextShape[0].length; j++, nj++) {
                    nextBoard[i][j] = nextShape[i][nj];
                }
            }

            Stream.of(nextBoard).forEach(blocks -> {
                Stream.of(blocks).forEach(this::printBlock);
                println();
            });

            println("-----------------------");

            Block[][] board = game.getBoard();

            Stream.of(board).forEach(blocks -> {
                Stream.of(blocks).forEach(this::printBlock);
                println();
            });

            String message = null;
            if (game.isPaused()) {
                message = "\n\tPAUSED";
            }
            if (game.isGameover()) {
                message = "\n\tGAMEOVER";
            }
            if (message != null) {
                Text msg = new Text(message);
                msg.setFill(Color.RED);
                msg.setFont(Font.font("Monospaced", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 30.0d));
                print(msg);
            }
        });
    }

    private void handleInput(KeyEvent c) {
        if (game.isPlaying()) {
            Movement movement = null;
            switch (c.getCode()) {
                case NUMPAD4:
                case A:
                case LEFT:
                    movement = Movement.LEFT;
                    break;
                case NUMPAD6:
                case D:
                case RIGHT:
                    movement = Movement.RIGHT;
                    break;
                case NUMPAD2:
                case S:
                case DOWN:
                    movement = Movement.SPEED;
                    break;
                case NUMPAD5:
                case SPACE:
                    movement = Movement.ROTATE;
                    break;
                case P:
                    game.pause();
                    break;
                case Q:
                    game.stop();
                    break;
                case R:
                    game.start();
                    break;
                case G:
                    // cheating
                    if (c.isShiftDown()) {
                        game.changeNextPiece();
                    }
                    break;
            }
            if (movement != null) {
                game.handleMovement(movement);
            }
        }
    }

    private void printBlock(Block block) {
        if (block != null) {
            Text text = new Text();
            text.setText("# ");
            text.setFill(Color.valueOf(block.getColor()));
            print(text);
        } else {
            print("  ");
        }
    }

    private void println() {
        println("");
    }

    private void println(String msg) {
        print(msg + "\n");
    }

    private void print(String msg) {
        Text text = new Text(msg);
        text.setFill(Color.WHITE);
        print(text);
    }

    private void print(Text text) {
        text.setFont(FONT);
        console.getChildren().add(text);
    }

    private void clearScreen() {
        console.getChildren().clear();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
