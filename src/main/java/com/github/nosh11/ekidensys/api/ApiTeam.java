package com.github.nosh11.ekidensys.api;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.trait.RunnerTrait;
import com.github.nosh11.ekidensys.session.SessionManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;

public class ApiTeam {
    public int id;
    public String name;
    public int rank;
    public int point;
    public int time;
    public List<Integer> memberIds;
    public List<ApiSession> sessions;
    private NPC npc;
    public float transition;

    public ApiTeam(int id, String name, int rank, int point, int time, List<Integer> memberIds, List<ApiSession> sessions) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.point = point;
        this.time = time;
        this.memberIds = memberIds;
        this.sessions = sessions;
    }

    public int getId() {
        return id;
    }

    public String getColor() {
        return String.format("<transition:#ff7777:#77ff77:#7777ff:%.4f>", transition);
    }

    public String getLightColor() {
        return String.format("<transition:#ffaaaa:#aaffaa:#aaaaff:%.4f>", transition);
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
        return "[" + id + "] " + name;
    }
    public String getMemberNameWithTeamName(ApiMember member) {
        return String.format("<%s> %s", name, member.name);
    }

    public void broadcastTeamInfo(Component component) {
        EkidenSys.broadcast(
                component.append(
                        MiniMessage.miniMessage().deserialize("      <color:gray><u>TP</u>")
                                .clickEvent(ClickEvent.runCommand("/team tp " + id)))
        );
    }

    public void onSuccess(int added_point) {
        if (npc == null && added_point < 0) return;
        ApiSession session = getCurrentSession();
        RunnerTrait trait = npc.getTraitNullable(RunnerTrait.class);
        trait.setCurrentLocation(session.point);

        Component text = MiniMessage.miniMessage().deserialize(String.format(
                "<color:#b5ffa5>[正解] %s%s</transition> +%dpt",
                getColor(),
                getMemberNameWithTeamName(getCurrentMember()),
                added_point
        ));

        broadcastTeamInfo(text);
    }

    public void onFail() {
        ApiMember member = getCurrentMember();
        int stamina = member.stamina;
        npc.getTraitNullable(RunnerTrait.class).setStamina(stamina);

        Component text;
        if (stamina <= 0)
            text = MiniMessage.miniMessage().deserialize(String.format(
                    "<color:red><b>[脱落] %s%s : スタミナを全損しました",
                    getColor(),
                    getMemberNameWithTeamName(member)
                    )
            );
        else
            text = MiniMessage.miniMessage().deserialize(String.format(
                    "<color:red>[失敗] %s%s : %s",
                    getColor(),
                    getMemberNameWithTeamName(member),
                    member.getHeart()
                    )
            );
        broadcastTeamInfo(text);
    }

    public void onRankChanged(int old_rank, int new_rank) {
        if (old_rank > new_rank)
            broadcastTeamInfo(
                    MiniMessage.miniMessage().deserialize(String.format(
                            "<color:#eeee66>[順位変動] %s%s : </transition><color:gold>%d位 → <b>%d位</b></color>",
                            getColor(),
                            getNameWithID(),
                            old_rank,
                            new_rank))
            );
    }


    public void tpNpc(Location loc) {
        if (npc == null) resetNpc();
        if (!npc.isSpawned()) npc.spawn(loc);
        else npc.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }


    public void resetNpc() {
        if (npc != null) npc.destroy();
        NPC npc = CitizensAPI.getNPCRegistry()
                .createNPC(EntityType.PLAYER, getCurrentMember().name);
        npc.addTrait(RunnerTrait.class);
        npc.spawn(SessionManager.getInstance().get(EkidenSys.getInstance().getCurrentSessionId())
                .getCourse(0).origin());
        npc.getTraitNullable(RunnerTrait.class).resetTeam(this.id);
        this.npc = npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
        npc.getTraitNullable(RunnerTrait.class).setCurrentLocation(getCurrentSession().point, true);
    }
    public void backToOrigin() {
        npc.getTraitNullable(RunnerTrait.class).back();
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

    public Component getComponent() {
        String color = getColor();
        String lightColor = getLightColor();
        String name = getNameWithID();
        Component component = MiniMessage.miniMessage().deserialize(color + name)
                .clickEvent(ClickEvent.runCommand("/team tp " + id));

        int i = 1;
        for (ApiSession session : sessions) {
            ApiMember member = ApiManager.getInstance().getMember(session.memberId);
            String session_text = String.format(
                    "[%d区] %s %s : %d pt",
                    i,
                    member.name,
                    member.getHeart(),
                    session.point
            );
            Component hover = MiniMessage.miniMessage().deserialize(lightColor + session_text);
            int j = 1;
            for (ApiLevel level : session.levels) {
                StringBuilder hoverText = new StringBuilder("\n - Lv.").append(j);
                if (level.time == 0)
                    hoverText
                            .append(" : <color:#ff8888>×</color> ")
                            .append("失敗回数: ")
                            .append(level.death);
                else
                    hoverText
                            .append(" : <color:#88ff88>〇</color> ")
                            .append("解答時間: ")
                            .append(EkidenSys.getTimeWithMillis(level.time));
                j++;
                hover = hover.append(
                        MiniMessage.miniMessage().deserialize(hoverText.toString())
                );
            }
            Component sessionComponent = MiniMessage.miniMessage().deserialize(lightColor + session_text)
                    .hoverEvent(HoverEvent.showText(hover));
                    //.clickEvent(ClickEvent.runCommand("/member info " + member.id));
            component = component.appendNewline().append(sessionComponent);
            i++;
        }
        Component totalComponent = MiniMessage.miniMessage()
                .deserialize(String.format(color + " >> <b>Total : %d pt (%d位)", point, rank));
        component = component.appendNewline().append(totalComponent);
        return component;
    }
}