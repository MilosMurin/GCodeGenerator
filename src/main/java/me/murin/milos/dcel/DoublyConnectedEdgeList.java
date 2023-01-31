package me.murin.milos.dcel;

import java.util.ArrayList;
import java.util.List;

public class DoublyConnectedEdgeList {


    private final List<Edge> edges;
    private final List<Vertex> vertices;
    private final List<Face> faces;

    public DoublyConnectedEdgeList() {
        this.edges = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.faces = new ArrayList<>();
    }

    public void addVertex(Vertex vertex) {
        this.vertices.add(vertex);
    }

    public Vertex getVertex(int pos) {
        return this.vertices.get(pos);
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    public Edge getEdge(int pos) {
        return this.edges.get(pos);
    }

    /**
     * Gets the twin edge of an edge that starts at the end vertex and ends at the origin vertex
     * Going the oposite way
     *
     * @param origin the origin of the main edge (the end of the twin edge to search)
     * @param end the end of the main edge (the origin of the twin edge to search)
     * @return twin edge if such edge exists, null if nothing found
     */
    public Edge getTwin(Vertex origin, Vertex end) {
        for (Edge e : edges) {
            if (e.isTwin(origin, end)) {
                return e;
            }
        }
        return null;
    }

    public void addFace(Face face) {
        this.faces.add(face);
    }

    public Face getFace(int pos) {
        return this.faces.get(pos);
    }

}
