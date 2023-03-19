package me.murin.milos.utils;

import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Node;
import info.pavie.basicosmparser.model.Way;

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

    /**
     * Helper method to not make mistakes :D
     * Converts longitude and latitude to an axis
     * X - Latitude
     * Y - DOES NOT EXIST throws an error
     * Z - Longitude
     *
     * @param node the node to get the lat and lon from
     * @param axis the axis to get
     * @return a float containing coordinate on the given axis
     */
    public static float getCoordFromNode(Node node, Axis axis) {
        return switch (axis) {
            case X -> (float) node.getLat();
            case Y -> throw new IllegalArgumentException("Nodes dont have a y corrdinate!");
            case Z -> (float) node.getLon();
        };
    }
}
