package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;

public class HalfPlane {

    private final double a;
    private final double b;
    private final double c;

    private boolean greater;
    private boolean greaterSet = false;

    public HalfPlane(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public void setGreater(boolean greater) {
        this.greater = greater;
        this.greaterSet = true;
    }

    public double testPoint(Vertex vertex) {
        return a * vertex.getX() + b * vertex.getZ() + c;
    }

    public boolean isInHalfPlane(Vertex vertex) {
        // if i will not want to take the edges change this to be a sharp inequality
        if (greaterSet) {
            if (greater) {
                return testPoint(vertex) >= 0;
            } else {
                return testPoint(vertex) <= 0;
            }
        }
        return false;
    }

}
