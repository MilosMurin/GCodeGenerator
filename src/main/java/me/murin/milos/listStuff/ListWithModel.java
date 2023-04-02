package me.murin.milos.listStuff;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.render.Material;
import me.murin.milos.render.Mesh;
import me.murin.milos.render.Model;
import me.murin.milos.utils.Axis;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public abstract class ListWithModel {

    protected final Extremes extremes = new Extremes();

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

    protected void testExtremes(Vertex vertex) {
        extremes.testExtremes2D(vertex);
    }

    protected double getMax(Axis axis) {
        return extremes.getMax(axis);
    }

    protected double getMin(Axis axis) {
        return extremes.getMin(axis);
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
