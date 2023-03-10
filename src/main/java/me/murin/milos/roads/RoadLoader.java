package me.murin.milos.roads;

import info.pavie.basicosmparser.controller.OSMParser;
import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Node;
import info.pavie.basicosmparser.model.Way;
import me.murin.milos.render.Material;
import me.murin.milos.render.Mesh;
import me.murin.milos.render.Model;
import org.joml.Vector4f;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.murin.milos.Utils.isRoad;
import static me.murin.milos.Utils.isWay;
import static org.lwjgl.opengl.GL11.GL_LINES;

public class RoadLoader {

    // LAT - X, LON - Z

    private HashMap<String, Node> nodes = new HashMap<>();
    private ArrayList<Way> ways = new ArrayList<>();
    private HashMap<String, Integer> nodeIds = new HashMap<>();

    private float scaleX = 0.5f;
    private float scaleZ = 0.5f;

    private final float[] extremeX = new float[] {Float.MAX_VALUE, Float.MIN_VALUE}; // 0 - min, 1 - max
    private final float[] extremeZ = new float[] {Float.MAX_VALUE, Float.MIN_VALUE};

    private Model model = null;

    public RoadLoader(String path) {
        OSMParser p = new OSMParser();
        File osmFile = new File(path);

        try {
            Map<String, Element> result = p.parse(osmFile);
            int counter = 0;
            for (String key : result.keySet()) {
                Way way = isWay(result.get(key));
                if (way != null) {
                    if (isRoad(way)) {
                        ways.add(way);
                        for (Node n : way.getNodes()) {
                            nodes.putIfAbsent(n.getId(), n);
                            if (n.getLat() < extremeX[0]) {
                                extremeX[0] = (float) n.getLat();
                            } else if (n.getLat() > extremeX[1]) {
                                extremeX[1] = (float) n.getLat();
                            }
                            if (n.getLon() < extremeZ[0]) {
                                extremeZ[0] = (float) n.getLon();
                            } else if (n.getLon() > extremeZ[1]) {
                                extremeZ[1] = (float) n.getLon();
                            }
                        }
                        counter++;
                    }
                }
            }
            scaleX = 2 / (extremeX[1] - extremeX[0]);
            scaleZ = 2 / (extremeZ[1] - extremeZ[0]);
            System.out.println(counter);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    public void createModel() {
        List<Material> materials = new ArrayList<>();
        Material material = new Material();

        // add mesh
        // vertices
        float[] vertexBuffer = new float[nodes.size() * 3];
        int i = 0;
        for (String s: nodes.keySet()) {
            nodeIds.put(s, i);
            vertexBuffer[3 * i] = (float) (nodes.get(s).getLat() - extremeX[0]) * scaleX - 1;
            vertexBuffer[3 * i + 1] = 1f;
            vertexBuffer[3 * i + 2] = (float) (nodes.get(s).getLon() - extremeZ[0]) * scaleZ - 1;
            i++;
        }
        // indices
        List<Integer> indices = new ArrayList<>();
        for (Way w : ways) {
            List<Node> nodes2 = w.getNodes();
            // make pairs for each two nodes that follow each other
            Node prev = null;
            for (Node n : w.getNodes()) {
                if (prev == null) {
                    prev = n;
                    continue;
                }
                indices.add(nodeIds.get(prev.getId()));
                indices.add(nodeIds.get(n.getId()));
                prev = n;
            }
        }
        material.getMeshList().add(new Mesh(vertexBuffer, indices.stream().mapToInt(Integer::intValue).toArray(),
                GL_LINES));

        material.setDiffuseColor(new Vector4f(1.0f, 0.0f, 0.0f, 1.0f));

        materials.add(material);
        model = new Model("RoadModel", materials);
    }

    public Model getModel() {
        if (model == null) {
            createModel();
        }
        return model;
    }

}
