package me.murin.milos.dcel;

import jdk.jshell.execution.Util;
import me.murin.milos.utils.Utils;

import java.util.Objects;

public class Vertex {

    private final int id;
    private final double x;
    private final double y;
    private final double z;
    private Edge incident;

    public Vertex(double x, double y, double z) {
        this(-1, x, y, z);
    }

    public Vertex(int id, double x, double y, double z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    /**
     * @param index 0-x, 1-y, 2-z, other-x
     */
    public double getCoord(int index) {
        return switch (index) {
            case 1 -> y;
            case 2 -> z;
            default -> x;
        };
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Edge getIncident() {
        return incident;
    }

    public void setIncident(Edge incident) {
        this.incident = incident;
    }

    public boolean isPositionEqual(Vertex other) {
        return Utils.isAlmostEqual(this.x, other.x) && Utils.isAlmostEqual(this.y, other.y) &&
                Utils.isAlmostEqual(this.z, other.z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vertex vertex) {
            return/* vertex.getId() == this.id &&*/ isPositionEqual(vertex);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, (float) x, (float) y, (float) z);
    }
}
