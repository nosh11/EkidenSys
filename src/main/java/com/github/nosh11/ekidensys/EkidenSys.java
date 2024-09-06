package com.github.nosh11.ekidensys;

import com.github.nosh11.ekidensys.course.CourseManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EkidenSys extends JavaPlugin {
    private static EkidenSys instance;

    @Override
    public void onEnable() {
        instance = this;
        CourseManager.getInstance().reload();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static EkidenSys getInstance() {
        return instance;
    }
}
