package me.murin.milos.utils;

import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Way;
import me.murin.milos.dcel.Vertex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {


    private Utils() {
        // Utility class
    }

    public static String readFile(String filePath) {
        String str;
        try {
            str = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException excp) {
            throw new RuntimeException("Error reading file [" + filePath + "]", excp);
        }
        return str;
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
            // TODO: Change -1 based on the origin position
            case X -> vertex.setX((vertex.getX() - min) * scale - diffX); // -1 is for origin position
//            case Y -> throw new IllegalArgumentException("Y axis does not get ajusted!");
            case Z -> vertex.setZ((vertex.getZ() - min) * scale - diffZ);
        }
    }


    public static boolean isAlmostEqual(double d1, double d2) {
        return d1 - 0.00000001 < d2 && d1 + 0.00000001 > d2;
    }
}
