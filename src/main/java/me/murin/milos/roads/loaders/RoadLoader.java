package me.murin.milos.roads.loaders;

import info.pavie.basicosmparser.controller.OSMParser;
import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Node;
import info.pavie.basicosmparser.model.Way;
import me.murin.milos.geometry.Road;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static me.murin.milos.utils.Utils.isRoad;
import static me.murin.milos.utils.Utils.isWay;

public class RoadLoader extends RoadImporter {



    public RoadLoader(String path) {
        OSMParser p = new OSMParser();
        File osmFile = new File(path);

        try {
            Map<String, Element> result = p.parse(osmFile);
            for (String key : result.keySet()) {
                Way way = isWay(result.get(key));
                if (way != null) {
                    if (isRoad(way)) {
                        Node prev = null;
                        Road prevRoad = null;
                        for (Node n : way.getNodes()) {
                            roadList.addNode(n);
                            if (prev != null) {
                                Road r = new Road(prev, n);
                                if (prevRoad != null) {
                                    prevRoad.setNext(r);
                                } else {
                                    roadList.addStartRoad(r);
                                }
                                prevRoad = r;
                            }
                            prev = n;
                        }
                    }
                }
            }
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }
    }
}
