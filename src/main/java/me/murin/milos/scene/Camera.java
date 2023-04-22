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

    private float t = 0;
    private float a = 0;
    private float b = 0;
    private float c = 0;

    public Camera() {
        position = new Vector3f();
        rotation = new Vector2f();
        up = new Vector3f();
        viewMatrix = new Matrix4f();
        defaultPosition();
    }

    public void defaultPosition() {
        rotation.set((float) Math.PI / 2, (float) Math.PI / 2);
        position.set(0, 0, 4f); // sets the default position a bit back to see the model
        recalculate();
    }

    public void addRotation(float x, float y) {
        rotation.add(x, y);
        recalculate();
    }

    public void moveFurther(float inc) {
        // TODO: make custom calculations based on rotation
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

    public void setYCoord(float yCoord) {
        position.set(position.x(), position.y(), yCoord);
        recalculate();
    }

    public Vector3f getPosition() {
        return position;
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}
