package br.com.ldavip.jtetris.game;

import br.com.ldavip.jtetris.pieces.Polyomino;

import java.util.ArrayList;
import java.util.List;

public class Tetromino {

    private final Polyomino polyomino;
    private Position position;
    private boolean movable;
    private Block[][] blocks;
    private boolean removable;

    public Tetromino(Polyomino polyomino, Position position, boolean removable) {
        this.polyomino = polyomino;
        this.position = position;
        this.removable = removable;
        updateShape();
    }

    public Tetromino(Polyomino polyomino, Position position) {
        this(polyomino, position, true);
    }

    public Tetromino(Polyomino polyomino) {
        this(polyomino, null);
    }

    public Polyomino getPolyomino() {
        return polyomino;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getHeight() {
        return getShape().length;
    }

    public int getWidth() {
        return getShape().length > 0 ? getShape()[0].length : 0;
    }

    public Position getPosition() {
        return position;
    }

    public Block[][] getShape() {
        return blocks;
    }

    public String getColor() {
        return polyomino.getColor();
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void turnClockwise() {
        if (isMovable()) {
            polyomino.setDirection(polyomino.getDirection().getNextClockwise());
            updateShape();
        }
    }

    public void turnAntiClockwise() {
        if (isMovable()) {
            polyomino.setDirection(polyomino.getDirection().getNextAntiClockwise());
            updateShape();
        }
    }

    public void cut(List<Integer> completeLines) {
        if (!isMovable()) {
            List<Block[]> newShape = new ArrayList<>();
            for (int i = 0; i < blocks.length; i++) {
                int y = position.getY() + i;
                if (!completeLines.contains(y)) {
                    newShape.add(blocks[i]);
                }
            }
            if (newShape.size() != blocks.length) {
                blocks = newShape.toArray(Block[][]::new);
            }
        }
    }

    private void updateShape() {
        boolean[][] shape = polyomino.getShape();
        blocks = new Block[shape.length][];
        for (int i = 0; i < shape.length; i++) {
            boolean[] line = shape[i];
            blocks[i] = new Block[line.length];

            for (int j = 0; j < line.length; j++) {
                if (shape[i][j]) {
                    blocks[i][j] = new Block(polyomino.getColor());
                }
            }
        }
    }

    @Override
    public String toString() {
        return polyomino + " " + position;
    }
}
