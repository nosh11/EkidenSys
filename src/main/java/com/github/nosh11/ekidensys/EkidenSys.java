package com.github.nosh11.ekidensys;

import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.command.SessionCmd;
import com.github.nosh11.ekidensys.command.EkidenCmd;
import com.github.nosh11.ekidensys.command.TeamCmd;
import com.github.nosh11.ekidensys.runner.RunnerTrait;
import com.github.nosh11.ekidensys.session.SessionManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public final class EkidenSys extends JavaPlugin {
    private static EkidenSys instance;
    public static EkidenSys getInstance() {
        return instance;
    }

    private int currentSession = 0;
    private boolean isRunning = false;
    private final boolean apimode = false;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        this.currentSession = getConfig().getInt("current_session");
        this.isRunning = getConfig().getBoolean("is_running");

        SessionManager.getInstance().reload();
        ApiManager.getInstance().reloadAll();

        new SessionCmd(this);
        new EkidenCmd(this);
        new TeamCmd(this);

        // RunnerTrait を Citizensに登録
        Plugin citizens = getServer().getPluginManager().getPlugin("Citizens");
        if (citizens == null)
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found");
        else if (!citizens.isEnabled())
            getLogger().log(Level.SEVERE, "Citizens 2.0 not enabled");
        else {
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(RunnerTrait.class));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (isRunning & apimode) ApiManager.getInstance().update();
            }
        }.runTaskLater(this, 200L);
        getLogger().info("hello");
    }

    @Override
    public void onDisable() {
        getConfig().set("current_session", this.currentSession);
        getConfig().set("is_running", this.isRunning);
        saveConfig();

        SessionManager.getInstance().save();
        getLogger().info("bye");
    }

    public static void broadcast(String msg) {
        instance.getServer().broadcast(
                MiniMessage.miniMessage().deserialize(msg)
        );
    }

    public void setCurrentSessionId(int session_id) {
        this.currentSession = session_id;
    }

    public int getCurrentSessionId() {
        return this.currentSession;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void isRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
