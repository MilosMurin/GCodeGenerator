package me.murin.milos.gcode;


public class GCodeGenerator {

    private final StringBuilder sb;

    public GCodeGenerator() {
        sb = new StringBuilder();
    }

    public void addGcode(String gcode) {
        sb.append(gcode).append("\n");
    }

    public void addGcodeWithE(String gcode, double eAmount) {
        sb.append(gcode).append(String.format(" E%.4f", eAmount)).append("\n");
    }

    public void addComment(String comment) {
        sb.append("; ").append(comment).append("\n");
    }

    public void addColorChange() {
        sb.append("M600").append("\n");
        sb.append("G1 E0.4 F1500").append("\n");
    }

    public void setFeedRate(int feedRate) {
        sb.append(String.format("G1 F%d", feedRate)).append("\n");
    }

    public void setAbsolutePositioning() {
        sb.append("G90").append("\n");
    }


    @Override
    public String toString() {
        return sb.toString();
    }
}
