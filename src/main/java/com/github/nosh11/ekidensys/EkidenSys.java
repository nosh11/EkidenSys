package com.github.nosh11.ekidensys;

import com.github.nosh11.ekidensys.command.CourseCmd;
import com.github.nosh11.ekidensys.course.CourseManager;
import com.github.nosh11.ekidensys.runner.RunnerRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public final class EkidenSys extends JavaPlugin {
    private static EkidenSys instance;

    @Override
    public void onEnable() {
        instance = this;

        CourseManager.getInstance().reload();

        new CourseCmd(instance);
        new RunnerRunnable().runTaskTimer(instance, 2L, 2L);

        getLogger().info("hello");
    }

    @Override
    public void onDisable() {
        getLogger().info("bye");
    }

    public static EkidenSys getInstance() {
        return instance;
    }
}
