package me.murin.milos.utils;

import me.murin.milos.dcel.DoublyConnectedEdgeList;
import me.murin.milos.dcel.Edge;
import me.murin.milos.dcel.Face;
import me.murin.milos.dcel.Vertex;
import me.murin.milos.geometry.Line;
import me.murin.milos.geometry.LineList;
import me.murin.milos.geometry.Road;

import java.util.List;

public class Intersectorator {

    private List<Road> starts;
    private DoublyConnectedEdgeList dcel;
    private LineList result;

    public Intersectorator(List<Road> starts, DoublyConnectedEdgeList dcel) {
        this.starts = starts;
        this.dcel = dcel;
        this.result = new LineList();
    }

    public void intersect() {
        for (Road start : starts) {
            // get the first face it intersects with
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

            Face face = dcel.getFaceForPoint(start.getFirst().getX(), start.getFirst().getZ());
            if (face == null) {
                System.out.printf("Point (%f, %f) didnt find a face", start.getFirst().getX(), start.getFirst().getZ());
                continue;
            }
            Road road = start;
            Vertex end = null;
            Line prev = null;
            Edge intersected = null; // the line that was crossed to get to this face
            Edge intersect;
            Line lineIntersection;
            while (road != null) { // will not work if the last road goes through more faces
                lineIntersection = face.intersection(road);
                if (end == null) { // if this is the first road set the starting point as the start of the road
                    lineIntersection.setStartPoint(lineIntersection.getPointOnLine(road.getFirst()));
                    result.addVertex(lineIntersection.getStartPoint());
                } else { // othervise set it as the ending point of the previous road
                    lineIntersection.setStartPoint(end);
                }
                // test if the road ending is within the face
                boolean endInFace = face.isPointInFace(road.getLast());
                // if endInFace is false set the end point of the line to the intersection
                // set end bound to end of road
                lineIntersection.setEndPoint(lineIntersection.getPointOnLine(road.getLast())); // if not end in face this will
                // get rewritten
                if (endInFace) {
                    // set the ending point
                    end = lineIntersection.getPointOnLine(road.getLast());
                    // get next road
                    road = road.getNext();
                    intersected = null;
                } else {
                    intersect = face.getFirstEdge();
                    // set end bound to the intersection with an edge of the face
                    // need to find out what side to go to
                    Vertex v = intersect.intersect(lineIntersection);
                    while (true) {
                        if (lineIntersection.isWithinBounds(v)) {
                            if (intersected == null) {
                                if (intersect.getTwinEdge() != null) {
                                    // an edge with no twin edge is ont the edge of the model
                                    break;
                                }
                                // TODO: save the result and if nothing else was found then use this one
                            } else  {
                                if (!intersect.equals(intersected.getTwinEdge())) {
                                    break;
                                }
                            }
                        }
                        intersect = intersect.getNextEdge();
                        if (intersect == face.getFirstEdge()) {
                            break;
                        }
                        v = intersect.intersect(lineIntersection);
                    }
                    intersected = intersect;
                    if (v == null) {
                        System.out.println("Line:");
                        System.out.println(lineIntersection);
                        System.out.println("Edges:");
                        Edge e = face.getFirstEdge();
                        System.out.println(e.getLine());
                        e = e.getNextEdge();
                        System.out.println(e.getLine());
                        e = e.getNextEdge();
                        System.out.println(e.getLine());
                        System.out.println("Face:");
                        System.out.println(face);
                        System.out.println("Road:");
                        System.out.println(road);
                        throw new IllegalStateException("The line has no intersections with any of the edges?????");
                    }
                    lineIntersection.setEndPoint(v);
                    end = v;
                    // set next face based on the edge that it intersects with
                    Edge twin = intersect.getTwinEdge();
                    if (twin == null) {
                        // this should mean that we stepped out of the bounds of the base model
                        break;
                    }
                    face = twin.getIncidentFace();
                }
                result.addVertex(end);
                if (prev != null) {
                    prev.setNext(lineIntersection);
                } else {
                    result.addLine(lineIntersection);
                }
                prev = lineIntersection;
            }
        }
    }

    public LineList getResult() {
        if (result.isEmpty()) {
            intersect();
        }
        return result;
    }
}
