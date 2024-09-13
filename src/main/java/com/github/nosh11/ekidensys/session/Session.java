package com.github.nosh11.ekidensys.session;

import com.github.nosh11.ekidensys.course.Course;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private final List<Course> courses = new ArrayList<>();
    private final int id;

    public Session(int session_id) {
        this.id = session_id;
        File f = new File("plugins/EkidenSys/session_" + session_id +
                "/courses.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        for (int i = 0; i < 11; i++) {
            String course_name = config.getString(i + ".name", "無名の中継所");
            List<Location> locations = new ArrayList<>();
            int j = 0;
            while (config.contains(i + ".locations." + j)) {
                locations.add(config.getLocation(i + ".locations." + j));
                j++;
            }
            courses.add(new Course(locations, course_name));
        }
    }

    public Course getCourse(int index) {
        if (this.courses.size() <= index) return null;
        return this.courses.get(index);
    }

    public void save() {
        File f = new File("plugins/EkidenSys/session_" + this.id +
                "/courses.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        for (int i = 0; i < courses.size(); i++) {
            if (getCourse(i) == null) break;
            Course course = getCourse(i);
            config.set(i + ".name", course.getName());
            for (int j = 0; j < course.getAllPoints().size(); j++) {
                config.set(i + ".locations." + j, course.getPoint(j));
            }
        }
        try {
            config.save(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
