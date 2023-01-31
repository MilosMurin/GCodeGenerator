package me.murin.milos;

import me.murin.milos.render.Model;
import me.murin.milos.render.Render;
import me.murin.milos.scene.Entity;
import me.murin.milos.scene.ModelLoader;
import me.murin.milos.scene.Scene;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements AppLogic {

    private Entity cubeEntity;
    private Vector4f displInc = new Vector4f();
    private float rotation;
    private boolean rotate = false;

    public static void main(String[] args) {
        Main main = new Main();
        Engine gameEng = new Engine("GCodeGenerator", new Window.WindowOptions(), main);
        gameEng.start();
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void init(Window window, Scene scene, Render render) {
        Model cubeModel = ModelLoader.loadModel("model1", "src/main/resources/models/cube/cube.obj",
                scene.getTextureCache());
        scene.addModel(cubeModel);

        cubeEntity = new Entity("entity1", cubeModel.getId());
        cubeEntity.setPosition(0, 0, -2);
        scene.addEntity(cubeEntity);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {

        displInc.zero();
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            displInc.y = 1;
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            displInc.y = -1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            displInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            displInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_W)) {
            displInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_Q)) {
            displInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            displInc.w = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            displInc.w = 1;
        }

        if (window.isKeyPressed(GLFW_KEY_A)) {
            rotation += 1.5;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            rotation -= 1.5;
        }

        if (window.wasKeyReleased(GLFW_KEY_R)) {
            rotate = !rotate;
        }

        if (rotation > 360) {
            rotation = 0;
        }


        displInc.mul(diffTimeMillis / 1000.0f);

        Vector3f entityPos = cubeEntity.getPosition();
        cubeEntity.setPosition(displInc.x + entityPos.x, displInc.y + entityPos.y, displInc.z + entityPos.z);
        cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
        cubeEntity.setScale(cubeEntity.getScale() + displInc.w);
        cubeEntity.updateModelMatrix();
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
//        Constant model rotation
//        if (rotate) {
//            rotation += 1.5;
//            if (rotation > 360) {
//                rotation = 0;
//            }
//            cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
//            cubeEntity.updateModelMatrix();
//        }
    }
}