package me.murin.milos.geometry;

import info.pavie.basicosmparser.model.Node;

/**
 * Road loaded from an osm file
 * it creates a vertical plane to be able to create an intersection with a fcae of an object
 */
public class Road {

    private Node first;
    private Node last;
    private float y = 1f;
    private Road next = null;

    private float a;
    private float b;
    private float c;
    private float d;

    public Road(Node first, Node last) {
        this.first = first;
        this.last = last;

        // create two vectors to be able to do a cross product to get the normal vector of a vertical plane
        float a1 = (float) (last.getLat() - first.getLat());
        float a2 = 0;
        float a3 = (float) (last.getLon() - first.getLon());
        float b1 = (float) (last.getLat() - first.getLat());
        float b2 = 0 - y;
        float b3 = (float) (last.getLon() - first.getLon());

        this.a = a2 * b3 - a3 * b2;
        this.b = a3 * b1 - a1 * b3;
        this.c = a1 * b2 - a2 * b1;
        this.d = (float) -(this.a * first.getLat() + this.b * y + this.b * first.getLon());
    }


    public Node getFirst() {
        return first;
    }

    public Node getLast() {
        return last;
    }

    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public float getC() {
        return c;
    }

    public float getD() {
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
