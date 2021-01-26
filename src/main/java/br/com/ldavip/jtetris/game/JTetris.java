package br.com.ldavip.jtetris.game;

import br.com.ldavip.jtetris.pieces.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class JTetris extends TimerTask {
    private static long seed = 0;
    private static long tick = 500;

    private static final Position INITIAL_POSITION = new Position(0, 5);
    private static final int HEIGHT = 22;
    private static final int WIDTH = 12;

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
    private AtomicBoolean ticking = new AtomicBoolean(false);
    private Timer timer;

    private TickListener listener;

    private AtomicReference<Tetromino> next = new AtomicReference<>();
    private AtomicReference<Tetromino> current = new AtomicReference<>();
    private AtomicBoolean paused = new AtomicBoolean(false);

    public JTetris() {
        initBoard();
        startTicking();
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

    public Tetromino getNext() {
        return next.get();
    }

    public void setListener(TickListener listener) {
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

    private void startTicking() {
        ticking.set(true);
        timer = new Timer();
        timer.schedule(this, tick, tick);
    }

    @Override
    public void run() {
        if (!paused.get()) {
            if (current.get() == null) {
                List<Integer> completeLines = searchCompleteLines();
                if (!completeLines.isEmpty()) {
                    tetrominoes.parallelStream().forEach(p -> p.cut(completeLines));
                    completeLines.sort(Comparator.naturalOrder());
                    int linesCut = completeLines.size();
                    tetrominoes.parallelStream()
                            .filter(p -> p.getPosition().getY() < completeLines.get(linesCut - 1))
                            .forEach(p -> p.setPosition(p.getPosition().addY(linesCut)));
                    tetrominoes.removeIf(p -> p.isRemovable() && (p.getPosition().getY() + p.getShape().length) > HEIGHT - 1);
                }

                next.get().setPosition(INITIAL_POSITION);
                next.get().setMovable(true);
                tetrominoes.add(next.get());

                current.set(next.get());
                sortNextPiece();

                if (hasColission(current.get(), current.get().getPosition().addY(1))) {
                    setTicking(false);
                    if (listener != null) {
                        listener.tick();
                    }
                    throw new RuntimeException("GAMEOVER!");
                }
            } else {
                if (hasColission(current.get(), current.get().getPosition().addY(1))) {
                    current.get().setMovable(false);
                    current.set(null);
                } else {
                    current.get().setPosition(current.get().getPosition().addY(1));
                }
            }

            if (listener != null) {
                listener.tick();
            }
        }
    }

    private List<Integer> searchCompleteLines() {
        final Block[][] board = getBoard();
        return IntStream.range(0, board.length)
                .parallel()
                .filter(l -> (l < HEIGHT - 1) && Stream.of(board[l])
                        .allMatch(Objects::nonNull))
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
        if (movement != null) {
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
}
