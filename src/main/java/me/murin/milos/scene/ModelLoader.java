package me.murin.milos.scene;

import me.murin.milos.dcel.DoublyConnectedEdgeList;
import me.murin.milos.dcel.Edge;
import me.murin.milos.dcel.Face;
import me.murin.milos.dcel.Vertex;
import me.murin.milos.render.Material;
import me.murin.milos.render.Mesh;
import me.murin.milos.render.Model;
import me.murin.milos.render.TextureCache;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.AI_MATKEY_COLOR_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiGetMaterialColor;
import static org.lwjgl.assimp.Assimp.aiGetMaterialTexture;
import static org.lwjgl.assimp.Assimp.aiImportFile;
import static org.lwjgl.assimp.Assimp.aiProcess_CalcTangentSpace;
import static org.lwjgl.assimp.Assimp.aiProcess_FixInfacingNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_LimitBoneWeights;
import static org.lwjgl.assimp.Assimp.aiProcess_PreTransformVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiReturn_SUCCESS;
import static org.lwjgl.assimp.Assimp.aiTextureType_DIFFUSE;
import static org.lwjgl.assimp.Assimp.aiTextureType_NONE;

public class ModelLoader {

    private static final List<DoublyConnectedEdgeList> dcels = new ArrayList<>();
    private static boolean makeDcel = false;

    private ModelLoader() { }

    public static Model loadModelWithDcel(String modelId, String modelPath, TextureCache textureCache) {
        makeDcel = true;
        return loadModel(modelId, modelPath, textureCache,
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                        aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                        aiProcess_PreTransformVertices);
    }

    public static Model loadModel(String modelId, String modelPath, TextureCache textureCache) {
        makeDcel = false;
        return loadModel(modelId, modelPath, textureCache,
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices |
                        aiProcess_Triangulate | aiProcess_FixInfacingNormals | aiProcess_CalcTangentSpace | aiProcess_LimitBoneWeights |
                        aiProcess_PreTransformVertices);
    }

    public static Model loadModel(String modelId, String modelPath, TextureCache textureCache, int flags) {
        File file = new File(modelPath);
        if (!file.exists()) {
            throw new RuntimeException("Model path does not exist [" + modelPath + "]");
        }
        String dir = file.getParent();

        AIScene aiScene = aiImportFile(modelPath, flags);
        if (aiScene == null) {
            throw new RuntimeException("Error loading model [modelPath: " + modelPath + "]");
        }

        int numMaterials = aiScene.mNumMaterials();
        List<Material> materialList = new ArrayList<>();

        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiScene.mMaterials().get(i));
            materialList.add(processMaterial(aiMaterial, dir, textureCache));
        }

        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Material defaultMaterial = new Material();

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh);
            int materialIndex = aiMesh.mMaterialIndex();
            Material material;
            if (materialIndex >= 0 && materialIndex < materialList.size()) {
                material = materialList.get(materialIndex);
            } else {
                material = defaultMaterial;
            }
            material.getMeshList().add(mesh);
        }
        if (!defaultMaterial.getMeshList().isEmpty()) {
            materialList.add(defaultMaterial);
        }

        Model model = new Model(modelId, materialList);
        if (makeDcel) {
            model.createDcelModel(dcels);
        }
        return model;

    }

    public static Material processMaterial(AIMaterial aiMaterial, String modelDir, TextureCache textureCache) {
        Material material = new Material();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIColor4D color = AIColor4D.create();

            int result = aiGetMaterialColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, aiTextureType_NONE, 0, color);

            if (result == aiReturn_SUCCESS) {
                material.setDiffuseColor(new Vector4f(color.r(), color.g(), color.b(), color.a()));
            }

            AIString aiTexturePath = AIString.callocStack(stack);
            aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, aiTexturePath, (IntBuffer) null, null, null,
                    null, null, null);
            String texturePath = aiTexturePath.dataString();

            if (texturePath.length() > 0) {
                material.setTexturePath(modelDir + File.separator + new File(texturePath).getName());
                textureCache.createTexture(material.getTexturePath());
                material.setDiffuseColor(Material.DEFAULT_COLOR);
            }
            return material;
        }
    }

    public static Mesh processMesh(AIMesh aiMesh) {
        DoublyConnectedEdgeList dcel1 = new DoublyConnectedEdgeList();

        float[] vertices = processVertices(aiMesh, dcel1);
        float[] texCoords = processTexCoords(aiMesh);
        int[] indices = processIndices(aiMesh, dcel1);
        if (texCoords.length == 0) {
            int numElements = (vertices.length / 3) * 2;
            texCoords = new float[numElements];
        }
        if (makeDcel) {
            dcels.add(dcel1);
        }

        return new Mesh(vertices, texCoords, indices);
    }

    private static float[] processVertices(AIMesh aiMesh, DoublyConnectedEdgeList dcel1) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        float[] data = new float[buffer.remaining() * 3];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D texCoord = buffer.get();
            data[pos++] = texCoord.x();
            data[pos++] = texCoord.y();
            data[pos++] = texCoord.z();
            if (makeDcel) {
                dcel1.addVertex(new Vertex((pos / 3) - 1, data[pos - 3], data[pos - 2], data[pos - 1]));
            }
        }
        return data;
    }

    private static float[] processTexCoords(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);
        if (buffer == null) {
            return new float[]{};
        }
        float[] data = new float[buffer.remaining() * 2];
        int pos = 0;
        while (buffer.remaining() > 0) {
            AIVector3D texCoord = buffer.get();
            data[pos++] = texCoord.x();
            data[pos++] = 1 - texCoord.y();
        }
        return data;
    }

    private static int[] processIndices(AIMesh aiMesh, DoublyConnectedEdgeList dcel1) {
        List<Integer> indices = new ArrayList<>();
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer faces = aiMesh.mFaces();


        int edgeId = 0;
        for (int i = 0; i < numFaces; i++) {
            Face myFace = new Face(i);
            Edge first = null, prev = null, current = null;
            Vertex vertex;

            AIFace face = faces.get(i);
            IntBuffer buffer = face.mIndices();
            while (buffer.remaining() > 0) {
                int vertexPos = buffer.get();
                indices.add(vertexPos);
                if (makeDcel) {
                    // Dcel Fill
                    vertex = dcel1.getVertex(vertexPos);
                    // if there already is an edge with the reverse origin and end base it on that
                    Edge twin = null;

                    if (prev != null) {
                        twin = dcel1.getTwin(prev.getOrigin(), vertex);
                        if (twin != null) {
                            prev.updateIdWithTwin(twin);
                        }
                    }

                    current = new Edge(edgeId++, 1, vertex, myFace);

                    if (first == null) {
                        first = current;
                        myFace.setFirstEdge(current);
                    } else {
                        prev.setNextEdge(current);
                    }

                    dcel1.addEdge(current);
                    prev = current;
                }
            }
            if (makeDcel) {
                // link first and last(current)
                // update id of first if it has a twin
                if (current != null) {
                    Edge twin = dcel1.getTwin(current.getOrigin(), first.getOrigin());
                    if (twin != null) {
                        current.updateIdWithTwin(twin);
                    }

                    current.setNextEdge(first);
                }
                dcel1.addFace(myFace);
            }
        }

        return indices.stream().mapToInt(Integer::intValue).toArray();
    }

    public static Model getDcelModel() {
        List<Material> materials = new ArrayList<>();
        Material material = new Material();

        for (DoublyConnectedEdgeList dcl : dcels) {
            material.getMeshList().add(dcl.getMesh());
        }

        materials.add(material);
        return new Model("dcelModel", materials);
    }

}
