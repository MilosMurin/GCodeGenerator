package me.murin.milos;

import me.murin.milos.render.Render;
import me.murin.milos.scene.Scene;

public interface AppLogic {

    void cleanup();

    void init(Window window, Scene scene, Render render);

    void input(Window window, Scene scene, long diffTimeMillis);

    void update(Window window, Scene scene, long diffTimeMillis);

}
