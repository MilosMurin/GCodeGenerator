package me.murin.milos.geometry;

import info.pavie.basicosmparser.model.Node;
import me.murin.milos.dcel.Vertex;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.Utils;

/**
 * A line that is the result of an intersection procces of a road and a Face of an object
 */
public class Line {

    private final double x0;
    private final double y0;
    private final double z0;
    private final double tx;
    private final double ty;
    private final double tz;

    private double tStart = Double.MAX_VALUE;
    private double tEnd = Double.MIN_VALUE;

    private Line next;

    public Line(double pointX, double pointY, double pointZ, double vectorX, double vectorY, double vectorZ) {
        this.x0 = pointX;
        this.y0 = pointY;
        this.z0 = pointZ;
        this.tx = vectorX;
        this.ty = vectorY;
        this.tz = vectorZ;
    }

    public Line(double a1, double b1, double c1, double d1, double a2, double b2, double c2, double d2) {
        if (b1 == 0) {
            // b1 cannot be 0 because we cannot 3d print on a object tha is vertical
            throw new IllegalArgumentException("First plane cannot be vertical!");
        }
        if (b2 != 0) {
            // b2 should always be 0 because road should be a vertical plane
            throw new IllegalArgumentException("Second plane has to be vertical!");
        }

        if (a2 == 0) {
            if (c2 == 0) {
                throw new IllegalArgumentException("WTF is even happening! How did the plane end up in a form d=0??");
            }
            this.z0 = -(d2 / c2);// c2 is not zero because it would not be a plane
            this.tz = 0;
            if (a1 == 0) {
                this.ty = 0;
                this.y0 = -((c1 * d2) / (b1 * c2) + d1 / b1);// c2 is not zero because it would not be a plane
                this.tx = 0;
                this.x0 = 0;
            } else {
                this.ty = 1;
                this.y0 = 0;
                this.tx = -((c2 * b1) / (a1 * c2)); // a1 is not 0 bc: this is in the else statement
                this.x0 = (c1 * d2 - c2 * d1) / (a1 * c2);// c2 is not zero because it would not be a plane
            }
        } else {
            // a2 and b1 != 0
            this.x0 = -(d2 / a2);
            this.tx = -(c2 / a2);
            this.ty = (a1 * c2 - a2 * c1) / (a2 * b1);
            this.y0 = (a1 * d2 - a2 * d1) / (a2 * b1);
            this.z0 = 0;
            this.tz = 1;
        }
    }

    public double getT(Vertex vertex) {

        double t = tx != 0 ? getTForX(vertex.getX()) : ty != 0 ? getTForY(vertex.getY()) : tz != 0 ?
                getTForZ(vertex.getZ()) : 0;
        if (isVertexInLine(vertex, t)) {
            return t;
        } else {
            System.out.printf("Line:\nx=%f+%ft\ny=%f+%ft\nz=%f+%ft\n", x0, tx, y0, ty, z0, tz);
            System.out.printf("Point: (%f, %f, %f)\n", vertex.getX(), vertex.getY(), vertex.getZ());
            System.out.printf("t: %f\n", t);
            throw new IllegalArgumentException("Cannot set bounds with vertices that dont belong to the line!");
        }
    }

    public boolean isVertexInLine(Vertex v, double t) {
        return Utils.isAlmostEqual(v.getX(), x0 + tx * t) && Utils.isAlmostEqual(v.getY(), y0 + ty * t) &&
                Utils.isAlmostEqual(v.getZ(), z0 + tz * t);
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
        return (x - x0) / tx;
    }

    private double getTForY(double y) {
        return (y - y0) / ty;
    }

    private double getTForZ(double z) {
        return (z - z0) / tz;
    }

    public Vertex getPoint(double t) {
        if ((t >= tStart && t <= tEnd) || (t <= tStart && t >= tEnd)) {
            return new Vertex(x0 + tx * t, y0 + ty * t, z0 + tz * t);
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

    public Vertex setStartPoint(Node start) {
        return setPoint(start, true);
    }

    public void setEndPoint(Node end) {
        setPoint(end, false);
    }

    private Vertex setPoint(Node point, boolean start) {
        double xt = getXtForNode(point);
        // calculate y
        if (start) {
            tStart = xt;
        } else {
            tEnd = xt;
        }
        return getPoint(xt);
    }

    public double getXtForNode(Node node) {
        double x = Utils.getCoordFromNode(node, Axis.X);
        double z = Utils.getCoordFromNode(node, Axis.Z);
        double xt = getTForX(x);
        double zt = getTForZ(z);
        if (!Utils.isAlmostEqual(xt, zt)) {
            System.out.printf("Line x=%f+%ft; y=%f+%ft; z=%f+%ft\n", x0, tx, y0, ty, z0, tz);
            System.out.printf("Point x=%f; z=%f\n", x, z);
            System.out.printf("t x=%f; z=%f\n", xt, zt);
            throw new IllegalArgumentException("Node cannot be set on the line!");
        }
        return xt;
    }

    public Vertex getPointOnLine(Node node) {
        double xt = getXtForNode(node);
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
        double t1bottom = (other.tx * this.ty - this.tx * other.ty);
        Double t1 = null;
        if (t1bottom == 0) {
            if (this.ty == 0 && other.ty == 0) {
                t1bottom = (other.tx * this.tz - this.tx * other.tz);
                t1 = (other.tz * (this.x0 - other.x0) + other.tx * (other.z0 - this.z0)) / t1bottom;
            } else if (this.tx == 0 && other.tx == 0) {
                t1bottom = (other.tz * this.ty - this.tz * other.ty);
                t1 = (other.tz * (this.z0 - other.z0) + other.tz * (other.z0 - this.z0)) / t1bottom;
            }
            // TODO: Maybe fix this if it throws any more errors
//            System.out.println("T1 bottom is null");
        } else {
            t1 = (other.ty * (this.x0 - other.x0) + other.tx * (other.y0 - this.y0)) / t1bottom;
        }
        if (t1 == null) {
            return null;
        }
        return getPoint(t1);
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

    public double getX0() {
        return x0;
    }

    public double getY0() {
        return y0;
    }

    public double getZ0() {
        return z0;
    }

    public double getTx() {
        return tx;
    }

    public double getTy() {
        return ty;
    }

    public double getTz() {
        return tz;
    }
}
