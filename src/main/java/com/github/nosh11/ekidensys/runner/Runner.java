package com.github.nosh11.ekidensys.runner;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;

public class Runner {
    private final String displayName;
    private int hp;
    private final NPC npc;
    private final RunnerNavi navi;

    public Runner(String course_id, String displayName, int hp) {
        this.displayName = displayName;
        this.hp = hp;
        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, displayName);
        this.navi = new RunnerNavi(course_id);
        this.npc.spawn(this.navi.origin().getLocation());
    }

    public void update() {
        if (navi.check(npc.getStoredLocation())) {
            npc.getNavigator().setTarget(
                    navi.current().getLocation()
            );
        }
    }
}
