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
            for (ApiTeam team : ApiManager.getInstance().getTeams()
                    .stream().sorted(Comparator.comparing(ApiTeam::getId)).toList()) {
                sender.sendMessage(MiniMessage.miniMessage()
                        .deserialize(team.getColor() + team.getNameWithID() + " : " + team.getCurrentSession().point));
            }
            return false;
        }

        else if (args.length < 2) return false;
        ApiTeam team = ApiManager.getInstance().getTeam(Integer.parseInt(args[1]));
        if (team == null) {
            sender.sendMessage("そのチームは存在しません");
            return false;
        }

        switch (args[0]) {
            case "info" -> sender.sendMessage(team.getComponent());
            case "set_point" -> {
                if (EkidenSys.getInstance().apiMode) {
                    sender.sendMessage("APIMode がTrueのとき これは実行できません");
                    return false;
                }
                if (args.length < 3) {
                    sender.sendMessage("/team setpoint " + args[1] + " <ポイント>");
                    return false;
                }
                try {
                    int new_point = Integer.parseInt(args[2]);
                    int old_point = team.getCurrentSession().point;
                    team.getCurrentSession().point = new_point;
                    team.onSuccess(new_point - old_point);
                } catch (NumberFormatException e) {
                    sender.sendMessage("数値のフォーマットが正しくありません");
                }
            }
            case "set_stamina" -> {
                if (EkidenSys.getInstance().apiMode) {
                    sender.sendMessage("APIMode がTrueのとき これは実行できません");
                    return false;
                }
                if (args.length < 3) {
                    sender.sendMessage("/team setpoint " + args[1] + " <スタミナ>");
                    return false;
                }
                try {
                    team.getCurrentMember().stamina = Integer.parseInt(args[2]);
                    team.onFail();
                } catch (NumberFormatException e) {
                    sender.sendMessage("数値のフォーマットが正しくありません");
                }
            }
            case "tp" -> {
                if (sender instanceof Player player) {
                    CameraManManager.getInstance().get(player).setTarget(team, true);
                }
            }
            case "back" -> team.backToOrigin();
        }
        return false;
    }
}
