package me.murin.milos.listStuff;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.utils.Axis;

public class Extremes {

    private final double[] minimums = new double[] {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE}; // 0 - x, 1 - y,
    // 2 - z
    private final double[] maximums = new double[] {-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE}; // 0 - x,
    // 1 - y, 2 - z

    public void testExtremes(Vertex vertex) {
        testExtremes2D(vertex);
        testExtremes(Axis.Y, vertex.getY());
    }

    public void testExtremes2D(Vertex vertex) {
        testExtremes(Axis.X, vertex.getX());
        testExtremes(Axis.Z, vertex.getZ());
    }

    public void testExtremes(Axis axis, double amount) {
        if (amount < getMin(axis)) {
            minimums[axis.getId()] = amount;
        }
        if (amount > getMax(axis)) {
            maximums[axis.getId()] = amount;
        }
    }

    public void testExtremes(Extremes extremes) {
        for (Axis a : Axis.values()) {
            this.testExtremes(a, extremes.getMax(a));
            this.testExtremes(a, extremes.getMin(a));
        }
    }

    public double getMax(Axis axis) {
        return maximums[axis.getId()];
    }

    public double getMin(Axis axis) {
        return minimums[axis.getId()];
    }

    public double getSize(Axis axis) {
        return getMax(axis) - getMin(axis);
    }

}
