package me.murin.milos.dcel;

import me.murin.milos.geometry.Line;
import me.murin.milos.geometry.Road;

public class Face {

    private final int id;
    private Edge firstEdge;

    private double a;
    private double b;
    private double c;
    private double d;


    public Face(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Edge getFirstEdge() {
        return firstEdge;
    }

    public void setFirstEdge(Edge firstEdge) {
        this.firstEdge = firstEdge;
    }

    public void finishUpFace() {
        // for analytical geometry
        Edge second = firstEdge.getNextEdge();
        Edge third = second.getNextEdge();
        Vertex p1 = firstEdge.getOrigin();
        Vertex p2 = second.getOrigin();
        Vertex p3 = third.getOrigin();

        firstEdge.setGreater(firstEdge.testVertex(p3) > 0);
        second.setGreater(second.testVertex(p1) > 0);
        third.setGreater(third.testVertex(p2) > 0);

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

    public boolean isPointInFace(Vertex vertex) {
        return isPointInFace(vertex.getX(), vertex.getZ());
    }

    public boolean isPointInFace(double x, double z) {
        boolean isIn = true;
        Edge current = firstEdge;
        while (isIn) {
            isIn = current.isInHalfPlane(x, z);
            current = current.getNextEdge();
            if (current == firstEdge) {
                break;
            }
        }
        return isIn;
    }

    public Line intersection(Road other) {
        return new Line(intersect(other.getFirst()), intersect(other.getLast()));
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
