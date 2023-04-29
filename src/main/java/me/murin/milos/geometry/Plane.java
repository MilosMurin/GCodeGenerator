package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;

public class Plane {

    private final double a;
    private final double b;
    private final double c;
    private final double d;

    public Plane(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Plane(Vertex p1, Vertex p2, Vertex p3) {
        double a1 = p2.getX() - p1.getX();
        double a2 = p2.getY() - p1.getY();
        double a3 = p2.getZ() - p1.getZ();
        double b1 = p3.getX() - p1.getX();
        double b2 = p3.getY() - p1.getY();
        double b3 = p3.getZ() - p1.getZ();

        this.a = a2 * b3 - a3 * b2;
        this.b = a3 * b1 - a1 * b3;
        this.c = a1 * b2 - a2 * b1;
        this.d = -(this.a * p1.getX() + this.b * p1.getY() + this.c * p1.getZ());
    }

    public Line project(PointPair other) {
        return new Line(intersect(other.getStart()), intersect(other.getEnd()));
    }

    public Vertex intersect(Vertex vertex) {
        if (b == 0) {
            return null;
        } else {
            double num = (a * vertex.getX() + b + c * vertex.getZ() + d) / b;
            return new Vertex(vertex.getX(), 1 - num, vertex.getZ());
        }
    }


    @Override
    public String toString() {
        return String.format("%.4f*x + %.4f*y + %.4f*z + %.4f = 0\n", a, b, c, d);
    }
}
