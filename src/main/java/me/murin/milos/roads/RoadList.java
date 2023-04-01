package me.murin.milos.roads;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.geometry.Road;
import me.murin.milos.render.Mesh;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.ListWithModel;
import me.murin.milos.utils.Utils;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class RoadList extends ListWithModel {

    private final ArrayList<Road> starts = new ArrayList<>();
    private int nextNodeId = 0;

    private final HashSet<Vertex> vertices = new HashSet<>();

    public void addVertex(Vertex vertex) {
        if (vertices.add(vertex)) {
            vertex.setId(nextNodeId++);
            testExtremes(Axis.X, vertex.getX());
            testExtremes(Axis.Z, vertex.getZ());
            invalidateModel();
        }
    }

    public void addStartRoad(Road start) {
        starts.add(start);
        invalidateModel();
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
