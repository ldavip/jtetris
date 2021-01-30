package br.com.ldavip.jtetris.ui.console;

import br.com.ldavip.jtetris.game.Block;
import br.com.ldavip.jtetris.game.JTetris;
import br.com.ldavip.jtetris.game.Movement;
import br.com.ldavip.jtetris.game.Tetromino;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ConsoleUI {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static final AtomicBoolean playing = new AtomicBoolean(true);

    public static void main(String[] args) {
        try {
            JTetris game = new JTetris();
            listenCommands(game, new RawConsoleInputReader());
            game.setListener(() -> {
                clearScreen();

                println("\n\n\n\n");

                println("Level: " + game.getCurrentLevel());
                println("Score: " + game.getScore());
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
                    Stream.of(blocks).forEach(ConsoleUI::printBlock);
                    println();
                });

                println("_______________________\n");

                Block[][] board = game.getBoard();

                Stream.of(board).forEach(blocks -> {
                    Stream.of(blocks).forEach(ConsoleUI::printBlock);
                    println();
                });
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
            playing.set(false);
            System.exit(0);
        }
    }

    private static void printBlock(Block block) {
        if (block != null) {
            print(getAnsiColor(block.getColor()) + "#" + ANSI_RESET + " ");
        } else {
            print("  ");
        }
    }

    static void listenCommands(JTetris game, InputReader inputReader) {
        new Thread(() -> {
            while (playing.get()) {
                try {
                    Movement movement = null;
                    int c = inputReader.readInput();
                    switch (c) {
                        case '4':
                        case 'a':
                            movement = Movement.LEFT;
                            break;
                        case '6':
                        case 'd':
                            movement = Movement.RIGHT;
                            break;
                        case '2':
                        case 's':
                            movement = Movement.SPEED;
                            break;
                        case '5':
                        case ' ':
                            movement = Movement.ROTATE;
                            break;
                        case 'p':
                            game.pause();
                            break;
                        case 'q':
                            println("GAMEOVER");
                            System.exit(0);
                            break;
                    }
                    if (movement != null) {
                        game.handleMovement(movement);
                    }
                } catch (IOException ignored) {
                }
            }
        }).start();
    }

    public static void clearScreen() {
        print("\033[H\033[2J");
        System.out.flush();
    }

    static String getAnsiColor(String color) {
        switch (color.toLowerCase()) {
            case "blue":
                return ANSI_BLUE;
            case "cyan":
                return ANSI_CYAN;
            case "green":
                return ANSI_GREEN;
            case "orange": // white
                return ANSI_WHITE;
            case "purple":
                return ANSI_PURPLE;
            case "red":
                return ANSI_RED;
            case "yellow":
                return ANSI_YELLOW;
            case "gray": // black
                return ANSI_BLACK;
        }
        return ANSI_WHITE;
    }

    private static void print(String msg) {
        System.out.print(msg);
    }

    private static void println() {
        System.out.println();
    }

    private static void println(String msg) {
        System.out.println(msg);
    }
}
