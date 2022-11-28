package me.murin.milos.render;

import me.murin.milos.scene.Entity;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String id;
    private List<Mesh> meshList;

    private List<Entity> entityList;

    public Model(String id, List<Mesh> meshList) {
        this.id = id;
        this.meshList = meshList;
        this.entityList = new ArrayList<>();
    }

    public void cleanup() {
        meshList.forEach(Mesh::cleanup);
    }

    public String getId() {
        return id;
    }

    public List<Mesh> getMeshList() {
        return meshList;
    }

    public void addEntity(Entity entity) {
        entityList.add(entity);
    }

    public List<Entity> getEntityList() {
        return entityList;
    }
}
