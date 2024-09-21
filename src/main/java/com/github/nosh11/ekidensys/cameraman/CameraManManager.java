package com.github.nosh11.ekidensys.cameraman;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraManManager {
    private static final CameraManManager instance = new CameraManManager();
    public static CameraManManager getInstance() {
        return instance;
    }

    public void update() {
        for (CameraMan camera : list.values()) {
            if (camera.getPlayer().isOnline()) camera.update();
        }
    }

    private final Map<String, CameraMan> list = new HashMap<>();

    public void add(Player p) {
        list.put(p.getUniqueId().toString(), new CameraMan(p));
    }

    public CameraMan get(String uuid) {
        return list.get(uuid);
    }

    public CameraMan get(Player p) {
        return get(p.getUniqueId().toString());
    }

    public List<CameraMan> getAll() {
        return list.values().stream().toList();
    }
}
