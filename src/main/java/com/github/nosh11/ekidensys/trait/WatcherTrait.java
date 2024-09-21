package com.github.nosh11.ekidensys.trait;

import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiTeam;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.Random;

@TraitName("watcher")
public class WatcherTrait extends Trait {
    public WatcherTrait() {
        super("watcher");
    }

    // Team
    @Persist int team_id = 0;

    public void resetTeam(int team_id) {
        this.team_id = team_id;
    }
    @Override
    public void onSpawn() {
        npc.addTrait(LookClose.class);
        setState(state);
    }
    // state:
    //  0 -> Standing
    //  1 -> Jumping
    //  2 -> Running
    //  3 -> Dead
    @Persist int state = 0;
    public void setState(int new_state) {
        state = new_state;
        npc.getOrAddTrait(LookClose.class)
                .lookClose(true);
    }

    @Override
    public void run() {
        if (npc.getEntity() == null) return;
        Entity e = npc.getEntity();
        if (state == 0)
            return;
        if (state == 1)
            if (e.isOnGround()) e.setVelocity(new Vector(0, 0.4, 0));
        if (state == 2)
            npc.setSneaking(new Random().nextInt(100) <= 20);
    }
    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
        if (event.getNPC() != npc) return;
        if (event.getClicker().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND)) {
            if (npc.getNavigator().getEntityTarget() != null)
                npc.getNavigator().cancelNavigation();
            else {
                setState(0);
                npc.getNavigator().setTarget(event.getClicker(), true);
            }
        }
        if (event.getClicker().getInventory().getItemInMainHand().getType().equals(Material.END_ROD))
            setState((state + 1) % 4);
        ApiTeam team = ApiManager.getInstance().getTeam(team_id);
        if (team != null)
            event.getClicker().sendMessage(team.getComponent());
    }
}