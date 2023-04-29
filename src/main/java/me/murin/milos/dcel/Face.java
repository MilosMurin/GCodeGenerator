package me.murin.milos.dcel;

import me.murin.milos.geometry.Line;
import me.murin.milos.geometry.Plane;
import me.murin.milos.geometry.PointPair;

public class Face {

    private final int id;
    private Edge firstEdge;

    private Plane plane = null;

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

        this.plane = new Plane(p1, p2, p3);
    }

    public boolean isPointInFace(Vertex vertex) {
        boolean isIn = true;
        Edge current = firstEdge;
        while (isIn) {
            isIn = current.isInHalfPlane(vertex);
            current = current.getNextEdge();
            if (current == firstEdge) {
                break;
            }
        }
        return isIn;
    }

    public Line project(PointPair other) {
        return this.plane.project(other);
    }

    @Override
    public String toString() {
        return this.plane.toString();
    }
}
