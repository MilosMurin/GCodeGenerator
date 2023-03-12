package me.murin.milos.geometry;

import info.pavie.basicosmparser.model.Node;

public class Road {

    private Node first;
    private Node last;

    public Road(Node first, Node last) {
        this.first = first;
        this.last = last;

        // TODO: Create a plane from this road
    }


    public Node getFirst() {
        return first;
    }

    public Node getLast() {
        return last;
    }
}
