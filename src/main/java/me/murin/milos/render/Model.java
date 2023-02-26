package me.murin.milos.render;

import me.murin.milos.scene.Entity;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String id;
    private List<Material> materialList;

    private List<Entity> entityList;

    private boolean visible = true;

    public Model(String id, List<Material> materialList) {
        this.id = id;
        this.materialList = materialList;
        this.entityList = new ArrayList<>();
    }

    public void cleanup() {
        materialList.forEach(Material::cleanup);
    }

    public String getId() {
        return id;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }

    public void addEntity(Entity entity) {
        entityList.add(entity);
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
