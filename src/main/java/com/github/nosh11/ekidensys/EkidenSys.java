package com.github.nosh11.ekidensys;

import com.github.nosh11.ekidensys.command.CourseCmd;
import com.github.nosh11.ekidensys.course.CourseManager;
import com.github.nosh11.ekidensys.runner.RunnerManager;
import com.github.nosh11.ekidensys.runner.RunnerRunnable;
import com.github.nosh11.ekidensys.runner.RunnerTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class EkidenSys extends JavaPlugin {
    private static EkidenSys instance;

    @Override
    public void onEnable() {
        instance = this;

        CourseManager.getInstance().reload();

        new CourseCmd(instance);
        new RunnerRunnable().runTaskTimer(instance, 2L, 2L);

        Plugin citizens = getServer().getPluginManager().getPlugin("Citizens");
        if (citizens == null)
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found");
        else if (!citizens.isEnabled())
            getLogger().log(Level.SEVERE, "Citizens 2.0 not enabled");
        else {
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(RunnerTrait.class));
        }

        getLogger().info("hello");
    }

    @Override
    public void onDisable() {
        CourseManager.getInstance().save();
        getLogger().info("bye");
    }

    public static EkidenSys getInstance() {
        return instance;
    }
}
