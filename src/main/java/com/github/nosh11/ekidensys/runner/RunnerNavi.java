package com.github.nosh11.ekidensys.runner;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.course.Course;
import com.github.nosh11.ekidensys.course.CourseManager;
import com.github.nosh11.ekidensys.course.Point;
import com.github.nosh11.ekidensys.util.Calc;
import org.bukkit.Location;

public class RunnerNavi {
    private final Course course;
    private int current;

    public RunnerNavi(String id) {
        this.course = CourseManager.getInstance().get(id);
        this.current = 0;
    }

    public Point origin() {
        return course.origin();
    }

    public Point current() {
        return course.getPoint(current);
    }

    public Point next() {
        return course.getPoint(current + 1);
    }

    public boolean check(Location loc) {
        Point next_point = next();
        if (next_point == null) return false;
        Location next = next_point.getLocation();
        double distance = Calc.getDistance(loc, next);
        EkidenSys.getInstance().getLogger().info(String.valueOf(distance));
        return distance <= 2.5d;
    }

    public void add() {
        current ++;
    }
}
