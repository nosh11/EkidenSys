package com.github.nosh11.ekidensys.course;

import com.github.nosh11.ekidensys.EkidenSys;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CourseManager {
    private static final CourseManager instance = new CourseManager();
    public static CourseManager getInstance() {
        return instance;
    }

    private final Map<String, Course> course_list = new HashMap<>();

    public Course get(String key) {
        return course_list.get(key);
    }

    public void reload() {
        for (int i = 1; i <= 3; i++) {
            final List<Point> points = new ArrayList<>();
            final String course_id = "course_" + i;

            File f = new File("plugins/EkidenSys/" + course_id + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(f);

            if (config.getKeys(false).isEmpty()) {
                course_list.put(course_id, new Course(points, course_id));
                continue;
            }

            List<String> keys = config.getKeys(false)
                    .stream().sorted(Comparator.comparing(Integer::parseInt)).toList();

            for (String k : keys) {
                points.add(new Point(
                        config.getLocation(k + ".location"),
                        Integer.parseInt(k),
                        config.getBoolean(k + ".is_checkpoint")
                ));
            }

            course_list.put(course_id, new Course(points, course_id));
        }
    }

    public void save() {
        for (Course course : course_list.values()) {
            try {
                File f = new File("plugins/EkidenSys/" + course.getId() + ".yml");
                FileConfiguration config = YamlConfiguration.loadConfiguration(f);

                for (Point p : course.getAllPoints()) {
                    config.set(p.getIndex() + ".is_checkpoint", p.isCheckPoint());
                    config.set(p.getIndex() + ".location", p.getLocation());
                }

                config.save(f);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Collection<Course> getAll() {
        return course_list.values();
    }

    public boolean contains(String key) {
        return course_list.containsKey(key);
    }
}
