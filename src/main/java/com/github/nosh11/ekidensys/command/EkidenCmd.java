package com.github.nosh11.ekidensys.command;

import com.github.nosh11.ekidensys.EkidenSys;
import com.github.nosh11.ekidensys.api.ApiManager;
import com.github.nosh11.ekidensys.api.ApiMember;
import com.github.nosh11.ekidensys.api.ApiTeam;
import com.github.nosh11.ekidensys.trait.RunnerTrait;
import com.github.nosh11.ekidensys.trait.WatcherTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class EkidenCmd extends Cmd implements TabCompleter {
    public EkidenCmd(com.github.nosh11.ekidensys.EkidenSys plugin) {
        super(plugin);
    }
    @Override
    Cmd getInstance() {
        return this;
    }
    @Override
    public String getCommandName() {
        return "ekiden";
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }

        switch (args[0]) {
            case "reload" -> {
                if (args.length < 2) return false;
                if (args[1].equals("config") | args[1].equals("all")){
                    sender.sendMessage("Configのリロードが完了しました。");
                    EkidenSys.getInstance().loadConfig();
                }
                if (args[1].equals("api") | args[1].equals("all")){
                    sender.sendMessage("APIのリロードが完了しました。");
                    ApiManager.getInstance().reloadAll();
                }
            }
            case "api_mode" -> {
                EkidenSys.getInstance().apiMode = !EkidenSys.getInstance().apiMode;
                EkidenSys.getInstance().saveConfig();
                sender.sendMessage("ApiMode を" + EkidenSys.getInstance().apiMode + "にしました");
            }
            case "api_url" -> {
                if (args.length < 2) return false;
                EkidenSys.getInstance().apiUrl = args[1];
                EkidenSys.getInstance().saveConfig();
                sender.sendMessage("ApiUrl を" + EkidenSys.getInstance().apiUrl + "にしました");
            }
            case "watcher" -> {
                if (sender instanceof Player p) {
                    for (NPC npc : CitizensAPI.getNPCRegistry()) {
                        if (npc.getTraitNullable(WatcherTrait.class) != null)
                            npc.removeTrait(WatcherTrait.class);
                    }
                    Random random = new Random();
                    for (ApiMember member : ApiManager.getInstance().getMembers()) {
                        ApiTeam team = ApiManager.getInstance().getTeam(member.team_id);
                        if (team == null) continue;
                        NPC npc = CitizensAPI.getNPCRegistry()
                                .createNPC(EntityType.PLAYER, team.getMemberNameWithTeamName(member));
                        npc.addTrait(WatcherTrait.class);
                        npc.getTraitNullable(WatcherTrait.class).resetTeam(member.team_id);
                        npc.spawn(p.getLocation().add(random.nextDouble(10)-5,0, random.nextDouble(30)-15));
                    }
                }
            }


            case "help" -> sender.sendMessage("本システムのヘルプについては配られたドキュメントを読んでほしいです");
        }
        return false;
    }
    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command cmd,
            @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 1) {
            if (args[0].equals("reload"))
                return List.of("config", "api", "all");
        }
        return List.of("api_mode", "api_url", "reload", "help");
    }
}
