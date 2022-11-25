package me.murin.milos.scene;

import me.murin.milos.render.Mesh;

import java.util.HashMap;
import java.util.Map;

public class Scene {

    private Map<String, Mesh> meshMap;
    private Projection projection;

    public Scene(int width, int height) {
        meshMap = new HashMap<>();
        projection = new Projection(width, height);
    }

    public Projection getProjection() {
        return projection;
    }

    public void resize(int width, int height) {
        projection.updateMatrix(width, height);
    }

    public void addMesh(String meshId, Mesh mesh) {
        meshMap.put(meshId, mesh);
    }

    public Map<String, Mesh> getMeshMap() {
        return meshMap;
    }

    public void cleanup() {
        meshMap.values().forEach(Mesh::cleanup);
    }
}
