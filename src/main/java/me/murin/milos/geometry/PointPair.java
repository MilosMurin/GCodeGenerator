package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;

public class PointPair {

    public static final double FIL_FOR_CM = 0.031354;

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

    public double getFilamentAmount() {
        Vertex vector = new Vertex(end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ());
        double size = Math.sqrt(Math.pow(vector.getX(), 2) + Math.pow(vector.getY(), 2) + Math.pow(vector.getZ(), 2));
        return size * FIL_FOR_CM;
    }

    public boolean endsInSame(PointPair pointPair) {
        return this.end.equals(pointPair.end);
    }
}
