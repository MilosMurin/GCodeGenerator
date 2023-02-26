package me.murin.milos;

import me.murin.milos.render.Model;
import me.murin.milos.render.Render;
import me.murin.milos.scene.Camera;
import me.murin.milos.scene.Entity;
import me.murin.milos.scene.ModelLoader;
import me.murin.milos.scene.Scene;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class Main implements AppLogic {

    // TODO: ciary
    // TODO: input buttons?


    private static final String RES_PATH = "src/main/resources/models/";
    private static final String CUBE_PATH = "cube/cube.obj";
    private static final String TEST_PATH = "test/test.obj";
    private static final String TESTFULL_PATH = "test/testFull.obj";
    private static final String CHAIR_PATH = "chair/chair.obj";
    private static final String TREE_PATH = "tree/tree.obj";
    private static final String FISH_PATH = "fish/Goldfish_01.obj";

    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.05f;

    private Entity cubeEntity;

    private boolean dcelVisible = false;
    private Model mainModel;
    private Model dcelModel;

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
        mainModel = ModelLoader.loadModelWithDcel("mainModel", RES_PATH + TESTFULL_PATH,
                scene.getTextureCache());
        scene.addModel(mainModel);

        cubeEntity = new Entity("mainEntity", mainModel.getId());
        scene.addEntity(cubeEntity);

        dcelModel = mainModel.getDcelModel();
        scene.addModel(dcelModel);
        dcelModel.setVisible(dcelVisible);

        Entity entity = new Entity("dcelEntity", dcelModel.getId());
        scene.addEntity(entity);
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();

        // TODO: Remake controls -> mouse: Middle click hold - rotate around origin, scroll - zoom

        dcelVisible = window.isKeyPressed(GLFW_KEY_Q);
        dcelModel.setVisible(dcelVisible);
        mainModel.setVisible(!dcelVisible);

        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(move);
        }
        if (window.isKeyPressed(GLFW_KEY_SPACE)) {
            camera.moveUp(move);
        } else if (window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            camera.moveDown(move);
        }

        MouseInput input = window.getMouseInput();
        if (input.isRightButtonPressed()) {
            Vector2f displVec = input.getDisplVec();
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY),
                    (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

    }
}