package com.github.nosh11.ekidensys.command;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiTeam;
import com.github.nosh11.ekidensys.course.Course;
import com.github.nosh11.ekidensys.session.Session;
import com.github.nosh11.ekidensys.session.SessionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SessionCmd extends Cmd {
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
        if (args.length >= 1) {
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

                case "run" -> {
                    if (args.length < 2) return false;
                    EkidenSys.getInstance().setCurrentSessionId(Integer.parseInt(args[1]));
                    EkidenSys.getInstance().isRunning(true);
                    for (ApiTeam team : ApiManager.getInstance().getTeams()) {
                        team.resetNpc();
                    }
                }

                case "info" -> {
                    for (int j = 1; j <= 3; j++) {
                        Session session = SessionManager.getInstance().get(j-1);
                        sender.sendMessage(j + "区: ");
                        for (int i = 1; i <= 11; i++) {
                            sender.sendMessage(" - " + i + "000点: " + session.getCourse(i-1).getAllPoints().size());
                        }
                    }
                }

                case "stop" -> {
                    EkidenSys.getInstance().isRunning(false);
                }

                case "save" -> SessionManager.getInstance().save();
                case "reload" -> SessionManager.getInstance().reload();
            }
        }
        return false;
    }
}
