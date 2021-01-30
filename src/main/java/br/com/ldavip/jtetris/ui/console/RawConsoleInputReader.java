package br.com.ldavip.jtetris.ui.console;

import jline.console.ConsoleReader;

import java.io.IOException;

public class RawConsoleInputReader implements InputReader {

    private final ConsoleReader reader;

    public RawConsoleInputReader() throws IOException {
        reader = new ConsoleReader(System.in, System.out);
    }

    @Override
    public int readInput() throws IOException {
        int input = reader.readCharacter();
        reader.accept();
        return input;
    }
}
