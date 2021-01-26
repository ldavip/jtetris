package br.com.ldavip.jtetris.pieces;

public class PurpleT extends Polyomino {

    @Override
    public String getColor() {
        return "purple";
    }

    @Override
    protected boolean[][] getUp() {
        return new boolean[][]{
                {true, true, true},
                {false, true, false},
        };
    }

    @Override
    protected boolean[][] getRight() {
        return new boolean[][]{
                {false, true},
                {true, true},
                {false, true},
        };
    }

    @Override
    protected boolean[][] getDown() {
        return new boolean[][]{
                {false, true, false},
                {true, true, true},
        };
    }

    @Override
    protected boolean[][] getLeft() {
        return new boolean[][]{
                {true, false},
                {true, true},
                {true, false},
        };
    }
}
