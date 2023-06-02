package me.murin.milos.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyFile {

    private final File file;
    private final ResourceType type;
    private boolean isResource = false;
    private String path;
    private boolean exists = false;


    public MyFile(String path, ResourceType type) {
        this.type = type;
        this.file = getFileFirstResource(path);
    }


    public File getFileFirstResource(String path) {
        this.path = getResourcePath(path);
        File file;
        if (this.path != null) {
            file = new File(this.path);
            if (file.exists()) {
                this.exists = true;
                this.isResource = true;
                return file;
            }
        }
        System.out.println("Resource at: [" + this.type.getSubFile() + path + "] was not found!");
        System.out.println("Trying to search in non resorce files.");
        this.path = path;
        file = new File(this.path);
        if (file.exists()) {
            this.exists = true;
            this.isResource = false;
            return file;
        }
        throw new RuntimeException("File at paths: [" + this.path + "] was not found!");
    }

    public String getResourcePath(String resourcePath) {
        URL url = getClass().getResource(this.type.getSubFile() + resourcePath);
        if (url == null) {
            return null;
        }
        String res = url.getPath();
        int resS = res.indexOf("/") + 1;
        char first = res.charAt(0);
        char second = res.charAt(1);
        if (Character.isAlphabetic(first) && second == ':') {
            return res;
        }
        return res.substring(resS);
    }

    public String readFile() {
        String str;
        try {
            str = new String(Files.readAllBytes(Paths.get(this.path)));
        } catch (IOException excp) {
            throw new RuntimeException("Error reading file [" + this.path + "]", excp);
        }
        return str;
    }

    public boolean isResource() {
        return isResource;
    }

    public String getPath() {
        return path;
    }

    public File getFile() {
        return file;
    }

    public boolean exists() {
        return exists;
    }

    public String getParent() {
        return this.file.getParent();
    }


    public enum ResourceType {
        MODEL("/models"),
        ROAD("/roads"),
        SHADER("/shaders");

        private final String subFile;

        ResourceType(String subFile) {
            this.subFile = subFile;
        }

        public String getSubFile() {
            return subFile;
        }
    }
}
