package me.murin.milos.dcel;

import me.murin.milos.geometry.HalfPlane;
import me.murin.milos.geometry.Line;

public class Edge {

    private int id;
    private int edgeId; // 1 or 2
    private final Vertex origin;
    private Edge twinEdge;
    private Face incidentFace;
    private Edge nextEdge;

    private Line line;
    private HalfPlane halfPlane;

    public Edge(int id, int edgeId, Vertex origin, Face incidentFace) {
        this.id = id;
        this.edgeId = edgeId;
        this.origin = origin;
        this.incidentFace = incidentFace;
    }

    /**
     * Update the id to an id gathered from the twin edge
     *
     * @param twinEdge the twin edge to this edge
     */
    public void updateIdWithTwin(Edge twinEdge) {
        this.id = twinEdge.getId();
        this.edgeId = 2;

        this.twinEdge = twinEdge;
        twinEdge.setTwinEdge(this);
    }

    public boolean isTwin(Vertex origin, Vertex end) {
        if (end.equals(this.origin)) {
            if (this.nextEdge != null) {
                return this.nextEdge.getOrigin().equals(origin);
            }
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public int getEdgeId() {
        return edgeId;
    }

    public Vertex getOrigin() {
        return origin;
    }

    public Edge getTwinEdge() {
        return twinEdge;
    }

    public void setTwinEdge(Edge twinEdge) {
        this.twinEdge = twinEdge;
    }

    public Face getIncidentFace() {
        return incidentFace;
    }

    public void setIncidentFace(Face incidentFace) {
        this.incidentFace = incidentFace;
    }

    public Edge getNextEdge() {
        return nextEdge;
    }

    public void setNextEdge(Edge nextEdge) {
        this.nextEdge = nextEdge;
        Vertex nextOrigin = nextEdge.getOrigin();

        // for analytical geometry
        double a = nextOrigin.getZ() - this.origin.getZ();
        double b = -(nextOrigin.getX() - this.origin.getX());
        double c = -(a * this.origin.getX() + b * this.origin.getZ());
        this.halfPlane = new HalfPlane(a, b, c);

        line = new Line(this.origin.getX(), this.origin.getY(), this.origin.getZ(),
                (nextOrigin.getX() - this.origin.getX()),
                (nextOrigin.getY() - this.origin.getY()), (nextOrigin.getZ() - this.origin.getZ()));
        line.setStartPoint(this.origin);
        line.setEndPoint(nextOrigin);
    }

    public double testVertex(Vertex v) {
        return this.halfPlane.testPoint(v);
    }

    public void setGreater(boolean greater) {
        this.halfPlane.setGreater(greater);
    }

    public boolean isInHalfPlane(Vertex vertex) {
        return this.halfPlane.isInHalfPlane(vertex);
    }

    public Vertex intersect(Line line) {
        return this.line.intersect(line);
    }

    public Line getLine() {
        return line;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Edge other) {
            return this.origin.equals(other.origin) && this.nextEdge.origin.equals(other.nextEdge.origin);
        }
        return false;
    }
}
