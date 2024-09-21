package com.github.nosh11.ekidensys.trait;

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
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.Random;

@TraitName("runner")
public class RunnerTrait extends Trait {
    public RunnerTrait() {
        super("runner");
    }

    // Team
    @Persist int team_id = 0;

    public void resetTeam(int team_id) {
        this.team_id = team_id;
    }
    public void resetTeam() {
        if (team_id == 0) return;
        ApiTeam team = ApiManager.getInstance().getTeam(team_id);
        if (team == null) return;
        team.setNpc(npc);
    }
    @Override
    public void onSpawn() {
        resetTeam();
        npc.addTrait(LookClose.class);
        setState(state);
    }

    @Persist int currentCourse = 0;
    @Persist int currentLocation = 0;
    @Persist int stamina = 4;
    // state:
    //  0 -> Standing
    //  1 -> Jumping
    //  2 -> Running
    //  3 -> Dead
    @Persist int state = 0;
    public void setState(int new_state) {
        state = new_state;
        npc.getTraitNullable(LookClose.class)
                .lookClose(state == 0 || state == 1);
    }

    @Override
    public void run() {
        Entity entity = npc.getEntity();
        if (entity == null) return;
        boolean isRunning = EkidenSys.getInstance().isRunning();
        if (state == 3) {
            return;
        }
        // State = 0, 1
        if (state == 0 | state == 1) {
            if (state == 1 && entity.isOnGround())
                npc.getEntity().setVelocity(new Vector(0, 0.4, 0));
            if (isRunning)
                if (currentCourse == 10 && getCurrentCourse().getPoint(currentLocation + 1) == null) return;
                else setState(2);
            return;
        }

        // State = 2
        if (!isRunning) {
            setState(0);
            return;
        }
        if (stamina == 0) {
            setState(3);
            return;
        }
        // ポイントに到達
        if (check()) {
            currentLocation++;
            if (getCurrentCourse().getPoint(currentLocation + 1) == null) {
                // 10kptコースの終点に到達 -> Jumping になる
                if (currentCourse == 10) {
                    setState(1);
                    return;
                }
                // 終点に到達 -> Originにテレポートで戻る
                npc.teleport(getCurrentCourse().origin(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                currentLocation = 0;
            }
        }
        Location next = getCurrentCourse().getPoint(currentLocation + 1);
        if (next != null & entity.isOnGround() & entity.getVelocity().length() <= 0.1)
            updateTarget(next);
    }
    public void back() {
        this.currentLocation = 0;
        npc.getNavigator().cancelNavigation();
        if (npc.isSpawned())
            npc.getEntity().teleport(getCurrentCourse().origin());
        else
            npc.spawn(getCurrentCourse().origin());
        if (getCurrentCourse().getPoint(currentLocation + 1) != null)
            updateTarget(getCurrentCourse().getPoint(currentLocation + 1));
    }
    public Course getCurrentCourse() {
        return SessionManager.getInstance()
                .get(EkidenSys.getInstance().getCurrentSessionId())
                .getCourse(currentCourse);
    }
    public void setCurrentLocation(int currentLocation) {
        setCurrentLocation(currentLocation, false);
    }
    public void setCurrentLocation(int currentLocation, boolean force) {
        if (this.currentCourse != currentLocation / 1000 || force) {
            this.currentCourse = currentLocation / 1000;
            back();
        }
    }


    public void setStamina(int new_stamina) {
        stamina = new_stamina;
        if (stamina == 0)
            setState(3);
    }

    public boolean check() {
        Location loc = npc.getStoredLocation(), next = getCurrentCourse().getPoint(currentLocation + 1);
        if (next == null) return false;
        double distance = Calc.getDistance(loc, next);
        return distance <= 3.0d;
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
        CameraManManager.getInstance().get(event.getClicker()).setTarget(team);
    }

    @Override
    public void onAttach() {
        npc.getNavigator().getLocalParameters().baseSpeed(2f);
    }
}