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

    private Float a = null;
    private Float b = null;
    private Float c = null;
    private Float d = null;

    public Road(Node first, Node last) {
        this.first = first;
        this.last = last;
    }

    private void calculateGeometry() {
        float y = 1f;
        // create two vectors to be able to do a cross product to get the normal vector of a vertical plane
        float lastX = getCoordFromNode(last, X);
        float lastZ = getCoordFromNode(last, Z);
        float firstX = getCoordFromNode(first, X);
        float firstZ = getCoordFromNode(first, Z);
        float a1 = lastX - firstX;
        float a2 = 0;
        float a3 = lastZ - firstZ;
        float b1 = lastX - firstX;
        float b2 = 0 - y;
        float b3 = lastZ - firstZ;

        this.a = a2 * b3 - a3 * b2;
        this.b = a3 * b1 - a1 * b3;
        this.c = a1 * b2 - a2 * b1;
        this.d = -(this.a * firstX + this.b * y + this.b * firstZ);
    }


    public Node getFirst() {
        return first;
    }

    public Node getLast() {
        return last;
    }

    public float getA() {
        if (a == null) {
            calculateGeometry();
        }
        return a;
    }

    public float getB() {
        if (b == null) {
            calculateGeometry();
        }
        return b;
    }

    public float getC() {
        if (c == null) {
            calculateGeometry();
        }
        return c;
    }

    public float getD() {
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
