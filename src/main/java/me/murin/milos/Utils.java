package me.murin.milos;

import info.pavie.basicosmparser.model.Element;
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
}
