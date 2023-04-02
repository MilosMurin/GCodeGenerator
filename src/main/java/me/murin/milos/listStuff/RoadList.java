package me.murin.milos.listStuff;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.geometry.Road;
import me.murin.milos.render.Mesh;
import me.murin.milos.utils.Axis;
import me.murin.milos.listStuff.ListWithModel;
import me.murin.milos.utils.Utils;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class RoadList extends ListWithModel {

    private final ArrayList<Road> starts = new ArrayList<>();
    private int extVertexId = 0;

    private final ArrayList<Vertex> vertices = new ArrayList<>();

    public void addVertex(Vertex vertex) {
        vertices.add(vertex);
        vertex.setId(extVertexId++);
        testExtremes(vertex);
        invalidateModel();
    }

    public void addStartRoad(Road start) {
        starts.add(start);
        invalidateModel();
    }

    public void adjustToModel(float sizeX, float sizeZ) {
        double scaleX = sizeX / (getMax(Axis.X) - getMin(Axis.X));
        double scaleZ = sizeZ / (getMax(Axis.Z) - getMin(Axis.Z));
        for (Vertex v : vertices) {
            Utils.adjustCoordOnAxis(v, Axis.X, getMin(Axis.X), scaleX);
            Utils.adjustCoordOnAxis(v, Axis.Z, getMin(Axis.Z), scaleZ);
        }
    }

    public void adjustToScale() {
        // TODO: Chnage the 2 to the size of the base model
        double scaleX = 2 / (getMax(Axis.X) - getMin(Axis.X));
        double scaleZ = 2 / (getMax(Axis.Z) - getMin(Axis.Z));
        for (Vertex v : vertices) {
            Utils.adjustCoordOnAxis(v, Axis.X, getMin(Axis.X), scaleX);
            Utils.adjustCoordOnAxis(v, Axis.Z, getMin(Axis.Z), scaleZ);
        }
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
        List<Integer> indices = new ArrayList<>();
        for (Road r : starts) {
            Road current = r;
            indices.add(current.getFirst().getId());
            indices.add(current.getLast().getId());

            while (current.hasNext()) {
                current = current.getNext();
                indices.add(current.getFirst().getId());
                indices.add(current.getLast().getId());
            }
        }

        return new Mesh(vertexBuffer, indices, GL_LINES);
    }

    public ArrayList<Road> getStarts() {
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
