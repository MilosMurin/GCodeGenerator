package me.murin.milos.roads;

import info.pavie.basicosmparser.controller.OSMParser;
import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Node;
import info.pavie.basicosmparser.model.Way;
import me.murin.milos.dcel.Vertex;
import me.murin.milos.geometry.Road;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Map;

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
            Map<String, Element> result = p.parse(file);
            for (String key : result.keySet()) {
                Way way = isWay(result.get(key));
                if (way != null) {
                    if (isRoad(way)) {
                        Vertex prev = null, current;
                        Road prevRoad = null;
                        for (Node n : way.getNodes()) {
                            current = new Vertex(n);
                            roadList.addVertex(current);
                            if (prev != null) {
                                Road r = new Road(prev, current);
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
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }
}
