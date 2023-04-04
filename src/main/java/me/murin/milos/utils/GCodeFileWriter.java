package me.murin.milos.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GCodeFileWriter {

    private FileWriter writer;

    public GCodeFileWriter(String path) throws IOException {
        writer = new FileWriter(new File(path));
    }

    public void write(String line) throws IOException {
        writer.write(line);
    }

    public void close() throws IOException {
        writer.close();
    }

}
