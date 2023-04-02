package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;

public class PointPair {

    private Vertex start;
    private Vertex end;

    private PointPair next = null;

    public PointPair(Vertex start, Vertex end) {
        this.start = start;
        this.end = end;
    }

    public boolean hasNext() {
        return next != null;
    }

    public void setNext(PointPair next) {
        this.next = next;
    }

    public PointPair getNext() {
        return next;
    }

    public Vertex getStart() {
        return start;
    }

    public void setStart(Vertex start) {
        this.start = start;
    }

    public Vertex getEnd() {
        return end;
    }

    public void setEnd(Vertex end) {
        this.end = end;
    }
}
