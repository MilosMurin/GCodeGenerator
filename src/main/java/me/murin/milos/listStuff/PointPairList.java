package me.murin.milos.listStuff;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.gcode.GCodeGenerator;
import me.murin.milos.geometry.PointPair;
import me.murin.milos.render.Mesh;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.Utils;
import org.joml.Vector4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class PointPairList extends ListWithModel {

    private int nextVertexId = 0;

    private final ArrayList<Vertex> vertices = new ArrayList<>();
    private final ArrayList<PointPair> lines = new ArrayList<>();

    private PointPair previous = null;

    private OriginPosition originPosition = OriginPosition.CENTER;

    private double displayToAdd = 0.02f;
    private double layerHeight = 0.2f;

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
        if (pointPair.getFilamentAmount() == 0) {
            return;
        }
        if (previous != null) {
            if (!previous.endsInSame(pointPair)) {
                previous.setNext(pointPair);
                previous = pointPair;
            }
        } else {
            lines.add(pointPair);
            previous = pointPair;
        }
    }

    public void clearPrevious() {
        this.previous = null;
    }

    public void changeY(double toAdd) {
        this.displayToAdd = toAdd;
        invalidateModel();
    }

    public void setLayerHeight(double layerHeight) {
        this.layerHeight = layerHeight;
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
            vertexBuffer[3 * id + 1] = (float) (v.getY() + displayToAdd);
            vertexBuffer[3 * id + 2] =
                    (float) ((v.getZ() - extremes.getMin(Axis.Z)) * scaleZ + originPosition.multiplyZ * diffZ);
        }

        // indices

        return new Mesh(vertexBuffer, Utils.fillIndicies(lines), GL_LINES);
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

    public String generateGCode(Extremes extremes) {
        GCodeGenerator generator = new GCodeGenerator();

        boolean first = true;
        String last = "";

        // Color change
        // M600
        //  G1 E0.4 F1500 ; prime after color change
        generator.addColorChange();
        generator.setFeedRate(3000);
        generator.setAbsolutePositioning();
        generator.newLine();

        for (PointPair pointPair : lines) {
            if (!first) {
                generator.addComment("Move to another starting road");
                generator.addGcode(last);
                generator.addGcode(getGcodeFromPointWithMaxY(pointPair.getStart(), extremes));
                generator.addComment("END Move to another starting road");
            } else {
                generator.addGcode(getGcodeFromPointWithMaxY(pointPair.getStart(), extremes));
                first = false;
            }
            generator.addGcode(getGcodeFromPoint(pointPair.getStart(), extremes));
            PointPair current = pointPair;

            generator.addGcodeWithE(getGcodeFromPoint(current.getEnd(), extremes), current.getFilamentAmount());
            last = getGcodeFromPointWithMaxY(current.getEnd(), extremes);

            while (current.hasNext()) {
                current = current.getNext();
                generator.addGcodeWithE(getGcodeFromPoint(current.getEnd(), extremes), current.getFilamentAmount());
                last = getGcodeFromPointWithMaxY(current.getEnd(), extremes);
            }
        }
        return generator.toString();
    }

    public String getGcodeFromPoint(Vertex vertex, Extremes extremes) {
        return String.format("G1 X%.3f Y%.3f Z%.3f",
                (float) (extremes == null ? 0 : extremes.getMin(Axis.Z)) +
                        ((vertex.getZ() - this.extremes.getMin(Axis.Z)) * scaleZ + OriginPosition.BOTTOM_LEFT.multiplyZ * diffZ),
                (float) (extremes == null ? 0 : extremes.getMin(Axis.X)) +
                        ((vertex.getX() - this.extremes.getMin(Axis.X)) * scaleX + OriginPosition.BOTTOM_LEFT.multiplyX * diffX),
                vertex.getY() + layerHeight);
    }

    public String getGcodeFromPointWithMaxY(Vertex vertex, Extremes extremes) {
        return String.format("G1 X%.3f Y%.3f Z%.3f",
                (float) (extremes == null ? 0 : extremes.getMin(Axis.Z)) +
                        ((vertex.getZ() - this.extremes.getMin(Axis.Z)) * scaleZ + OriginPosition.BOTTOM_LEFT.multiplyZ * diffZ),
                (float) (extremes == null ? 0 : extremes.getMin(Axis.X)) +
                        ((vertex.getX() - this.extremes.getMin(Axis.X)) * scaleX + OriginPosition.BOTTOM_LEFT.multiplyX * diffX),
                this.extremes.getMax(Axis.Y) + 1);
    }


    public enum OriginPosition {
        // NOT FINISHED
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
