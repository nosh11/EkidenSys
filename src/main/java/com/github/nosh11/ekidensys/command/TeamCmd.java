package com.github.nosh11.ekidensys.command;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiTeam;
import com.github.nosh11.ekidensys.cameraman.CameraManManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

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
                    for (ApiTeam team : ApiManager.getInstance().getTeams()
                            .stream().sorted(Comparator.comparing(ApiTeam::getId)).toList()) {
                        sender.sendMessage(MiniMessage.miniMessage()
                                .deserialize(team.getColor() + team.getNameWithID() + " : " + team.getCurrentSession().point));
                    }
                }
                case "info" -> {
                    if (args.length < 2) return false;
                    ApiTeam team = ApiManager.getInstance().getTeam(Integer.parseInt(args[1]));
                    sender.sendMessage(team.toString());
                }
                case "tp" -> {
                    if (args.length < 2) return false;
                    ApiTeam team = ApiManager.getInstance().getTeam(Integer.parseInt(args[1]));
                    if (team != null & sender instanceof Player) {
                        Player player = (Player) sender;
                        CameraManManager.getInstance().get(player).setTarget(team, true);
                    }
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
