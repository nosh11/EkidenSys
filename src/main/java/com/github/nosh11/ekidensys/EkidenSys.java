package com.github.nosh11.ekidensys;

import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiTeam;
import com.github.nosh11.ekidensys.cameraman.CameraMan;
import com.github.nosh11.ekidensys.cameraman.CameraManBoardManager;
import com.github.nosh11.ekidensys.cameraman.CameraManListener;
import com.github.nosh11.ekidensys.cameraman.CameraManManager;
import com.github.nosh11.ekidensys.command.SessionCmd;
import com.github.nosh11.ekidensys.command.EkidenCmd;
import com.github.nosh11.ekidensys.command.TeamCmd;
import com.github.nosh11.ekidensys.trait.RunnerTrait;
import com.github.nosh11.ekidensys.session.SessionManager;
import com.github.nosh11.ekidensys.trait.WatcherTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.kyori.adventure.text.Component;
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

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Config, API のロード
        getLogger().info("Loading Config and Api");
        loadConfig();
        SessionManager.getInstance().reload();
        ApiManager.getInstance().reloadAll();

        // Listener の登録
        getLogger().info("Loading Listeners");
        getServer().getPluginManager().registerEvents(new CameraManListener(), this);

        // Command の登録
        getLogger().info("Loading Commands");
        new SessionCmd(this);
        new EkidenCmd(this);
        new TeamCmd(this);

        // Citizens: Trait の登録
        getLogger().info("Loading Traits");
        Plugin citizens = getServer().getPluginManager().getPlugin("Citizens");
        if (citizens == null)
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found");
        else if (!citizens.isEnabled())
            getLogger().log(Level.SEVERE, "Citizens 2.0 not enabled");
        else {
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(RunnerTrait.class));
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(WatcherTrait.class));
        }

        // Runnable の登録
        getLogger().info("Loading Runnable");
        // - 10秒おきに API から情報を得る
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getRemainingTime() > -20000 && apiMode) {
                    ApiManager.getInstance().update();
                }
            }
        }.runTaskTimer(this, 200L, 200L);
        // - 1秒おきに状態をアップデートする
        new BukkitRunnable() {
            @Override
            public void run() {
                update();
            }
        }.runTaskTimer(this, 20L, 20L);

        // おわり
        getLogger().info("Completely Loaded!");
    }
    @Override
    public void onDisable() {
        getLogger().info("Saving...");
        // Config のセーブ
        saveConfig();
        // Manager のセーブ
        SessionManager.getInstance().save();
        getLogger().info("Good Night");
    }

    // Config
    private int currentSession;
    private boolean isRunning;
    public boolean apiMode;
    public String apiUrl;
    public long startTime;
    public void loadConfig() {
        this.currentSession = getConfig().getInt("current_session", 0);
        this.isRunning = getConfig().getBoolean("is_running", false);
        this.startTime = getConfig().getLong("start_time", 0L);
        this.apiMode = getConfig().getBoolean("api_mode", false);
        this.apiUrl = getConfig().getString("api_url", "https://ekiden2024.event.techful-programming.com/api/ranking");
    }
    public void saveConfig() {
        getConfig().set("current_session", this.currentSession);
        getConfig().set("is_running", this.isRunning);
        getConfig().set("start_time", this.startTime);
        getConfig().set("api_mode", this.apiMode);
        getConfig().set("api_url", this.apiUrl);
        super.saveConfig();
    }

    // Game
    public void gameStart(int session_id) {
        setCurrentSessionId(session_id);
        isRunning(true);
        resetStartTime();

        for (ApiTeam team : ApiManager.getInstance().getTeams()) {
            team.resetNpc();
        }
    }

    public void gameEnd() {
        for (CameraMan camera : CameraManManager.getInstance().getAll()) {
            camera.sendResult();
        }
        isRunning(false);
    }

    public void update() {
        if (!isRunning) {
            CameraManBoardManager.getInstance().hide();
            return;
        }
        if (isEnded()) {
            gameEnd();
            return;
        }
        CameraManManager.getInstance().update();
        CameraManBoardManager.getInstance().update();

    }

    // メソッド
    public static void broadcast(String msg) {
        instance.getServer().broadcast(
                MiniMessage.miniMessage().deserialize(msg)
        );
    }
    public static void broadcast(Component msg) {
        instance.getServer().broadcast(msg);
    }

    public int getCurrentSessionId() {
        return this.currentSession;
    }
    public void setCurrentSessionId(int session_id) {
        this.currentSession = session_id;
    }
    public boolean isRunning() {
        return isRunning;
    }
    public void isRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    public void resetStartTime() {
        this.startTime = System.currentTimeMillis();
    }
    public long getElapsedTime() {
        return System.currentTimeMillis() - this.startTime;
    }

    public long getRemainingTime() {
        return 2400000 - getElapsedTime();
    }
    public boolean isEnded() {
        return getRemainingTime() <= 0;
    }


    public static String getTime(int m) {
        int sec = (m / 1000) % 60;
        int min = m / 60000;
        return String.format(
                "%02d:%02d", min, sec
        );
    }
    public static String getTimeWithMillis(long m) {
        int r = (int) m;
        int millis = r % 1000;
        int sec = (r / 1000) % 60;
        int min = r / 60000;
        return String.format(
                "%02d:%02d.%03d", min, sec, millis
        );
    }
    public String getRemainingTimeAsString() {
        return getTime((int)getRemainingTime());
    }
}
