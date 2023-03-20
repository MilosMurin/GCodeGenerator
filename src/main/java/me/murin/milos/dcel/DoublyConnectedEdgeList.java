package me.murin.milos.dcel;

import me.murin.milos.render.Mesh;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class DoublyConnectedEdgeList {


    private final List<Edge> edges;
    private final List<Vertex> vertices;
    private final List<Face> faces;

    private final float[] extremeX = new float[] {Float.MAX_VALUE, Float.MIN_VALUE}; // 0 - min, 1 - max
    private final float[] extremeZ = new float[] {Float.MAX_VALUE, Float.MIN_VALUE};

    public DoublyConnectedEdgeList() {
        this.edges = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.faces = new ArrayList<>();
    }

    public void addVertex(Vertex vertex) {
        this.vertices.add(vertex);
        if (vertex.getX() < extremeX[0]) {
            extremeX[0] = vertex.getX();
        } else if (vertex.getX() > extremeX[1]) {
            extremeX[1] = vertex.getX();
        }
        if (vertex.getZ() < extremeZ[0]) {
            extremeZ[0] = vertex.getZ();
        } else if (vertex.getZ() > extremeZ[1]) {
            extremeZ[1] = vertex.getZ();
        }
    }

    public float getExtremeX(boolean max) {
        if (max) {
            return extremeX[1];
        } else {
            return extremeX[0];
        }
    }

    public float getExtremeZ(boolean max) {
        if (max) {
            return extremeZ[1];
        } else {
            return extremeZ[0];
        }
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

    public Face getFace(int pos) {
        return this.faces.get(pos);
    }

    public Face getFaceForPoint(float x, float z) {
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
                vertexBuffer[3 * i + j] = vertices.get(i).getCoord(j);
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

}
