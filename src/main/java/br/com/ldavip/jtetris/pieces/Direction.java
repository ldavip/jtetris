package br.com.ldavip.jtetris.pieces;

public enum Direction {
    UP, RIGHT, DOWN, LEFT;

    public Direction getNextClockwise() {
        switch (this) {
            case UP:
                return RIGHT;
            case RIGHT:
                return DOWN;
            case DOWN:
                return LEFT;
            case LEFT:
                return UP;
        }
        throw new IllegalStateException("Invalid position!");
    }

    public Direction getNextAntiClockwise() {
        switch (this) {
            case UP:
                return LEFT;
            case LEFT:
                return DOWN;
            case DOWN:
                return RIGHT;
            case RIGHT:
                return UP;
        }
        throw new IllegalStateException("Invalid position!");
    }
}
