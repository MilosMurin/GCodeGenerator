package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.utils.MyMatrix;
import me.murin.milos.utils.Utils;

/**
 * A line that is the result of an intersection procces of a road and a Face of an object
 */
public class Line {

    private final double x;
    private final double y;
    private final double z;
    private final double a;
    private final double b;
    private final double c;

    private double tStart = Double.MAX_VALUE;
    private double tEnd = Double.MIN_VALUE;

    private Line next;


    public Line(double pointX, double pointY, double pointZ, double vectorX, double vectorY, double vectorZ) {
        this.x = pointX;
        this.y = pointY;
        this.z = pointZ;
        this.a = vectorX;
        this.b = vectorY;
        this.c = vectorZ;
        if (this.a == 0 && this.b == 0 && this.c == 0) {
            throw new IllegalArgumentException("You've created a point and not a line!");
        }
    }

    public Line(Vertex p, Vertex p2) {
        this(p.getX(), p.getY(), p.getZ(), p2.getX() - p.getX(), p2.getY() - p.getY(), p2.getZ() - p.getZ());
        this.setStartPoint(p);
        this.setEndPoint(p2);
    }

    public double getT(Vertex vertex) {

        double t = a != 0 ? getTForX(vertex.getX()) : b != 0 ? getTForY(vertex.getY()) : c != 0 ?
                getTForZ(vertex.getZ()) : 0;
        if (isVertexInLine(vertex, t)) {
            return t;
        } else {
            System.out.printf("Line:\nx=%f+%ft\ny=%f+%ft\nz=%f+%ft\n", x, a, y, b, z, c);
            System.out.printf("Point: (%f, %f, %f)\n", vertex.getX(), vertex.getY(), vertex.getZ());
            System.out.printf("t: %f\n", t);
            throw new IllegalArgumentException("Cannot set bounds with vertices that dont belong to the line!");
        }
    }

    public boolean isVertexInLine(Vertex v, double t) {
        return Utils.isAlmostEqual(v.getX(), x + a * t) && Utils.isAlmostEqual(v.getY(), y + b * t) &&
                Utils.isAlmostEqual(v.getZ(), z + c * t);
    }

    public boolean isVertexInLine(Vertex v) {
        try {
            getT(v);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private double getTForX(double x) {
        return (x - this.x) / a;
    }

    private double getTForY(double y) {
        return (y - this.y) / b;
    }

    private double getTForZ(double z) {
        return (z - this.z) / c;
    }

    public Vertex getPoint(double t) {
        if ((t >= tStart && t <= tEnd) || (t <= tStart && t >= tEnd)) {
            return new Vertex(x + a * t, y + b * t, z + c * t);
        } else {
            return null;
//            throw new IllegalArgumentException("Parameter \"t\" is out of bounds of this line");
        }
    }

    public Vertex getStartPoint() {
        return getPoint(tStart);
    }

    public void setStartPoint(Vertex start) {
        tStart = getT(start);
    }

    public double getXtForFlat(Vertex vertex) {
        double x = vertex.getX();
        double z = vertex.getZ();
        double xt = getTForX(x);
        double zt = getTForZ(z);
        if (!Utils.isAlmostEqual(xt, zt)) {
            System.out.printf("Line x=%f+%ft; y=%f+%ft; z=%f+%ft\n", this.x, a, y, b, this.z, c);
            System.out.printf("Point x=%f; z=%f\n", x, z);
            System.out.printf("t x=%f; z=%f\n", xt, zt);
            throw new IllegalArgumentException("Node cannot be set on the line!");
        }
        return xt;
    }

    public Vertex getPointOnLine(Vertex vertex) {
        double xt = getXtForFlat(vertex);
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
        MyMatrix matrix = new MyMatrix(this, other);
        matrix.solve();
        if (matrix.hasSolution()) {
            Vertex vt = getPoint(matrix.getT());
            Vertex vs = other.getPoint(matrix.getS());
            if (vt == null || vs == null) {
                return null;
            }
            if (vt.equals(vs)) {
                return vt;
            }
        }
        return null;
    }

    public boolean isWithinBounds(Vertex vertex) {
        if (vertex == null) {
            return false;
        }
        double t = getT(vertex);
        if (t >= tStart && t <= tEnd) {
            return true;
        } else {
            return t <= tStart && t >= tEnd;
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    @Override
    public String toString() {
        return String.format("x=%.4f + %.4f*t\ny=%.4f + %.4f*t\nz=%.4f + %.4f*t\n", x, a, y, b, z, c);
    }
}
