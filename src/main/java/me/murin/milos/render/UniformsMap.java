package me.murin.milos.render;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class UniformsMap {

    private int programId;
    private Map<String, Integer> uniforms;

    public UniformsMap(int programId) {
        this.programId = programId;
        uniforms = new HashMap<>();
    }

    public void createUniforms(String name) {
        int uniformLocation = glGetUniformLocation(programId, name);
        if (uniformLocation < 0) {
            throw new RuntimeException("Could not find uniform [" + name + "] in shader program [" + programId + "]");
        }
        uniforms.put(name, uniformLocation);
    }

    public int getUniformLocation(String uniformName) {
        Integer location = uniforms.get(uniformName);
        if (location == null) {
            throw new RuntimeException("Could not find uniform [" + uniformName + "]");
        }
        return location;
    }

    public void setUniform(String name, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(getUniformLocation(name), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String name, int value) {
        glUniform1i(getUniformLocation(name), value);
    }
}
