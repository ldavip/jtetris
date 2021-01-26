package br.com.ldavip.jtetris;

import br.com.ldavip.jtetris.game.Block;
import br.com.ldavip.jtetris.game.JTetris;
import br.com.ldavip.jtetris.game.Movement;
import br.com.ldavip.jtetris.game.Tetromino;
import br.com.ldavip.jtetris.pieces.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class ConsoleTest {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    private static final Random rand = new Random();

    private static AtomicBoolean playing = new AtomicBoolean(true);

    public static void main(String[] args) {
        try {
            JTetris game = new JTetris();
            listenCommands(game);
            game.setListener(() -> {
                clearScreen();

                System.out.println("\n\n\n\n");

                System.out.println("Next: ");
                Tetromino next = game.getNext();
                Block[][] nextShape = next.getShape();
                for (int i = 0; i < nextShape.length; i++) {
                    System.out.print("\t");
                    for (int j = 0; j < nextShape[i].length; j++) {
                        if (nextShape[i][j] != null) {
                            String block = getAnsiColor(nextShape[i][j].getColor()) + "#" + ANSI_RESET + " ";
                            System.out.print(block);
                        } else {
                            System.out.print("  ");
                        }
                    }
                    System.out.println();
                }

                System.out.println("_______________________\n");

                Block[][] board = game.getBoard();

                for (int i = 0; i < board.length; i++) {
                    for (int j = 0; j < board[i].length; j++) {
                        if (board[i][j] != null) {
                            String block = getAnsiColor(board[i][j].getColor()) + "#" + ANSI_RESET + " ";
                            System.out.print(block);
                        } else {
                            System.out.print("  ");
                        }
                    }
                    System.out.println();
                }
            });
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            playing.set(false);
        }
    }

    static void listenCommands(JTetris game) {
        new Thread(() -> {
            while (playing.get()) {
                try {
                    Movement movement = null;
                    int c = System.in.read();
                    if (c == '4') {
                        movement = Movement.LEFT;
                    } else if (c == '6') {
                        movement = Movement.RIGHT;
                    } else if (c == '2') {
                        movement = Movement.SPEED;
                    } else if (c == '5') {
                        movement = Movement.ROTATE;
                    } else if (c == 'p') {
                        game.pause();
                    } else if (c == 'q') {
                        System.out.println("GAMEOVER");
                        System.exit(0);
                    }
                    if (movement != null) {
                        game.handleMovement(movement);
                    }
                } catch (IOException e) {
                }
            }
        }).start();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void testShowAllPieces() {
        List<Polyomino> pieces = List.of(new BlueJ(), new CyanI(), new GreenS(), new OrangeL(), new PurpleT(), new RedZ(), new YellowO());
        String[][] m = new String[4][4];

        for (Polyomino piece : pieces) {
            System.out.println(piece.getClass().getSimpleName());
            for (int pos = 0; pos < Direction.values().length; pos++) {
                System.out.println(piece.getDirection());
                Stream.of(m).forEach(l -> Arrays.fill(l, " "));

                boolean[][] shape = piece.getShape();
                for (int i = 0; i < shape.length; i++) {
                    for (int j = 0; j < shape[i].length; j++) {
                        if (shape[i][j]) {
                            m[i][j] = getAnsiColor(piece.getColor()) + "*" + ANSI_RESET;
                        }
                    }
                }

                for (int i = 0; i < m.length; i++) {
                    for (int j = 0; j < m[i].length; j++) {
                        System.out.print(m[i][j] + " ");
                    }
                    System.out.println();
                }

                System.out.println();
                piece.setDirection(piece.getDirection().getNextClockwise());
            }

            System.out.println("\n------------------\n");
        }
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

    static String getAnsiBg(String color) {
        String[] bg = new String[7];

        Arrays.fill(bg, "white");

        switch (color.toLowerCase()) {
            case "blue":
                bg = new String[]{
                        ANSI_BLACK_BACKGROUND,
                        ANSI_RED_BACKGROUND,
                        ANSI_GREEN_BACKGROUND,
                        ANSI_YELLOW_BACKGROUND,
                        ANSI_PURPLE_BACKGROUND,
                        ANSI_CYAN_BACKGROUND,
                        ANSI_WHITE_BACKGROUND
                };
                break;
            case "cyan":
                bg = new String[]{
                        ANSI_BLACK_BACKGROUND,
                        ANSI_RED_BACKGROUND,
                        ANSI_GREEN_BACKGROUND,
                        ANSI_YELLOW_BACKGROUND,
                        ANSI_BLUE_BACKGROUND,
                        ANSI_PURPLE_BACKGROUND,
                        ANSI_WHITE_BACKGROUND
                };
                break;
            case "green":
                bg = new String[]{
                        ANSI_BLACK_BACKGROUND,
                        ANSI_RED_BACKGROUND,
                        ANSI_YELLOW_BACKGROUND,
                        ANSI_BLUE_BACKGROUND,
                        ANSI_PURPLE_BACKGROUND,
                        ANSI_CYAN_BACKGROUND,
                        ANSI_WHITE_BACKGROUND
                };
                break;
            case "orange": // white
                bg = new String[]{
                        ANSI_BLACK_BACKGROUND,
                        ANSI_RED_BACKGROUND,
                        ANSI_GREEN_BACKGROUND,
                        ANSI_YELLOW_BACKGROUND,
                        ANSI_BLUE_BACKGROUND,
                        ANSI_PURPLE_BACKGROUND,
                        ANSI_CYAN_BACKGROUND,
                };
                break;
            case "purple":
                bg = new String[]{
                        ANSI_BLACK_BACKGROUND,
                        ANSI_RED_BACKGROUND,
                        ANSI_GREEN_BACKGROUND,
                        ANSI_YELLOW_BACKGROUND,
                        ANSI_BLUE_BACKGROUND,
                        ANSI_CYAN_BACKGROUND,
                        ANSI_WHITE_BACKGROUND
                };
                break;
            case "red":
                bg = new String[]{
                        ANSI_BLACK_BACKGROUND,
                        ANSI_GREEN_BACKGROUND,
                        ANSI_YELLOW_BACKGROUND,
                        ANSI_BLUE_BACKGROUND,
                        ANSI_PURPLE_BACKGROUND,
                        ANSI_CYAN_BACKGROUND,
                        ANSI_WHITE_BACKGROUND
                };
                break;
            case "yellow":
                bg = new String[]{
                        ANSI_BLACK_BACKGROUND,
                        ANSI_RED_BACKGROUND,
                        ANSI_GREEN_BACKGROUND,
                        ANSI_BLUE_BACKGROUND,
                        ANSI_PURPLE_BACKGROUND,
                        ANSI_CYAN_BACKGROUND,
                        ANSI_WHITE_BACKGROUND
                };
                break;
            case "gray": // black
                bg = new String[]{
                        ANSI_RED_BACKGROUND,
                        ANSI_GREEN_BACKGROUND,
                        ANSI_YELLOW_BACKGROUND,
                        ANSI_BLUE_BACKGROUND,
                        ANSI_PURPLE_BACKGROUND,
                        ANSI_CYAN_BACKGROUND,
                        ANSI_WHITE_BACKGROUND
                };
                break;
        }
        return bg[rand.nextInt(7)];
    }
}
