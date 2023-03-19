package me.murin.milos.geometry;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.render.Mesh;
import me.murin.milos.utils.ListWithModel;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class LineList extends ListWithModel {

    private List<Vertex> vertices = new ArrayList<>();
    private List<Line> lines = new ArrayList<>();

    private OriginPosition originPosition;

    // 00 location - midle
    // max size - width, height

    public void changeSize(float width, float length) {
        if (width <= 0 || length <= 0) {
            throw new IllegalArgumentException("Cannot change the size to a value less than or equal to zero!");
        }
        // TODO: Recalculate sizes
        invalidateModel();
    }


    public void changeY(float toAdd) {
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
//        for (String s : nodeIds.keySet()) {
//
//
//        }

        // indices
        List<Integer> indices = new ArrayList<>();
        for (Line l : lines) {
            // fill indices
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
