package br.com.ldavip.jtetris.game;

import br.com.ldavip.jtetris.pieces.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JTetris {
    public static int LEVEL_INCREASE = 20;
    private static long seed = 0;
    private static long LEVEL_UP = 1000;
    private static long STARTING_TICK = 500;
    private static long[] LINE_CLEAR_SCORE = {0, 40, 100, 300, 1200};

    public static final Position INITIAL_POSITION = new Position(0, 5);
    public static final int HEIGHT = 22;
    public static final int WIDTH = 12;

    private Random rand = seed > 0 ? new Random(seed) : new Random();

    private final List<Tetromino> tetrominoes = new ArrayList<>();

    private final List<Supplier<Polyomino>> polyominos = List.of(
            BlueJ::new,
            CyanI::new,
            GreenS::new,
            OrangeL::new,
            PurpleT::new,
            RedZ::new,
            YellowO::new
    );
    private final AtomicBoolean ticking = new AtomicBoolean(false);
    private final AtomicBoolean gameover = new AtomicBoolean(false);
    private final AtomicBoolean cheated = new AtomicBoolean(false);
    private Timer timer;

    private GameOverListener listener;

    private AtomicReference<Tetromino> next = new AtomicReference<>();
    private AtomicReference<Tetromino> current = new AtomicReference<>();
    private AtomicBoolean paused = new AtomicBoolean(false);
    private long score = 0L;
    private long currentLevel = 0;
    private long tick;

    public JTetris() {
        start();
    }

    public void start() {
        score = 0;
        currentLevel = 0;
        tetrominoes.clear();
        initBoard();
        startTicking(STARTING_TICK);
    }

    private void setTicking(boolean ticking) {
        if (this.ticking.get() && !ticking && timer != null) {
            timer.cancel();
        }
        this.ticking.set(ticking);
    }

    public Block[][] getBoard() {

        Block[][] blocks = new Block[22][12];
        for (Tetromino piece : tetrominoes) {
            Block[][] shape = piece.getShape();
            Position position = piece.getPosition();

            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    if (shape[i][j] != null) {
                        blocks[i + position.getY()][j + position.getX()] = new Block(piece.getColor());
                    }
                }
            }
        }

        return blocks;
    }

    public long getTick() {
        return tick;
    }

    public long getCurrentLevel() {
        return currentLevel + 1;
    }

    public Tetromino getNext() {
        return next.get();
    }

    public long getScore() {
        return score;
    }

    public void setListener(GameOverListener listener) {
        this.listener = listener;
    }

    private void sortNextPiece() {
        Supplier<Polyomino> polyomino = polyominos.get(rand.nextInt(polyominos.size()));
        next.set(new Tetromino(polyomino.get()));
    }

    private void initBoard() {
        tetrominoes.add(new FixedBlock(new GrayBlock(), new Position(0, 0)));
        tetrominoes.add(new FixedBlock(new GrayBlock(), new Position(0, WIDTH - 1)));

        for (int i = 0; i < HEIGHT - 1; i++) {
            tetrominoes.add(new FixedBlock(new GrayBlock(), new Position(i, 0)));
            tetrominoes.add(new FixedBlock(new GrayBlock(), new Position(i, WIDTH - 1)));
        }

        for (int i = 0; i < WIDTH; i++) {
            tetrominoes.add(new FixedBlock(new GrayBlock(), new Position(HEIGHT - 1, i)));
        }

        sortNextPiece();
    }

    private void startTicking(long tick) {
        this.tick = tick;
        ticking.set(true);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                update();
            }
        };
        timer.schedule(task, 0, tick);
    }

    private void updateTickFrequency(long tick) {
        this.tick = tick;
        startTicking(tick);
    }

    public void update() {
        if (!paused.get()) {
            if (current.get() == null) {
                verifyCompletedLines();
                addNextPiece();
                checkGameOver();
            } else {
                if (hasColission(current.get(), current.get().getPosition().addY(1))) {
                    current.get().setMovable(false);
                    current.set(null);
                } else {
                    current.get().setPosition(current.get().getPosition().addY(1));
                }
            }
        }
    }

    private void checkGameOver() {
        if (hasColission(current.get(), current.get().getPosition().addY(1))) {
            stop();
        }
    }

    private void addNextPiece() {
        next.get().setPosition(INITIAL_POSITION);
        next.get().setMovable(true);
        tetrominoes.add(next.get());

        current.set(next.get());
        sortNextPiece();
    }

    private void verifyCompletedLines() {
        List<Integer> completeLines = searchCompleteLines();
        if (!completeLines.isEmpty()) {
            int linesCut = completeLines.size();
            List<Tetromino> pieces = getGameTetrominoes();

            // cut pieces of complete lines
            pieces.parallelStream()
                    .forEach(p -> p.cut(completeLines));

            // adjust pieces position
            Predicate<Tetromino> piecesBeforeLastLineCut = p -> p.getPosition().getY() < completeLines.get(linesCut - 1);
            pieces.parallelStream()
                    .filter(piecesBeforeLastLineCut)
                    .forEach(p -> p.setPosition(p.getPosition().addY(linesCut)));

            // remove pieces completely cut
            pieces.removeIf(p -> p.getHeight() == 0);

            computeScore(linesCut);
        }
    }

    private void computeScore(int linesCut) {
        score += LINE_CLEAR_SCORE[linesCut];
        updateLevel();
    }

    private void updateLevel() {
        long newLevel = score / LEVEL_UP;
        if (newLevel > currentLevel) {
            currentLevel = newLevel;
            updateTickFrequency(STARTING_TICK - (newLevel * LEVEL_INCREASE));
        }
    }

    private List<Tetromino> getGameTetrominoes() {
        return tetrominoes
                .parallelStream()
                .filter(Tetromino::isRemovable)
                .collect(Collectors.toList());
    }

    private List<Integer> searchCompleteLines() {
        final Block[][] board = getBoard();
        return IntStream.range(0, board.length)
                .parallel()
                .filter(l -> (l < HEIGHT - 1) && Stream.of(board[l])
                        .allMatch(Objects::nonNull))
                .sorted()
                .boxed()
                .collect(Collectors.toList());
    }

    private boolean[][] getCollisionMatrix(List<Tetromino> pieces) {
        boolean[][] matrix = new boolean[HEIGHT][WIDTH];
        for (Tetromino piece : pieces) {
            Block[][] shape = piece.getShape();
            Position position = piece.getPosition();

            for (int i = 0; i < shape.length; i++) {
                for (int j = 0; j < shape[0].length; j++) {
                    int x = j + position.getX();
                    int y = i + position.getY();

                    matrix[y][x] = shape[i][j] != null;
                }
            }
        }
        return matrix;
    }

    public void handleMovement(Movement movement) {
        if (isPaused()) {
            return;
        }
        if (movement != null && current.get() != null) {
            switch (movement) {
                case LEFT: {
                    Position newPosition = current.get().getPosition().addX(-1);
                    if (!hasColission(current.get(), newPosition)) {
                        current.get().setPosition(newPosition);
                    }
                }
                break;
                case RIGHT: {
                    Position newPosition = current.get().getPosition().addX(1);
                    if (!hasColission(current.get(), newPosition)) {
                        current.get().setPosition(newPosition);
                    }
                }
                break;
                case SPEED: {
                    Position newPosition = current.get().getPosition().addY(2);
                    if (!hasColission(current.get(), newPosition)) {
                        current.get().setPosition(newPosition);
                    }
                }
                break;
                case ROTATE: {
                    current.get().turnClockwise();
                    if (hasColission(current.get(), current.get().getPosition())) {
                        current.get().turnAntiClockwise();
                    }
                }
                break;
            }
        }
    }

    public void pause() {
        paused.set(!paused.get());
    }

    public boolean isPaused() {
        return paused.get();
    }

    public boolean isGameover() {
        return gameover.get();
    }

    public void stop() {
        setTicking(false);
        paused.set(false);
        gameover.set(true);
        if (listener != null) {
            listener.gameover();
        }
    }

    private boolean hasColission(Tetromino piece, Position position) {
        boolean[][] matrix = getCollisionMatrix(tetrominoes
                .parallelStream()
                .filter(p -> p != piece)
                .collect(Collectors.toList()));

        Block[][] shape = piece.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[0].length; j++) {
                int x = j + position.getX();
                int y = i + position.getY();

                if (shape[i][j] != null && matrix[y][x]) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void setSeed(long seed) {
        JTetris.seed = seed;
    }

    public boolean isPlaying() {
        return ticking.get();
    }

    public void changeNextPiece() {
        if (!cheated.get()) {
            LEVEL_INCREASE *= 2.5;
        }
        sortNextPiece();
    }
}
