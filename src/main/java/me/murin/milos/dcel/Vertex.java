package me.murin.milos.dcel;

public class Vertex {

    private final int id;
    private final float x;
    private final float y;
    private final float z;
    private Edge incident;

    public Vertex(int id, float x, float y, float z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public Edge getIncident() {
        return incident;
    }

    public void setIncident(Edge incident) {
        this.incident = incident;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vertex vertex) {
            return vertex.getId() == this.id;
        }
        return false;
    }
}
