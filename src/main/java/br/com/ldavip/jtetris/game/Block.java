package br.com.ldavip.jtetris.game;

public class Block {
    private final String color;

    public Block(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return '[' + color + ']';
    }
}
