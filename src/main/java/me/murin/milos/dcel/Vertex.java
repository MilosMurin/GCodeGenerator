package me.murin.milos.dcel;

import info.pavie.basicosmparser.model.Node;
import me.murin.milos.utils.Utils;

import java.util.Objects;

public class Vertex {

    private int id;
    private double x;
    private double y;
    private double z;

    public Vertex(Node node) {
        this(-1, node.getLat(), 1, node.getLon());
    }

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

    public void setId(int id) {
        this.id = id;
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

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isPositionEqual(Vertex other) {
        return Utils.isAlmostEqual(this.x, other.x) && Utils.isAlmostEqual(this.y, other.y) &&
                Utils.isAlmostEqual(this.z, other.z);
    }

    public void transform() {

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
        return Objects.hash((float) x, (float) y, (float) z);
    }
}
