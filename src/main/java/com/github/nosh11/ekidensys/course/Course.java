package com.github.nosh11.ekidensys.course;

import org.bukkit.Location;

import java.util.*;

public class Course {
    private final List<Point> points = new ArrayList<>();
    private final String id;

    public Course(List<Point> points, String id) {
        this.points.addAll(points);
        this.id = id;
    }

    public Point origin() {
        return points.getFirst();
    }

    public Point getPoint(int i) {
        if (points.size() <= i) return null;
        return points.get(i);
    }

    public String getId() {
        return this.id;
    }

    public List<Point> getAllPoints() {
        return points;
    }

    public void addPoint(Location location) {
        points.add(new Point(location, points.size(), false));
    }
}
