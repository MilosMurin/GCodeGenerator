package me.murin.milos.gcode;

import java.io.FileWriter;
import java.io.IOException;

public class GCodeFileWriter {

    private final FileWriter writer;

    public GCodeFileWriter(String path) throws IOException {
        writer = new FileWriter(path);
    }

    public void write(String line) throws IOException {
        writer.write(line);
    }

    public void close() throws IOException {
        writer.close();
    }

}
