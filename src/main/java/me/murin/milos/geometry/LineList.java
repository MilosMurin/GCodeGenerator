package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.render.Mesh;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.ListWithModel;
import me.murin.milos.utils.Utils;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class LineList extends ListWithModel {

    private Set<Vertex> vertices = new HashSet<>();
    private HashMap<Vertex, Integer> vertexIds = new HashMap<>();
    private List<Line> lines = new ArrayList<>();

    private int vertexId = 0;

    private OriginPosition originPosition;

    // 00 location - midle
    // max size - width, height

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
        vertexIds.put(vertex, vertexId++);
    }

    public void addLine(Line startLine) {
        lines.add(startLine);
    }

    public void changeSize(double width, double length) {
        if (width <= 0 || length <= 0) {
            throw new IllegalArgumentException("Cannot change the size to a value less than or equal to zero!");
        }
        // TODO: Recalculate sizes
        invalidateModel();
    }


    public void changeY(double toAdd) {
        if (toAdd == 0) {
            return;
        }
        invalidateModel();
    }

    public void changeOriginPosition(OriginPosition originPosition) {
        if (originPosition == this.originPosition) {
            return;
        }
        this.originPosition = originPosition;
        for (Line l : lines) {
            // TODO: Do all the calculations (maybe include this in the calculation in the mesh so here i will call
            //  only invalidate model)
        }
    }

    @Override
    protected Mesh getMesh() {
        // vertices
        float[] vertexBuffer = new float[vertices.size() * 3];
        for (Vertex v : vertexIds.keySet()) {
            int id = vertexIds.get(v);
            vertexBuffer[3 * id] = (float) v.getX(); // -1 is for origin position
            vertexBuffer[3 * id + 1] = (float) v.getY();
            vertexBuffer[3 * id + 2] = (float) v.getZ();

        }

        // indices
        List<Integer> indices = new ArrayList<>();
        for (Line l : lines) {
            // fill indices
            Line current = l;
            indices.add(vertexIds.get(l.getStartPoint()));
            indices.add(vertexIds.get(l.getEndPoint()));

            while (current.hasNext()) {
                current = current.getNext();
                indices.add(vertexIds.get(l.getStartPoint()));
                indices.add(vertexIds.get(l.getEndPoint()));
            }
        }

        return new Mesh(vertexBuffer, indices, GL_LINES);
    }

    @Override
    protected Vector4f getDiffuseColor() {
        return  new Vector4f(0.0f, 1.0f, 0.0f, 1.0f); // GREEN
    }

    @Override
    protected String getModelName() {
        return "LineModel";
    }


    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public enum OriginPosition {
        CENTER,
        TOP_RIGHT,
        TOP_LEFT,
        BOTTOM_RIGHT,
        BOTTOM_LEFT
    }
}
