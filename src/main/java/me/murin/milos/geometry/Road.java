package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;

/**
 * Road loaded from an osm file
 * it creates a vertical plane to be able to create an intersection with a fcae of an object
 */
public class Road {

    private final Vertex first;
    private final Vertex last;
    private Road next = null;


    public Road(Vertex first, Vertex last) {
        this.first = first;
        this.last = last;
    }

    public Vertex getFirst() {
        return first;
    }

    public Vertex getLast() {
        return last;
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
