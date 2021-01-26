package br.com.ldavip.jtetris.pieces;

public class OrangeL extends Polyomino {

    @Override
    public String getColor() {
        return "orange";
    }

    @Override
    protected boolean[][] getUp() {
        return new boolean[][]{
                {true, false},
                {true, false},
                {true, true}
        };
    }

    @Override
    protected boolean[][] getRight() {
        return new boolean[][]{
                {true, true, true},
                {true, false, false}
        };
    }

    @Override
    protected boolean[][] getDown() {
        return new boolean[][]{
                {true, true},
                {false, true},
                {false, true}
        };
    }

    @Override
    protected boolean[][] getLeft() {
        return new boolean[][]{
                {false, false, true},
                {true, true, true}
        };
    }
}
