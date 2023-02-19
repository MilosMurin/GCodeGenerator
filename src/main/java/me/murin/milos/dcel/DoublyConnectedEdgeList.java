package me.murin.milos.dcel;

import me.murin.milos.render.Material;
import me.murin.milos.render.Mesh;
import me.murin.milos.render.Model;

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
        this.faces.add(face);
    }

    public Face getFace(int pos) {
        return this.faces.get(pos);
    }


    public Model createModel() {
        List<Material> materials = new ArrayList<>();
        Material material = new Material();

        // vertices
        float[] vertexBuffer = new float[vertices.size() * 3];
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < 3; j++) {
                vertexBuffer[3 * i + j] = vertices.get(i).getCoord(j);
            }
        }
        // texture coords
        int numElements = (vertices.size() / 3) * 2;
        float[] texCoords = new float[numElements];
        // indices
        List<Integer> indices = new ArrayList<>();
        for (Face f : faces) {
            Edge current = f.getFirstEdge();
            indices.add(current.getOrigin().getId());
            for (int i = 0; i < 2; i++) {
                current = current.getNextEdge();
                indices.add(current.getOrigin().getId());
            }
        }
        material.getMeshList().add(new Mesh(vertexBuffer, texCoords, indices.stream().mapToInt(Integer::intValue).toArray()));

        materials.add(material);
        return new Model("Dcel model", materials);
    }

}
