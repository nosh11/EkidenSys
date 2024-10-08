package com.github.nosh11.ekidensys.cameraman;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.api.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class CameraMan {
    private final Player player;
    private ApiTeam target = null;

    public CameraMan(Player player) {
        this.player = player;
    }
    public void update() {
        if (target != null) {
            String transition = target.getColor();
            player.sendActionBar(MiniMessage.miniMessage()
                    .deserialize(String.format(
                            "%s[%s] <color:#cccccc>%s <color:#cc1111>%s%s %d pt",
                            transition,
                            target.name,
                            target.getCurrentMember().name,
                            transition,
                            target.getCurrentMember().getHeart(),
                            target.getCurrentSession().point
                    ))
            );
        }
        else if (!EkidenSys.getInstance().isRunning()) {
            player.sendActionBar(Component.text(
                    "試合は開始されていません"
            ));
        }
    }

    public void sendResult() {
        player.sendMessage("結果");
    }

    public Player getPlayer() {
        return this.player;
    }

    public ApiTeam getTarget() {
        return this.target;
    }

    public void unsetTarget() {
        this.target = null;
    }

    public void setTarget(ApiTeam team) {
        setTarget(team, false);
    }

    public void setTarget(ApiTeam team, boolean teleport) {
        this.target = team;
        if (teleport) {
            player.teleport(target.getNpc().getStoredLocation());
        }
        sendTeamInfo();
        update();
    }

    public void sendTeamInfo() {
        player.sendMessage(target.getComponent());
    }
}