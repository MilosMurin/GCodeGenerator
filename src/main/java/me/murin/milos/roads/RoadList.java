package me.murin.milos.roads;

import info.pavie.basicosmparser.model.Node;
import me.murin.milos.geometry.Road;
import me.murin.milos.render.Mesh;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.ListWithModel;
import me.murin.milos.utils.Utils;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class RoadList extends ListWithModel {

    private final HashMap<String, Node> nodes = new HashMap<>();
    private final ArrayList<Road> starts = new ArrayList<>();
    private final HashMap<String, Integer> nodeIds = new HashMap<>();
    private int nextNodeId = 0;

    private float scaleX = 0.5f;
    private float scaleZ = 0.5f;

    public void addNode(Node n) {
        nodes.putIfAbsent(n.getId(), n);
        testExtremes(Axis.X, Utils.getCoordFromNode(n, Axis.X));
        testExtremes(Axis.Z, Utils.getCoordFromNode(n, Axis.Z));

        scaleX = 2 / (getMax(Axis.X) - getMin(Axis.X));
        scaleZ = 2 / (getMax(Axis.Z) - getMin(Axis.Z));
        if (!nodeIds.containsKey(n.getId())) {
            nodeIds.put(n.getId(), nextNodeId++);
        }
        invalidateModel();
    }

    public void addStartRoad(Road start) {
        starts.add(start);
        invalidateModel();
    }


    @Override
    protected Mesh getMesh() {
        // vertices
        float[] vertexBuffer = new float[nodes.size() * 3];
        for (String s : nodeIds.keySet()) {
            int id = nodeIds.get(s);
            Node node = nodes.get(s);
            vertexBuffer[3 * id] = (Utils.getCoordFromNode(node, Axis.X) - getMin(Axis.X)) * scaleX - 1;
            vertexBuffer[3 * id + 1] = 1f;
            vertexBuffer[3 * id + 2] = (Utils.getCoordFromNode(node, Axis.Z) - getMin(Axis.Z)) * scaleZ - 1;
        }

        // indices
        List<Integer> indices = new ArrayList<>();
        for (Road r : starts) {
            Road current = r;
            indices.add(nodeIds.get(r.getFirst().getId()));
            indices.add(nodeIds.get(r.getLast().getId()));

            while (current.hasNext()) {
                current = current.getNext();
                indices.add(nodeIds.get(current.getFirst().getId()));
                indices.add(nodeIds.get(current.getLast().getId()));
            }
        }

        return new Mesh(vertexBuffer, indices, GL_LINES);
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
