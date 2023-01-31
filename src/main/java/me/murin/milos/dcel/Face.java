package me.murin.milos.dcel;

public class Face {

    private final int id;
    private Edge firstEdge;

    public Face(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Edge getFirstEdge() {
        return firstEdge;
    }

    public void setFirstEdge(Edge firstEdge) {
        this.firstEdge = firstEdge;
    }
}
