package br.com.ldavip.jtetris.pieces;

public class GreenS extends Polyomino {

    @Override
    public String getColor() {
        return "green";
    }

    @Override
    protected boolean[][] getUp() {
        return new boolean[][]{
                {false, true, true},
                {true, true, false}
        };
    }

    @Override
    protected boolean[][] getRight() {
        return new boolean[][]{
                {true, false},
                {true, true},
                {false, true}
        };
    }

    @Override
    protected boolean[][] getDown() {
        return new boolean[][]{
                {false, true, true},
                {true, true, false}
        };
    }

    @Override
    protected boolean[][] getLeft() {
        return new boolean[][]{
                {true, false},
                {true, true},
                {false, true}
        };
    }
}
