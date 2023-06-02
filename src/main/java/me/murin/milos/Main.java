package me.murin.milos;

import me.murin.milos.gcode.GCodeFileWriter;
import me.murin.milos.gcode.GCodeReader;
import me.murin.milos.listStuff.PointPairList;
import me.murin.milos.listStuff.RoadList;
import me.murin.milos.render.Model;
import me.murin.milos.render.Render;
import me.murin.milos.roads.RoadObjLoader;
import me.murin.milos.roads.RoadOsmLoader;
import me.murin.milos.scene.Camera;
import me.murin.milos.scene.Entity;
import me.murin.milos.scene.ModelLoader;
import me.murin.milos.scene.Scene;
import me.murin.milos.utils.Axis;
import me.murin.milos.utils.InputManager;
import me.murin.milos.utils.Intersectorator;
import me.murin.milos.utils.MouseInput;
import org.joml.Vector2f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NFDFilterItem;

import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_FreePath;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_OKAY;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_OpenDialog;

public class Main implements AppLogic {

    private static final String CUBE_PATH = "/cube/cube.obj";
    private static final String TEST_PATH = "/test/malyZlozity.obj";
    private static final String TESTFULL_PATH = "/test/testFull.obj";
    private static final String TESTSMALL_PATH = "/test/testSmall.obj";
    private static final String TESTMINI_PATH = "/test/testMini.obj";
    private static final String NEWTEST_PATH = "/newTest/velkyJednoduchy.obj";

    private static final String MAP_OSM = "/jednoducheCiary.osm";
    private static final String TEST_OSM = "/zloziteCiary.osm";

    private static final float MOUSE_SENSITIVITY = 0.3f;
    private static final float MOVEMENT_SPEED = 0.01f;


    private static final String MODEL = TEST_PATH;
    private static final String ROADS = MAP_OSM;

    private boolean dcelVisible = false;
    private boolean roadsVisible = true;
    private Model mainModel = null;
    private Model dcelModel = null;
    private Model roadModel = null;
    private Model intersectModel = null;
    private RoadList roadList = null;
    private Intersectorator intersectorator;
    private GCodeReader reader = null;

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
        window.getInputManager().track(GLFW_KEY_F);
        window.getInputManager().track(GLFW_KEY_R);
        window.getInputManager().track(GLFW_KEY_G);
        window.getInputManager().track(GLFW_KEY_H);

        mainModel = ModelLoader.loadModelWithDcel("mainModel", TEST_PATH, scene.getTextureCache());
        addModelAndEntity(scene, mainModel, "mainEntity", true);

        dcelModel = mainModel.getDcelModel();
        addModelAndEntity(scene, dcelModel, "dcelEntity", dcelVisible);

        scene.getCamera().setYCoord((float) (2 *
                Math.max(mainModel.getExtremes().getSize(Axis.X), mainModel.getExtremes().getSize(Axis.Z))));

        RoadOsmLoader rl = new RoadOsmLoader(ROADS);
        roadList = rl.getRoadList();

        refreshRoadModel(scene);

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

        if (input.wasReleased(GLFW_KEY_H)) {
            roadsVisible = !roadsVisible;
            roadModel.setVisible(roadsVisible);
        }

        if (input.wasReleased(GLFW_KEY_F)) { // load object
            try (MemoryStack stack = stackPush()) {
                NFDFilterItem.Buffer filters = NFDFilterItem.malloc(1);
                filters.get(0)
                        .name(stack.UTF8("Objects"))
                        .spec(stack.UTF8("obj"));

                String path = loadPath(stack, filters);
                if (path != null) {
                    mainModel.cleanup();
                    mainModel = ModelLoader.loadModelWithDcel("mainModel", path, scene.getTextureCache());
                    addModelAndEntity(scene, mainModel, "mainEntity", true);

                    dcelModel.cleanup();
                    dcelModel = mainModel.getDcelModel();
                    addModelAndEntity(scene, dcelModel, "dcelEntity", dcelVisible);

                    scene.getCamera().setYCoord((float) (2 *
                            Math.max(mainModel.getExtremes().getSize(Axis.X), mainModel.getExtremes().getSize(Axis.Z))));

                    refreshRoadModel(scene);

                    if (intersectModel != null) {
                        intersectModel.cleanup();
                        intersectModel = null;
                    }

                    intersectorator.setDcel(mainModel.getDcel());
                }
            }
        }

