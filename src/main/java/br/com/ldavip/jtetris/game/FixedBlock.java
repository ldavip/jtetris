package br.com.ldavip.jtetris.game;

import br.com.ldavip.jtetris.pieces.Polyomino;

public class FixedBlock extends Tetromino {
    public FixedBlock(Polyomino polyomino, Position position) {
        super(polyomino, position, false);
    }
}
