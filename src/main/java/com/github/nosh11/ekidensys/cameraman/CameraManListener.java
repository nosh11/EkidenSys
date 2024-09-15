package com.github.nosh11.ekidensys.cameraman;

import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiTeam;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CameraManListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        CameraManManager.getInstance().add(e.getPlayer());
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (!(e.getAction().isLeftClick() | e.getAction().isRightClick())) return;
        // 棒を持ってクリックすると ランダムな走者にターゲットを変更する
        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
            ApiTeam team = ApiManager.getInstance().getRandomTeam();
            CameraManManager.getInstance().get(e.getPlayer()).setTarget(team, true);
        }
    }
}
