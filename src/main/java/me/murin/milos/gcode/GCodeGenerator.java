package me.murin.milos.gcode;


public class GCodeGenerator {

    private final StringBuilder sb;
    private boolean addedFinish = false;

    public GCodeGenerator() {
        sb = new StringBuilder();
        newLine();
        addComment("Generated by GCodeGenerator program");
        newLine();
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
        addComment("Color change gcode");
        addGcode("M600");
        addGcode("G1 E0.4 F1500");
        newLine();
    }

    public void newLine() {
        sb.append("\n");
    }

    public void setFeedRate(int feedRate) {
        sb.append(String.format("G1 F%d", feedRate)).append("\n");
    }

    public void setAbsolutePositioning() {
        addGcode("G90");
    }


    public void addFinish() {
        newLine();
        addComment("END Generated by GCodeGenerator program");
        newLine();
        addedFinish = true;
    }

    @Override
    public String toString() {
        if (!addedFinish) {
            addFinish();
        }
        return sb.toString();
    }
}
