package com.github.nosh11.ekidensys.runner;

import com.github.nosh11.ekidensys.course.Course;
import com.github.nosh11.ekidensys.course.CourseManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.EntityType;

public class Runner {
    private final String displayName;
    private int hp;
    private final NPC npc;
    private final String uuid;


    public Runner(NPC npc) {
        this.uuid = npc.getUniqueId().toString();
        this.npc = npc;

        this.displayName = npc.getName();
        this.hp = 4;
    }

    public Runner(String course_id, String displayName, int hp) {
        this.displayName = displayName;
        this.hp = hp;

        Course course = CourseManager.getInstance().get(course_id);

        this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, displayName);
        // CitizensAPI.getNPCRegistry().deregister(this.npc);
        this.uuid = this.npc.getUniqueId().toString();

        npc.getNavigator().getDefaultParameters().baseSpeed(2f);
        npc.addTrait(new RunnerTrait());
        npc.getOrAddTrait(RunnerTrait.class).init(course);
        this.npc.spawn(course.origin().getLocation());
    }
}