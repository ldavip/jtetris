package br.com.ldavip.jtetris.pieces;

public abstract class Polyomino {

    private Direction direction = Direction.UP;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public abstract String getColor();

    protected abstract boolean[][] getUp();

    protected abstract boolean[][] getRight();

    protected abstract boolean[][] getDown();

    protected abstract boolean[][] getLeft();

    public boolean[][] getShape() {
        switch (direction) {
            case UP:
                return getUp();
            case RIGHT:
                return getRight();
            case DOWN:
                return getDown();
            case LEFT:
                return getLeft();
        }
        return new boolean[0][0];
    }

    @Override
    public String toString() {
        return '[' + getClass().getSimpleName() + ":"+ direction + ']';
    }
}
