package com.github.nosh11.ekidensys.course;

import org.bukkit.Location;

public class Point {
    private final Location location;
    private final int index;
    private final boolean isCheckPoint;

    public Point(Location location, int index, boolean isCheckPoint) {
        this.location = location;
        this.index = index;
        this.isCheckPoint = isCheckPoint;
    }

    public Location getLocation() {
        return this.location;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean isCheckPoint() {
        return isCheckPoint;
    }
}
