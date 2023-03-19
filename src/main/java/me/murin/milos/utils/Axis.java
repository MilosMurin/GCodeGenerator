package me.murin.milos.utils;

public enum Axis {
    X(0),
    Y(1),
    Z(2);

    private final int id;

    Axis(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}