package me.murin.milos.dcel;

public class Edge {

    private int id;
    private int edgeId; // 1 or 2
    private final Vertex origin;
    private Edge twinEdge;
    private Face incidentFace;
    private Edge nextEdge;
    private Edge prevEdge;

    public Edge(int id, int edgeId, Vertex origin, Face incidentFace) {
        this.id = id;
        this.edgeId = edgeId;
        this.origin = origin;
        this.incidentFace = incidentFace;
    }

    /**
     * Update the id to an id gathered from the twin edge
     * @param twinEdge the twin edge to this edge
     */
    public void updateIdWithTwin(Edge twinEdge) {
        this.id = twinEdge.getId();
        this.edgeId = 2;

        this.twinEdge = twinEdge;
        twinEdge.setTwinEdge(this);
    }

    public boolean isTwin(Vertex origin, Vertex end) {
        if (end == this.origin) {
            if (this.nextEdge != null) {
                return this.nextEdge.getOrigin() == origin;
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
    }

    public Edge getPrevEdge() {
        return prevEdge;
    }

    public void setPrevEdge(Edge prevEdge) {
        this.prevEdge = prevEdge;
    }
}
