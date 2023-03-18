package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;

/**
 * A line that is the result of an intersection procces of a road and a Face of an object
 */
public class Line {

    private final float x0;
    private final float y0;
    private final float z0;
    private final float tx;
    private final float ty;
    private final float tz;

    private float tStart = Float.MAX_VALUE;
    private float tEnd = Float.MIN_VALUE;

    private Line next;

    public Line(float a1, float b1, float c1, float d1, float a2, float b2, float c2, float d2) {
        this.y0 = (a2 * d1 - a1 * d2) / (a1 * b2 - a2 * b1);
        this.ty = (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1);
        this.x0 = (-d1 - b1 * y0) / a1;
        this.tx = (-c1) / (a1);

        this.z0 = 0;
        this.tz = 1;
    }

    public float getT(Vertex vertex) {
        float t = (vertex.getX() - x0) / tx;
        if (isVertexInLine(t, vertex.getY(), vertex.getZ())) {
            return t;
        } else {
            throw new IllegalArgumentException("Cannot set bounds with vertices that dont belong to the line!");
        }
    }

    public boolean isVertexInLine(Vertex v) {
        float t = (v.getX() - x0) / tx;
        return isVertexInLine(t, v.getY(), v.getZ());
    }

    private boolean isVertexInLine(float xt, float y, float z) {
        return (y == y0 + ty * xt) && (z == z0 + tz * xt);
    }

    public Vertex getPoint(float t) {
        if (t <= tEnd || t >= tStart) {
            throw new IllegalArgumentException("Parameter \"t\" is out of bounds of this line");
        }
        return new Vertex(x0 + tx * t, y0 + ty * t, z0 + tz * t);
    }

    public Vertex getStartPoint() {
        return getPoint(tStart);
    }

    public void setStartPoint(Vertex start) {
        tStart = getT(start);
    }

    public Vertex getEndPoint() {
        return getPoint(tEnd);
    }

    public void setEndPoint(Vertex end) {
        tEnd = getT(end);
    }

    public Line getNext() {
        return next;
    }

    public void setNext(Line next) {
        this.next = next;
    }

    public boolean isWithinBounds(Vertex vertex) {
        float t = getT(vertex);
        if (t >= tStart && t <= tEnd) {
            return true;
        } else return t <= tStart && t >= tEnd;
    }
}
