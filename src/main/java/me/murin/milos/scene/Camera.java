package me.murin.milos.scene;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {
    // this class is a bit different than in the tutorial because this rotates the camera around the origin

    private Vector3f position;
    private Vector2f rotation;
    private Vector3f up;
    private Matrix4f viewMatrix;

    public Camera() {
        position = new Vector3f();
        rotation = new Vector2f();
        up = new Vector3f();
        viewMatrix = new Matrix4f();
    }

    public void addRotation(float x, float y) {
        rotation.add(x, y);
        recalculate();
    }

    public void moveFurther(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.sub(up);
        recalculate();
    }

    public void moveCloser(float inc) {
        viewMatrix.positiveY(up).mul(inc);
        position.add(up);
        recalculate();
    }

    private void recalculate() {
        // using this differently than in the tutorial because this rotates the camera around the origin
        viewMatrix.identity()
                .translate(-position.x, -position.y, -position.z)
                .rotateX(rotation.x)
                .rotateY(rotation.y);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
        recalculate();
    }

    public void setRotation(float x, float y) {
        rotation.set(x, y);
        recalculate();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
