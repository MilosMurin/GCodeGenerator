package me.murin.milos.utils;

import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Way;
import me.murin.milos.dcel.Vertex;
import me.murin.milos.geometry.PointPair;

import java.util.ArrayList;
import java.util.List;

public class Utils {


    private Utils() {
        // Utility class
    }


    public static boolean isRoad(Way way) {
        var tags = way.getTags();
        if (tags.containsKey("highway")) {
            String value = tags.get("highway");
            return switch (value) {
                case "motorway", "trunk", "primary", "secondary", "tertiary", "unclassified",
                        "residential", "motorway_link", "trunk_link", "primary_link",
                        "secondary_link", "tertiary_link" -> true;
                default -> false;
            };
        }
        return false;
    }

    public static Way isWay(Element element) {
        if (element.getId().startsWith("W")) {
            if (element instanceof Way) {
                return (Way) element;
            }
        }
        return null;
    }

    public static void adjustCoordOnAxis(Vertex vertex, Axis axis, double min, double scale, double diffX,
                                         double diffZ) {
        switch (axis) {
            case X -> vertex.setX((vertex.getX() - min) * scale - diffX); // -1 is for origin position
//            case Y -> throw new IllegalArgumentException("Y axis does not get ajusted!");
            case Z -> vertex.setZ((vertex.getZ() - min) * scale - diffZ);
        }
    }

    public static int[] fillIndicies(List<PointPair> lines) {
        List<Integer> indices = new ArrayList<>();
        for (PointPair l : lines) {
            // fill indices
            PointPair current = l;
            indices.add(current.getStart().getId());
            indices.add(current.getEnd().getId());

            while (current.hasNext()) {
                current = current.getNext();
                indices.add(current.getStart().getId());
                indices.add(current.getEnd().getId());
            }
        }
        return indices.stream().mapToInt(Integer::intValue).toArray();
    }


    public static boolean isAlmostEqual(double d1, double d2) {
        return d1 - 0.00000001 < d2 && d1 + 0.00000001 > d2;
    }
}
