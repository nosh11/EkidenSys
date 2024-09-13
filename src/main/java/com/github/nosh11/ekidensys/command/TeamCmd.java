package com.github.nosh11.ekidensys.command;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TeamCmd extends Cmd {
    public TeamCmd(EkidenSys plugin) {
        super(plugin);
    }

    @Override
    Cmd getInstance() {
        return this;
    }

    @Override
    public String getCommandName() {
        return "team";
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }
        else {
            switch (args[0]) {
                case "list" -> {
                    for (ApiTeam team : ApiManager.getInstance().getTeams()) {
                        sender.sendMessage(team.id + ": " + team.name);
                    }
                }
                case "info" -> {
                    if (args.length < 2) return false;
                    ApiTeam team = ApiManager.getInstance().getTeam(Integer.parseInt(args[1]));
                    sender.sendMessage(team.toString());
                }
                case "setpoint" -> {
                    if (args.length < 3) return false;
                    ApiTeam team = ApiManager.getInstance().getTeam(Integer.parseInt(args[1]));
                    team.getCurrentSession().point = Integer.parseInt(args[2]);
                    team.onSuccess();
                }
                case "back" -> {
                    if (args.length < 2) return false;
                    ApiTeam team = ApiManager.getInstance().getTeam(Integer.parseInt(args[1]));
                    team.backToOrigin();
                }
            }
        }
        return false;
    }
}
