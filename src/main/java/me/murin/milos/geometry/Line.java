package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;

public class Line {

    private final float x0;
    private final float y0;
    private final float z0;
    private final float tx;
    private final float ty;
    private final float tz;

    private float tMax = Float.MAX_VALUE;
    private float tMin = Float.MIN_VALUE;

    public Line(float a1, float b1, float c1, float d1, float a2, float b2, float c2, float d2) {
        this.y0 = (a2 * d1 - a1 * d2) / (a1 * b2 - a2 * b1);
        this.ty = (a2 * c1 - a1 * c2) / (a1 * b2 - a2 * b1);
        this.x0 = (-d1 - b1 * y0) / a1;
        this.tx = (-c1) / (a1);

        this.z0 = 0;
        this.tz = 1;
    }

    public void setBounds(Vertex v1, Vertex v2) {
        if (!(isVertexInLine(v1) && isVertexInLine(v2))) {
            throw new IllegalArgumentException("Cannot set bounds with vertices that dont belong to the line!");
        }
        if (v1 == v2) {
            throw new IllegalArgumentException("Vertices cannot be equal! Just use a point!");
        }
        float t1 = (v1.getX() - x0) / tx;
        float t2 = (v2.getX() - x0) / tx;
        tMax = Math.max(t1, t2);
        tMin = Math.min(t1, t2);
    }

    public boolean isVertexInLine(Vertex v) {
        float t = (v.getX() - x0) / tx;
        return (v.getY() == y0 + ty * t) && (v.getZ() == z0 + tz * t);
    }

    public Vertex getPoint(float t) {
        if (t <= tMin || t >= tMax) {
            throw new IllegalArgumentException("Parameter \"t\" is out of bounds of this line");
        }
        return new Vertex(x0 + tx * t, y0 + ty * t, z0 + tz * t);
    }

}
