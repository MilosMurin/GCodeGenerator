package me.murin.milos.dcel;

import info.pavie.basicosmparser.model.Node;
import me.murin.milos.geometry.Line;
import me.murin.milos.geometry.Road;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.Utils;

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

    public boolean isPointInFace(Node node) {
        return isPointInFace(Utils.getCoordFromNode(node, Axis.X), Utils.getCoordFromNode(node, Axis.Z));
    }

    public Vertex instersect(Line line) {
        // the line should be crated from a point of a road
        double top = -(a * line.getX0() + b * line.getY0() + c * line.getZ0() + d);
        double bottom = a * line.getTx() + b * line.getTy() + c * line.getTz();
        if (bottom == 0) {
            return null; // intersection is the line or none
        }
        return line.getPoint(top / bottom);
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

    public Line intersection(double oa, double ob, double oc, double od) {
        double k = a / oa;
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
