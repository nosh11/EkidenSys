package com.github.nosh11.ekidensys.runner;

import com.github.nosh11.ekidensys.course.Course;
import com.github.nosh11.ekidensys.course.Point;
import com.github.nosh11.ekidensys.util.Calc;
import net.citizensnpcs.api.persistence.DelegatePersistence;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Location;
import org.bukkit.entity.Entity;


@TraitName("runner")
public class RunnerTrait extends Trait {
    public RunnerTrait() {
        super("runner");
    }

    public void init(Course course) {
        this.course = course;
        this.current = 0;
    }

    @Persist int current = 0;
    @Persist
    @DelegatePersistence(CoursePersister.class)
    private Course course;

    public boolean check() {
        Location loc = npc.getStoredLocation();
        Point next_point = course.getPoint(current + 1);

        if (next_point == null) return false;

        Location next = next_point.getLocation();
        double distance = Calc.getDistance(loc, next);
        return distance <= 2.5d;
    }

    @Override
    public void run() {
        Entity entity = npc.getEntity();
        if (entity == null || this.course == null) return;

        if (check()) current ++;
        if (course.getPoint(current + 1) != null && entity.isOnGround() && entity.getVelocity().length() <= 0.1)
            npc.getNavigator().setTarget(course.getPoint(current + 1).getLocation());
    }
}
