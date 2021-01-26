package br.com.ldavip.jtetris.pieces;

public class BlueJ extends Polyomino {

    @Override
    public String getColor() {
        return "blue";
    }

    @Override
    protected boolean[][] getUp() {
        return new boolean[][]{
                {false, true},
                {false, true},
                {true, true}
        };
    }

    @Override
    protected boolean[][] getRight() {
        return new boolean[][]{
                {true, false, false},
                {true, true, true}
        };
    }

    @Override
    protected boolean[][] getDown() {
        return new boolean[][]{
                {true, true},
                {true, false},
                {true, false}
        };
    }

    @Override
    protected boolean[][] getLeft() {
        return new boolean[][]{
                {true, true, true},
                {false, false, true}
        };
    }
}
