package me.murin.milos;

import org.lwjgl.glfw.*;

import java.util.concurrent.Callable;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private long handle;

    private int width;
    private int height;

    private Callable<Void> resizeFunction;

    public Window(String title, WindowOptions options, Callable<Void> resizeFunction) {
        this.resizeFunction = resizeFunction;
        if (!glfwInit()) {
            throw new IllegalStateException("GLFW not initialized!");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // hide the window after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // set the window resizable

        if (options.width > 0 && options.height > 0) { // if size is requested then set the size
            this.width = options.width;
            this.height = options.height;
        } else { // if not set the size to maximized
            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            width = vidMode.width();
            height = vidMode.height();
        }

        handle = glfwCreateWindow(width, height, title, NULL, NULL);

        if (handle == NULL) {
            throw new RuntimeException("Failed to create window!");
        }

        // resize callback -> when window is resized the method resized gets called
        glfwSetFramebufferSizeCallback(handle, (window, w, h) -> resized(w, h));

        // ESC to close the window
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            }
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(handle);
        // Enable v-sync
        glfwSwapInterval(1);


        // Make the window visible
        glfwShowWindow(handle);

        // gets the current width and height
        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(handle, arrWidth, arrHeight);
        width = arrWidth[0];
        height = arrHeight[0];
    }

    public void cleanup() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);
        glfwTerminate();
    }


    public void update() {
        // push to render
        glfwSwapBuffers(handle);
    }

    public boolean shouldClose() {
        // gets wheter the window should close
        return glfwWindowShouldClose(handle);
    }

    public void pollEvents() {
        // processes events
        glfwPollEvents();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(handle, key) == GLFW_PRESS;
    }

    protected void resized(int width, int height) {
        this.width = width;
        this.height = height;
        try {
            resizeFunction.call();
        } catch (Exception e) {
            System.err.println("Error calling resize callback");
            e.printStackTrace();
        }
    }

    public static class WindowOptions {
        public int fps;
        public int height;
        public int ups = 30;
        public int width;
    }

}
