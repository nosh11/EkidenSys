package com.github.nosh11.ekidensys.cameraman;

import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiTeam;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CameraManListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        CameraManManager.getInstance().add(player);
        CameraManBoardManager.getInstance().add(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        CameraManBoardManager.getInstance().remove(player);
    }


    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (!(e.getAction().isLeftClick() | e.getAction().isRightClick())) return;
        // 棒を持ってクリックすると ランダムな走者にターゲットを変更する
        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.BLAZE_ROD)) {
            ApiTeam team = ApiManager.getInstance().getRandomTeam();
            CameraManManager.getInstance().get(e.getPlayer()).setTarget(team, true);
        }
    }
}
