package me.murin.milos.utils;

import me.murin.milos.render.Material;
import me.murin.milos.render.Mesh;
import me.murin.milos.render.Model;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public abstract class ListWithModel {


    private final float[] minimums = new float[3]; // 0 - x, 1 - y, 2 - z
    private final float[] maximums = new float[3]; // 0 - x, 1 - y, 2 - z

    protected Model model;
    private boolean modelValid = false;

    protected abstract Mesh getMesh();

    protected abstract String getModelName();
    protected abstract Vector4f getDiffuseColor();

    public void createModel() {
        List<Material> materials = new ArrayList<>();
        Material material = new Material();

        // call of abstract method
        material.getMeshList().add(this.getMesh());

        material.setDiffuseColor(this.getDiffuseColor());

        materials.add(material);
        setModel(new Model(this.getModelName(), materials));
    }

    protected void setModel(Model model) {
        this.model = model;
        this.validateModel();
    }

    protected void testExtremes(Axis axis, float amount) {
        if (amount < getMin(axis)) {
            minimums[axis.getId()] = amount;
        } else if (amount > getMax(axis)) {
            maximums[axis.getId()] = amount;
        }
    }

    protected float getMax(Axis axis) {
        return maximums[axis.getId()];
    }

    protected float getMin(Axis axis) {
        return minimums[axis.getId()];
    }

    protected void invalidateModel() {
        modelValid = false;
    }

    protected void validateModel() {
        modelValid = true;
    }


    public Model getModel() {
        if (model == null || !modelValid) {
            createModel();
        }
        return model;
    }
}
