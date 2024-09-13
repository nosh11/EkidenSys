package com.github.nosh11.ekidensys.course;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.*;


// List<Points> points: 座標の連結リスト。Runnerはこれに従って走ります。
// id: 識別用ID
// name: Course の名前

public class Course {
    private final List<Location> locations;
    private final String name;

    public Course(List<Location> locations, String name) {
        this.locations = locations;
        this.name = name;
    }
    public Location origin() {
        Random r = new Random();
        return locations.getFirst().clone().add(new Vector((r.nextDouble()-0.5d)*6, 0, (r.nextDouble()-0.5d)*6));
    }
    public Location getPoint(int i) {
        if (locations.size() <= i) return null;
        return locations.get(i);
    }
    public String getName() {
        return this.name;
    }
    public List<Location> getAllPoints() {
        return locations;
    }
    public void addPoint(Location location) {
        locations.add(location);
    }
}
