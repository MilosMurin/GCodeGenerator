package me.murin.milos;

import me.murin.milos.listStuff.RoadList;
import me.murin.milos.render.Model;
import me.murin.milos.render.Render;
import me.murin.milos.roads.RoadOsmLoader;
import me.murin.milos.scene.Camera;
import me.murin.milos.scene.Entity;
import me.murin.milos.scene.ModelLoader;
import me.murin.milos.scene.Scene;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.GCodeFileWriter;
import me.murin.milos.utils.InputManager;
import me.murin.milos.utils.Intersectorator;
import me.murin.milos.utils.MouseInput;
import org.joml.Vector2f;

import java.io.IOException;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
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
    private static final String NEWTEST_PATH = "newTest/testTest.obj";

    private static final String MAP_OSM = "osm/map.osm";
    private static final String TEST_OSM = "osm/test.osm";

    private static final float MOUSE_SENSITIVITY = 0.3f;
    private static final float MOVEMENT_SPEED = 0.01f;


    private static final String MODEL = MODEL_PATH + NEWTEST_PATH;
    private static final String ROADS = RES_PATH + TEST_OSM;

    private boolean dcelVisible = false;
    private Model mainModel;
    private Model dcelModel;
    private Model roadModel;
    private Model intersectModel;
    private Intersectorator intersectorator;

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
        window.getInputManager().track(GLFW_KEY_I);

        mainModel = ModelLoader.loadModelWithDcel("mainModel", MODEL, scene.getTextureCache());
        addModelAndEntity(scene, mainModel, "mainEntity", true);

        dcelModel = mainModel.getDcelModel();
        addModelAndEntity(scene, dcelModel, "dcelEntity", dcelVisible);

        scene.getCamera().setYCoord((float) (2 *
                        Math.max(mainModel.getExtremes().getSize(Axis.X), mainModel.getExtremes().getSize(Axis.Z))));

        RoadOsmLoader rl = new RoadOsmLoader(ROADS);
        RoadList roadList = rl.getRoadList();
        roadList.adjustToModel((float) mainModel.getExtremes().getSize(Axis.X),
                (float) mainModel.getExtremes().getSize(Axis.Z), mainModel.getExtremes().getMax(Axis.Y) + 1);
        roadModel = roadList.getModel();
        addModelAndEntity(scene, roadModel, "roadEntity", true);

        intersectorator = new Intersectorator(roadList.getStarts(), mainModel.getDcel());
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

        if (input.wasReleased(GLFW_KEY_I)) {
            intersectorator.intersect();
            intersectModel = intersectorator.getResult().getModel();
            addModelAndEntity(scene, intersectModel, "intersectEntity", true);
            try {
                String gcode = intersectorator.getResult().generateGCode();
                GCodeFileWriter writer = new GCodeFileWriter("out.gcode");
                writer.write(gcode);
                writer.close();
                System.out.println(gcode);
            } catch (IOException ignored) { }
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