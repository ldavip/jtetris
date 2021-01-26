package br.com.ldavip.jtetris.pieces;

public class GrayBlock extends Polyomino {

    private static final boolean[][] SHAPE = {{true}};

    @Override
    public String getColor() {
        return "gray";
    }

    @Override
    protected boolean[][] getUp() {
        return SHAPE;
    }

    @Override
    protected boolean[][] getRight() {
        return SHAPE;
    }

    @Override
    protected boolean[][] getDown() {
        return SHAPE;
    }

    @Override
    protected boolean[][] getLeft() {
        return SHAPE;
    }
}
