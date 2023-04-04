package me.murin.milos.dcel;

import me.murin.milos.listStuff.Extremes;
import me.murin.milos.render.Mesh;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class DoublyConnectedEdgeList {


    private final List<Edge> edges;
    private final List<Vertex> vertices;
    private final List<Face> faces;

    private final Extremes extremes = new Extremes();

    public DoublyConnectedEdgeList() {
        this.edges = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.faces = new ArrayList<>();
    }

    public void addVertex(Vertex vertex) {
        this.vertices.add(vertex);
        extremes.testExtremes(vertex);
    }

    public Vertex getVertex(int pos) {
        return this.vertices.get(pos);
    }

    public void addEdge(Edge edge) {
        this.edges.add(edge);
    }

    /**
     * Gets the twin edge of an edge that starts at the end vertex and ends at the origin vertex
     * Going the oposite way
     *
     * @param origin the origin of the main edge (the end of the twin edge to search)
     * @param end    the end of the main edge (the origin of the twin edge to search)
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
        face.finishUpFace();
        this.faces.add(face);
    }

    public Face getFaceForPoint(double x, double z) {
        for (Face f : faces) {
            if (f.isPointInFace(x, z)) {
                return f;
            }
        }
        return null;
    }

    public Mesh getMesh() {
        // vertices
        float[] vertexBuffer = new float[vertices.size() * 3];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < 3; j++) {
                vertexBuffer[3 * i + j] = (float) vertices.get(i).getCoord(j);
            }
        }
        // indices
        List<Integer> indices = new ArrayList<>();
        for (Face f : faces) {
            Edge current = f.getFirstEdge();
            indices.add(current.getOrigin().getId());
            for (int i = 0; i < 2; i++) {
                current = current.getNextEdge();
                indices.add(current.getOrigin().getId());
                indices.add(current.getOrigin().getId());
            }
            indices.add(f.getFirstEdge().getOrigin().getId());
        }
        return new Mesh(vertexBuffer, indices, GL_LINES);
    }

    public Extremes getExtremes() {
        return extremes;
    }
}
