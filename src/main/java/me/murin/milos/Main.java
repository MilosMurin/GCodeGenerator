package me.murin.milos;

import me.murin.milos.render.Model;
import me.murin.milos.render.Render;
import me.murin.milos.roads.RoadList;
import me.murin.milos.roads.loaders.RoadLoader;
import me.murin.milos.scene.Camera;
import me.murin.milos.scene.Entity;
import me.murin.milos.scene.ModelLoader;
import me.murin.milos.scene.Scene;
import me.murin.milos.utils.InputManager;
import me.murin.milos.utils.Intersectorator;
import me.murin.milos.utils.MouseInput;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;

public class Main implements AppLogic {

    private static final String RES_PATH = "src/main/resources/";
    private static final String MODEL_PATH = RES_PATH + "models/";
    private static final String CUBE_PATH = "cube/cube.obj";
    private static final String TEST_PATH = "test/test.obj";
    private static final String TESTFULL_PATH = "test/testFull.obj";
    private static final String TESTSMALL_PATH = "test/testSmall.obj";
    private static final String TESTMINI_PATH = "test/testMini.obj";

    private static final String MAP_OSM = "osm/map.osm";
    private static final String TEST_OSM = "osm/test.osm";

    private static final float MOUSE_SENSITIVITY = 0.3f;
    private static final float MOVEMENT_SPEED = 0.01f;


    private static final String MODEL = MODEL_PATH + TESTMINI_PATH;
    private static final String ROADS = RES_PATH + MAP_OSM;

    private boolean dcelVisible = false;
    private Model mainModel;
    private Model dcelModel;
    private Model roadModel;
    private Model intersectModel;

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
        window.getInputManager().track(GLFW_KEY_Q);

        mainModel = ModelLoader.loadModelWithDcel("mainModel", MODEL, scene.getTextureCache());
        addModelAndEntity(scene, mainModel, "mainEntity", true);

        dcelModel = mainModel.getDcelModel();
        addModelAndEntity(scene, dcelModel, "dcelEntity", dcelVisible);

        RoadLoader rl = new RoadLoader(ROADS);
        RoadList roadList = rl.getRoadList();
        roadModel = roadList.getModel();
        Entity en = addModelAndEntity(scene, roadModel, "roadEntity", true);

        var intersectorator = new Intersectorator(roadList.getStarts(), mainModel.getDcel());
        intersectorator.intersect();
        intersectModel = intersectorator.getResult().getModel();
        addModelAndEntity(scene, intersectModel, "intersectEntity", true);
    }

    public Entity addModelAndEntity(Scene scene, Model model, String entityId, boolean visible) {
        scene.addModel(model);
        model.setVisible(visible);

        Entity dcelEntity = new Entity(entityId, model.getId());
        scene.addEntity(dcelEntity);
        return dcelEntity;
    }

    @Override
    public void input(Window window, Scene scene, long diffTimeMillis) {
        InputManager input = window.getInputManager();
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();

        if (input.wasReleased(GLFW_KEY_Q)) {
            dcelVisible = !dcelVisible;
            dcelModel.setVisible(dcelVisible);
            mainModel.setVisible(!dcelVisible);
        }

        if (input.isKeyPressed(GLFW_KEY_W)) {
            camera.moveCloser(move);
        } else if (input.isKeyPressed(GLFW_KEY_S)) {
            camera.moveFurther(move);
        }

        MouseInput mInput = input.getMouseInput();
        if (mInput.isRightButtonPressed()) {
            Vector2f displVec = mInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(displVec.x * MOUSE_SENSITIVITY),
                    (float) Math.toRadians(displVec.y * MOUSE_SENSITIVITY));
        }
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

    }
}