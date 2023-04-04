package me.murin.milos.listStuff;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.geometry.PointPair;
import me.murin.milos.render.Mesh;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.GCodeFileWriter;
import org.joml.Vector4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class PointPairList extends ListWithModel {

    private int nextVertexId = 0;

    private final ArrayList<Vertex> vertices = new ArrayList<>();
    private final ArrayList<PointPair> lines = new ArrayList<>();


    private OriginPosition originPosition = OriginPosition.CENTER;

    private double toAdd = 0.02f;

    private double sizeX = 2;
    private double sizeZ = 2;

    double scaleX = 0;
    double scaleZ = 0;
    double diffX = 0;
    double diffZ = 0;

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
        testExtremes(vertex);
        vertex.setId(nextVertexId++);
    }

    public void addLine(PointPair pointPair) {
        lines.add(pointPair);
    }

    public void changeY(double toAdd) {
        this.toAdd = toAdd;
        invalidateModel();
    }

    public void changeOriginPosition(OriginPosition originPosition) {
        if (originPosition == this.originPosition) {
            return;
        }
        this.originPosition = originPosition;

    }

    public void changeSize(double sizeX, double sizeZ) {
        if (sizeX <= 0 || sizeZ <= 0) {
            throw new IllegalArgumentException("Cannot change the size to a value less than or equal to zero!");
        }
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        invalidateModel();
    }


    @Override
    protected Mesh getMesh() {
        scaleX = sizeX / (extremes.getSize(Axis.X));
        scaleZ = sizeZ / (extremes.getSize(Axis.X));
        diffX = extremes.getMax(Axis.X);
        diffZ = extremes.getMax(Axis.Z);

        // vertices
        float[] vertexBuffer = new float[vertices.size() * 3];
        for (Vertex v : vertices) {
            int id = v.getId();
            vertexBuffer[3 * id] =
                    (float) ((v.getX() - extremes.getMin(Axis.X)) * scaleX + originPosition.multiplyX * diffX);
            vertexBuffer[3 * id + 1] = (float) (v.getY() + toAdd);
            vertexBuffer[3 * id + 2] =
                    (float) ((v.getZ() - extremes.getMin(Axis.Z)) * scaleZ + originPosition.multiplyZ * diffZ);
        }

        // indices
        List<Integer> indices = new ArrayList<>();
        for (PointPair l : lines) {
            // fill indices
            PointPair current = l;
            indices.add(current.getStart().getId());
            indices.add(current.getEnd().getId());

            while (current.hasNext()) {
                current = current.getNext();
                indices.add(current.getStart().getId());
                indices.add(current.getEnd().getId());
            }
        }

        return new Mesh(vertexBuffer, indices, GL_LINES);
    }

    @Override
    protected String getModelName() {
        return "ResultModel";
    }

    @Override
    protected Vector4f getDiffuseColor() {
        return new Vector4f(0.0f, 1.0f, 0.0f, 1.0f); // GREEN
    }


    public boolean isEmpty() {
        return lines.isEmpty();
    }

    public String generateGCode() {
        StringBuilder str = new StringBuilder();
        for (PointPair pointPair : lines){
            str.append(getGcodeFromPoint(pointPair.getStart())).append("\n");
            PointPair current = pointPair;
            str.append(getGcodeFromPoint(current.getEnd()))
                    .append(String.format(" E%f", current.getFilamentAmount())).append("\n");
            while (current.hasNext()) {
                current = current.getNext();
                str.append(getGcodeFromPoint(current.getEnd()))
                        .append(String.format(" E%f", current.getFilamentAmount())).append("\n");
            }
            // TODO: Add a move to another start (go up move to next start)
        }
        return str.toString();
    }

    public String getGcodeFromPoint(Vertex vertex) {
        return String.format("G1 X%.3f Y%.3f Z%.3f",
                (float) ((vertex.getZ() - extremes.getMin(Axis.Z)) * scaleZ + OriginPosition.BOTTOM_LEFT.multiplyZ * diffZ),
                (float) ((vertex.getX() - extremes.getMin(Axis.X)) * scaleX + OriginPosition.BOTTOM_LEFT.multiplyX * diffX),
                vertex.getY());
    }


    public enum OriginPosition {
        // TODO: synchronize with printer
        CENTER(-1, -1),
        TOP_RIGHT(1, 1), // MAX_MAX
        TOP_LEFT(-2, 0), // MAX_MIN
        BOTTOM_RIGHT(1, -1), // MIN_MAX
        BOTTOM_LEFT(0, 0); // default

        public final int multiplyX;
        public final int multiplyZ;

        OriginPosition(int multiplyX, int multiplyZ) {
            this.multiplyX = multiplyX;
            this.multiplyZ = multiplyZ;
        }
    }
}
