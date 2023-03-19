package me.murin.milos.roads.loaders;

import me.murin.milos.roads.RoadList;

public abstract class RoadImporter {

    protected final RoadList roadList = new RoadList();

    public RoadList getRoadList() {
        return roadList;
    }

}
