package me.murin.milos.render;

import me.murin.milos.dcel.DoublyConnectedEdgeList;
import me.murin.milos.scene.Entity;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private final String id;
    private final List<Material> materialList;

    private final List<Entity> entityList;

    private Model dcelModel;

    private boolean visible = true;

    private List<DoublyConnectedEdgeList> dcels;

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

    public void createDcelModel(List<DoublyConnectedEdgeList> dcels) {
        this.dcels = dcels;
        List<Material> materials = new ArrayList<>();
        Material material = new Material();

        for (DoublyConnectedEdgeList dcl : dcels) {
            material.getMeshList().add(dcl.getMesh());
        }

        materials.add(material);
        dcelModel = new Model(id + "Dcel", materials);
    }

    public Model getDcelModel() {
        return dcelModel;
    }

    public DoublyConnectedEdgeList getDcel() {
        return dcels.get(0);
    }




}
