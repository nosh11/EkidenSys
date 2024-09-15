package com.github.nosh11.ekidensys.runner;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiTeam;
import com.github.nosh11.ekidensys.cameraman.CameraManManager;
import com.github.nosh11.ekidensys.course.Course;
import com.github.nosh11.ekidensys.session.SessionManager;
import com.github.nosh11.ekidensys.util.Calc;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.Random;

@TraitName("runner")
public class RunnerTrait extends Trait {
    @Persist int team_id = 0;
    @Persist int current_course = 0;
    @Persist int point = 0;
    @Persist int stamina = 4;

    public RunnerTrait() {
        super("runner");
    }

    public void back() {
        this.point = 0;
        npc.getNavigator().cancelNavigation();
        if (npc.isSpawned())
            npc.teleport(getCurrentCourse().origin(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        else
            npc.spawn(getCurrentCourse().origin());
        if (getCurrentCourse().getPoint(point + 1) != null)
            updateTarget(getCurrentCourse().getPoint(point + 1));
    }

    public void init(int team_id) {
        this.team_id = team_id;
    }
    public void init() {
        if (team_id == 0) return;
        ApiTeam team = ApiManager.getInstance().getTeam(team_id);
        if (team == null) {
            EkidenSys.getInstance().getLogger().info("チーム ["+team_id+"] が存在しませんでした");
            return;
        }
        team.setNpc(npc);
    }

    @Override
    public void onSpawn() {
        init();
    }

    public Course getCurrentCourse() {
        return SessionManager.getInstance()
                .get(EkidenSys.getInstance().getCurrentSessionId())
                .getCourse(current_course);
    }

    public void setPoint(int point) {
        if (this.current_course != point / 1000) {
            this.current_course = point / 1000;
            back();
        }
    }


    public void setStamina(int new_hp) {
        this.stamina = new_hp;
    }

    public boolean check() {
        Location loc = npc.getStoredLocation(), next = getCurrentCourse().getPoint(point + 1);
        if (next == null) return false;
        double distance = Calc.getDistance(loc, next);
        return distance <= 3.0d;
    }

    @Override
    public void run() {
        Entity entity = npc.getEntity();
        if (entity == null | getCurrentCourse() == null) return;
        if (check()) {
            point++;
            // 終点に到達 -> Originにテレポートで戻る
            if (getCurrentCourse().getPoint(point + 1) == null) {
                npc.teleport(getCurrentCourse().origin(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                point = 0;
            }
        }
        Location next = getCurrentCourse().getPoint(point + 1);
        if (next != null && entity.isOnGround() && entity.getVelocity().length() <= 0.1) {
            updateTarget(next);
        }
    }

    private void updateTarget(Location location) {
        Random r = new Random();
        Location next = location.clone().add(new Vector((r.nextDouble()-0.5d)*2.5, 0, (r.nextDouble()-0.5d)*2.5));
        if (!npc.isSpawned())
            npc.spawn(npc.getStoredLocation());
        npc.getNavigator().setTarget(next);
    }

    @EventHandler
    public void click(net.citizensnpcs.api.event.NPCRightClickEvent event) {
        if (event.getNPC() != npc) return;
        RunnerTrait trait = event.getNPC().getTraitNullable(RunnerTrait.class);

        ApiTeam team = ApiManager.getInstance().getTeam(trait.team_id);
        if (team == null) return;
        event.getClicker().sendMessage(String.format("チームID: %d", trait.team_id));
        CameraManManager.getInstance().get(event.getClicker()).setTarget(team);
    }

    @Override
    public void onAttach() {
        npc.getNavigator().getLocalParameters().baseSpeed(2f);
    }
}