        if (input.wasReleased(GLFW_KEY_R)) { // load linear objects
            try (MemoryStack stack = stackPush()) {
                NFDFilterItem.Buffer filters = NFDFilterItem.malloc(1);
                filters.get(0)
                        .name(stack.UTF8("Linear objects"))
                        .spec(stack.UTF8("obj,osm"));


                String path = loadPath(stack, filters);
                if (path != null) {
                    if (path.endsWith("osm")) {
                        RoadOsmLoader rl = new RoadOsmLoader(path);
                        roadList = rl.getRoadList();
                    } else if (path.endsWith("obj")) {
                        RoadObjLoader rl = new RoadObjLoader(path);
                        roadList = rl.getRoadList();
                    }
                    if (roadList != null) {
                        refreshRoadModel(scene);

                        if (intersectModel != null) {
                            intersectModel.cleanup();
                            intersectModel = null;
                        }

                        intersectorator.setRoads(roadList.getStarts());
                    }
                }
            }
        }

        if (input.wasReleased(GLFW_KEY_G)) { // import gcode
            try (MemoryStack stack = stackPush()) {
                NFDFilterItem.Buffer filters = NFDFilterItem.malloc(1);
                filters.get(0)
                        .name(stack.UTF8("Gcode files"))
                        .spec(stack.UTF8("gcode"));


                String path = loadPath(stack, filters);
                if (path != null) {
                    System.out.println(path);
                    reader = new GCodeReader(path);
                    try {
                        reader.load();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (input.isKeyPressed(GLFW_KEY_W)) {
            camera.moveCloser(move);
        } else if (input.isKeyPressed(GLFW_KEY_S)) {
            camera.moveFurther(move);
        }

        if (input.wasReleased(GLFW_KEY_I)) { // intersect button
            if (intersectModel != null) {
                intersectModel.cleanup();
            }
            intersectorator.intersect();
            PointPairList ppl = intersectorator.getResult();
            ppl.createModel();
            intersectModel = ppl.getModel();
            addModelAndEntity(scene, intersectModel, "intersectEntity", true);
            if (reader != null) {
                try {
                    String gcode = ppl.generateGCode(reader.getExtremes());
                    GCodeFileWriter writer = new GCodeFileWriter("out.gcode");
                    GCodeFileWriter writer2 = new GCodeFileWriter("out2.gcode");
                    writer.write(reader.getBeforeGCode());
                    writer.write(gcode);
                    writer2.write(gcode);
                    writer.write(reader.getAfterGCode());
                    writer.close();
                    writer2.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

    public void refreshRoadModel(Scene scene) {
        roadList.adjustToModel((float) mainModel.getExtremes().getSize(Axis.X),
                (float) mainModel.getExtremes().getSize(Axis.Z), mainModel.getExtremes().getMax(Axis.Y) * 1.5);
        if (roadModel != null) {
            roadModel.cleanup();
        }
        roadModel = roadList.getModel();
        addModelAndEntity(scene, roadModel, "roadEntity", true);
    }


    public String loadPath(MemoryStack stack, NFDFilterItem.Buffer filters) {

        PointerBuffer pp = stack.mallocPointer(1);

        int ret = NFD_OpenDialog(pp, filters, (ByteBuffer) null);

        if (ret == NFD_OKAY) {
            String s = pp.getStringUTF8(0); // this is the path to the absolute model
            NFD_FreePath(pp.get(0));
            return s;
        }

        return null;
    }
}