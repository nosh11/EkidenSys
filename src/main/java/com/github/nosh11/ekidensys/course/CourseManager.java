package com.github.nosh11.ekidensys.course;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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

            if (!config.contains("points")) {
                course_list.put("course_" + i, new Course(points, course_id));
                continue;
            }

            List<String> keys = config.createSection("points")
                    .getKeys(false)
                    .stream()
                    .sorted(Comparator.comparing((x) -> -Integer.parseInt(x)))
                    .toList();

            for (String k : keys) {
                points.add(new Point(
                        config.getLocation("points." + k + ".location"),
                        Integer.parseInt(k),
                        config.getBoolean("points." + k + ".is_checkpoint")
                ));
            }

            course_list.put("course_" + i, new Course(points, course_id));
        }
    }

    public Collection<Course> getAll() {
        return course_list.values();
    }

    public boolean contains(String key) {
        return course_list.containsKey(key);
    }
}
