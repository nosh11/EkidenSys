package com.github.nosh11.ekidensys.api;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.runner.RunnerTrait;
import com.github.nosh11.ekidensys.session.SessionManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.TabListRemover;
import org.bukkit.entity.EntityType;

import java.util.List;

public class ApiTeam {
    public int id;
    public String name;
    public int rank;
    public int point;
    public long time;
    public List<Integer> memberIds;
    public List<ApiSession> sessions;
    private NPC npc;
    public float transition;

    public int getId() {
        return id;
    }

    public String getColor() {
        return String.format(
                "<transition:#ff7777:#77ff77:#7777ff:%.4f>",
                transition
        );
    }


    public ApiTeam(int id, String name, int rank, int point, long time, List<Integer> memberIds, List<ApiSession> sessions) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.point = point;
        this.time = time;
        this.memberIds = memberIds;
        this.sessions = sessions;
    }
    public ApiMember getCurrentMember() {
        return ApiManager.getInstance().getMember(getCurrentSession().memberId);
    }
    public ApiSession getCurrentSession() {
        return sessions.get(EkidenSys.getInstance().getCurrentSessionId());
    }

    public NPC getNpc() {
        return npc;
    }

    public String getNameWithID() {
        return "["+id+"] "+ name;
    }

    public void onSuccess() {
        if (npc == null) return;
        ApiSession session = getCurrentSession();
        RunnerTrait trait = npc.getTraitNullable(RunnerTrait.class);
        trait.setPoint(session.point);
        EkidenSys.broadcast("チーム["+name+"] が正解! "+ session.point);
    }

    public void onFail() {
        ApiMember member = getCurrentMember();
        int stamina = member.stamina;
        EkidenSys.broadcast(member.name + " が失敗しました 残りスタミナ: " + stamina);
    }

    public void onRankChanged() {

    }



    public void resetNpc() {
        if (npc != null) npc.destroy();
        NPC npc = CitizensAPI.getNPCRegistry()
                .createNPC(EntityType.PLAYER, getCurrentMember().name);
        npc.addTrait(RunnerTrait.class);
        npc.spawn(SessionManager.getInstance().get(EkidenSys.getInstance().getCurrentSessionId())
                .getCourse(0).origin());
        npc.getTraitNullable(RunnerTrait.class).init(this.id);
        this.npc = npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
        npc.getTraitNullable(RunnerTrait.class).setPoint(getCurrentSession().point);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rank=" + rank +
                ", point=" + point +
                ", time=" + time +
                ", memberIds=" + memberIds +
                ", sessions=" + sessions +
                '}';
    }

    public void backToOrigin() {
        npc.getTraitNullable(RunnerTrait.class).back();
    }
}