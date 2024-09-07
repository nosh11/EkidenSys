package com.github.nosh11.ekidensys.runner;

import com.github.nosh11.ekidensys.course.Course;
import com.github.nosh11.ekidensys.course.CourseManager;
import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.util.DataKey;

public class CoursePersister implements Persister<Course> {
    @Override
    public Course create(DataKey dataKey) {
        return CourseManager.getInstance().get(dataKey.getString("course_id"));
    }

    @Override
    public void save(Course course, DataKey dataKey) {
        dataKey.setString("course_id", course.getId());
    }
}
