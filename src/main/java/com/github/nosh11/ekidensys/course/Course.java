package com.github.nosh11.ekidensys.course;

import java.util.*;

public class Course {
    private final List<Point> points = new ArrayList<>();

    public Course(List<Point> points) {
        this.points.addAll(points);
    }

    public Point origin() {
        return points.getFirst();
    }

    public Point getPoint(int i) {
        return points.get(i);
    }

}
