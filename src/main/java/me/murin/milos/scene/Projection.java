package me.murin.milos.scene;

import org.joml.Matrix4f;

public class Projection {


    public static final float FOV = (float) Math.toRadians(60.0);
    public static final float Z_FAR = 1000f;
    public static final float Z_NEAR = 0.01f;

    private Matrix4f projMatrix;


    public Projection(int width, int height) {

        projMatrix = new Matrix4f();
        updateMatrix(width, height);
    }

    public Matrix4f getProjMatrix() {
        return projMatrix;
    }

    public void updateMatrix(int width, int height) {
        projMatrix.setPerspective(FOV, (float) width / height, Z_NEAR, Z_FAR);
    }
}
