package me.murin.milos.dcel;

public class Edge {

    private int id;
    private int edgeId; // 1 or 2
    private final Vertex origin;
    private Edge twinEdge;
    private Face incidentFace;
    private Edge nextEdge;
    private Edge prevEdge;

    private float a;
    private float b;
    private float c;

    private boolean greater;
    private boolean greaterSet = false;

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

        // for analytical geometry
        this.a = nextEdge.getOrigin().getZ() - this.origin.getZ();
        this.b = -(nextEdge.getOrigin().getX() - this.origin.getX());
        this.c = -(this.a * this.origin.getX() + this.b * this.origin.getZ());
    }

    public Edge getPrevEdge() {
        return prevEdge;
    }

    public void setPrevEdge(Edge prevEdge) {
        this.prevEdge = prevEdge;
    }

    public float testVertex(Vertex v) {
        return this.testPoint(v.getX(), v.getZ());
    }

    public float testPoint(float x, float z) {
        return a * x + b * z + c;
    }

    public void setGreater(boolean greater) {
        this.greater = greater;
        this.greaterSet = true;
    }

    public boolean isInHalfPlane(float x, float z) {
        // if i will not want to take the edges change this to be a sharp inequality
        if (greaterSet) {
            if (greater) {
                return testPoint(x, z) >= 0;
            } else {
                return testPoint(x, z) <= 0;
            }
        }
        return false;
    }
}
