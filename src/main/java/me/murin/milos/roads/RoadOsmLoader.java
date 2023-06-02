package me.murin.milos.roads;

import info.pavie.basicosmparser.controller.OSMParser;
import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Node;
import info.pavie.basicosmparser.model.Way;
import me.murin.milos.dcel.Vertex;
import me.murin.milos.geometry.PointPair;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static me.murin.milos.utils.Utils.isRoad;
import static me.murin.milos.utils.Utils.isWay;

public class RoadOsmLoader extends RoadImporter {

    public RoadOsmLoader(String path) {
        super(path);
    }

    @Override
    public void load() {
        OSMParser p = new OSMParser();

        try {
            Map<String, Element> result = p.parse(file.getFile());
            for (String key : result.keySet()) {
                Way way = isWay(result.get(key));
                if (way != null) {
                    if (isRoad(way)) {
                        Vertex prev = null, current;
                        PointPair prevRoad = null;
                        for (Node n : way.getNodes()) {
                            current = new Vertex(n);
                            roadList.addVertex(current);
                            if (prev != null) {
                                PointPair r = new PointPair(prev, current);
                                if (prevRoad != null) {
                                    prevRoad.setNext(r);
                                } else {
                                    roadList.addStartRoad(r);
                                }
                                prevRoad = r;
                            }
                            prev = current;
                        }
                    }
                }
            }
            Scanner sc = new Scanner(file.getFile());
            for (int i = 0; i < 2; i++) {
                sc.nextLine();
            }
//            <bounds minlat="0" minlon="0" maxlat="2" maxlon="2"/>
            String line = sc.nextLine();
            String[] split = line.split(" ");
            Vertex min = new Vertex(0, 0, 0);
            Vertex max = new Vertex(0, 0, 0);
            for (String s : split) {
                int f = s.indexOf("\"");
                int l = s.lastIndexOf("\"");
                if (f == -1 || l == -1) {
                    continue;
                }
                Double d = Double.parseDouble(s.substring(f + 1, l));
                if (s.contains("minlat")) {
                    min.setX(d);
                    System.out.printf("min x: %f\n", d);
                }
                if (s.contains("minlon")) {
                    min.setZ(d);
                    System.out.printf("min z: %f\n", d);
                }
                if (s.contains("maxlat")) {
                    max.setX(d);
                    System.out.printf("max x: %f\n", d);
                }
                if (s.contains("maxlon")) {
                    max.setZ(d);
                    System.out.printf("max z: %f\n", d);
                }
            }
            roadList.setMin(min);
            roadList.setMax(max);

            System.out.println(min);
            System.out.println(max);

        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }
}
