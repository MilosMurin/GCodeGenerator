package me.murin.milos.render;

import me.murin.milos.Window;
import me.murin.milos.scene.Scene;
import org.lwjgl.opengl.GL;


public class Render {

    private SceneRender sceneRender;

    public Render() {
        GL.createCapabilities();
        sceneRender = new SceneRender();
    }

    public void cleanup() {
        sceneRender.cleanup();
    }

    public void render(Window window, Scene scene) {
        sceneRender.render(scene);
    }


}
