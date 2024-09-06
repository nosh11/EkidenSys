package com.github.nosh11.ekidensys.runner;

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
        Location next = next().getLocation();
        double distance = Calc.getDistance(loc, next);

        // 次のポイントとの距離が 2.5 ブロック未満 -> 次のポイントにターゲットを変更する
        if (distance <= 2.5d) {
            current ++;
            return true;
        }

        return false;
    }
}
