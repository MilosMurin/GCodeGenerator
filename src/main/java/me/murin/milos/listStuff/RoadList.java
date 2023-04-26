package me.murin.milos.listStuff;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.geometry.PointPair;
import me.murin.milos.render.Mesh;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.Utils;
import org.joml.Vector4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class RoadList extends ListWithModel {

    private final ArrayList<PointPair> starts = new ArrayList<>();
    private int extVertexId = 0;

    private final ArrayList<Vertex> vertices = new ArrayList<>();

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
        vertex.setId(extVertexId++);
        testExtremes(vertex);
        invalidateModel();
    }

    public PointPair addRoad(int sId, int eId, PointPair prev) {
        PointPair pp = new PointPair(vertices.get(sId), vertices.get(eId));
        if (prev != null) {
            prev.setNext(pp);
        } else {
            starts.add(pp);
        }
        return pp;
    }

    public void addStartRoad(PointPair start) {
        starts.add(start);
        invalidateModel();
    }

    public void adjustToModel(float sizeX, float sizeZ, double y) {
        double scaleX = sizeX / (getMax(Axis.X) - getMin(Axis.X));
        double scaleZ = sizeZ / (getMax(Axis.Z) - getMin(Axis.Z));
        double halfX = sizeX / 2;
        double halfZ = sizeZ / 2;
        double minX = getMin(Axis.X);
        double minZ = getMin(Axis.Z);
        for (Vertex v : vertices) {
            Utils.adjustCoordOnAxis(v, Axis.X, minX, scaleX, halfX, halfZ);
            v.setY(y);
            Utils.adjustCoordOnAxis(v, Axis.Z, minZ, scaleZ, halfX, halfZ);
            testExtremes(v);
        }
        invalidateModel();
    }


    @Override
    protected Mesh getMesh() {
        // vertices
        float[] vertexBuffer = new float[vertices.size() * 3];
        for (Vertex v : vertices) {
            int id = v.getId();
            vertexBuffer[3 * id] = (float) v.getX();
            vertexBuffer[3 * id + 1] = (float) v.getY();
            vertexBuffer[3 * id + 2] = (float) v.getZ();
        }

        // indices

        return new Mesh(vertexBuffer, Utils.fillIndicies(starts), GL_LINES);
    }

    public ArrayList<PointPair> getStarts() {
        return starts;
    }

    @Override
    protected Vector4f getDiffuseColor() {
        return new Vector4f(1.0f, 0.0f, 0.0f, 1.0f); // RED
    }

    @Override
    protected String getModelName() {
        return "RoadModel";
    }
}
