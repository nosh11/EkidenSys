package com.github.nosh11.ekidensys.runner;

import com.github.nosh11.ekidensys.EkidenSys;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.Bukkit;
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

        npc.getNavigator().getDefaultParameters().baseSpeed(0.5f);
        npc.getOrAddTrait(Trait.class);
        npc.addRunnable(() -> {
            boolean check = navi.check(npc.getStoredLocation());
            if (check) {
                npc.getNavigator().setTarget(navi.next().getLocation());
                navi.add();
            }
        });

        this.npc.spawn(this.navi.origin().getLocation());
    }
}