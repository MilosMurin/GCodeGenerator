package me.murin.milos.dcel;

public class Vertex {

    private final int id;
    private final int x;
    private final int y;
    private final int z;
    private Edge incident;

    public Vertex(int id, int x, int y, int z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Edge getIncident() {
        return incident;
    }

    public void setIncident(Edge incident) {
        this.incident = incident;
    }
}
