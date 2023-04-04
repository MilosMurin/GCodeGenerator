package me.murin.milos.roads;

import me.murin.milos.listStuff.RoadList;

import java.io.File;

public abstract class RoadImporter {

    protected final RoadList roadList = new RoadList();
    protected final File file;

    public RoadImporter(String path) {
        file = new File(path);
        this.load();
    }

    public abstract void load();

    public RoadList getRoadList() {
        return roadList;
    }

}
