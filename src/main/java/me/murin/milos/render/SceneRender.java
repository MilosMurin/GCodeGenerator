package me.murin.milos.render;


import me.murin.milos.render.ShaderProgram.ShaderModuleData;
import me.murin.milos.scene.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class SceneRender {


    private ShaderProgram shaderProgram;

    private UniformsMap uniformsMap;


    public SceneRender() {
        List<ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        String start = "src/main/resources/shaders/";
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(start + "scene.vert", GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(start + "scene.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);

        cerateUniforms();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    public void render(Scene scene) {
        shaderProgram.bind();

        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());

        scene.getMeshMap().values().forEach(mesh -> {
            glBindVertexArray(mesh.getVaoId());
            glDrawElements(GL_TRIANGLES, mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
        });

        glBindVertexArray(0);

        shaderProgram.unbind();
    }

    private void cerateUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniforms("projectionMatrix");
    }

}
