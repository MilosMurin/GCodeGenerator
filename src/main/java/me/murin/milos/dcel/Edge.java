package me.murin.milos.dcel;

public class Edge {

    private final int id;
    private final int edgeId; // 1 or 2
    private final Vertex origin;
    private Edge twinEdge;
    private Face incidentFace;
    private Edge nextEdge;
    private Edge prevEdge;

    public Edge(int id, int edgeId, Vertex origin, Vertex end) {
        this.id = id;
        this.edgeId = edgeId;
        this.origin = origin;
        // TODO: Finalize the end vertex
        this.twinEdge = new Edge(id, edgeId == 1 ? 2 : 1, end, this);
    }

    public Edge(int id, int edgeId, Vertex origin, Edge twinEdge) {
        this.id = id;
        this.edgeId = edgeId;
        this.origin = origin;
        this.twinEdge = twinEdge;
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
    }

    public Edge getPrevEdge() {
        return prevEdge;
    }

    public void setPrevEdge(Edge prevEdge) {
        this.prevEdge = prevEdge;
    }
}
