package me.murin.milos.utils;

import me.murin.milos.dcel.DoublyConnectedEdgeList;
import me.murin.milos.dcel.Face;
import me.murin.milos.geometry.Line;
import me.murin.milos.geometry.Road;

import java.util.ArrayList;
import java.util.List;

public class Intersectorator {

    private List<Road> starts;
    private DoublyConnectedEdgeList dcel;
    private List<Line> result;

    public Intersectorator(List<Road> starts, DoublyConnectedEdgeList dcel) {
        this.starts = starts;
        this.dcel = dcel;
        this.result = new ArrayList<>();
    }

    public void intersect() {
        for (Road start : starts) {
            // get the first face it intersects with TODO: ask if theres a better way but i dont think so+
            // do:
            // 1.make intersection of road plane and face plane
            // 2.set the bounds of the line to the starting point of the road and
            //      a point which is the ending point of the road if the point is within the same face
            //      otherwise set the ending point as the intersection of the edge of the face and the new line
            // 3.1 if the bound was set as the ending point of the road get the next road and go to step 1.
            // 3.2 if not then switch the next face and go to step 1. with the starting point of the road set to the
            // intersection of the edge and the new line that was formed

            // if theres no next road go to the next one of the starts


            // note: make also a list of nodes (that remember the lines going out of them and into them) for the
            // traveling salesman problem

            Face face = dcel.getFaceForPoint((float) start.getFirst().getLat(), (float) start.getFirst().getLon());
            Road road = start;
            while (road.hasNext()) {
                var line = face.intersection(road);
                // test if the road ending is within the face
                boolean endInFace = false;
                // if endInFace is false set the end point of the line to the intersection
                if (endInFace) {
                    // set end bound to end of road
                    // get next road
                    road = road.getNext();
                } else {
                    // set end bound to the intersection with an edge of tha face
                    // set next face based on the edge that it intersects with
                    // need to find out what side to go to
                }
            }
        }
    }

    public List<Line> getResult() {
        if (result.isEmpty()) {
            intersect();
        }
        return result;
    }
}
