package me.murin.milos.gcode;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.listStuff.Extremes;
import me.murin.milos.utils.Axis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GCodeReader {

    private final File file;

    private final StringBuilder beforeGCode = new StringBuilder();
    private final StringBuilder afterGCode = new StringBuilder();
    private final Extremes extremes;
    private boolean before = true;
    private String lastLine = "";

    public GCodeReader(String path) {
        file = new File(path);
        extremes = new Extremes();
    }

    public void load() throws FileNotFoundException {
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Vertex v = getVertexFromGcode(line);
            if (v != null) {
                extremes.testExtremes2D(v);
            }

            if (line.equals(";WIPE_START")) {
                if (lastLine.startsWith("; stop printing object")) {
                    before = false;
                }
            }

            if (before) {
                beforeGCode.append(line).append("\n");
            } else {
                afterGCode.append(line).append("\n");
            }

            if (!line.isEmpty()) {
                lastLine = line;
            }
        }

    }


    public Vertex getVertexFromGcode(String line) {
        if (line.startsWith("G1")) {
            String[] split = line.split(" ");
            Double z = null;
            Double x = null;
            for (String s : split) {
                if (s.startsWith("X")) {
                    z = Double.parseDouble(s.substring(1));
                } else if (s.startsWith("Y")) {
                    x = Double.parseDouble(s.substring(1));
                }
            }
            if (z != null && x != null) {
                if (z > 0 && x > 0) {
                    extremes.testExtremes(Axis.X, x);
                    extremes.testExtremes(Axis.Z, z);
                }
            }

        }
        return null;
    }

    public Extremes getExtremes() {
        return extremes;
    }

    public String getBeforeGCode() {
        return beforeGCode.toString();
    }

    public String getAfterGCode() {
        return afterGCode.toString();
    }
}
