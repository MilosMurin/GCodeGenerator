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

    public void finishUpFace() {
        // for analytical geometry
        Edge second = firstEdge.getNextEdge();
        Edge third = second.getNextEdge();

        firstEdge.setGreater(firstEdge.testVertex(third.getOrigin()) > 0);
        second.setGreater(second.testVertex(firstEdge.getOrigin()) > 0);
        third.setGreater(third.testVertex(second.getOrigin()) > 0);
    }

    public boolean isPointInFace(float x, float z) {
        boolean isIn = true;
        int counter = 0;
        Edge current = firstEdge;
        while (isIn && counter < 3) {
            isIn = current.isInHalfPlane(x, z);
            current = current.getNextEdge();
            counter++;
        }
        return isIn;
    }
}
