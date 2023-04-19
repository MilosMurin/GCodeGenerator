package me.murin.milos.roads;

import me.murin.milos.dcel.Vertex;
import me.murin.milos.geometry.PointPair;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class RoadObjLoader extends RoadImporter {

    public RoadObjLoader(String path) {
        super(path);
    }

    @Override
    public void load() {
        try {
            Scanner sc = new Scanner(file);
            PointPair prev = null;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] split = line.split(" ");
                if (split[0].equals("v")) {
                    if (split.length < 4) {
                        continue;
                    }
                    roadList.addVertex(new Vertex(Double.parseDouble(split[1]), Double.parseDouble(split[2]),
                            Double.parseDouble(split[3])));
                } else if (split[0].equals("l")) {
                    if (split.length < 3) {
                        continue;
                    }
                    prev = roadList.addRoad(Integer.parseInt(split[1]) - 1, Integer.parseInt(split[2]) - 1, prev);
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
