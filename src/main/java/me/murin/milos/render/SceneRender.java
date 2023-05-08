package me.murin.milos.render;


import me.murin.milos.render.ShaderProgram.ShaderModuleData;
import me.murin.milos.scene.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL30.GL_TEXTURE0;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL30.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glActiveTexture;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDrawElements;

public class SceneRender {


    private ShaderProgram shaderProgram;

    private UniformsMap uniformsMap;


    public SceneRender() {
        List<ShaderModuleData> shaderModuleDataList = new ArrayList<>();
//        String start = "/"; // for jar
        String frag = getClass().getResource("/scene.frag").getPath().substring(6);
        String vert = getClass().getResource("/scene.vert").getFile().substring(6);
        System.out.println(frag);

        String start = "src/main/resources/shaders/";
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(vert, GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData(frag, GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);

        cerateUniforms();
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    public void render(Scene scene) {
        shaderProgram.bind();

        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjMatrix());
        uniformsMap.setUniform("viewMatrix", scene.getCamera().getViewMatrix());

        uniformsMap.setUniform("txtSampler", 0);

        TextureCache cache = scene.getTextureCache();

        for (Model m : scene.getModelMap().values()) {
            if (!m.isVisible()) {
                continue;
            }
            m.getMaterialList().forEach(material -> {
                uniformsMap.setUniform("material.diffuse", material.getDiffuseColor());
                Texture texture = cache.getTexture(material.getTexturePath());
                glActiveTexture(GL_TEXTURE0);
                texture.bind();
                material.getMeshList().forEach(mesh -> {
                    glBindVertexArray(mesh.getVaoId());
                    m.getEntityList().forEach(entity -> {
                        uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                        glDrawElements(mesh.getDrawType(), mesh.getNumVertices(), GL_UNSIGNED_INT, 0);
                    });
                });
            });
        }

        glBindVertexArray(0);

        shaderProgram.unbind();
    }

    private void cerateUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniforms("projectionMatrix");
        uniformsMap.createUniforms("modelMatrix");
        uniformsMap.createUniforms("viewMatrix");
        uniformsMap.createUniforms("txtSampler");
        uniformsMap.createUniforms("material.diffuse");
    }

}
