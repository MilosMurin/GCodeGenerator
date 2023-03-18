package me.murin.milos.dcel;

import me.murin.milos.geometry.Line;
import me.murin.milos.geometry.Road;

public class Face {

    private final int id;
    private Edge firstEdge;

    private float a;
    private float b;
    private float c;
    private float d;


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

        float a1 = p2.getX() - p1.getX();
        float a2 = p2.getY() - p1.getY();
        float a3 = p2.getZ() - p1.getZ();
        float b1 = p3.getX() - p1.getX();
        float b2 = p3.getY() - p1.getY();
        float b3 = p3.getZ() - p1.getZ();

        this.a = a2 * b3 - a3 * b2;
        this.b = a3 * b1 - a1 * b3;
        this.c = a1 * b2 - a2 * b1;
        this.d = -(this.a * p1.getX() + this.b * p1.getY() + this.b * p1.getZ());
    }

    public boolean isPointInFace(float x, float z) {
        boolean isIn = true;
        int counter = 0;
        Edge current = firstEdge;
        while (isIn && counter < 3) {
            isIn = current.isInHalfPlane(x, z);
            current = current.getNextEdge();
            counter++;
        }
        return isIn;
    }

    public Line intersection(float oa, float ob, float oc, float od) {
        float k = a / oa;
        if (k * this.b == ob && k * this.c == oc) {
            // vectors are linearily dependant :D
            return null;
        }
        return new Line(this.a, this.b, this.c, this.d, oa, ob, oc, od);
    }

    public Line intersection(Face other) {
        return intersection(other.a, other.b, other.c, other.d);
    }

    public Line intersection(Road other) {
        return intersection(other.getA(), other.getB(), other.getC(), other.getD());
    }
}
