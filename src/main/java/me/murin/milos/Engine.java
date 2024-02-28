package me.murin.milos;

import me.murin.milos.Window.WindowOptions;
import me.murin.milos.render.Render;
import me.murin.milos.scene.Scene;


public class Engine {

    private final AppLogic appLogic;
    private final Window window;
    private Render render;
    private boolean running;
    private Scene scene;
    private int targetFps;

    public Engine(String title, WindowOptions options, AppLogic appLogic) {
        window = new Window(title, options, () -> {
            resize();
            return null;
        });
        targetFps = options.fps;
        this.appLogic = appLogic;
        render = new Render();
        scene = new Scene(window.getWidth(), window.getHeight());
        appLogic.init(window, scene, render);
        running = true;
    }

    private void cleanup() {
        appLogic.cleanup();
        render.cleanup();
        scene.cleanup();
        window.cleanup();
    }

    public void resize() {
        if (window != null) {
            scene.resize(window.getWidth(), window.getHeight());
        }
    }

    private void run() {
        long initialTime = System.currentTimeMillis();
        float timeR = targetFps > 0 ? 1000.0f / targetFps : 0;
        float deltaFps = 0;


        while (running && !window.shouldClose()) {
            window.pollEvents();

            long now = System.currentTimeMillis();
            deltaFps += (now - initialTime) / timeR;

            if (targetFps <= 0 || deltaFps >= 1) {
                window.getInputManager().tick();
                appLogic.input(window, scene, now - initialTime);
            }

            if (targetFps <= 0 || deltaFps >= 1) {
                render.render(window, scene);
                deltaFps--;
                window.update();
            }
            initialTime = now;
        }
        cleanup();
    }

    public void start() {
        running = true;
        run();
    }

    public void stop() {
        running = false;
    }

}
