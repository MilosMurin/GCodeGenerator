package me.murin.milos.geometry;

import info.pavie.basicosmparser.model.Node;

import static me.murin.milos.utils.Utils.getCoordFromNode;
import static me.murin.milos.utils.Axis.*;

/**
 * Road loaded from an osm file
 * it creates a vertical plane to be able to create an intersection with a fcae of an object
 */
public class Road {

    private final Node first;
    private final Node last;
    private Road next = null;

    private Double a = null;
    private Double b = null;
    private Double c = null;
    private Double d = null;

    public Road(Node first, Node last) {
        this.first = first;
        this.last = last;
    }

    private void calculateGeometry() {
        float y = 1f;
        // create two vectors to be able to do a cross product to get the normal vector of a vertical plane
        double lastX = getCoordFromNode(last, X);
        double lastZ = getCoordFromNode(last, Z);
        double firstX = getCoordFromNode(first, X);
        double firstZ = getCoordFromNode(first, Z);
        double a1 = lastX - firstX;
        double a2 = 0;
        double a3 = lastZ - firstZ;
        double b1 = lastX - firstX;
        double b2 = 0 - y;
        double b3 = lastZ - firstZ;

        this.a = a2 * b3 - a3 * b2;
        this.b = a3 * b1 - a1 * b3;
        this.c = a1 * b2 - a2 * b1;
        this.d = -(this.a * firstX + this.b * y + this.c * firstZ);
    }


    public Node getFirst() {
        return first;
    }

    public Node getLast() {
        return last;
    }

    public double getA() {
        if (a == null) {
            calculateGeometry();
        }
        return a;
    }

    public double getB() {
        if (b == null) {
            calculateGeometry();
        }
        return b;
    }

    public double getC() {
        if (c == null) {
            calculateGeometry();
        }
        return c;
    }

    public double getD() {
        if (d == null) {
            calculateGeometry();
        }
        return d;
    }


    public Road getNext() {
        return next;
    }

    public void setNext(Road next) {
        this.next = next;
    }

    public boolean hasNext() {
        return this.next != null;
    }
}
