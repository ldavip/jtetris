package br.com.ldavip.jtetris.pieces;

public class RedZ extends Polyomino {

    @Override
    public String getColor() {
        return "red";
    }

    @Override
    protected boolean[][] getUp() {
        return new boolean[][]{
                {true, true, false},
                {false, true, true},
        };
    }

    @Override
    protected boolean[][] getRight() {
        return new boolean[][]{
                {false, true},
                {true, true},
                {true, false},
        };
    }

    @Override
    protected boolean[][] getDown() {
        return new boolean[][]{
                {true, true, false},
                {false, true, true},
        };
    }

    @Override
    protected boolean[][] getLeft() {
        return new boolean[][]{
                {false, true},
                {true, true},
                {true, false},
        };
    }
}
