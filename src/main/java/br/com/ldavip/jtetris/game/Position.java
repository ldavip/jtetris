package br.com.ldavip.jtetris.game;

public class Position {
    private final int y;
    private final int x;

    public Position(int y, int x) {
        this.y = y;
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position addX(int value) {
        return add(value, 0);
    }

    public Position addY(int value) {
        return add(0, value);
    }

    public Position add(int x, int y) {
        return new Position(this.y + y, this.x + x);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }
}
