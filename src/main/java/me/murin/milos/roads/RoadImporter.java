package me.murin.milos.roads;

import me.murin.milos.listStuff.RoadList;

public abstract class RoadImporter {

    protected final RoadList roadList = new RoadList();

    public RoadList getRoadList() {
        return roadList;
    }

}
