package me.murin.milos.dcel;

public class Face {

    private final int id;
    private Edge outerComponent;
    private Edge innerComponent;

    public Face(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Edge getOuterComponent() {
        return outerComponent;
    }

    public void setOuterComponent(Edge outerComponent) {
        this.outerComponent = outerComponent;
    }

    public Edge getInnerComponent() {
        return innerComponent;
    }

    public void setInnerComponent(Edge innerComponent) {
        this.innerComponent = innerComponent;
    }
}
