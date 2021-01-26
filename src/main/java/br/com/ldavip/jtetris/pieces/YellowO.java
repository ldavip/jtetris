package br.com.ldavip.jtetris.pieces;

public class YellowO extends Polyomino {

    @Override
    public String getColor() {
        return "yellow";
    }

    @Override
    protected boolean[][] getUp() {
        return new boolean[][]{
                {true, true},
                {true, true},
        };
    }

    @Override
    protected boolean[][] getRight() {
        return new boolean[][]{
                {true, true},
                {true, true},
        };
    }

    @Override
    protected boolean[][] getDown() {
        return new boolean[][]{
                {true, true},
                {true, true},
        };
    }

    @Override
    protected boolean[][] getLeft() {
        return new boolean[][]{
                {true, true},
                {true, true},
        };
    }
}
