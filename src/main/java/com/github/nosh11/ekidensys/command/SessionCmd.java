package com.github.nosh11.ekidensys.command;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiTeam;
import com.github.nosh11.ekidensys.course.Course;
import com.github.nosh11.ekidensys.trait.RunnerTrait;
import com.github.nosh11.ekidensys.session.Session;
import com.github.nosh11.ekidensys.session.SessionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SessionCmd extends Cmd implements TabCompleter {
    public SessionCmd(EkidenSys plugin) {
        super(plugin);
    }

    @Override
    Cmd getInstance() {
        return this;
    }

    @Override
    public String getCommandName() {
        return "session";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length < 1) {
            if (EkidenSys.getInstance().isRunning()) {
                for (ApiTeam team : ApiManager.getInstance().getTeams()) {
                    StringBuilder sb = new StringBuilder();
                    int point = team.getCurrentSession().point/1000;
                    sb.append("■".repeat(Math.max(0, point)));
                    sender.sendMessage(
                            "["+team.id+"]" + team.name + " : " +team.getCurrentMember().name
                    );
                    sender.sendMessage(
                            " >> " + point * 1000 + "pt " + sb
                    );
                }
            }
            return false;
        }
        switch (args[0]) {
            case "addpoint" -> {
                if (args.length < 3) return false;
                int session_id = Integer.parseInt(args[1]);
                int course_id = Integer.parseInt(args[2]);
                Course course = SessionManager.getInstance().get(session_id)
                        .getCourse(course_id);
                if (sender instanceof Player p) {
                    course.addPoint(p.getLocation());
                }
            }

            case "start" -> {
                EkidenSys.getInstance().isRunning(true);
            }

            case "run" -> {
                if (args.length < 2) return false;
                try {
                    int session_id = Integer.parseInt(args[1]);
                    if (session_id < 0 | 3 <= session_id) {
                        sender.sendMessage("Session ID が不正です");
                        return false;
                    }
                    EkidenSys.getInstance().gameStart(session_id);
                } catch (NumberFormatException e) {
                    sender.sendMessage("Session ID は整数値である必要がありますです");
                }
            }
            case "reset_npc" -> {
                for (ApiTeam t : ApiManager.getInstance().getTeams()) {
                    sender.sendMessage(t.name);
                    RunnerTrait trait = t.getNpc().getTraitNullable(RunnerTrait.class);
                    if (trait == null) continue;
                    trait.setCurrentLocation(t.getCurrentSession().point);
                }
            }


            case "wait" -> {
                if (args.length < 2) return false;
                try {
                    int session_id = Integer.parseInt(args[1]);
                    if (session_id < 0 | 3 <= session_id) {
                        sender.sendMessage("Session ID が不正です");
                        return false;
                    }
                    EkidenSys.getInstance().setCurrentSessionId(session_id);
                    for (ApiTeam team : ApiManager.getInstance().getTeams()) {
                        team.tpNpc(SessionManager.getInstance().get(session_id).getCourse(0).origin(10));
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("Session ID は整数値である必要がありますです");
                }
            }

            case "location" -> {
                for (int j = 1; j <= 3; j++) {
                    Session session = SessionManager.getInstance().get(j-1);
                    sender.sendMessage(j + "区: ");
                    for (int i = 1; i <= 11; i++) {
                        sender.sendMessage(" - " + (i-1)*1000 + "点: " + session.getCourse(i-1).getAllPoints().size());
                    }
                }
            }

            case "update" -> {
                ApiManager.getInstance().update();
            }

            case "stop" -> {
                EkidenSys.getInstance().isRunning(false);
            }

            case "save" -> SessionManager.getInstance().save();
            case "reload" -> SessionManager.getInstance().reload();
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command cmd,
            @NotNull String label,
            @NotNull String[] args) {
        if (args.length >= 1)
            if (args[0].equals("reload"))
                return List.of("config", "api", "all");
            else return List.of();
        return List.of("reload", "help", "save", "location", "run", "wait");
    }
}
