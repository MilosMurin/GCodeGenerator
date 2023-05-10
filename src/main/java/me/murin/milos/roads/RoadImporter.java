package me.murin.milos.roads;

import me.murin.milos.listStuff.RoadList;
import me.murin.milos.utils.MyFile;
import me.murin.milos.utils.MyFile.ResourceType;

public abstract class RoadImporter {

    protected final RoadList roadList = new RoadList();
    protected final MyFile file;

    public RoadImporter(String path) {
        file = new MyFile(path, ResourceType.ROAD);
        this.load();
    }

    public abstract void load();

    public RoadList getRoadList() {
        return roadList;
    }

}
