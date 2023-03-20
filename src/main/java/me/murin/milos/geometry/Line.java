package me.murin.milos.geometry;

import info.pavie.basicosmparser.model.Node;
import me.murin.milos.dcel.Vertex;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.Utils;

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

    public Line(float pointX, float pointY, float pointZ, float vectorX, float vectorY, float vectorZ) {
        this.x0 = pointX;
        this.y0 = pointY;
        this.z0 = pointZ;
        this.tx = vectorX;
        this.ty = vectorY;
        this.tz = vectorZ;
    }

    public Line(float a1, float b1, float c1, float d1, float a2, float b2, float c2, float d2) {
        // b2 should always be 0 because it is a vertical plane
        if (b1 == 0) {
            throw new IllegalArgumentException("First plane cannot be vertical!");
        }
        if (b2 != 0) {
            throw new IllegalArgumentException("Second plane has to be vertical!");
        }

        this.y0 = (a2 * d1 - a1 * d2) / (-a2 * b1);
        this.ty = (a2 * c1 - a1 * c2) / (-a2 * b1);
        this.x0 = (-d1 - b1 * y0) / a1;
        this.tx = (-c1) / (a1);

        this.z0 = 0;
        this.tz = 1;
    }

    public float getT(Vertex vertex) {

        float t = tx != 0 ? getTForX(vertex.getX()) : ty != 0 ? getTForY(vertex.getY()) : tz != 0 ?
                getTForZ(vertex.getZ()) : 0;
        if (isVertexInLine(vertex, t)) {
            return t;
        } else {
            throw new IllegalArgumentException("Cannot set bounds with vertices that dont belong to the line!");
        }
    }

    public boolean isVertexInLine(Vertex v, float t) {
        return (v.getX() == x0 + tx * t) && (v.getY() == y0 + ty * t) && (v.getZ() == z0 + tz * t);
    }

    public boolean isVertexInLine(Vertex v) {
        try {
            getT(v);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private float getTForX(float x) {
        return (x - x0) / tx;
    }

    private float getTForY(float y) {
        return (y - y0) / ty;
    }

    private float getTForZ(float z) {
        return (z - z0) / tz;
    }

    public Vertex getPoint(float t) {
        if (!(t <= tEnd && t >= tStart) || !(t >= tEnd && t <= tStart)) {
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

    public Vertex setStartPoint(Node start) {
        return setPoint(start, true);
    }

    public void setEndPoint(Node end) {
        setPoint(end, false);
    }

    private Vertex setPoint(Node point, boolean start) {
        float xt = getXtForNode(point);
        // calculate y
        if (start) {
            tStart = xt;
        } else {
            tEnd = xt;
        }
        return getPoint(xt);
    }

    public float getXtForNode(Node node) {
        float x = Utils.getCoordFromNode(node, Axis.X);
        float z = Utils.getCoordFromNode(node, Axis.Z);
        float xt = getTForX(x);
        float zt = getTForZ(z);
        if (xt != zt) {
            System.out.printf("Line x=%f+%ft; y=%f+%ft; z=%f+%ft\n", x0, tx, y0, ty, z0, tz);
            System.out.printf("Point x=%f; z=%f\n", x, z);
            throw new IllegalArgumentException("Node cannot be set on the line!");
        }
        return xt;
    }

    public Vertex getPointOnLine(Node node) {
        float xt = getXtForNode(node);
        return getPoint(xt);
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

    public boolean hasNext() {
        return this.next != null;
    }

    public Vertex intersect(Line other) {
        if (this == other) {
            return null;
        }
        float xt = (other.x0 - this.x0) / (this.tx - other.tx);
        float yt = (other.y0 - this.y0) / (this.ty - other.ty);
        float zt = (other.z0 - this.z0) / (this.tz - other.tz);
        if (xt != yt) {
            return null;
        }
        if (yt != zt) {
            return null;
        }
        return getPoint(xt);
    }

    public boolean isWithinBounds(Vertex vertex) {
        if (vertex == null) {
            return false;
        }
        float t = getT(vertex);
        if (t >= tStart && t <= tEnd) {
            return true;
        } else {
            return t <= tStart && t >= tEnd;
        }
    }

    public float getX0() {
        return x0;
    }

    public float getY0() {
        return y0;
    }

    public float getZ0() {
        return z0;
    }

    public float getTx() {
        return tx;
    }

    public float getTy() {
        return ty;
    }

    public float getTz() {
        return tz;
    }
}
