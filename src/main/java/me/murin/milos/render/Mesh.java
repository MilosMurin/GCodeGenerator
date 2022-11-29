package me.murin.milos.render;

import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private int numVertices;
    private int vaoId;
    private List<Integer> vboIdList;

    public Mesh(float[] positions, float[] textCoords, int[] indices) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            this.numVertices = indices.length;
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays(); // creates vao
            glBindVertexArray(vaoId); // binds vao


            // Positions VBO
            int vboId = glGenBuffers(); // creates a vbo
            vboIdList.add(vboId);

            // buffer for positions
            FloatBuffer positionsBuffer = stack.callocFloat(positions.length);
            positionsBuffer.put(0, positions);

            glBindBuffer(GL_ARRAY_BUFFER, vboId); // binds the vbo
            glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW); // loads data into buffer
            glEnableVertexAttribArray(0);
            /* parameters information
                index: Specifies the location where the shader expects this data.
                size: Specifies the number of components per vertex attribute (from 1 to 4). In this case, we are passing 3D coordinates, so it should be 3.
                type: Specifies the type of each component in the array, in this case a float.
                normalized: Specifies if the values should be normalized or not.
                stride: Specifies the byte offset between consecutive generic vertex attributes. (We will explain it later).
                offset: Specifies an offset to the first component in the buffer.
             */
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0); // defines the structre of our data

            // Texture coordinates VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            FloatBuffer textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
            textCoordsBuffer.put(0, textCoords);
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            IntBuffer indicesBuffer = stack.callocInt(indices.length);
            indicesBuffer.put(0, indices);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            // unbinds vbos and vaos
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
    }

    public void cleanup() {
        vboIdList.stream().forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(vaoId);
    }

    public int getNumVertices() {
        return numVertices;
    }

    public int getVaoId() {
        return vaoId;
    }
}
