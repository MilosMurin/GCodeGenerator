package me.murin.milos.scene;

import me.murin.milos.render.Mesh;

import java.util.HashMap;
import java.util.Map;

public class Scene {

    private Map<String, Mesh> meshMap;

    public Scene() {
        meshMap = new HashMap<>();
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
