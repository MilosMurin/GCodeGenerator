package me.murin.milos.utils;

import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class InputManager {

    private final long handle;
    private final MouseInput mouseInput;

    private final HashMap<Integer, Boolean> released = new HashMap<>();
    private final HashMap<Integer, Boolean> pressed = new HashMap<>();

    public InputManager(long handle) {
        this.handle = handle;
        this.mouseInput = new MouseInput(handle);
    }

    public void tick() {
        mouseInput.input();
        for (Integer key : pressed.keySet()) {
            if (released.get(key)) {
                released.replace(key, false);
            }

            if (isKeyPressed(key)) {
                if (!pressed.get(key)) {
                    pressed.replace(key, true);
                }
            } else {
                if (pressed.get(key)) {
                    pressed.replace(key, false);
                    released.replace(key, true);
                }
            }
        }
    }

    public void track(int key) {
        released.put(key, false);
        pressed.put(key, false);
    }

    public boolean wasReleased(int key) {
        return released.get(key);
    }

    public boolean isKeyPressed(int key) {
        return glfwGetKey(handle, key) == GLFW_PRESS;
    }

    public MouseInput getMouseInput() {
        return mouseInput;
    }
}
